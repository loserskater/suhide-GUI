/*
 *    Copyright 2016 Jason Maxfield
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.loserskater.suhidegui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.l4digital.fastscroll.FastScrollRecyclerView;
import com.loserskater.suhidegui.adapters.PackageAdapter;
import com.loserskater.suhidegui.utils.Utils;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class PackageActivity extends AppCompatActivity {

    private static final String XDA_LINK = "http://forum.xda-developers.com/apps/supersu/suhide-t3450396";
    private FastScrollRecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);

        mRecyclerView = (FastScrollRecyclerView) findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        new getSUAndSetAdapter().execute();
    }

    private void showIncorrectIDs() {
        if (!Utils.invalidIDs.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String id : Utils.invalidIDs) {
                sb.append(id).append("\n");
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.invalid_ids))
                    .setMessage(String.format(getString(R.string.invalid_ids_message), sb.toString()))
                    .setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Utils.removeInvalidUIDs(PackageActivity.this);
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(true)
                    .create().show();
        }

    }

    private class getSUAndSetAdapter extends AsyncTask<Void, Void, Boolean> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(PackageActivity.this, getString(R.string.please_wait), getString(R.string.check_su_and_load_packages), true);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPostExecute(Boolean exists) {
            mRecyclerView.setAdapter(mAdapter);
            dialog.dismiss();
            if (exists == null) {
                Utils.haveRoot = false;
                Snackbar.make(mCoordinatorLayout, getString(R.string.no_root), Snackbar.LENGTH_INDEFINITE).show();
            } else if (!exists) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PackageActivity.this);
                builder.setTitle(getString(R.string.suhide_not_found))
                        .setMessage(getString(R.string.suhide_not_found_message))
                        .setNeutralButton(getString(R.string.xda_thread), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(XDA_LINK)));
                                finish();
                            }
                        })
                        .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create().show();
            } else {
                    showIncorrectIDs();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            mAdapter = new PackageAdapter(PackageActivity.this);
            if (!Shell.SU.available()) {
                return null;
            }
            List<String> output = Shell.SU.run(String.format(Utils.COMMAND_CHECK_FILE_EXISTS, Utils.UID_FILE_PATH));
            return output.get(0).matches(Utils.EXISTS);
        }
    }
}

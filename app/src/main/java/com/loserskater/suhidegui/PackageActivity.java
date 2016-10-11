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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.loserskater.suhidegui.fragments.PackageFragment;
import com.loserskater.suhidegui.utils.Utils;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class PackageActivity extends AppCompatActivity {

    private static final String XDA_LINK = "http://forum.xda-developers.com/apps/supersu/suhide-t3450396";

    private CoordinatorLayout mCoordinatorLayout;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);

        new getSUAndPackages().execute();
    }

    private class getSUAndPackages extends AsyncTask<Void, Void, Boolean> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(PackageActivity.this, getString(R.string.please_wait), getString(R.string.check_su_and_load_packages), true);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPostExecute(Boolean exists) {
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

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
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (!Shell.SU.available()) {
                return null;
            }
            Utils.initiateLists(PackageActivity.this);
            List<String> output = Shell.SU.run(String.format(Utils.COMMAND_CHECK_FILE_EXISTS, Utils.UID_FILE_PATH));
            return output.get(0).matches(Utils.EXISTS);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PackageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.uid);
                case 1:
                    return getString(R.string.process_name);
//                case 2:
//                    return getString(R.string.custom);
            }
            return null;
        }
    }
}

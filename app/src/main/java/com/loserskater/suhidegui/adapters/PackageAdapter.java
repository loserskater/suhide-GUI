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

package com.loserskater.suhidegui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.loserskater.suhidegui.R;
import com.loserskater.suhidegui.objects.Package;
import com.loserskater.suhidegui.utils.Utils;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.PackageViewHolder> implements FastScrollRecyclerView.SectionedAdapter {
    private ArrayList<Package> appn;
    private ArrayList<Integer> addedUIDs;

    public PackageAdapter(Context context) {
        this.appn = Utils.getInstalledApps(context);
        this.addedUIDs = Utils.getAddedUIDs();
    }


    @Override
    public int getItemCount() {
        return appn.size();
    }

    @Override
    public void onBindViewHolder(PackageViewHolder holder, int pos) {
        holder.setPackage(appn.get(pos));
        holder.UID.setText(Integer.toString(holder.mPackage.getUid()));
        holder.name.setText(holder.mPackage.getName());
        holder.icon.setImageDrawable(holder.mPackage.getIcon());
        holder.checkBox.setEnabled(Utils.haveRoot);
        holder.checkBox.setClickable(Utils.haveRoot);
        if (addedUIDs.contains(holder.mPackage.getUid())) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }
    }

    @Override
    public PackageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_package, viewGroup, false);
        return new PackageViewHolder(itemView);
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return appn.get(position).getName().substring(0, 1).toUpperCase();
    }

    public class PackageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView UID;
        TextView name;
        ImageView icon;
        Package mPackage;
        CheckBox checkBox;


        public PackageViewHolder(View v) {
            super(v);
            UID = (TextView) v.findViewById(R.id.package_UID);
            name = (TextView) v.findViewById(R.id.package_name);
            icon = (ImageView) v.findViewById(R.id.package_icon);
            checkBox = (CheckBox) v.findViewById(R.id.package_enabled_checkbox);
            checkBox.setOnClickListener(this);
        }


        public void setPackage(Package aPackage) {
            this.mPackage = aPackage;
        }

        @Override
        public void onClick(View v) {
            final boolean isChecked = checkBox.isChecked();
            int uid = mPackage.getUid();
            if (isChecked) {
                Utils.addUID(uid);
                addedUIDs.add(uid);
            } else {
                Utils.removeUID(uid);
                addedUIDs.remove(Integer.valueOf(uid));
            }
            notifyDataSetChanged();
        }
    }
}
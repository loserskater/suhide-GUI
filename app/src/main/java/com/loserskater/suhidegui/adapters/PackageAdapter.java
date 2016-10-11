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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.l4digital.fastscroll.FastScroller;
import com.loserskater.suhidegui.R;
import com.loserskater.suhidegui.objects.Package;
import com.loserskater.suhidegui.utils.Utils;

import java.util.ArrayList;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.PackageViewHolder> implements Filterable, FastScroller.SectionIndexer {
    private ArrayList<Package> currentList;
    private ArrayList<Package> filterList;

    public PackageAdapter(ArrayList<Package> list) {
        this.currentList = list;
        this.filterList = list;
    }

    @Override
    public int getItemCount() {
        return currentList.size();
    }

    @Override
    public void onBindViewHolder(PackageViewHolder holder, int pos) {
        holder.setPackage(currentList.get(pos));
        holder.name.setText(holder.mPackage.getName());
        // Really hacky way of handling both Package and process names
        // If it's a process we don't have an icon so we'll use that to hide the UID
        if (holder.mPackage.getIcon() != null) {
            holder.icon.setImageDrawable(holder.mPackage.getIcon());
            holder.UID.setText(holder.mPackage.getUid());
        }
        holder.checkBox.setEnabled(Utils.haveRoot);
        holder.checkBox.setClickable(Utils.haveRoot);
        if (Utils.getAddedIDs().contains(holder.mPackage.getUid())) {
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
    public String getSectionText(int position) {
        return currentList.get(position).getName().substring(0, 1).toUpperCase();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                currentList = (ArrayList<Package>) results.values;
                PackageAdapter.this.notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<Package> filteredResults = null;
                if (constraint.length() == 0) {
                    filteredResults = filterList;
                } else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }
        };
    }

    protected ArrayList<Package> getFilteredResults(String constraint) {
        ArrayList<Package> results = new ArrayList<>();

        for (Package item : filterList) {
            if (item.getName().toLowerCase().contains(constraint)) {
                results.add(item);
            }
        }
        return results;
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
            String uid = mPackage.getUid();
            if (isChecked) {
                Utils.addUID(uid);
                Utils.getAddedIDs().add(uid);
            } else {
                Utils.removeUID(uid);
                Utils.getAddedIDs().remove(uid);
            }
            notifyDataSetChanged();
        }
    }
}
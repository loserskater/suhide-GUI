package com.loserskater.suhidegui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.l4digital.fastscroll.FastScrollRecyclerView;
import com.loserskater.suhidegui.R;
import com.loserskater.suhidegui.adapters.PackageAdapter;
import com.loserskater.suhidegui.objects.Package;
import com.loserskater.suhidegui.utils.Utils;

import java.util.ArrayList;

public class PackageFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int UID = 0;
    private static final int PROCESS = 1;

    private FastScrollRecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Package> list;

    public PackageFragment() {
    }

    public static PackageFragment newInstance(int section) {
        PackageFragment fragment = new PackageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, section);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (FastScrollRecyclerView) rootView.findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        if (getArguments().getInt(ARG_SECTION_NUMBER) == UID) {
            mAdapter = new PackageAdapter(Utils.getPackages());
        } else {
            mAdapter = new PackageAdapter(Utils.getProcesses());
        }
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }
}

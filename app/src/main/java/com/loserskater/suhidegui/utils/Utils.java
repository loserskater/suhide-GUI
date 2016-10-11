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

package com.loserskater.suhidegui.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import com.loserskater.suhidegui.objects.Package;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class Utils {

    public static final String UID_FILE_PATH = "/su/suhide/suhide.uid";
    public static final String COMMAND_UID_LIST = "/su/suhide/list";
    public static final String COMMAND_UID_ADD = "/su/suhide/add \"%s\"";
    public static final String COMMAND_UID_REMOVE = "/su/suhide/rm \"%s\"";
    public static final String EXISTS = "exists";
    public static final String DOES_NOT_EXIST = "doesnt_exist";
    public static final String COMMAND_CHECK_FILE_EXISTS = "ls %s > /dev/null 2>&1 && echo " + EXISTS + " || echo " + DOES_NOT_EXIST;
    public static boolean haveRoot = true;
    public static ArrayList<Package> packages;
    public static ArrayList<Package> processes;
    public static ArrayList<String> addedIDs;


    public static void initiateLists(Context context) {
        getInstalledApps(context);
        getRunningProcesses();
        getAddedUIDs();
    }

    public static ArrayList<Package> getPackages() {
        return packages;
    }

    public static void setPackages(ArrayList<Package> packages) {
        Utils.packages = packages;
    }

    public static void setProcesses(ArrayList<Package> processes) {
        Utils.processes = processes;
    }


    public static ArrayList<Package> getProcesses() {
        return processes;
    }

    public static ArrayList<String> getAddedIDs() {
        return addedIDs;
    }

    public static void setAddedIDs(ArrayList<String> addedIDs) {
        Utils.addedIDs = addedIDs;
    }

    private static void getInstalledApps(Context context) {
        ArrayList<Package> packages = new ArrayList<Package>();
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            String name = p.applicationInfo.loadLabel(context.getPackageManager()).toString();
            String uid = Integer.toString(p.applicationInfo.uid);
            Drawable icon = p.applicationInfo.loadIcon(context.getPackageManager());
            packages.add(new Package(name, uid, icon));
        }
        Collections.sort(packages, new Package.PackageNameComparator());
        setPackages(packages);
    }

    private static void getRunningProcesses() {
        List<Package> processes = new ArrayList<>();
        List<String> processNames = Shell.SU.run("for proc in /proc/*[0-9]*/cmdline; do cat \"$proc\"; echo \"\" ;done");
        for (String processName : processNames) {
            if (!processName.trim().isEmpty() && !isBadName(processName)) {
                Package p = new Package(processName.trim(), processName.trim(), null);
                processes.add(p);
            }
        }
        Collections.sort(processes, new Package.PackageNameComparator());
        setProcesses(new ArrayList<>(processes));
    }

    private static boolean isBadName(String processName) {
        return processName.contains("/") || !processName.contains(".");
    }

    private static void getAddedUIDs() {
        List<String> list = Shell.SU.run(COMMAND_UID_LIST);
        setAddedIDs(new ArrayList<>(list));
    }

    public static void addUID(String uid) {
        new runBackgroudTask()
                .execute(String.format(COMMAND_UID_ADD, uid));
    }

    public static void removeUID(String uid) {
        new runBackgroudTask()
                .execute(String.format(COMMAND_UID_REMOVE, uid));
    }

    private static class runBackgroudTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            Shell.SU.run(params);
            return null;
        }
    }
}

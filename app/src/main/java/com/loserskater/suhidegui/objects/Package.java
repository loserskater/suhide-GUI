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

package com.loserskater.suhidegui.objects;

import android.graphics.drawable.Drawable;

import java.util.Comparator;

public class Package {

    private String name;
    private int uid;
    private Drawable icon;

    public Package(String name, int uid, Drawable icon) {
        this.name = name;
        this.uid = uid;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setNames(String name) {
        this.name = name;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public static class PackageNameComparator implements Comparator<Package> {
        public int compare(Package left, Package right) {
            return left.name.compareToIgnoreCase(right.name);
        }
    }

}

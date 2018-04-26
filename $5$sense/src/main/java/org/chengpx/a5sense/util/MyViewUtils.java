package org.chengpx.a5sense.util;

import android.view.View;

import org.chengpx.a5sense.SenseApp;

/**
 * create at 2018/4/23 12:12 by chengpx
 */
public class MyViewUtils {

    public static View inflate(int resId) {
        return View.inflate(SenseApp.getContext(), resId, null);
    }

}

package org.chengpx.a2trafficlight.util;

import android.view.View;

import org.chengpx.a2trafficlight.TraLightApp;

/**
 * create at 2018/4/23 12:12 by chengpx
 */
public class MyViewUtils {

    public static View inflate(int resId) {
        return View.inflate(TraLightApp.getContext(), resId, null);
    }

}

package org.chengpx.a3caraccountrecharge.util;

import android.view.View;

import org.chengpx.a3caraccountrecharge.CarApp;

/**
 * create at 2018/4/23 12:12 by chengpx
 */
public class MyViewUtils {

    public static View inflate(int resId) {
        return View.inflate(CarApp.getContext(), resId, null);
    }

}

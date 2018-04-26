package org.chengpx.a3caraccountrecharge;

import android.app.Application;
import android.content.Context;

/**
 * create at 2018/4/24 9:54 by chengpx
 */
public class CarApp extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
    }

    public static Context getContext() {
        return sContext;
    }

}

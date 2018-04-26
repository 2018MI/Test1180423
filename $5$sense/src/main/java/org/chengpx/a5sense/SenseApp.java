package org.chengpx.a5sense;

import android.app.Application;
import android.content.Context;

/**
 * create at 2018/4/25 14:00 by chengpx
 */
public class SenseApp extends Application {

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

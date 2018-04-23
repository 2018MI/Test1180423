package org.chengpx.a2trafficlight;

import android.app.Application;
import android.content.Context;

/**
 * create at 2018/4/23 12:16 by chengpx
 */
public class TraLightApp extends Application {

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

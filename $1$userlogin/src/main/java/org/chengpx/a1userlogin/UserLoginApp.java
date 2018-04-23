package org.chengpx.a1userlogin;

import android.app.Application;
import android.content.Context;

/**
 * create at 2018/4/23 11:22 by chengpx
 */
public class UserLoginApp extends Application {

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

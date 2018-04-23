package org.chengpx.a1userlogin.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import org.chengpx.a1userlogin.UserLoginApp;

/**
 * sp 工具类
 * <p>
 * create at 2018/4/23 11:05 by chengpx
 */
public class SPUtils {

    private static SharedPreferences sUsercfg;
    private static SharedPreferences.Editor sEdit;

    static {
        sUsercfg = UserLoginApp.getContext().getSharedPreferences("usercfg", Context.MODE_PRIVATE);
        sEdit = sUsercfg.edit();
    }

    public static boolean getBoolean(String key, boolean defaultVal) {
        return sUsercfg.getBoolean(key, defaultVal);
    }

    public static String getString(String key, String defaultVal) {
        return sUsercfg.getString(key, defaultVal);
    }

    public static void setBoolean(String key, boolean value) {
        sEdit.putBoolean(key, value);
        sEdit.commit();
    }

    public static void setString(String key, String value) {
        sEdit.putString(key, value);
        sEdit.commit();
    }

}

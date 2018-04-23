package org.chengpx.a1userlogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * 登录之后主界面
 * <p>
 * create at 2018/4/23 10:44 by chengpx
 */
public class SencondActivity extends Activity {

    public static void start(Context context) {
        context.startActivity(new Intent(context, SencondActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sencond);
    }

}

package org.chengpx.a3caraccountrecharge;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

/**
 * create at 2018/4/28 10:58 by chengpx
 */
public class HomeActivity extends SlidingFragmentActivity {

    private FrameLayout car_fl_content;
    private FrameLayout car_fl_leftmenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        initFragment();
    }

    private void initFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.car_fl_slidingmenuleft, new SlidingMenuLeftFragment(), "");
        fragmentTransaction.commit();
    }

    private void initView() {
        setBehindContentView(R.layout.slidingmenu_lef);// 设置左边侧滑菜单
        SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);// 设置全屏触摸划出菜单
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        assert windowManager != null;
        int width = windowManager.getDefaultDisplay().getWidth();
        slidingMenu.setBehindOffset((int) (width * 1.0f * (1.0f * 350 / 500 )));// 菜单之外的布局预留
    }

}

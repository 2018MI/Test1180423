package org.chengpx.a5sense;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {

    private static String sTag = "org.chengpx.a5sense.MainActivity";

    private RadioGroup sense_radiogroup_indicator;
    private TextView sense_tv_desc;
    private ViewPager sense_viewpager_content;

    private String[] mSenseNameArr = {
            "temperature", "humidity", "co2", "LightIntensity", "pm2.5"
    };
    private int[][] mRangeArrArr = {
            new int[]{0, 37}, new int[]{20, 80}, new int[]{350, 7000},
            new int[]{1, 5000}, new int[]{0, 300}
    };
    private MyPagerAdapter mMyPagerAdapter;
    private List<SenseFragment> mSenseFragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initListener();
        main();
    }

    private void initListener() {
        sense_viewpager_content.setOnPageChangeListener(this);
    }

    private void main() {
        mMyPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        sense_viewpager_content.setAdapter(mMyPagerAdapter);
        sense_radiogroup_indicator.check(sense_radiogroup_indicator.getChildAt(0).getId());
        sense_viewpager_content.setCurrentItem(0);
        sense_tv_desc.setText(mSenseNameArr[0] + " 指数");
    }

    private void initData() {
        // init sense_radiogroup_indicator
        mSenseFragmentList = new ArrayList<>();
        for (int index = 0; index < mSenseNameArr.length; index++) {
            SenseFragment senseFragment = new SenseFragment();
            Bundle bundle = new Bundle();
            bundle.putString("SenseName", mSenseNameArr[index]);
            bundle.putIntArray("range", mRangeArrArr[index]);
            senseFragment.setArguments(bundle);
            mSenseFragmentList.add(senseFragment);
        }
        // init sense_radiogroup_indicator
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        for (String aMSenseNameArr : mSenseNameArr) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setLayoutParams(layoutParams);
            radioButton.setEnabled(false);// RadioButton 不可点击
            sense_radiogroup_indicator.addView(radioButton);
        }
    }


    private void initView() {
        sense_viewpager_content = (ViewPager) findViewById(R.id.sense_viewpager_content);
        sense_radiogroup_indicator = (RadioGroup) findViewById(R.id.sense_radiogroup_indicator);
        sense_tv_desc = (TextView) findViewById(R.id.sense_tv_desc);
    }

    @Override
    public void onPageSelected(int position) {
        Log.d(sTag, "onPageSelected(int position): " + position);
        sense_radiogroup_indicator.check(sense_radiogroup_indicator.getChildAt(position).getId());
        sense_tv_desc.setText(mSenseNameArr[position] + " 指数");
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mSenseFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mSenseFragmentList.size();
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

}

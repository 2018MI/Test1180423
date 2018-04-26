package org.chengpx.a5sense;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.chengpx.a5sense.util.MyViewUtils;
import org.chengpx.mylib.AppException;
import org.chengpx.mylib.DataUtils;
import org.chengpx.mylib.HttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * create at 2018/4/25 12:44 by chengpx
 */
public class SenseFragment extends Fragment {

    private static String sTag = "org.chengpx.a5sense.SenseFragment";
    private static FragmentActivity sFragmentActivity;

    private LineChart sense_linechart_data;
    private String mSenseName;
    private Timer mTimer;
    private int[] mRangeArr;

    /**
     * 第一次创建
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sFragmentActivity = getActivity();
        Bundle bundle = getArguments();
        assert bundle != null;
        mRangeArr = bundle.getIntArray("range");
        mSenseName = bundle.getString("SenseName");
        mTimer = new Timer();
        View view = MyViewUtils.inflate(R.layout.fragment_sense);
        initView(view);
        initData();
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTimer.cancel();
    }

    private void initData() {
        float f1 = (mRangeArr[0] + mRangeArr[1]) * 1.0f * 0.6f;
        Map<String, String> values = new HashMap<>();
        values.put("SenseName", mSenseName);
        HttpUtils.getInstance().sendPost("http://192.168.2.19:9090/transportservice/type/jason/action/GetSenseByName.do",
                values, new InitCallBack(Map.class, mSenseName, values,
                        sense_linechart_data, f1, mTimer));
    }

    private void initView(View view) {
        // init sense_linechart_data
        sense_linechart_data = (LineChart) view.findViewById(R.id.sense_linechart_dataSub);

        sense_linechart_data.getDescription().setEnabled(false);
        sense_linechart_data.animateXY(200, 500);

        Legend legend = sense_linechart_data.getLegend();
        legend.setForm(Legend.LegendForm.LINE);

        XAxis xAxis = sense_linechart_data.getXAxis();
        xAxis.setLabelCount(10);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);// 不绘制轴的网格线
        xAxis.setGranularity(1);// 设置粒度
        // xAxis.setValueFormatter(new MyValueFormatter(10, 1));

        YAxis axisLeft = sense_linechart_data.getAxisLeft();
        axisLeft.setAxisMinimum(mRangeArr[0]);
        axisLeft.setAxisMaximum(mRangeArr[1]);
        axisLeft.setDrawGridLines(false);// 不绘制网格线
        sense_linechart_data.getAxisRight().setEnabled(false);

    }

    private static class TimerCallBack extends HttpUtils.Callback<Map> {

        private final LineChart sense_linechart_data_sub;
        private final String mSenseNameSub;
        private final float mF1;
        private int mRemoveEntryCount;

        public TimerCallBack(Class<Map> mapClass, String senseName, LineChart sense_linechart_data, float f1) {
            super(mapClass);
            mSenseNameSub = senseName;
            sense_linechart_data_sub = sense_linechart_data;
            mF1 = f1;
        }

        @Override
        protected void onSuccess(Map map) {
            Log.d(sTag, map.toString());
            try {
                LineData lineData = sense_linechart_data_sub.getData();
                if (lineData == null || lineData.getDataSetCount() == 0) {
                    return;
                }
                float senseValue = DataUtils.obj2float(map.get(mSenseNameSub));
                Entry entry = new Entry(lineData.getDataSetByIndex(0).getEntryCount() + mRemoveEntryCount,
                        senseValue);
                if (senseValue <= mF1) {
                    entry.setIcon(sFragmentActivity.getResources().getDrawable(R.drawable.shape_green_point));
                } else {
                    entry.setIcon(sFragmentActivity.getResources().getDrawable(R.drawable.shape_red_point));
                    notifyMsg(senseValue, mSenseNameSub);
                }
                lineData.addEntry(entry, 0);
                if (lineData.getDataSetByIndex(0).getEntryCount() > 10) {
                    lineData.removeEntry(lineData.getDataSetByIndex(0).getEntryForIndex(0), 0);
                    mRemoveEntryCount++;
                }
                Log.d(sTag, lineData.getDataSetByIndex(0).toString());
                lineData.notifyDataChanged();
                sense_linechart_data_sub.notifyDataSetChanged();
                sense_linechart_data_sub.moveViewTo(lineData.getEntryCount() - 7, 0, YAxis.AxisDependency.LEFT);
                sense_linechart_data_sub.invalidate();
            } catch (AppException e) {
                e.printStackTrace();
            }
        }

    }

    private static class MyTimeTask extends TimerTask {

        private final Map<String, String> mValues;
        private final TimerCallBack mTimerCallBack;

        public MyTimeTask(Map<String, String> values, TimerCallBack timerCallBack) {
            mValues = values;
            mTimerCallBack = timerCallBack;
        }

        @Override
        public void run() {
            HttpUtils.getInstance().sendPost("http://192.168.2.19:9090/transportservice/type/jason/action/GetSenseByName.do",
                    mValues, mTimerCallBack);
        }

    }

    private static class InitCallBack extends HttpUtils.Callback<Map> {

        private final List<Entry> mInitEntryList;
        private final String mSenseNameSub;
        private final Map<String, String> mValues;
        private final LineChart sense_linechart_data_sub;
        private final float mF1;
        private final Timer mTimerSub;
        private int sReqIndex;

        public InitCallBack(Class<Map> mapClass, String senseName, Map<String, String> values, LineChart sense_linechart_data, float f1, Timer timer) {
            super(mapClass);
            mSenseNameSub = senseName;
            mValues = values;
            sense_linechart_data_sub = sense_linechart_data;
            mF1 = f1;
            mTimerSub = timer;
            mInitEntryList = new ArrayList<>();
        }

        @Override
        protected void onSuccess(Map map) {
            try {
                sReqIndex++;
                float senseValue = DataUtils.obj2float(map.get(mSenseNameSub));
                Entry entry = new Entry(mInitEntryList.size(), senseValue);
                if (senseValue <= mF1) {
                    entry.setIcon(sFragmentActivity.getResources().getDrawable(R.drawable.shape_green_point));
                } else {
                    entry.setIcon(sFragmentActivity.getResources().getDrawable(R.drawable.shape_red_point));
                    notifyMsg(senseValue, mSenseNameSub);
                }
                mInitEntryList.add(entry);
                if (sReqIndex < 1) {
                    HttpUtils.getInstance().sendPost("http://192.168.2.19:9090/transportservice/type/jason/action/GetSenseByName.do",
                            mValues, this);
                } else {
                    LineData lineData = new LineData();
                    sense_linechart_data_sub.setData(lineData);

                    LineDataSet lineDataSet = new LineDataSet(mInitEntryList, "Sense DataSet 1");

                    lineDataSet.setLineWidth(2.5f);
                    // lineDataSet.setCircleRadius(4.5f);
                    // lineDataSet.setDrawCircleHole(false);
                    lineDataSet.setDrawIcons(true);
                    lineDataSet.setColor(Color.GRAY);
                    lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                    lineDataSet.setValueTextSize(10f);

                    lineData.addDataSet(lineDataSet);
                    lineData.notifyDataChanged();
                    sense_linechart_data_sub.notifyDataSetChanged();
                    sense_linechart_data_sub.invalidate();
                    mTimerSub.schedule(new MyTimeTask(mValues,
                            new TimerCallBack(Map.class, mSenseNameSub, sense_linechart_data_sub, mF1)), 0, 1000);
                }
            } catch (AppException e) {
                e.printStackTrace();
            }
        }

    }

    private static void notifyMsg(float senseValue, String senseName) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(sFragmentActivity);
        //设置小图标
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        //设置标题
        mBuilder.setContentTitle("当前 " + senseName + ": " + senseValue);
        //设置是否点击消息后自动clean
        mBuilder.setAutoCancel(true);
        //设置优先级
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        Intent intent = new Intent(sFragmentActivity, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(sFragmentActivity, 0, intent, 0);
        mBuilder.setContentIntent(pIntent);
        NotificationManager mNotificationManager = (NotificationManager) sFragmentActivity
                .getSystemService(Context.NOTIFICATION_SERVICE);
        assert mNotificationManager != null;
        mNotificationManager.notify(0, mBuilder.build());// 使用同一个通知(id)
    }

    //    private class MyValueFormatter implements IAxisValueFormatter {
    //
    //        private final int mScale;
    //        private float mFirstVisibleValue;
    //        private final ArrayList<Float> mValueList;
    //
    //
    //        /**
    //         * @param valueArrLength
    //         * @param scale          刻度
    //         */
    //        public MyValueFormatter(int valueArrLength, int scale) {
    //            mScale = scale;
    //            mFirstVisibleValue = 0;
    //            mValueList = new ArrayList<>();
    //            for (int index = 0; index < valueArrLength; index++) {
    //                mValueList.add(mFirstVisibleValue + scale * index);
    //            }
    //        }
    //
    //        @Override
    //        public String getFormattedValue(float value, AxisBase axis) {
    //            int index = ((int) value % mValueList.size());
    //            if ((int) value >= mValueList.size() - 1 && index == 0) {
    //                mValueList.add(mValueList.get(mValueList.size() - 1) + mScale);
    //            }
    //            Float aFloat = mValueList.get(index);
    //            return aFloat + "";
    //        }
    //
    //    }

}

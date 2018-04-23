package org.chengpx.a2trafficlight;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.chengpx.a2trafficlight.util.DataUtils;
import org.chengpx.a2trafficlight.util.HttpUtils;
import org.chengpx.a2trafficlight.util.MyViewUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Comparator<Map> {

    /**
     * 请求次数索引
     */
    private static int sRequestIndex;
    private static String tag = "org.chengpx.a2trafficlight.MainActivity";
    private static MyAdapter sMyAdapter;
    private static ListView tralight_lv_data;

    private Spinner tralight_spinner_rules;
    private Button tralight_btn_search;
    /**
     * 所有的排序规则字符串
     */
    private String[] mRuleStrArr = {
            "路口升序",
            "路口降序",
            "红灯升序",
            "红灯降序",
            "绿灯升序",
            "绿灯降序",
            "黄灯升序",
            "黄灯降序"
    };
    /**
     * 所有红绿灯编号
     */
    private static String[] sTrafficLightIdArr = {
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7"
    };
    private SortRule[] mSortRuleArr = {
            new SortRule("TrafficLightId", SortRule.ASC),
            new SortRule("TrafficLightId", SortRule.DESC),
            new SortRule("RedTime", SortRule.ASC),
            new SortRule("RedTime", SortRule.DESC),
            new SortRule("YellowTime", SortRule.ASC),
            new SortRule("YellowTime", SortRule.DESC),
            new SortRule("GreenTime", SortRule.ASC),
            new SortRule("GreenTime", SortRule.DESC),
    };
    /**
     * activity 是否是 onCreate?
     */
    private static boolean sIsFirstSort = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        tralight_spinner_rules.setAdapter(new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, mRuleStrArr));
        Map<String, String> values = new HashMap<>();
        Class<Map> mapClass = Map.class;
        sRequestIndex = 0;
        values.put("TrafficLightId", sTrafficLightIdArr[sRequestIndex]);
        HttpUtils.getInstance().sendPost("http://192.168.2.19:8080/transportservice/type/jason/action/GetTrafficLightConfigAction.do",
                values, new MyCalBack(Map.class));
    }

    private void initView() {
        tralight_spinner_rules = findViewById(R.id.tralight_spinner_rules);
        tralight_btn_search = findViewById(R.id.tralight_btn_search);
        tralight_lv_data = findViewById(R.id.tralight_lv_data);
        tralight_btn_search.setOnClickListener(this);
        tralight_lv_data.addHeaderView(MyViewUtils.inflate(R.layout.lv_tralight_lv_data_header));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tralight_btn_search:
                sortSearch();
                break;
        }
    }

    /**
     * 排序查询
     */
    private void sortSearch() {
        Collections.sort(sMyAdapter.getTrafficLightInfoList(), this);
        sMyAdapter.notifyDataSetChanged();
    }

    @Override
    public int compare(Map o1, Map o2) {
        int selectedItemPosition;
        if (sIsFirstSort) {
            selectedItemPosition = 0;
        } else {
            selectedItemPosition = tralight_spinner_rules.getSelectedItemPosition();
        }
        SortRule sortRule = mSortRuleArr[selectedItemPosition];
        String sortRuleKey = sortRule.getKey();
        String sortRuleArray = sortRule.getArray();
        Object val1 = o1.get(sortRuleKey);
        Object val2 = o2.get(sortRuleKey);
        if (val1 instanceof Double) {
            Double d1 = (Double) val1;
            Double d2 = (Double) val2;
            if (SortRule.ASC.equals(sortRuleArray)) {
                return (int) (d1 - d2);
            } else if (SortRule.DESC.equals(sortRuleArray)) {
                return (int) (d2 - d1);
            }
        } else if (val1 instanceof String) {
            String str1 = (String) val1;
            String str2 = (String) val2;
            if (SortRule.ASC.equals(sortRuleArray)) {
                return str1.compareTo(str2);
            } else if (SortRule.DESC.equals(sortRuleArray)) {
                return -str1.compareTo(str2);
            }
        }
        return 0;
    }


    private static class MyCalBack extends HttpUtils.Callback<Map> {

        private String tag = "org.chengpx.a2trafficlight.MainActivity.MyCalBack";
        private List<Map> mTrafficLightInfoList;

        public MyCalBack(Class<Map> mapClass) {
            super(mapClass);
            mTrafficLightInfoList = new ArrayList<>();
        }

        @Override
        protected void onSuccess(Map map) {
            Log.d(tag, map.toString());
            map.put("TrafficLightId", sTrafficLightIdArr[sRequestIndex]);
            mTrafficLightInfoList.add(map);
            sRequestIndex++;
            if (sRequestIndex < sTrafficLightIdArr.length) {
                Map<String, String> values = new HashMap<>();
                Class<Map> mapClass = Map.class;
                values.put("TrafficLightId", sTrafficLightIdArr[sRequestIndex]);
                HttpUtils.getInstance().sendPost("http://192.168.2.19:8080/transportservice/type/jason/action/GetTrafficLightConfigAction.do",
                        values, this);
            } else {
                initLvData(mTrafficLightInfoList);
            }
        }

    }

    private static void initLvData(List<Map> trafficLightInfoList) {
        Log.d(tag, trafficLightInfoList.toString());
        Log.d(tag, Thread.currentThread().getName());
        sIsFirstSort = true;
        Collections.sort(trafficLightInfoList, new MainActivity());
        sIsFirstSort = false;
        sMyAdapter = new MyAdapter(trafficLightInfoList);
        tralight_lv_data.setAdapter(sMyAdapter);
    }

    private static class MyAdapter extends BaseAdapter {

        private final List<Map> mTrafficLightInfoList;

        public MyAdapter(List<Map> trafficLightInfoList) {
            mTrafficLightInfoList = trafficLightInfoList;
        }

        @Override
        public int getCount() {
            return mTrafficLightInfoList.size();
        }

        @Override
        public Map getItem(int position) {
            return mTrafficLightInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = MyViewUtils.inflate(R.layout.lv_tralight_lv_data_content);
                viewHolder = ViewHolder.get(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Map map = getItem(position);
            viewHolder.getTralight_tv_roadid().setText(DataUtils.obj2str(map.get("TrafficLightId")));
            viewHolder.getTralight_tv_redtime().setText(DataUtils.obj2str(map.get("RedTime")));
            viewHolder.getTralight_tv_yellowtime().setText(DataUtils.obj2str(map.get("YellowTime")));
            viewHolder.getTralight_tv_greentime().setText(DataUtils.obj2str(map.get("GreenTime")));
            return convertView;
        }

        public List<Map> getTrafficLightInfoList() {
            return mTrafficLightInfoList;
        }

    }

    private static class ViewHolder {

        private TextView tralight_tv_roadid;
        private TextView tralight_tv_redtime;
        private TextView tralight_tv_yellowtime;
        private TextView tralight_tv_greentime;

        private ViewHolder(View view) {
            tralight_tv_roadid = view.findViewById(R.id.tralight_tv_roadid);
            tralight_tv_redtime = view.findViewById(R.id.tralight_tv_redtime);
            tralight_tv_yellowtime = view.findViewById(R.id.tralight_tv_yellowtime);
            tralight_tv_greentime = view.findViewById(R.id.tralight_tv_greentime);
        }

        public static ViewHolder get(View view) {
            Object tag = view.getTag();
            if (tag == null) {
                tag = new ViewHolder(view);
                view.setTag(tag);
            }
            return (ViewHolder) tag;
        }

        public TextView getTralight_tv_roadid() {
            return tralight_tv_roadid;
        }

        public TextView getTralight_tv_redtime() {
            return tralight_tv_redtime;
        }

        public TextView getTralight_tv_yellowtime() {
            return tralight_tv_yellowtime;
        }

        public TextView getTralight_tv_greentime() {
            return tralight_tv_greentime;
        }

    }

    private class SortRule {

        public static final String ASC = "asc";
        public static final String DESC = "desc";

        private final String mKey;
        private final String mArray;

        public SortRule(String key, String array) {
            mKey = key;
            mArray = array;
        }

        public String getKey() {
            return mKey;
        }

        public String getArray() {
            return mArray;
        }

    }

}

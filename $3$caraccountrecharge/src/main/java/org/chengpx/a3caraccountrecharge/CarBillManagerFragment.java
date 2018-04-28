package org.chengpx.a3caraccountrecharge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.chengpx.a3caraccountrecharge.dao.CarDao;
import org.chengpx.a3caraccountrecharge.domain.CarBean;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * create at 2018/4/28 13:04 by chengpx
 */
public class CarBillManagerFragment extends Fragment implements View.OnClickListener, Comparator<CarBean> {

    private Spinner carbalancerecharge_spinner_rule;
    private Button carbalancerecharge_btn_search;
    private ListView carbalancerecharge_lv_data;

    private FragmentActivity mFragmentActivity;
    private TextView carbalancerecharge_tv_defaluttip;
    private List<CarBean> mCarBeanList;
    private String[] mRulsStrArr = {
            "时间升序", "时间降序"
    };
    private SortRule[] mSortRuleArr = {
            new SortRule("rechargeDate", SortRule.ASC),
            new SortRule("rechargeDate", SortRule.DESC)
    };
    private MyAdapter mMyAdapter;
    private ArrayAdapter<String> mSpinnerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carbillmanagerment, container, false);
        mFragmentActivity = getActivity();
        initView(view);
        initData();
        main();
        initListener();
        return view;
    }

    private void main() {
        mSpinnerAdapter = new ArrayAdapter<>(
                mFragmentActivity, android.R.layout.simple_list_item_1, mRulsStrArr
        );
        carbalancerecharge_spinner_rule.setAdapter(mSpinnerAdapter);
        mSpinnerAdapter.notifyDataSetChanged();
        mMyAdapter = new MyAdapter();
        carbalancerecharge_lv_data.setAdapter(mMyAdapter);
        mMyAdapter.notifyDataSetChanged();
        showDataUI();
    }

    private void initData() {
        carbalancerecharge_spinner_rule.setSelection(1);
        mCarBeanList = CarDao.getInstance(mFragmentActivity).select();
        Collections.sort(mCarBeanList, this);
        showDataUI();
    }

    private void showDataUI() {
        if (mCarBeanList == null || mCarBeanList.size() == 0) {
            carbalancerecharge_tv_defaluttip.setVisibility(View.VISIBLE);
            carbalancerecharge_lv_data.setVisibility(View.GONE);
        } else {
            carbalancerecharge_tv_defaluttip.setVisibility(View.GONE);
            carbalancerecharge_lv_data.setVisibility(View.VISIBLE);
        }
    }

    private void initListener() {
        carbalancerecharge_btn_search.setOnClickListener(this);
    }

    private void initView(View view) {
        carbalancerecharge_spinner_rule = (Spinner) view.findViewById(R.id.carbalancerecharge_spinner_rule);
        carbalancerecharge_btn_search = (Button) view.findViewById(R.id.carbalancerecharge_btn_search);
        carbalancerecharge_lv_data = (ListView) view.findViewById(R.id.carbalancerecharge_lv_data);
        carbalancerecharge_tv_defaluttip = (TextView) view.findViewById(R.id.carbalancerecharge_tv_defaluttip);
        carbalancerecharge_tv_defaluttip.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.carbalancerecharge_btn_search:
                Collections.sort(mCarBeanList, this);
                mMyAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public int compare(CarBean o1, CarBean o2) {
        int selectedItemPosition = carbalancerecharge_spinner_rule.getSelectedItemPosition();
        if (selectedItemPosition < 0) {
            selectedItemPosition = 0;
        }
        SortRule sortRule = mSortRuleArr[selectedItemPosition];
        try {
            Class<? extends CarBean> carBeanClass = o1.getClass();
            Field field = carBeanClass.getDeclaredField(sortRule.getKey());
            field.setAccessible(true);
            Object keyVal1 = field.get(o1);
            Object keyVal2 = field.get(o2);
            if (!(keyVal1 instanceof Comparable) || !(keyVal2 instanceof Comparable)) {
                return 0;
            }
            Comparable comparable1 = (Comparable) keyVal1;
            Comparable comparable2 = (Comparable) keyVal2;
            if (SortRule.ASC.equals(sortRule.getOrder())) {
                return comparable1.compareTo(comparable2);
            } else if (SortRule.DESC.equals(sortRule.getOrder())) {
                return comparable2.compareTo(comparable1);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private class MyAdapter extends BaseAdapter {

        private final DateFormat mSimpleDateFormat;

        public MyAdapter() {
            mSimpleDateFormat = new SimpleDateFormat("yyyy 年 MM 月 dd 日 HH:mm:ss");
        }

        @Override
        public int getCount() {
            return mCarBeanList.size();
        }

        @Override
        public CarBean getItem(int position) {
            return mCarBeanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mFragmentActivity).inflate(R.layout.lv_carbalancerecharge_lv_data,
                        carbalancerecharge_lv_data, false);
                viewHolder = ViewHolder.get(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            CarBean carBean = getItem(position);
            viewHolder.getCarbalancerecharge_tv_num().setText(position + "");
            viewHolder.getCarbalancerecharge_tv_CarId().setText(carBean.getCarId() + "");
            viewHolder.getCarbalancerecharge_tv_Money().setText(carBean.getMoney() + "");
            viewHolder.getCarbalancerecharge_tv_operator().setText("admin");
            viewHolder.getCarbalancerecharge_tv_rechargeDate().setText(mSimpleDateFormat.format(carBean.getRechargeDate()));
            return convertView;
        }

    }

    private class SortRule {

        public static final String ASC = "asc";
        public static final String DESC = "desc";

        private final String mKey;
        private final String mOrder;

        public SortRule(String key, String order) {
            mKey = key;
            mOrder = order;
        }

        public String getKey() {
            return mKey;
        }

        public String getOrder() {
            return mOrder;
        }

    }

    private static class ViewHolder {

        private final TextView carbalancerecharge_tv_num;
        private final TextView carbalancerecharge_tv_CarId;
        private final TextView carbalancerecharge_tv_Money;
        private final TextView carbalancerecharge_tv_operator;
        private final TextView carbalancerecharge_tv_rechargeDate;

        private ViewHolder(View view) {
            carbalancerecharge_tv_num = (TextView) view.findViewById(R.id.carbalancerecharge_tv_num);
            carbalancerecharge_tv_CarId = (TextView) view.findViewById(R.id.carbalancerecharge_tv_CarId);
            carbalancerecharge_tv_Money = (TextView) view.findViewById(R.id.carbalancerecharge_tv_Money);
            carbalancerecharge_tv_operator = (TextView) view.findViewById(R.id.carbalancerecharge_tv_operator);
            carbalancerecharge_tv_rechargeDate = (TextView) view.findViewById(R.id.carbalancerecharge_tv_rechargeDate);
        }

        public static ViewHolder get(View view) {
            Object tag = view.getTag();
            if (tag == null) {
                tag = new ViewHolder(view);
                view.setTag(tag);
            }
            return (ViewHolder) tag;
        }

        public TextView getCarbalancerecharge_tv_num() {
            return carbalancerecharge_tv_num;
        }

        public TextView getCarbalancerecharge_tv_CarId() {
            return carbalancerecharge_tv_CarId;
        }

        public TextView getCarbalancerecharge_tv_Money() {
            return carbalancerecharge_tv_Money;
        }

        public TextView getCarbalancerecharge_tv_operator() {
            return carbalancerecharge_tv_operator;
        }

        public TextView getCarbalancerecharge_tv_rechargeDate() {
            return carbalancerecharge_tv_rechargeDate;
        }

    }

}

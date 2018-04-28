package org.chengpx.a3caraccountrecharge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * create at 2018/4/28 12:45 by chengpx
 */
public class SlidingMenuLeftFragment extends Fragment implements AdapterView.OnItemClickListener {

    private String[] mItemArr = {
            "小车充值",
            "账单管理"
    };
    private Fragment[] mFragmentArr = {
            new CarBalanceRechargeFragment(),
            new CarBillManagerFragment()
    };
    private ListView car_lv_slidingmenleft;
    private FragmentActivity mFragmentActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentActivity = getActivity();
        View view = inflater.inflate(R.layout.fragment_slidingmenuleft, container, false);
        initView(view);
        initData();
        main();
        initListener();
        return view;
    }

    private void initListener() {
        car_lv_slidingmenleft.setOnItemClickListener(this);
    }

    private void main() {
        car_lv_slidingmenleft.setAdapter(new ArrayAdapter<String>(mFragmentActivity,
                android.R.layout.simple_list_item_1, mItemArr));
    }

    private void initData() {

    }

    private void initView(View view) {
        car_lv_slidingmenleft = (ListView) view.findViewById(R.id.car_lv_slidingmenleft);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        assert getFragmentManager() != null;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.car_fl_slidingcontent, mFragmentArr[position], "");
        fragmentTransaction.commit();
    }

}

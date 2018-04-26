package org.chengpx.a3caraccountrecharge;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.chengpx.a3caraccountrecharge.domain.CarBean;
import org.chengpx.a3caraccountrecharge.domain.ServerInfoBean;
import org.chengpx.a3caraccountrecharge.util.MyViewUtils;
import org.chengpx.mylib.HttpUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static int sReqIndex;
    private static String sTag = "org.chengpx.a3caraccountrecharge.MainActivity";

    private static ListView car_lv_data;

    /**
     * 所有小车 id 数组
     */
    private static Integer[] sCarIdArr = {
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15
    };
    private static MyAdapter sMyAdapter;
    private static MainActivity sActivity;
    private static GetCarAccountBalanceCallBack sGetCarAccountBalanceCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sActivity = this;
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        sReqIndex = 0;
        CarBean carBean = new CarBean();
        carBean.setCarId(sCarIdArr[sReqIndex]);
        sGetCarAccountBalanceCallBack = new GetCarAccountBalanceCallBack(CarBean.class);
        HttpUtils.getInstance().sendPost("http://192.168.2.19:9090/transportservice/type/jason/action/GetCarAccountBalance.do",
                carBean, sGetCarAccountBalanceCallBack);
    }

    private void initView() {
        car_lv_data = (ListView) findViewById(R.id.car_lv_data);
    }

    private static class GetCarAccountBalanceCallBack extends HttpUtils.Callback<CarBean> {

        private List<CarBean> mCarBeanList;

        public GetCarAccountBalanceCallBack(Class<CarBean> carBeanClass) {
            super(carBeanClass);
            mCarBeanList = new ArrayList<>();
        }

        @Override
        protected void onSuccess(CarBean carBean) {
            Log.d(sTag, carBean.toString());
            carBean.setCarId(sCarIdArr[sReqIndex]);
            mCarBeanList.add(carBean);
            sReqIndex++;
            if (sReqIndex < sCarIdArr.length) {
                CarBean values = new CarBean();
                values.setCarId(sCarIdArr[sReqIndex]);
                HttpUtils.getInstance().sendPost("http://192.168.2.19:9090/transportservice/type/jason/action/GetCarAccountBalance.do",
                        values, this);
            } else {
                initLvData(mCarBeanList);
            }
        }

        private void initLvData(List<CarBean> carBeanList) {
            Log.d(sTag, carBeanList.toString());
            sMyAdapter = new MyAdapter(carBeanList);
            car_lv_data.setAdapter(sMyAdapter);
        }

    }

    private static class MyAdapter extends BaseAdapter implements View.OnClickListener {

        private final List<CarBean> mCarBeanList;
        private AlertDialog mAlertDialog;
        private EditText car_et_money;
        private Integer mRechargeCarId;

        public MyAdapter(List<CarBean> carBeanList) {
            mCarBeanList = carBeanList;
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
                convertView = MyViewUtils.inflate(R.layout.lv_car_lv_data);
                viewHolder = ViewHolder.get(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            CarBean item = getItem(position);
            viewHolder.getCar_tv_cardesc().setText(item.getCarId() + " 号小车");
            viewHolder.getCar_tv_balance().setText(item.getBalance() + "");
            Button car_btn_recharge = viewHolder.getCar_btn_recharge();
            car_btn_recharge.setTag(item.getCarId());
            car_btn_recharge.setOnClickListener(this);
            return convertView;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.car_btn_recharge:
                    mRechargeCarId = (Integer) v.getTag();
                    showRechargeDialog();
                    break;
                case R.id.car_btn_yes:
                    recharge();
                    break;
                case R.id.car_btn_no:
                    mAlertDialog.dismiss();
                    break;
            }
        }

        private void recharge() {
            String money = car_et_money.getText().toString();
            if (!checkMoney(money)) {
                Toast.makeText(sActivity, "金额非法", Toast.LENGTH_SHORT).show();
                return;
            }
            CarBean carBean = new CarBean();
            carBean.setCarId(mRechargeCarId);
            carBean.setMoney(Integer.parseInt(money));
            HttpUtils.getInstance().sendPost("http://192.168.2.19:9090/transportservice/type/jason/action/SetCarAccountRecharge.do",
                    carBean, new GetCarAccountBalanceCallBack(ServerInfoBean.class));
            mAlertDialog.dismiss();
        }

        private boolean checkMoney(String money) {
            return money.matches("^[1-9]\\d{0,3}$") && Integer.parseInt(money) <= 1000;
        }

        private void showRechargeDialog() {
            mAlertDialog = new AlertDialog.Builder(sActivity).create();
            View view = MyViewUtils.inflate(R.layout.dialog_recharge);
            mAlertDialog.setView(view);
            car_et_money = view.findViewById(R.id.car_et_money);
            view.findViewById(R.id.car_btn_yes).setOnClickListener(this);
            view.findViewById(R.id.car_btn_no).setOnClickListener(this);
            mAlertDialog.show();
        }

        private static class GetCarAccountBalanceCallBack extends HttpUtils.Callback<ServerInfoBean> {

            public GetCarAccountBalanceCallBack(Class<ServerInfoBean> resultBeanClass) {
                super(resultBeanClass);
            }

            @Override
            protected void onSuccess(ServerInfoBean serverInfoBean) {
                Log.d(sTag, serverInfoBean.toString());
                Toast.makeText(sActivity, serverInfoBean.getResult(), Toast.LENGTH_SHORT).show();
                sReqIndex = 0;
                CarBean carBean = new CarBean();
                carBean.setCarId(sCarIdArr[sReqIndex]);
                sGetCarAccountBalanceCallBack = new MainActivity.GetCarAccountBalanceCallBack(CarBean.class);
                HttpUtils.getInstance().sendPost("http://192.168.2.19:9090/transportservice/type/jason/action/GetCarAccountBalance.do",
                        carBean, sGetCarAccountBalanceCallBack);
            }

        }

    }

    private static class ViewHolder {

        private final TextView car_tv_balance;
        private final TextView car_tv_cardesc;
        private final Button car_btn_recharge;

        private ViewHolder(View view) {
            car_tv_balance = view.findViewById(R.id.car_tv_balance);
            car_tv_cardesc = view.findViewById(R.id.car_tv_cardesc);
            car_btn_recharge = view.findViewById(R.id.car_btn_recharge);
        }

        public static ViewHolder get(View view) {
            Object tag = view.getTag();
            if (tag == null) {
                tag = new ViewHolder(view);
                view.setTag(tag);
            }
            return (ViewHolder) tag;
        }

        public TextView getCar_tv_balance() {
            return car_tv_balance;
        }

        public TextView getCar_tv_cardesc() {
            return car_tv_cardesc;
        }

        public Button getCar_btn_recharge() {
            return car_btn_recharge;
        }

    }

}

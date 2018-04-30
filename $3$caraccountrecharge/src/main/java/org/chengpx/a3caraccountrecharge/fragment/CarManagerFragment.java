package org.chengpx.a3caraccountrecharge.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.chengpx.a3caraccountrecharge.R;
import org.chengpx.a3caraccountrecharge.dao.CarDao;
import org.chengpx.a3caraccountrecharge.dao.UserDao;
import org.chengpx.a3caraccountrecharge.domain.CarBean;
import org.chengpx.a3caraccountrecharge.domain.UserBean;
import org.chengpx.mylib.common.BaseFragment;
import org.chengpx.mylib.http.HttpUtils;
import org.chengpx.mylib.http.RequestPool;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 车管局车辆账户管理功能
 * <p>
 * create at 2018/4/30 15:08 by chengpx
 */
public class CarManagerFragment extends BaseFragment implements View.OnClickListener {

    private static String sTag = "org.chengpx.a3caraccountrecharge.fragment.CarManagerFragment";

    private Button carmanager_btn_bulkrechage;
    private Button carmanager_btn_rechargerecording;
    private ListView carmanager_lv_carlist;

    private CarManagerFragment mCarManagerFragment;
    private int mCarIdReqIndex;
    private int[] mCarIdArr = {
            1, 2, 3, 4
    };
    private List<CarBean> mCarBeanList;
    private MyAdapter mMyAdapter;
    private EditText carmanager_dialoget_money;
    private TextView carmanager_dialogtv_carId;
    private AlertDialog mAlertDialog;

    @Override
    protected void initListener() {
        carmanager_btn_bulkrechage.setOnClickListener(this);
        carmanager_btn_rechargerecording.setOnClickListener(this);
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mCarManagerFragment = this;
        View view = inflater.inflate(R.layout.fragment_carmanager, container, false);
        carmanager_btn_bulkrechage = (Button) view.findViewById(R.id.carmanager_btn_bulkrechage);
        carmanager_btn_rechargerecording = (Button) view.findViewById(R.id.carmanager_btn_rechargerecording);
        carmanager_lv_carlist = (ListView) view.findViewById(R.id.carmanager_lv_carlist);
        return view;
    }

    @Override
    protected void onDie() {
    }

    @Override
    protected void main() {
        mMyAdapter = new MyAdapter();
        carmanager_lv_carlist.setAdapter(mMyAdapter);
    }

    @Override
    protected void initData() {
        mCarBeanList = new ArrayList<CarBean>();

        CarBean carBean = new CarBean();
        carBean.setCarId(mCarIdArr[mCarIdReqIndex]);
        RequestPool.getInstance().add("http://192.168.2.19:9090/transportservice/type/jason/action/GetCarAccountBalance.do",
                carBean, new GetCarAccountBalanceCallBack(CarBean.class, mCarManagerFragment));
    }

    @Override
    protected void onDims() {
        mCarBeanList = null;
        mMyAdapter = null;
        mCarIdReqIndex = 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.carmanager_btn_bulkrechage:
                bulkRecharge();
                break;
            case R.id.carmanager_btn_rechargerecording:
                assert getFragmentManager() != null;
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.car_fl_slidingcontent, new CarBillManagerFragment(), "");
                fragmentTransaction.commit();
                break;
            case R.id.carmanager_btn_recharge:// 充值
                mAlertDialog = getDialog();
                carmanager_dialogtv_carId.setText((int) v.getTag() + "");
                break;
            case R.id.carmanager_dialogbtn_recharge:// 确定充值
                recharge();
                break;
            case R.id.carmanager_dialogbtn_cancel:
                mAlertDialog.dismiss();
                mAlertDialog = null;
                break;
        }
    }

    /**
     * 批量充值
     */
    private void bulkRecharge() {
        List<CarBean> bulkRechargeCarBeanList = new ArrayList<>();
        StringBuilder carIdStrBuilder = new StringBuilder();
        for (int index = 0; index < carmanager_lv_carlist.getChildCount(); index++) {
            View childView = carmanager_lv_carlist.getChildAt(index);
            ViewHolder viewHolder = (ViewHolder) childView.getTag();
            if (!viewHolder.getCarmanager_checkbox_isbulkrecharge().isChecked()) {
                continue;
            }
            String strCarId = viewHolder.getCarmanager_tv_carId().getText().toString();
            if (TextUtils.isEmpty(strCarId)) {
                continue;
            }
            carIdStrBuilder.append(strCarId).append(",");
            CarBean carBean = new CarBean();
            carBean.setCarId(Integer.parseInt(strCarId));
            bulkRechargeCarBeanList.add(carBean);
        }
        if (bulkRechargeCarBeanList.isEmpty()) {
            Toast.makeText(mFragmentActivity, "请选择至少一个", Toast.LENGTH_SHORT).show();
            return;
        }
        carIdStrBuilder.delete(carIdStrBuilder.length() - 1, carIdStrBuilder.length());
        mAlertDialog = getDialog();
        carmanager_dialogtv_carId.setText(carIdStrBuilder.toString());
    }

    private void recharge() {
        String strMoney = carmanager_dialoget_money.getText().toString();
        if (TextUtils.isEmpty(strMoney)) {
            Toast.makeText(mFragmentActivity, "金额不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!strMoney.matches("^[1-9]\\d{0,2}$")) {
            Toast.makeText(mFragmentActivity, "金额非法", Toast.LENGTH_SHORT).show();
            return;
        }
        int money = Integer.parseInt(strMoney);
        if (money > 999) {
            Toast.makeText(mFragmentActivity, "请输入 1-999 范围内数字", Toast.LENGTH_SHORT).show();
            return;
        }
        String strCarId = carmanager_dialogtv_carId.getText().toString();
        String[] strCarIdArr = strCarId.split(",");
        CarBean reqCarBean = new CarBean();
        reqCarBean.setCarId(Integer.parseInt(strCarIdArr[0]));
        reqCarBean.setMoney(money);
        RequestPool.getInstance().add("http://192.168.2.19:9090/transportservice/type/jason/action/SetCarAccountRecharge.do",
                reqCarBean, new SetCarAccountRechargeCallBack(Map.class, mCarManagerFragment, reqCarBean, strCarIdArr));
        mAlertDialog.dismiss();
        mAlertDialog = null;
    }

    private AlertDialog getDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mFragmentActivity);
        View dialogView = LayoutInflater.from(mFragmentActivity).inflate(R.layout.dialog_carmanagerrecharge, null);
        dialogBuilder.setView(dialogView);
        mAlertDialog = dialogBuilder.create();
        mAlertDialog.show();
        carmanager_dialogtv_carId = (TextView) dialogView.findViewById(R.id.carmanager_dialogtv_carId);
        carmanager_dialoget_money = (EditText) dialogView.findViewById(R.id.carmanager_dialoget_money);
        Button carmanager_dialogbtn_recharge = (Button) dialogView.findViewById(R.id.carmanager_dialogbtn_recharge);
        Button carmanager_dialogbtn_cancel = (Button) dialogView.findViewById(R.id.carmanager_dialogbtn_cancel);
        carmanager_dialogbtn_recharge.setOnClickListener(this);
        carmanager_dialogbtn_cancel.setOnClickListener(this);
        return mAlertDialog;
    }

    private static class SetCarAccountRechargeCallBack extends HttpUtils.Callback<Map> {

        private final CarManagerFragment mCarManagerFragment_inner;
        private final CarBean mRechargeCarBean;
        private final String[] mStrCarIdArr;
        private int mReqCarIdIndex;

        /**
         * @param mapClass           结果数据封装体类型字节码
         * @param carManagerFragment
         * @param rechargeCarBean
         * @param strCarIdArr
         */
        SetCarAccountRechargeCallBack(Class<Map> mapClass, CarManagerFragment carManagerFragment, CarBean rechargeCarBean, String[] strCarIdArr) {
            super(mapClass);
            mCarManagerFragment_inner = carManagerFragment;
            mRechargeCarBean = rechargeCarBean;
            mStrCarIdArr = strCarIdArr;
            mReqCarIdIndex = 0;
        }

        @Override
        protected void onSuccess(Map map) {
            UserBean selectUserBean = UserDao.getInstance(mCarManagerFragment_inner.getActivity())
                    .select("uname", "admin");
            if (selectUserBean != null) {
                mRechargeCarBean.setUser(selectUserBean);
            }
            mRechargeCarBean.setRechargeDate(new Date());
            mRechargeCarBean.setCarId(Integer.parseInt(mStrCarIdArr[mReqCarIdIndex]));
            int insert = CarDao.getInstance(mCarManagerFragment_inner.getActivity()).insert(mRechargeCarBean);
            Log.d(sTag, "CarDao insert: " + insert);
            mReqCarIdIndex++;
            if (mReqCarIdIndex < mStrCarIdArr.length) {
                mRechargeCarBean.setCarId(Integer.parseInt(mStrCarIdArr[mReqCarIdIndex]));
                RequestPool.getInstance().add("http://192.168.2.19:9090/transportservice/type/jason/action/SetCarAccountRecharge.do",
                        mRechargeCarBean, this);

            } else {
                Object result = map.get("result");
                Toast.makeText(mCarManagerFragment_inner.getActivity(), (String) result, Toast.LENGTH_SHORT).show();
                mCarManagerFragment_inner.onDims();
                mCarManagerFragment_inner.initData();
                mCarManagerFragment_inner.main();
            }
        }

    }

    private class MyAdapter extends BaseAdapter {

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
                convertView = LayoutInflater.from(mFragmentActivity).inflate(R.layout.lv_carmanager_lv_carlist,
                        carmanager_lv_carlist, false);
                viewHolder = ViewHolder.get(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            CarBean carBean = getItem(position);
            viewHolder.getCarmanager_tv_carId().setText(carBean.getCarId() + "");
            viewHolder.getCarmanager_tv_balance().setText("余额: " + carBean.getBalance() + " 元");
            Button carmanager_btn_recharge = viewHolder.getCarmanager_btn_recharge();
            carmanager_btn_recharge.setTag(carBean.getCarId());
            carmanager_btn_recharge.setOnClickListener(CarManagerFragment.this);
            return convertView;
        }
    }

    private static class ViewHolder {

        private final TextView carmanager_tv_carId;
        private final TextView carmanager_tv_balance;
        private final CheckBox carmanager_checkbox_isbulkrecharge;
        private final Button carmanager_btn_recharge;

        private ViewHolder(View view) {
            carmanager_tv_carId = (TextView) view.findViewById(R.id.carmanager_tv_carId);
            carmanager_tv_balance = (TextView) view.findViewById(R.id.carmanager_tv_balance);
            carmanager_checkbox_isbulkrecharge = (CheckBox) view.findViewById(R.id.carmanager_checkbox_isbulkrecharge);
            carmanager_btn_recharge = (Button) view.findViewById(R.id.carmanager_btn_recharge);
        }

        public static ViewHolder get(View view) {
            Object tag = view.getTag();
            if (tag == null) {
                tag = new ViewHolder(view);
                view.setTag(tag);
            }
            return (ViewHolder) tag;
        }

        public TextView getCarmanager_tv_carId() {
            return carmanager_tv_carId;
        }

        public TextView getCarmanager_tv_balance() {
            return carmanager_tv_balance;
        }

        public CheckBox getCarmanager_checkbox_isbulkrecharge() {
            return carmanager_checkbox_isbulkrecharge;
        }

        public Button getCarmanager_btn_recharge() {
            return carmanager_btn_recharge;
        }

    }

    private static class GetCarAccountBalanceCallBack extends HttpUtils.Callback<CarBean> {

        private final CarManagerFragment mCarManagerFragment_inner;

        /**
         * @param carBeanClass       结果数据封装体类型字节码
         * @param carManagerFragment
         */
        GetCarAccountBalanceCallBack(Class<CarBean> carBeanClass, CarManagerFragment carManagerFragment) {
            super(carBeanClass);
            mCarManagerFragment_inner = carManagerFragment;
            mCarManagerFragment_inner.getCarBeanList().clear();
        }

        @Override
        protected void onSuccess(CarBean carBean) {
            carBean.setCarId(mCarManagerFragment_inner.getCarIdArr()[mCarManagerFragment_inner.getCarIdReqIndex()]);
            mCarManagerFragment_inner.getCarBeanList().add(carBean);
            mCarManagerFragment_inner.setCarIdReqIndex(mCarManagerFragment_inner.getCarIdReqIndex() + 1);
            if (mCarManagerFragment_inner.getCarIdReqIndex() < mCarManagerFragment_inner.getCarIdArr().length) {
                CarBean reqCarBean = new CarBean();
                reqCarBean.setCarId(mCarManagerFragment_inner.getCarIdArr()[mCarManagerFragment_inner.getCarIdReqIndex()]);
                RequestPool.getInstance().add("http://192.168.2.19:9090/transportservice/type/jason/action/GetCarAccountBalance.do",
                        reqCarBean, this);
            } else {
                MyAdapter myAdapter = mCarManagerFragment_inner.getMyAdapter();
                if (myAdapter != null) {
                    myAdapter.notifyDataSetChanged();
                }
                Log.d(sTag, mCarManagerFragment_inner.carmanager_lv_carlist.toString());
            }
        }

    }

    public Button getCarmanager_btn_bulkrechage() {
        return carmanager_btn_bulkrechage;
    }

    public Button getCarmanager_btn_rechargerecording() {
        return carmanager_btn_rechargerecording;
    }

    public ListView getCarmanager_lv_carlist() {
        return carmanager_lv_carlist;
    }

    public CarManagerFragment getCarManagerFragment() {
        return mCarManagerFragment;
    }

    public int getCarIdReqIndex() {
        return mCarIdReqIndex;
    }

    public int[] getCarIdArr() {
        return mCarIdArr;
    }

    public List<CarBean> getCarBeanList() {
        return mCarBeanList;
    }

    public void setCarIdReqIndex(int carIdReqIndex) {
        mCarIdReqIndex = carIdReqIndex;
    }

    public MyAdapter getMyAdapter() {
        return mMyAdapter;
    }

}

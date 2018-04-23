package org.chengpx.a1userlogin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.chengpx.a1userlogin.util.SPUtils;

/**
 *
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button login_btn_login;
    private Button login_btn_regist;
    private EditText login_et_uname;
    private EditText login_et_pwd;
    private CheckBox login_cbox_autologin;
    private CheckBox login_cbox_remberpwd;
    private String mUname;
    private String mPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        if (SPUtils.getBoolean("isAutologin", false)) {// 自动登录
            try {
                login();
                SencondActivity.start(this);
            } catch (AppException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initData() {
        mUname = SPUtils.getString("uname", null);
        if (!TextUtils.isEmpty(mUname)) {
            login_et_uname.setText(mUname);
        }
        if (SPUtils.getBoolean("isRemberpwd", false)) {
            mPwd = SPUtils.getString("pwd", null);
            if (!TextUtils.isEmpty(mPwd)) {
                login_et_pwd.setText(mPwd);
            }
        }
        login_cbox_autologin.setChecked(SPUtils.getBoolean("isAutologin", false));
        login_cbox_remberpwd.setChecked(SPUtils.getBoolean("isRemberpwd", false));
    }

    private void login() throws AppException {
        mUname = login_et_uname.getText().toString();
        mPwd = login_et_pwd.getText().toString();
        if (TextUtils.isEmpty(mUname) || TextUtils.isEmpty(mPwd)) {
            throw new AppException("用户名或密码不能为空");
        }
        if (!"admin".equals(mUname) || !"adminpwd".equals(mPwd)) {
            throw new AppException("用户名或密码错误");
        }
        SPUtils.setBoolean("isAutologin", login_cbox_autologin.isChecked());
        boolean isRemberpwd = login_cbox_remberpwd.isChecked();
        SPUtils.setBoolean("isRemberpwd", isRemberpwd);
        SPUtils.setString("uname", mUname);
        if (isRemberpwd) {
            SPUtils.setString("pwd", mPwd);
        }
    }

    private void initView() {
        login_et_uname = findViewById(R.id.login_et_uname);
        login_et_pwd = findViewById(R.id.login_et_pwd);
        login_cbox_autologin = findViewById(R.id.login_cbox_autologin);
        login_cbox_remberpwd = findViewById(R.id.login_cbox_remberpwd);
        login_btn_login = findViewById(R.id.login_btn_login);
        login_btn_regist = findViewById(R.id.login_btn_regist);
        login_btn_login.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn_login:
                try {
                    login();
                    SencondActivity.start(this);
                } catch (AppException e) {
                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}

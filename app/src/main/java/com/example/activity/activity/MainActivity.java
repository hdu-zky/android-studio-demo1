package com.example.activity.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
//import android.support.design.widget.TextInputLayout;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.activity.R;
import com.example.activity.util.httpUtil;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TextInputLayout mLayoutUserName;
    private TextInputLayout mLayoutPassword;
    private CheckBox mSaveAccount;
    private CheckBox mAutoLogin;
    private EditText mEtUserName;
    private EditText mEtPassword;
    private Button loginBtn;
    private String userName;
    private String password;
    SharedPreferences mContextSp;
    SharedPreferences mActivitySp;

    private httpUtil http = new httpUtil();
    private static String httpUrl="http://192.168.0.109:3000/login";
//    private static String headImg;
//    private static String sex;
//    private static String nickName;
//    private static String signature;

    private static final int GET_DATA_SUCCESS = 1;
    private static final int NETWORK_ERROR = 2;
    private static final int SERVER_ERROR = 3;
    private static final int UNKOWN_ERROR = 4;
    private static final int JSONPARSE_ERROR = 5;
    private static final int QUIT_LOGIN = 6;
    private static int parseResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // TODO:使sdk高版本的仍可以在主进程中进行http请求
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        initView();

    }
    private void initView(){
        mContextSp = this.getSharedPreferences( "userInfo", Context.MODE_PRIVATE );
        mActivitySp = this.getPreferences( Context.MODE_PRIVATE );
        mActivitySp.edit().commit();//only create file

        loginBtn = findViewById(R.id.btn_login);
        mEtUserName = findViewById(R.id.login_username);
        mEtPassword = findViewById(R.id.login_pwd);
        mSaveAccount = findViewById(R.id.cb_saveAccount);
        mAutoLogin = findViewById(R.id.cb_autoLogin);

        mLayoutUserName = findViewById(R.id.login_username_layout);
        mLayoutPassword = findViewById(R.id.login_pwd_layout);

        //绑定输入框文本编辑事件，处理输入为空的情况
        mEtUserName.addTextChangedListener(new MyTextWatcher(mEtUserName));
        mEtPassword.addTextChangedListener(new MyTextWatcher(mEtPassword));

        // TODO:判断记住密码多选框的状态
        if(mContextSp.getBoolean("saveAccount", false)) {
            // TODO:为真则设置默认是记录密码状态
            mSaveAccount.setChecked(true);
            mEtUserName.setText(mContextSp.getString("userId",""));
            mEtPassword.setText(mContextSp.getString("password", ""));

            // TODO:判断自动登陆多选框状态
            if(mContextSp.getBoolean("autoLogin", false)) {
                // TODO:设置默认是自动登录状态
                mAutoLogin.setChecked(true);
                showWaitingDialog();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
                        login(true);
//                    }
//                },3000);
            }
        }
        //监听自动登录点击事件
        mAutoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAutoLogin.isChecked()){
                    //如果勾选了自动登录但又没勾选保存账密则无法自动登录
                    if(!mSaveAccount.isChecked()){
                        mSaveAccount.setError("必须勾选保存账密才能自动登录！");
                        mEtUserName.requestFocus();
                    }
                }
            }
        });
        //监听登录按钮点击事件
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = mEtUserName.getText().toString().trim();
                password = mEtPassword.getText().toString().trim();
                login(false);
            }
        });
    }
    /**
     * 登录功能实现，检查数据是否正确
     *
     * @return
     */
    private void login(boolean isAutoLogin){
        //先检查输入账号密码是否非空
        if (!isNameValid()) {
            showMessage(getString(R.string.error_username));
            return;
        }
        if (!isPasswordValid()) {
            showMessage(getString(R.string.error_pwd));
            return;
        }
        int res = loadNextData();
        if(res == GET_DATA_SUCCESS){
            showMessage("登录成功");
            // 如果是自动登录则不重复保存相关信息否则保存
            if(!isAutoLogin){
                SharedPreferences.Editor editor = mContextSp.edit();
                editor.putString( "userId", userName );
                editor.putBoolean("saveAccount", mSaveAccount.isChecked());
                editor.putBoolean("autoLogin", mAutoLogin.isChecked());
                //登录成功和记住密码框为选中状态才保存用户密码
                if(mSaveAccount.isChecked()) {
                    //记住用户名、密码
                    editor.putString( "password", password);
                    editor.commit();
                }
                editor.apply();
            }
            //打开主页面
            Intent index = new Intent(MainActivity.this, IndexActivity.class);
            startActivity(index);
        }else {
            showMessage(getString(R.string.error_login));
        }
    }
    //自动登录界面
    private void showWaitingDialog() {
        final AlertDialog.Builder waitingDialog =
                new AlertDialog.Builder(MainActivity.this);
        waitingDialog.setTitle("请稍候");
        waitingDialog.setMessage("正在自动登陆中...");
        waitingDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO:发送终止自动登录信号
                        //handler.sendEmptyMessage(QUIT_LOGIN);
                    }
                });
        // 显示
        waitingDialog.setCancelable(false);
        waitingDialog.show();
    }
    /**
     * 发起HTTP请求获取数据并展示在recyclerView上
     * */
    private int loadNextData(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("name", userName);
        map.put("pwd", password);
        String response = http.httpPost(httpUrl, map);
        System.out.println(response);
        int parseResult = parseJsonMulti(response);
        return parseResult;
    }
    // TODO:解析多个数据的Json
    private int parseJsonMulti(String strResult) {
        try {
            JSONObject status = new JSONObject(strResult);
            boolean success = status.getBoolean("success");
            System.out.println("success: "+success);
            if(!success){
                return 0;
            }
//            else{
//                JSONObject jsonObj = status.getJSONObject("data");
//                headImg = jsonObj.getString("headImg");
//                nickName = jsonObj.getString("nickName");
//            }
            return GET_DATA_SUCCESS;
        } catch (JSONException e) {
            System.out.println("Json parse error !");
            e.printStackTrace();
            return JSONPARSE_ERROR;
        }
    }
    /**
     * 监听输入框文本编辑结束事件并调用相关函数处理
     *
     * @return
     */
    private class MyTextWatcher implements TextWatcher {
        private View view;
        MyTextWatcher(View view){
            this.view = view;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (view.getId()){
                case R.id.login_username:
                    isNameValid();
                    break;
                case R.id.login_pwd:
                    isPasswordValid();
                    break;
            }
        }
    }
    /**
     * 检查输入的手机号码是否为空以及格式是否正确
     *
     * @return
     */
    public boolean isNameValid() {
        userName = mEtUserName.getText().toString().trim();
        if (TextUtils.isEmpty(userName) || "null".equalsIgnoreCase(userName)) {
//            mEtUserName.setError(getString(R.string.error_empty_username));
            mLayoutUserName.setErrorEnabled(true);
            mLayoutUserName.setError(getString(R.string.error_empty_username));
            mEtUserName.requestFocus();
            return false;
        }
        mLayoutUserName.setErrorEnabled(false);
        return true;
    }

    /**
     * 检查输入的密码是否为空
     *
     * @return
     */
    public boolean isPasswordValid() {
        password = mEtPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password) || "null".equalsIgnoreCase(password)) {
//            mEtPassword.setError(getString(R.string.error_empty_pwd));
            mLayoutPassword.setErrorEnabled(true);
            mLayoutPassword.setError(getResources().getString(R.string.error_empty_pwd));
            mEtPassword.requestFocus();
            return false;
        }
        mLayoutPassword.setErrorEnabled(false);
        return true;
    }
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

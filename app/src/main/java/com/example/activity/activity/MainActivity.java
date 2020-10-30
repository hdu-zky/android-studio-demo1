package com.example.activity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.activity.R;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {
    private TextInputLayout mLayoutUserName;
    private TextInputLayout mLayoutPassword;
    private EditText mEtUserName;
    private EditText mEtPassword;
    private Button loginBtn;
    private String userName;
    private String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        initView();

    }
    private void initView(){
        loginBtn = findViewById(R.id.btn_login);
        mEtUserName = findViewById(R.id.login_username);
        mEtPassword = findViewById(R.id.login_pwd);

        mLayoutUserName = findViewById(R.id.login_username_layout);
        mLayoutPassword = findViewById(R.id.login_pwd_layout);

        //绑定输入框文本编辑事件，处理输入为空的情况
        mEtUserName.addTextChangedListener(new MyTextWatcher(mEtUserName));
        mEtPassword.addTextChangedListener(new MyTextWatcher(mEtPassword));
        //监听登录按钮点击事件
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = mEtUserName.getText().toString().trim();
                password = mEtPassword.getText().toString().trim();
                login();
            }
        });
    }
    /**
     * 登录功能实现，检查数据是否正确
     *
     * @return
     */
    private void login(){
        //先检查输入账号密码是否非空
//        if (!isNameValid()) {
//            showMessage(getString(R.string.error_username));
//            return;
//        }
//        if (!isPasswordValid()) {
//            showMessage(getString(R.string.error_pwd));
//            return;
//        }
//        if(userName.equals("123") && password.equals("1234")){
            //打开主页面
            Intent index = new Intent(MainActivity.this, IndexActivity.class);
            startActivity(index);
//        }else {
//            showMessage(getString(R.string.error_login));
//        }
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

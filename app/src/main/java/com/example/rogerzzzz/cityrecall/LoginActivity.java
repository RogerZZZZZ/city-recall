package com.example.rogerzzzz.cityrecall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.example.rogerzzzz.cityrecall.utils.DialogUtils;
import com.example.rogerzzzz.cityrecall.utils.ToastUtils;
import com.example.rogerzzzz.cityrecall.utils.UserUtils;

/**
 * Created by rogerzzzz on 16/3/20.
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    private Button   loginBtn;
    private TextView registerBtn;
    private EditText username_et;
    private EditText password_et;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        username_et = (EditText) findViewById(R.id.username);
        password_et = (EditText) findViewById(R.id.password);

        loginBtn = (Button) findViewById(R.id.confirm_login);
        registerBtn = (TextView) findViewById(R.id.registerBtn);
        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm_login:
                userlogin();
                break;
            case R.id.registerBtn:
                Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void userlogin() {
        String username = username_et.getText().toString();
        String password = password_et.getText().toString();
        UserUtils.initCloudService(LoginActivity.this);

        AVUser.logInInBackground(username, password, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                if (e == null) {
                    //Todo successful
                    ToastUtils.showToast(LoginActivity.this, "login success", Toast.LENGTH_SHORT);
                    Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                    startActivity(intent);
                } else {
                    //Todo fail
                    DialogUtils.showMsgDialog(LoginActivity.this, "登录失败", "请检查用户名密码是否正确");

                }
            }
        });

    }
}

package com.example.rogerzzzz.cityrecall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by rogerzzzz on 16/3/20.
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    @Bind(R.id.confirm_login)
    Button loginBtn;
    @Bind(R.id.registerBtn)
    TextView registerBtn;
    @Bind(R.id.username)
    EditText username_et;
    @Bind(R.id.password)
    EditText password_et;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

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

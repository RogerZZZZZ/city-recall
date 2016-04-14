package com.example.rogerzzzz.cityrecall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.rogerzzzz.cityrecall.utils.UserUtils;

/**
 * Created by rogerzzzz on 16/3/21.
 */
public class WelcomPageActivity extends Activity {
    private Intent  intent;
    private boolean isLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserUtils.initCloudService(WelcomPageActivity.this);
        isLogin = UserUtils.isUserLogin();
        if (isLogin != false) {
            intent = new Intent(WelcomPageActivity.this, HomePageActivity.class);
        } else {
            intent = new Intent(WelcomPageActivity.this, LoginActivity.class);
        }
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

package com.example.rogerzzzz.cityrecall;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.example.rogerzzzz.cityrecall.utils.L;
import com.example.rogerzzzz.cityrecall.utils.UserUtils;

/**
 * Created by rogerzzzz on 16/3/21.
 */
public class WelcomPageActivity extends Activity {
    private Intent  intent;
    private boolean isLogin;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserUtils.initCloudService(WelcomPageActivity.this);
        isLogin = UserUtils.isUserLogin();
        initSetting();
        if (isLogin != false) {
            intent = new Intent(WelcomPageActivity.this, HomeActivity.class);
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

    private void initSetting(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isInitSetting = sharedPreferences.getBoolean("isInitSetting", false);
        editor = sharedPreferences.edit();
        if(!isInitSetting){
            L.d("inss");
            editor.putBoolean("isInitSetting", true);
            editor.putInt("seachScape", 3000);
            editor.putInt("seachNum", 10);
            editor.commit();
        }
    }
}

package com.example.rogerzzzz.cityrecall.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.example.rogerzzzz.cityrecall.R;
import com.example.rogerzzzz.cityrecall.enity.RegExp;
import com.example.rogerzzzz.cityrecall.utils.DialogUtils;
import com.example.rogerzzzz.cityrecall.utils.UserUtils;
import com.example.rogerzzzz.cityrecall.widget.NoScrollViewPager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rogerzzzz on 16/3/20.
 */
public class RegistTab extends Fragment implements View.OnClickListener {
    private EditText username;
    private EditText password;
    private EditText confirmPassword;
    private EditText emailAddress;
    private Button   confirmBtn;
    private Button   confirmBtnNotReady;
    private View     mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_regist, container, false);
        init();
        return mView;
    }

    private void init() {
        final Pattern pattern = Pattern.compile(RegExp.PASSWORD_VERIFY);

        username = (EditText) mView.findViewById(R.id.username);
        password = (EditText) mView.findViewById(R.id.password);
        confirmPassword = (EditText) mView.findViewById(R.id.password_again);
        emailAddress = (EditText) mView.findViewById(R.id.email);
        confirmBtn = (Button) mView.findViewById(R.id.confirm_regist);
        confirmBtnNotReady = (Button) mView.findViewById(R.id.confirm_regist_notReady);

        confirmBtn.setOnClickListener(this);
        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Matcher matcher = pattern.matcher(password.getText());
                if (!TextUtils.isEmpty(username.getText()) && matcher.find() && password.getText().toString().equals(confirmPassword.getText().toString())) {
                    confirmBtnNotReady.setVisibility(View.GONE);
                    confirmBtn.setVisibility(View.VISIBLE);
                } else {
                    confirmBtnNotReady.setVisibility(View.VISIBLE);
                    confirmBtn.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm_regist:
                sendRegistRequest();
                break;
        }

    }

    private void sendRegistRequest() {
        AVUser user;
        String usernameStr = username.getText().toString();
        String passwordStr = password.getText().toString();
        String emailAddressStr = "";
        if (!TextUtils.isEmpty(emailAddress.getText().toString())) {
            emailAddressStr = emailAddress.getText().toString();
        }
        UserUtils.initCloudService(getActivity());
        user = UserUtils.userRegist(usernameStr, passwordStr, emailAddressStr, getActivity());
        user.signUpInBackground(new SignUpCallback() {
            /*
             * @return e.getCode()
             *   -1 : initialize state
             *   0 : success
             *   202 : username has already been taken
             *   125 : the email address was invaild
             */
            @SuppressLint("ResourceAsColor")
            @Override
            public void done(AVException e) {
                if (e == null) {
                    //successful
                    TextView topBanner = (TextView) getActivity().findViewById(R.id.regist_top_banner_first);
                    TextView topBannerSecond = (TextView) getActivity().findViewById(R.id.regist_top_banner_second);
                    topBanner.setTextColor(Color.parseColor("#898989"));
                    topBannerSecond.setTextColor(Color.parseColor("#ff8200"));
                    NoScrollViewPager noScrollViewPager = (NoScrollViewPager) getActivity().findViewById(R.id.viewpager);
                    noScrollViewPager.setCurrentItem(1);
                } else {
                    //fail
                    Log.i("code-->", e.getCode() + "");
                    if (e.getCode() == 202) {
                        DialogUtils.showMsgDialog(getActivity(), "提示", "用户名已经被注册，请更换用户名在注册一次！");
                    } else if (e.getCode() == 125) {
                        DialogUtils.showMsgDialog(getActivity(), "提示", "邮箱已经被使用，请使用其他邮箱！");
                    }
                }
            }
        });
    }
}

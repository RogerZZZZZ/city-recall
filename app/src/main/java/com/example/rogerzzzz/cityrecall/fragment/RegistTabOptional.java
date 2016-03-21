package com.example.rogerzzzz.cityrecall.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.rogerzzzz.cityrecall.R;
import com.example.rogerzzzz.cityrecall.WelcomPageActivity;

/**
 * Created by rogerzzzz on 16/3/20.
 */
public class RegistTabOptional extends Fragment implements View.OnClickListener{
    private Button successBtn;
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_regist_optional, container, false);
        successBtn = (Button) mView.findViewById(R.id.completeRegist);
        successBtn.setOnClickListener(this);
        return mView;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), WelcomPageActivity.class);
        startActivity(intent);
    }
}

package com.example.rogerzzzz.cityrecall.widget.XhsKeyBoard;

import android.app.Activity;
import android.widget.TextView;

import com.example.rogerzzzz.cityrecall.widget.XhsKeyBoard.adapter.ChattingListAdapter;


public class QqChattingListAdapter extends ChattingListAdapter {

    public QqChattingListAdapter(Activity activity) {
        super(activity);
    }

    public void setContent(TextView tv_content, String content) {
        QqUtils.spannableEmoticonFilter(tv_content, content);
    }
}
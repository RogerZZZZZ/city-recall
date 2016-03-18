package com.example.rogerzzzz.cityrecall.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.example.rogerzzzz.cityrecall.R;

/**
 * Created by rogerzzzz on 16/3/18.
 */
public class DialogUtils {
    public static Dialog createLoadingDialog(Context context){
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.DialogCommon);
        return progressDialog;
    }

    /*
    * 弹出信息提示框
    * @param context
    * @param title 标题名称，内容为空即不设置标题
    * @param msg 提示信息
     */
    public static AlertDialog showMsgDialog(Context context, String title, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if(!TextUtils.isEmpty(title)){
            builder.setTitle(title);
        }
        AlertDialog dialog = builder.setMessage(msg).setNegativeButton("确定", null).show();
        return dialog;
    }

    /*
    * 弹出确认框
    * @param context
    * @param title
    * @param msg
    * @param onClickListener 确定按钮监听
    */
    public static AlertDialog showConfirmDialog(Context context, String title, String msg, DialogInterface.OnClickListener onClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if(TextUtils.isEmpty(title)){
            builder.setTitle(title);
        }
        AlertDialog dialog = builder.setMessage(msg)
                .setNegativeButton("取消", null)
                .setPositiveButton("确认", onClickListener)
                .show();
        return dialog;
    }

    /*
     * 列表型dialog
     *
     * @param context
     * @param title
     *            标题名称,内容为空时即不设置标题
     * @param items
     *            所有item选项的名称
     * @param onClickListener
     *            确定按钮监听
     * @return
     */
    public static AlertDialog showListDialog(Context context, String title, String[] items, DialogInterface.OnClickListener onClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        AlertDialog dialog = builder.setItems(items, onClickListener).show();
        return dialog;
    }

    /*
     * 单选框dialog
     *
     * @param context
     * @param title
     *            标题名称,内容为空时即不设置标题
     * @param items
     *            所有item选项的名称
     * @param defaultItemIndex
     *            默认选项
     * @param onClickListener
     *            确定按钮监听
     * @return
     */
    public static AlertDialog showSingleChoiceDialog(Context context,
                                                     String title, String[] items, int defaultItemIndex,
                                                     DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        AlertDialog dialog = builder
                .setSingleChoiceItems(items, defaultItemIndex, onClickListener)
                .setNegativeButton("取消", null).show();
        return dialog;
    }

    /*
     * 复选框对话框
     *
     * @param context
     * @param title
     *            标题名称,内容为空时即不设置标题
     * @param items
     *            所有item选项的名称
     * @param defaultCheckedItems
     *            初始化选择,和items同长度,true代表对应位置选中,如{true, true,
     *            false}代表第一二项选中,第三项不选中
     * @param onMultiChoiceClickListener
     *            多选监听
     * @param onClickListener
     *            确定按钮监听
     * @return
     */
    public static AlertDialog showMultiChoiceDialog(Context context, String title, String[] items,
                                                    boolean[] defaultCheckedItems,
                                                    DialogInterface.OnMultiChoiceClickListener onMultiChoiceClickListener,
                                                    DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        AlertDialog dialog = builder
                .setMultiChoiceItems(items, defaultCheckedItems,
                        onMultiChoiceClickListener)
                .setPositiveButton("确定", null).setNegativeButton("取消", null)
                .show();
        return dialog;
    }

    /*
    * 选择照片的方法，结果在activity的onActivityResult()方法中，
    * 利用ImageUtils.getImageOnActivityResult获取
    *
    * @param activity 调用该方法的Activity
     */
    public static void showImagePickDialog(final Activity activity) {
        String title = "选择获取图片的方式";
        String[] items = new String[] {"拍照","从相册中选择"};
        showListDialog(activity, title, items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                switch (i){
                    case 0:
                        ImageUtils.openCameraImage(activity);
                        break;
                    case 1:
                        ImageUtils.openLocalImage(activity);
                        break;
                }
            }
        });
    }

    public static void showImage(Context context, Uri uri){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/*");
        context.startActivity(intent);
    }

    // Dialog Builder 模式
    public static class MyBuilder{
        private Context context;

        public MyBuilder(Context context){
            this.context = context;
        }

        public Context getContext() {
            return context;
        }

        public MyBuilder setTitle(String title) {
            return this;
        }

        public MyBuilder setMessage(String message) {
            return this;
        }

        public MyBuilder setPositiveButton(String text, final DialogInterface.OnClickListener listener) {
            return this;
        }

        public MyBuilder setNegativeButton(String text, final DialogInterface.OnClickListener listener) {
            return this;
        }

        public Dialog create() {
            final Dialog dialog = new Dialog(context);
            return dialog;
        }

        public Dialog show() {
            Dialog dialog = create();
            dialog.show();
            return dialog;
        }
    }


}

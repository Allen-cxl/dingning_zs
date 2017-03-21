package com.dingning.cardzs.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.dingning.cardzs.R;
import com.yanzhenjie.permission.Rationale;

/**
 * Created by Allen on 2016/12/9.
 */

public class DialogUtils {


    public static void showPermissionDialg(Context context, String title, String msg, final Rationale rationale) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("好，给你", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        rationale.resume();// 用户同意继续申请。
                    }
                })
                .setNegativeButton("我拒绝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        rationale.cancel(); // 用户拒绝申请。
                    }
                }).show();
    }

    public static void showCarIDFailDialg(Context context, String msg) {

        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.dialog_title))
                .setMessage(msg)
                .show();
    }
}

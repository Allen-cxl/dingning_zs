package com.dingning.cardzs.weight;

/**
 * Created by Allen on 2016/12/11.
 */

import java.text.NumberFormat;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dingning.cardzs.R;

public class CommonProgressDialog extends AlertDialog {

    private ProgressBar mProgress;
    private TextView mProgressPercent;
    private TextView mProgressMessage;
    private TextView mProgressTitle;
    private Handler mViewUpdateHandler;
    private Button mBtTry,mBtCancel;
    private int mMax;
    private boolean mHasStarted;
    private int mProgressVal;
    private String TAG = "CommonProgressDialog";
    private NumberFormat mProgressPercentFormat;
    private LinearLayout mLlBt;
    private ReUploadListener reUploadlistenr;

    public CommonProgressDialog(Context context) {
        super(context);
// TODO Auto-generated constructor stub
        initFormats();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
// TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress);
        mProgress = (ProgressBar) findViewById(R.id.progress);
        mProgressPercent = (TextView) findViewById(R.id.progress_percent);
        mProgressMessage = (TextView) findViewById(R.id.progress_message);
        mProgressTitle = (TextView) findViewById(R.id.progress_title);
        mBtTry = (Button) findViewById(R.id.bt_try);
        mBtCancel = (Button) findViewById(R.id.bt_cancel);
        mLlBt = (LinearLayout) findViewById(R.id.ll_bt);

        mBtTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reUploadlistenr.reUpload();
            }
        });
        mBtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mViewUpdateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int progress = mProgress.getProgress();
                int max = mProgress.getMax();

                if (mProgressPercentFormat != null) {
                    double percent = (double) progress / (double) max;
                    SpannableString tmp = new SpannableString(mProgressPercentFormat.format(percent));
                    tmp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                            0, tmp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mProgressPercent.setText(tmp);
                } else {
                    mProgressPercent.setText("");
                }
            }
        };

        onProgressChanged();
        if (mMax > 0) {
            setMax(mMax);
        }
        if (mProgressVal > 0) {
            setProgress(mProgressVal);
        }
    }

    private void initFormats() {
        mProgressPercentFormat = NumberFormat.getPercentInstance();
        mProgressPercentFormat.setMaximumFractionDigits(0);
    }

    private void onProgressChanged() {
        mViewUpdateHandler.sendEmptyMessage(0);
    }

    public void setReUploadlistenr(ReUploadListener reUploadlistenr){

        this.reUploadlistenr = reUploadlistenr;
    }

    public void setLayerVisibil(){
        mLlBt.setVisibility(View.VISIBLE);
    }

    public int getMax() {
        if (mProgress != null) {
            return mProgress.getMax();
        }
        return mMax;
    }

    public void setMax(int max) {
        if (mProgress != null) {
            mProgress.setMax(max);
            onProgressChanged();
        } else {
            mMax = max;
        }
    }

    public void setProgress(int value) {
        if (mHasStarted) {
            mProgress.setProgress(value);
            onProgressChanged();
        } else {
            mProgressVal = value;
        }
    }
    public void setMessage(String message){
        mProgressMessage.setText(message);
    }

    public void setTitle(String title){
        mProgressTitle.setText(title);
    }

    @Override
    protected void onStart() {
// TODO Auto-generated method stub
        super.onStart();
        mHasStarted = true;
    }

    @Override
    protected void onStop() {
// TODO Auto-generated method stub
        super.onStop();
        mHasStarted = false;
    }

    public interface ReUploadListener {
        void reUpload();
    }
}

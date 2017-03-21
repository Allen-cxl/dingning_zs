package com.dingning.cardzs.utils;

import com.dingning.cardzs.listener.OnGetTipTxtListener;
import com.dingning.cardzs.model.StudentNotice;

import java.util.List;
import java.util.TimerTask;

/**
 * Created by Allen on 2016/12/8.
 */

public class TipUtils extends TimerTask {

    private List<StudentNotice> studentNotices;
    private int index;
    private OnGetTipTxtListener listener;

    public TipUtils(List<StudentNotice> studentNotices, OnGetTipTxtListener listener) {

        this.studentNotices = studentNotices;
        this.listener = listener;
    }


    @Override
    public void run() {

        if(studentNotices != null && studentNotices.size() > 0){
            listener.onGetTipTxt(studentNotices.get(index));
            index++;
            if (index == studentNotices.size()) {
                index = 0;
            }
        }else{
            listener.onGetTipTxt(null);
        }

    }
}

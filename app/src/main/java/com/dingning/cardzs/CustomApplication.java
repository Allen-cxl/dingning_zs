package com.dingning.cardzs;

import android.support.multidex.MultiDexApplication;

import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.dingning.cardzs.model.Student;
import com.dingning.cardzs.model.StudentNotice;
import com.dingning.cardzs.utils.CrashHandler;
import com.dingning.cardzs.utils.Key;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Allen on 2015/11/30.
 */
public class CustomApplication extends MultiDexApplication {

    private OSSClient ossClient;
    private Student student;
    private String carId;
    private List<StudentNotice> studentNotices;

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化Jpush
        JPushInterface.init(this);

        setOssClient();
        OkGo.init(this);
        OkGo.getInstance().debug("OkGo", Level.INFO, true);
        studentNotices = new ArrayList<>();
        CrashHandler.getInstance().init(this);
    }

    public Student getStudent() {
        return student;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public List<StudentNotice> getStudentNotices() {
        return studentNotices;
    }

    public void setStudentNotices(List<StudentNotice> studentNotices) {
        this.studentNotices = studentNotices;
    }

    public void setOssClient(){

        String endpoint = Key.oss_url;
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(Key.accessKeyId, Key.accessKeySecret);
        ossClient = new OSSClient(getApplicationContext(), endpoint, credentialProvider);
    }

    public  OSSClient getOssClient(){
        return ossClient;
    }

}

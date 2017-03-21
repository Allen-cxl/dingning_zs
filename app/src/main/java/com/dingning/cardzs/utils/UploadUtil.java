package com.dingning.cardzs.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.dingning.cardzs.Callback.JsonCallback;
import com.dingning.cardzs.CustomApplication;
import com.dingning.cardzs.R;
import com.dingning.cardzs.api.AppConstants;
import com.dingning.cardzs.api.Parameter;
import com.dingning.cardzs.model.BaseResponse;
import com.dingning.cardzs.model.StringEvent;
import com.dingning.cardzs.weight.CommonProgressDialog;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Allen on 2016/12/11.
 */

public class UploadUtil implements  CommonProgressDialog.ReUploadListener{

    private static final int SUCCESS = 0;
    private static final int FAIL = -1;

    private Context context;
    private String cloudFolder;
    private String uploadFilePath;

    private int type;
    private String url;
    private String studentId;
    private String parentID;
    private int watchTime;

    private CommonProgressDialog progressDialog;
    private VideoFile videoFile;
    private boolean isUploadding;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SUCCESS:

                    progressDialog.dismiss();
                    EventBus.getDefault().post(new StringEvent(context.getString(R.string.upload_success)));
                    videoFile.deleteFile();
                    break;
                case FAIL:
                    url = null;
                    progressDialog.setMessage(context.getString(R.string.upload_fail));
                    progressDialog.setLayerVisibil();
                    break;
                default:
                    break;
            }
        }
    };

    public UploadUtil(Context context, String cloudFolder,
                      String uploadFilePath,
                      CommonProgressDialog progressDialog,
                      VideoFile videoFile, int watchTime,
                      String studentId, String parentID,
                      int type){

        this.context = context;
        this.cloudFolder = cloudFolder;
        this.uploadFilePath = uploadFilePath;
        this.progressDialog = progressDialog;
        this.watchTime = watchTime;
        this.videoFile = videoFile;
        this.studentId = studentId;
        this.type = type;
        this.parentID = parentID;
        Log.i("UploadUtil","cloudFolder::"+cloudFolder+"---uploadFilePath:"+uploadFilePath+"---watchTime:"+watchTime);
    }


    void uploadFileUrltoServer() {

        OkGo.post(AppConstants.ADD_EXHORTREPLY_BY)
                .tag(this)
                .params(Parameter.STUDENT_ID, studentId)
                .params(Parameter.PARENTS, parentID)
                .params(type == Parameter.VIDEO ? Parameter.VIDEO_URL : Parameter.VOICE_URL , url)
                .params(Parameter.VOICE_TIME, watchTime)
                .execute(new JsonCallback<BaseResponse<Object>>() {
                    @Override
                    public void onSuccess(BaseResponse<Object> baseResponse, Call call, Response response) {
                        handler.sendEmptyMessage(SUCCESS);

                    }
                });
    }

    public void upload(){

        progressDialog.setTitle(context.getString(R.string.file_tip));
        progressDialog.setMessage(context.getString(R.string.uploading));
        progressDialog.setReUploadlistenr(this);
        PutObjectRequest put = new PutObjectRequest(Key.bucket_name, cloudFolder, uploadFilePath);

        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
                progressDialog.setMax((int)totalSize);
                progressDialog.setProgress((int)currentSize);
                isUploadding = true;
            }
        });

        CustomApplication application = (CustomApplication) ((Activity)context).getApplication();
        OSSAsyncTask task = application.getOssClient().asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {

                url = Key.oss_url + File.separator + cloudFolder;
                uploadFileUrltoServer();

                Log.d("PutObject", "UploadSuccess"+result.toString());
                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
                isUploadding = false;
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {

                // 请求异常
                isUploadding = false;
                handler.sendEmptyMessage(FAIL);
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }


    @Override
    public void reUpload() {
        if(!isUploadding){
            upload();
        }
    }
}

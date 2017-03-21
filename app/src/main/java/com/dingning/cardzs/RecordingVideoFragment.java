package com.dingning.cardzs;

import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.dingning.cardzs.api.Parameter;
import com.dingning.cardzs.listener.RecorderInterface;
import com.dingning.cardzs.model.StringEvent;
import com.dingning.cardzs.utils.CountDownTimerUtil;
import com.dingning.cardzs.utils.DialogUtils;
import com.dingning.cardzs.utils.StringUtils;
import com.dingning.cardzs.utils.UploadUtil;
import com.dingning.cardzs.utils.VideoFile;
import com.dingning.cardzs.weight.CameraRecord;
import com.dingning.cardzs.weight.CommonProgressDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RecordingVideoFragment extends BaseFragment implements RecorderInterface {

    private static final String PARENTID = "parentId";
    private static final String TAG = "RecordingVideoFragment";
    @BindView(R.id.camera_preview)
    FrameLayout cameraPreview;
    @BindView(R.id.tv_record_tip)
    TextView tvRecordTip;
    @BindView(R.id.bt_start_record)
    Button btStartRecord;
    @BindView(R.id.bt_back)
    Button btBack;

    private CameraRecord cameraRecord;
    private boolean isRecording;
    private CountDownTimerUtil timer;
    private Set<String> mParentIds;
    private String studentID, parentID;
    private View mViewContent;

    public RecordingVideoFragment() {

    }

    public static RecordingVideoFragment newInstance(Set<String> parentIds) {
        RecordingVideoFragment fragment = new RecordingVideoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARENTID, (Serializable) parentIds);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParentIds = (Set<String>) getArguments().getSerializable(PARENTID);
            studentID = application.getStudent().getStudent_id();
            parentID = StringUtils.getParentIds(mParentIds);
            EventBus.getDefault().register(this);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mViewContent == null){
            mViewContent = inflater.inflate(R.layout.fragment_recording_video, container, false);
            ButterKnife.bind(this, mViewContent);
            initValue();
        }

        ViewGroup parent = (ViewGroup) mViewContent.getParent();
        if (parent != null) {
            parent.removeView(mViewContent);
        }

        return mViewContent;
    }

    private void initValue() {

        cameraRecord = new CameraRecord(getActivity(), null, cameraPreview,  this);
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraRecord.openCamera();
    }

    @Subscribe
    public void onBuyEvent(StringEvent event) {
        tvRecordTip.setText(event.str);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(cameraRecord !=null){
            cameraRecord.releaseAllResources();
        }
        if(timer!=null){
            timer.cancel();
        }
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.bt_start_record, R.id.bt_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_start_record:

                if(isRecording){
                    stopRecording();

                }else{
                    startRecord();
                }
                break;
            case R.id.bt_back:
                onBack();
                break;
        }
    }

    public void stopRecording(){
        if(timer!=null){
            timer.cancel();
        }
        btStartRecord.setText(R.string.start_voice);
        cameraRecord.stopRecord();
        isRecording = false;
    }

    public void startRecord(){

        btStartRecord.setText(R.string.end_voice);
        timer = null;
        timer = new CountDownTimerUtil(30000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                tvRecordTip.setText(millisUntilFinished/1000+ "s");
            }

            @Override
            public void onFinish() {
                stopRecording();
            }
        };
        isRecording = true;
        cameraRecord.startRecord();
        timer.start();
    }

    @Override
    public void onRecordingStopped(VideoFile videoFile, int time) {
        CommonProgressDialog progressDialog = new CommonProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        UploadUtil uploadUtil = new UploadUtil(getActivity(), videoFile.getCloudFolder(),
                videoFile.getFullPath(), progressDialog,
                videoFile, time,
                studentID, parentID, Parameter.VIDEO);
        uploadUtil.upload();
    }

    @Override
    public void onRecordingStarted() {


    }
}

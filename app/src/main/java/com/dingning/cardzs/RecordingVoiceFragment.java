package com.dingning.cardzs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dingning.cardzs.api.Parameter;
import com.dingning.cardzs.listener.RecorderInterface;
import com.dingning.cardzs.model.StringEvent;
import com.dingning.cardzs.utils.StringUtils;
import com.dingning.cardzs.utils.UploadUtil;
import com.dingning.cardzs.utils.VideoFile;
import com.dingning.cardzs.weight.CommonProgressDialog;
import com.dingning.cardzs.weight.RecordView;
import com.dingning.cardzs.weight.VoiceRecord;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RecordingVoiceFragment extends BaseFragment implements RecorderInterface {

    private static final String PARENTID = "parentId";

    @BindView(R.id.bt_start_record)
    Button btStartRecord;
    @BindView(R.id.record_view)
    RecordView recordView;

    private VoiceRecord recorder;
    private static final int time = 30; //最长录制时间
    private boolean mIsRecordingVideo;
    private Set<String> mParentIds;
    private String studentID, parentID;
    private View mViewContent;

    public RecordingVoiceFragment() {
        // Required empty public constructor
    }


    public static RecordingVoiceFragment newInstance(Set<String> parentIds) {
        RecordingVoiceFragment fragment = new RecordingVoiceFragment();
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
        // Inflate the layout for this fragment
        if(mViewContent == null){
            mViewContent = inflater.inflate(R.layout.fragment_recording_voice, container, false);
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
        recorder = new VoiceRecord(getActivity(),  this);
        recordView.setCountdownTime(time);
        recordView.setModel(RecordView.MODEL_RECORD);
        recordView.setOnCountDownListener(new RecordView.OnCountDownListener() {
            @Override
            public void onCountDown() {
                stopRecord();
            }
        });
    }

    @Subscribe
    public void onBuyEvent(StringEvent event) {
        if (recordView != null) {
            recordView.setHintText(event.str);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if(recorder != null){
            recorder.release();
        }
    }

    public void startRecord(){

        try {
            recordView.setHintText("");
            btStartRecord.setText(getString(R.string.end_voice));
            recorder.startRecording();
            mIsRecordingVideo = true;
            recordView.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecord(){

        btStartRecord.setText(getString(R.string.start_voice));
        recorder.stopRecording();
        mIsRecordingVideo = false;
    }

    @OnClick({R.id.bt_start_record, R.id.bt_back})
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.bt_start_record:
                if (mIsRecordingVideo) {
                    recordView.cancel();
                    stopRecord();
                } else {
                    startRecord();
                }
                break;

            case R.id.bt_back:
                onBack();
                break;
        }

    }

    @Override
    public void onRecordingStopped(VideoFile videoFile, int time) {
        CommonProgressDialog progressDialog = new CommonProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        UploadUtil uploadUtil = new UploadUtil(getActivity(), videoFile.getCloudFolder(),
                videoFile.getFullPath(), progressDialog,
                videoFile, time,
                studentID, parentID, Parameter.VOICE);
        progressDialog.show();
        uploadUtil.upload();
    }

    @Override
    public void onRecordingStarted() {

    }
}

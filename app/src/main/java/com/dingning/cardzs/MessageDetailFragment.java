package com.dingning.cardzs;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dingning.cardzs.Callback.DialogCallback;
import com.dingning.cardzs.api.AppConstants;
import com.dingning.cardzs.api.Parameter;
import com.dingning.cardzs.model.BaseResponse;
import com.dingning.cardzs.model.Image;
import com.dingning.cardzs.model.Message;
import com.dingning.cardzs.model.MessageDetail;
import com.dingning.cardzs.model.NoticeEvent;
import com.dingning.cardzs.model.ReadEvent;
import com.dingning.cardzs.model.StudentNotice;
import com.dingning.cardzs.utils.GlideRoundTransform;
import com.dingning.cardzs.weight.MaterialBadgeTextView;
import com.dingning.cardzs.weight.ImagePreviewDialog;
import com.dingning.cardzs.weight.VideoDialog;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;


public class MessageDetailFragment extends BaseFragment {
    private static final String MESSAGE = "message";
    @BindView(R.id.iv_parent_avatar)
    ImageView ivParentAvatar;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.iv_video)
    ImageView ivVideo;
    @BindView(R.id.iv_voice)
    ImageView ivVoice;
    @BindView(R.id.iv_img)
    ImageView ivImg;
    @BindView(R.id.tv_img_count)
    MaterialBadgeTextView tvImgCount;
    @BindView(R.id.bt_reply_video)
    Button btReplyVideo;
    @BindView(R.id.bt_reply_voice)
    Button btReplyVoice;
    @BindView(R.id.bt_back)
    Button btBack;

    private MessageDetail messageDetail;
    private String exhortID;
    private String studentID;
    private String parentId;
    private Message message;
    private Set<String> mParentIds;
    private List<Image> images;
    private List<StudentNotice> studentNotices;
    private View mViewContent;

    public MessageDetailFragment() {
    }


    public static MessageDetailFragment newInstance(Message message) {
        MessageDetailFragment fragment = new MessageDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message = (Message) getArguments().getSerializable(MESSAGE);
            studentID = application.getStudent().getStudent_id();
            studentNotices = application.getStudentNotices();
            exhortID = message.getExhort_id();
            parentId = message.getParent_id();
            images = new ArrayList<>();
            mParentIds = new HashSet<>();
            mParentIds.add(parentId);
            removeStudentNotice(studentNotices, exhortID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(mViewContent == null){
            mViewContent = inflater.inflate(R.layout.fragment_message_detail, container, false);
            ButterKnife.bind(this, mViewContent);
            init();
        }

        ViewGroup parent = (ViewGroup) mViewContent.getParent();
        if (parent != null) {
            parent.removeView(mViewContent);
        }
        return mViewContent;
    }

    private void init() {

        ivVideo.setEnabled(false);
        ivVoice.setEnabled(false);
        ivImg.setEnabled(false);
        getMessageDeatail();

    }

    private void initValue() {
        images = messageDetail.getImgs();
        Glide.with(getActivity().getApplicationContext())
                .load(message.getParent_pic())
                .crossFade()
                .centerCrop()
                .transform(new GlideRoundTransform(getActivity(), 10))
                .placeholder(R.drawable.parent)
                .into(ivParentAvatar);

        tvMessage.setText(messageDetail.getContent());
        if (TextUtils.isEmpty(messageDetail.getVideo_url())) {
            ivVideo.setEnabled(false);
            ivVideo.setImageResource(R.drawable.no_play_video);
        } else {
            ivVideo.setEnabled(true);
            ivVideo.setImageResource(R.drawable.play_video);
        }

        if (TextUtils.isEmpty(messageDetail.getVoice_url())) {
            ivVoice.setEnabled(false);
            ivVoice.setImageResource(R.drawable.no_play_audi);
        } else {
            ivVoice.setEnabled(true);
            ivVoice.setImageResource(R.drawable.play_audi);
        }

        if (images.size() <= 0) {
            ivImg.setEnabled(false);
            tvImgCount.setText("");
        } else {
            ivImg.setEnabled(true);
            tvImgCount.setText(images.size()+"");
        }
    }

    void getMessageDeatail() {

        OkGo.post(AppConstants.GET_EXHORTBYID_BY)
                .tag(this)
                .params(Parameter.EXHORT_ID, exhortID)
                .params(Parameter.STUDENT_ID, studentID)
                .execute(new DialogCallback<BaseResponse<MessageDetail>>(getActivity()) {
                    @Override
                    public void onSuccess(BaseResponse<MessageDetail> baseResponse, Call call, Response response) {

                        messageDetail = baseResponse.data;
                        initValue();
                        EventBus.getDefault().post(new ReadEvent());
                    }
                });
    }

    public void removeStudentNotice(List<StudentNotice> studentNotices, String exhortID){

        if(studentNotices != null && studentNotices.size()>0){
            for (StudentNotice studentNotice : studentNotices) {
                String  rid= studentNotice.getRid();
                if(exhortID.equals(rid)){
                    studentNotices.remove(studentNotice);
                }
            }
        }

    }

    @OnClick({R.id.iv_video, R.id.iv_voice, R.id.iv_img, R.id.bt_reply_video, R.id.bt_reply_voice, R.id.bt_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_video:
                String urlVideo = messageDetail.getVideo_url();
                VideoDialog dialogVideo = new VideoDialog(getActivity(), R.style.view_dialog);
                dialogVideo.show();
                dialogVideo.initValue(urlVideo);
                break;
            case R.id.iv_voice:

                String urlAudi = messageDetail.getVoice_url();
                VideoDialog dialogVoice = new VideoDialog(getActivity(), R.style.view_dialog);
                dialogVoice.show();
                dialogVoice.initValue(urlAudi);
                break;
            case R.id.iv_img:
                ImagePreviewDialog dialog = new ImagePreviewDialog(getActivity(), R.style.view_dialog);
                dialog.show();
                dialog.initData(images);
                break;
            case R.id.bt_reply_video:

                enterFragment(RecordingVideoFragment.newInstance(mParentIds));
                break;
            case R.id.bt_reply_voice:

                enterFragment(RecordingVoiceFragment.newInstance(mParentIds));
                break;
            case R.id.bt_back:
                onBack();
                break;
        }
    }
}

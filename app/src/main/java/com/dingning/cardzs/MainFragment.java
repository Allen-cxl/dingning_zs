package com.dingning.cardzs;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dingning.cardzs.Callback.JsonCallback;
import com.dingning.cardzs.api.AppConstants;
import com.dingning.cardzs.api.Parameter;
import com.dingning.cardzs.model.BaseResponse;
import com.dingning.cardzs.model.Student;
import com.dingning.cardzs.utils.DialogUtils;
import com.dingning.cardzs.utils.SpUtils;
import com.dingning.cardzs.utils.SystemUtil;
import com.lzy.okgo.OkGo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;


public class MainFragment extends BaseFragment {
    @BindView(R.id.et_card)
    EditText etCard;
    @BindView(R.id.bt_back)
    Button btBack;
    @BindView(R.id.tv_version)
    TextView tvVersion;

    private Student student;
    private String carId;
    private boolean isLoading;
    private OnUpdateClassInfoListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUpdateClassInfoListener) {
            mListener = (OnUpdateClassInfoListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCloseMessageNoticeListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        initValue();
        return view;
    }

    private void initValue() {

        etCard.requestFocus();
        etCard.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {

                    if(!isLoading){
                        carId = etCard.getText().toString().trim();

                        if (!TextUtils.isEmpty(carId)) {
                            //处理事件
                            getStudentInfo();
                        }
                    }

                }
                return false;
            }
        });

        tvVersion.setText(getString(R.string.app_name) +": "+ SystemUtil.getAppVersionName(getActivity()));
    }

    public void getStudentInfo() {
        isLoading = true;
        OkGo.post(AppConstants.GET_STUDENTINFO_BY)    // 请求方式和请求url, get请求不需要拼接参数，支持get，post，put，delete，head，options请求
                .tag(this)               // 请求的 tag, 主要用于取消对应的请求
                .params(Parameter.CARD_ID, carId)
                //这里给出的泛型为 ServerModel，同时传递一个泛型的 class对象，即可自动将数据结果转成对象返回
                .execute(new JsonCallback<BaseResponse<Student>>() {

                    @Override
                    public void onSuccess(BaseResponse<Student> baseResponse, Call call, Response response) {
                        isLoading = false;
                        student = baseResponse.data;
                        String classID = student.getClass_id();
                        String className = student.getClass_name();
                        String classIDSp = SpUtils.getString(getActivity(), SpUtils.spClassID);

                        if (carId == null || student == null) {
                            DialogUtils.showCarIDFailDialg(getActivity(), getString(R.string.no_carid));
                            return;
                        }

                        if (!classID.equals(classIDSp)) {
                            DialogUtils.showCarIDFailDialg(getActivity(), getString(R.string.class_no_carid));
                            return;
                        }

                        if (!TextUtils.isEmpty(className)) {
                            SpUtils.putString(getActivity(), SpUtils.spClassName, className);
                            mListener.onUpdateClassName();
                        }
                        application.setCarId(carId);
                        application.setStudent(student);
                        enterFragment(MessageListFragment.newInstance());
                        etCard.setText(null);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        etCard.setText(null);
                        DialogUtils.showCarIDFailDialg(getActivity(), "获取信息失败,请重试");
                        isLoading = false;
                    }
                });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick({R.id.bt_back})
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.bt_back:

                openSolaApp();
                break;
            default:
                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        etCard.requestFocus();
    }

    public void openSolaApp() {

        String action = "com.sola.DMTPlayer";
        PackageManager packageManager = getActivity().getApplication().getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(action);

        if (intent == null) {
            DialogUtils.showCarIDFailDialg(getActivity(), getActivity().getString(R.string.no_app));
        } else {
            startActivity(intent);
        }
    }

    public interface OnUpdateClassInfoListener {

        void onUpdateClassName();
    }
}

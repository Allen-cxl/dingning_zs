package com.dingning.cardzs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dingning.cardzs.Callback.DialogCallback;
import com.dingning.cardzs.Callback.JsonCallback;
import com.dingning.cardzs.api.AppConstants;
import com.dingning.cardzs.api.Parameter;
import com.dingning.cardzs.model.BaseResponse;
import com.dingning.cardzs.model.NoticeEvent;
import com.dingning.cardzs.model.Student;
import com.dingning.cardzs.model.StudentNotice;
import com.dingning.cardzs.utils.Convert;
import com.dingning.cardzs.utils.DialogUtils;
import com.dingning.cardzs.utils.ScanService;
import com.dingning.cardzs.utils.SpUtils;
import com.dingning.cardzs.utils.StringUtils;
import com.dingning.cardzs.utils.SystemUtil;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;

import java.math.BigInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Response;


public class MainFragment extends BaseFragment {

    @BindView(R.id.bt_back)
    Button btBack;
    @BindView(R.id.tv_version)
    TextView tvVersion;

    private Student student;
    private boolean isLoading, isShow;
    private OnUpdateClassInfoListener mListener;
    private final String poatReceiverAciton = "android.intent.action.hal.barcodescanner.scandata";

    private BroadcastReceiver mTimeRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (poatReceiverAciton.equals(intent.getAction())) {
                String scanData = intent.getStringExtra("scanData");
                String string = StringUtils.changesPerialPortString(scanData);
                BigInteger carId = new BigInteger(string, 16);
                Log.i("serial_port_carId",carId.toString());
                if (!TextUtils.isEmpty(carId.toString())&& isShow) {
                    //处理事件
                    getStudentInfo(carId.toString());
                }
            }
        }


    };

    public MainFragment() {

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

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.hal.barcodescanner.scandata");
        intentFilter.addCategory(getActivity().getPackageName());
        getActivity().registerReceiver(mTimeRefreshReceiver, intentFilter);

        tvVersion.setText(getString(R.string.app_name) + ": " + SystemUtil.getAppVersionName(getActivity()));
    }

    public void getStudentInfo(final String carID) {
        isLoading = true;
        OkGo.post(AppConstants.GET_STUDENTINFO_BY)    // 请求方式和请求url, get请求不需要拼接参数，支持get，post，put，delete，head，options请求
                .tag(this)               // 请求的 tag, 主要用于取消对应的请求
                .params(Parameter.CARD_ID, carID)
                //这里给出的泛型为 ServerModel，同时传递一个泛型的 class对象，即可自动将数据结果转成对象返回
                .execute(new DialogCallback<BaseResponse<Student>>(getActivity()) {

                    @Override
                    public void onSuccess(BaseResponse<Student> baseResponse, Call call, Response response) {

                        student = baseResponse.data;
                        String classID = student.getClass_id();
                        String className = student.getClass_name();
                        String classIDSp = SpUtils.getString(getActivity(), SpUtils.spClassID);

                        if (carID == null || student == null) {
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
                        application.setCarId(carID);
                        application.setStudent(student);
                        enterFragment(MessageListFragment.newInstance());
                        isLoading = false;
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mTimeRefreshReceiver);
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
        isShow =true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isShow =false;
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

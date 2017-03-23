package com.dingning.cardzs;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.dingning.cardzs.Callback.DialogCallback;
import com.dingning.cardzs.Callback.JsonCallback;
import com.dingning.cardzs.api.AppConstants;
import com.dingning.cardzs.api.Parameter;
import com.dingning.cardzs.listener.OnGetTipTxtListener;
import com.dingning.cardzs.model.BaseResponse;
import com.dingning.cardzs.model.NoticeEvent;
import com.dingning.cardzs.model.School;
import com.dingning.cardzs.model.StringEvent;
import com.dingning.cardzs.model.Student;
import com.dingning.cardzs.model.StudentNotice;
import com.dingning.cardzs.model.Version;
import com.dingning.cardzs.utils.Convert;
import com.dingning.cardzs.utils.DialogUtils;
import com.dingning.cardzs.utils.GlideRoundTransform;
import com.dingning.cardzs.utils.ScanService;
import com.dingning.cardzs.utils.ScreenUtils;
import com.dingning.cardzs.utils.SpUtils;
import com.dingning.cardzs.utils.StringUtils;
import com.dingning.cardzs.utils.SystemUtil;
import com.dingning.cardzs.utils.TimeUtils;
import com.dingning.cardzs.utils.TipUtils;
import com.dingning.cardzs.weight.CommonProgressDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends FragmentActivity implements MainFragment.OnUpdateClassInfoListener, ViewSwitcher.ViewFactory {

    private static final int TIP_MSG = 0x00001;
    private static final int DIALOG_UPDATE = 0x00002;
    private static final int DIALOG_SET = 0x00003;

    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.tv_class_name)
    TextView tvClassName;
    @BindView(R.id.tv_school_name)
    TextView tvSchoolName;
    @BindView(R.id.ts_new_message)
    TextSwitcher tsNewMessage;
    @BindView(R.id.tv_day)
    TextView tvDay;
    @BindView(R.id.tv_data)
    TextView tvData;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.fragment)
    FrameLayout fragment;
    private FragmentManager mManager;
    private String className;
    private String classID;
    private String schoolName;
    private String schoolLogo;
    private int versionCode;
    private String versionName;
    private IntentFilter intentFilter;
    private Timer timer;
    private List<StudentNotice> studentNotices;
    private CustomApplication application;
    private Intent mIntentService;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIP_MSG:
                    StudentNotice studentNotice = (StudentNotice) msg.obj;

                    String message = null;
                    if(studentNotice != null){
                        message = studentNotice.getMessage();
                    }
                    tsNewMessage.setText(message);
            }
        }
    };

    private BroadcastReceiver mTimeRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                setTime();
            } else if ((JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction()))) {// 自定义消息

                String message = intent.getExtras().getString(JPushInterface.EXTRA_MESSAGE);
                String extra = intent.getExtras().getString(JPushInterface.EXTRA_EXTRA);

                StudentNotice studentNotice = Convert.fromJson(extra, StudentNotice.class);
                if (studentNotice != null) {
                    studentNotice.setMessage(message);
                    studentNotices.add(studentNotice);
                    Student student = application.getStudent();
                    if (student != null && student.getStudent_id().equals(studentNotice.getStudent_id())) {
                        EventBus.getDefault().post(new NoticeEvent());
                    }
                }

            }
        }


    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        application = (CustomApplication) getApplication();

        classID = SpUtils.getString(MainActivity.this, SpUtils.spClassID);
        versionCode = SystemUtil.getAppVersionCode(MainActivity.this);
        versionName = SystemUtil.getAppVersionName(MainActivity.this);

        mIntentService = new Intent(this, ScanService.class);
        Bundle bundle = new Bundle();
        bundle.putString("type", "on");
        mIntentService.putExtras(bundle);
        startService(mIntentService);

        if (TextUtils.isEmpty(classID)) {
            showInfoDialog(DIALOG_SET);
        } else {
            initValue();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setTime();
        getVersionNo();
    }

    private void initValue() {

        mManager = getSupportFragmentManager();
        studentNotices = application.getStudentNotices();
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(JPushInterface.ACTION_REGISTRATION_ID);
        intentFilter.addAction(JPushInterface.ACTION_MESSAGE_RECEIVED);
        intentFilter.addCategory(getPackageName());

        tsNewMessage.setFactory(this);
        timer = new Timer();
        timer.schedule(new TipUtils(studentNotices, listener), 0, 5000);

        registerReceiver(mTimeRefreshReceiver, intentFilter);

        init();

        AndPermission.with(this)
                .requestCode(101)
                .rationale(mRationaleListener)
                .permission(Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .send();
        FragmentTransaction transaction = mManager.beginTransaction();
        transaction.replace(R.id.fragment, MainFragment.newInstance()).commitAllowingStateLoss();

    }

    private RationaleListener mRationaleListener = new RationaleListener() {
        @Override
        public void showRequestPermissionRationale(int requestCode, final Rationale rationale) {
            DialogUtils.showPermissionDialg(MainActivity.this,
                    "友好提醒",
                    "没有相关权限，请打开权限！",
                    rationale);
        }
    };

    public void setTime() {
        tvDay.setText(TimeUtils.getDayInWeek());
        tvData.setText(TimeUtils.getDate());
        tvTime.setText(TimeUtils.getTime());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (timer != null) {
            timer.cancel();
        }
        handler = null;
        timer = null;
        try {
            application.setStudentNotices(null);
            unregisterReceiver(mTimeRefreshReceiver);
            stopService(mIntentService);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void init() {

        classID = SpUtils.getString(MainActivity.this, SpUtils.spClassID);
        className = SpUtils.getString(MainActivity.this, SpUtils.spClassName);
        schoolName = SpUtils.getString(MainActivity.this, SpUtils.spSchoolName);
        schoolLogo = SpUtils.getString(MainActivity.this, SpUtils.spSchoolLogo);
        if (!TextUtils.isEmpty(classID)) {
            JPushInterface.setAlias(this, classID, new TagAliasCallback() {
                @Override
                public void gotResult(int i, String s, Set<String> set) {

                }
            });
        }
        tvClassName.setText(className);
        tvSchoolName.setText(schoolName);
        Glide.with(getApplicationContext())
                .load(schoolLogo)
                .crossFade()
                .into(ivLogo);
    }

    public void getSchoolInfo(String classId) {

        OkGo.post(AppConstants.GET_SCHOOL_INFO)    // 请求方式和请求url, get请求不需要拼接参数，支持get，post，put，delete，head，options请求
                .tag(this)               // 请求的 tag, 主要用于取消对应的请求
                .params(Parameter.CLASS_ID, classId)
                //这里给出的泛型为 ServerModel，同时传递一个泛型的 class对象，即可自动将数据结果转成对象返回
                .execute(new DialogCallback<BaseResponse<School>>(this) {
                    @Override
                    public void onSuccess(BaseResponse<School> baseResponse, Call call, Response response) {

                        if (baseResponse != null) {

                            School school = baseResponse.data;

                            SpUtils.putString(MainActivity.this, SpUtils.spClassName, school.getClass_name());
                            SpUtils.putString(MainActivity.this, SpUtils.spSchoolName, school.getSchool_name());
                            SpUtils.putString(MainActivity.this, SpUtils.spSchoolLogo, school.getSchool_logo());
                            init();
                        }
                    }
                });
    }

    public void getVersionNo() {

        OkGo.post(AppConstants.GET_CARDVERSION_BY)    // 请求方式和请求url, get请求不需要拼接参数，支持get，post，put，delete，head，options请求
                .tag(this)               // 请求的 tag, 主要用于取消对应的请求
                .params(Parameter.VERSION_NAME, versionName)
                .params(Parameter.VERSION_CODE, versionCode)
                //这里给出的泛型为 ServerModel，同时传递一个泛型的 class对象，即可自动将数据结果转成对象返回
                .execute(new JsonCallback<BaseResponse<Version>>() {
                    @Override
                    public void onSuccess(BaseResponse<Version> baseResponse, Call call, Response response) {

                        if (baseResponse != null) {

                            Version version = baseResponse.data;
                            if (version != null) {
                                checkForUpdate(version);
                            }
                        }
                    }
                });
    }

    private void checkForUpdate(Version version) {

        int versionCodeTemp = version.getVersion_code();
        int versionType = version.getType();

        if (versionCodeTemp > versionCode && versionType == Version.UPDATE_TRUE) {
            downLoadApk(version);
        }

    }

    private void downLoadApk(Version version) {

        String url = version.getUrl();
        String content = version.getContent();

        final CommonProgressDialog dialog = new CommonProgressDialog(MainActivity.this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.setTitle(getString(R.string.version_tip));
        dialog.setMessage(content);
        OkGo.post(url)//
                .tag(this)//
                .execute(new FileCallback() {  //文件下载时，可以指定下载的文件目录和文件名
                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        // file 即为文件数据，文件保存在指定目录
                        dialog.dismiss();
                        openApk(file);
                    }

                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        //这里回调下载进度(该回调在主线程,可以直接更新ui)
                        dialog.setMax((int) totalSize);
                        dialog.setProgress((int) currentSize);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        dialog.dismiss();
                    }
                });

    }

    private void openApk(File file) {

        if (file.exists()) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            startActivity(intent);
        }
    }

    @Override
    public void onUpdateClassName() {

        className = SpUtils.getString(MainActivity.this, SpUtils.spClassName);
        tvClassName.setText(className);
    }

    OnGetTipTxtListener listener = new OnGetTipTxtListener() {
        @Override
        public void onGetTipTxt(StudentNotice studentNotice) {
            Message message = Message.obtain();
            message.obj = studentNotice;
            message.what = TIP_MSG;
            handler.sendMessage(message);
        }
    };

    @Override
    public View makeView() {

        TextView tv = new TextView(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        tv.setTextColor(this.getResources().getColor(R.color.white));
        tv.setTextSize(ScreenUtils.dip2px(this, 20));
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(layoutParams);
        return tv;
    }

    @OnClick(R.id.iv_logo)
    public void onClick() {

        showInfoDialog(DIALOG_UPDATE);
    }

    public void showInfoDialog(final int type) {

        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_set_classid, null);
        final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
        dialog.setView(new EditText(MainActivity.this));
        dialog.show();
        dialog.setContentView(view);

        dialog.setCancelable(false);
        final EditText etEntryClassID = (EditText) view.findViewById(R.id.et_entry_class_id);
        final EditText etEntryClassName = (EditText) view.findViewById(R.id.et_entry_class_name);
        final EditText etEntryAdminPassword = (EditText) view.findViewById(R.id.et_entry_admin_password);
        Button btOk = (Button) view.findViewById(R.id.bt_ok);
        Button btCancel = (Button) view.findViewById(R.id.bt_cancel);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String classIdStr = etEntryClassID.getText().toString().trim();
                String classNameStr = etEntryClassName.getText().toString().trim();
                String adminPasswrodStr = etEntryAdminPassword.getText().toString().trim();
                String adminPasswordSp = SpUtils.getString(MainActivity.this, SpUtils.spAdminPassword);

                if (TextUtils.isEmpty(classIdStr)) {
                    Toast.makeText(MainActivity.this, getString(R.string.null_class_id_tip), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(classNameStr)) {
                    Toast.makeText(MainActivity.this, getString(R.string.null_class_name_tip), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(adminPasswrodStr)) {
                    Toast.makeText(MainActivity.this, getString(R.string.null_class_admin_password_tip), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(type == DIALOG_UPDATE){
                    if (!adminPasswrodStr.equals(adminPasswordSp)) {
                        Toast.makeText(MainActivity.this, getString(R.string.admin_password_error), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                SpUtils.putString(MainActivity.this, SpUtils.spClassID, classIdStr);
                SpUtils.putString(MainActivity.this, SpUtils.spClassName, classNameStr);
                SpUtils.putString(MainActivity.this, SpUtils.spAdminPassword, adminPasswrodStr);
                dialog.dismiss();

                if(type == DIALOG_SET){
                    initValue();
                }

                getSchoolInfo(classIdStr);

                if(type == DIALOG_UPDATE){
                    Toast.makeText(MainActivity.this, getString(R.string.update_admin_password_success), Toast.LENGTH_SHORT).show();
                }

            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(type == DIALOG_SET){
                    finish();
                }
            }
        });

    }

}

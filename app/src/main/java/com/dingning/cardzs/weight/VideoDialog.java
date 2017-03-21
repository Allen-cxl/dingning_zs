package com.dingning.cardzs.weight;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dingning.cardzs.R;
import com.dingning.cardzs.utils.NetWorkUtil;
import com.dingning.cardzs.utils.ScreenUtils;
import com.dingning.cardzs.utils.TimeUtils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Allen on 2016/12/13.
 */

public class VideoDialog extends Dialog implements DialogInterface.OnDismissListener{


    private AutoVideoView mVideoView;
    private MediaPlayer mMediaPlayer;

    public static final String TAG = "SJZ";

    private static final int MSG_SURFACE_PREPARE = 0x00000000;
    private static final int MSG_SURFACE_START = 0x00000001;
    private static final int MSG_SURFACE_DESTORY = 0x00000003;

    private static final int MSG_UPDATE_PROGRESS = 0x00000006;
    private static final int MSG_MEDIA_CONTROLLER_HIDE = 0x00000007;
    private static final int MSG_MEDIA_PERENT = 0x00000008;
    private static final int MSG_MEDIA_ERROR = 0x00000009;


    private TextView mText_Current;
    private TextView mText_Durtion;
    private SeekBar mSeekBar;
    private ProgressBar mPro_Buffer;
    private ImageView mImage_PlayOrPause;
    private RelativeLayout mContainer;
    private LinearLayout mBotton_Controller;


    private Timer mServerTimer = null;
    private Timer mControllerTimer = null;
    private Context mContext;
    private String url;
    private int mDuration = -1;
    private boolean mIsHandPause = false;


    /**
     * 由于mediaplay为异步加载，防止程序进入后台后播放
     * onPause()时，记录当前播放状态，重新onResume()恢复之前状态
     * mIsPrepred 参数为onPause()判断当前是否初始化完成
     */
    private boolean mIsBackPrepared = false;           //由于mediaplay是异步加载，当home时可能会在后台播放的可能

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SURFACE_PREPARE:
                    String url = (String) msg.obj;
                    setDataSource(url);
                    break;

                case MSG_SURFACE_START:
                    mPro_Buffer.setVisibility(View.GONE);
                    mImage_PlayOrPause.setImageResource(R.drawable.jc_click_pause_selector);
                    mImage_PlayOrPause.setVisibility(View.VISIBLE);
                    mVideoView.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                    mVideoView.start();
                    startProgressTimer();
                    startControllerShowOrHide();
                    break;

                case MSG_SURFACE_DESTORY:
                    if (mBotton_Controller.getVisibility() == View.VISIBLE) {
                        mediaControllerHide();
                    } else {
                        mediaControllerShow();
                    }
                    break;

                case MSG_MEDIA_ERROR:
                    mPro_Buffer.setVisibility(View.GONE);
                    break;

                case MSG_UPDATE_PROGRESS:
                    setProgressController(0);
                    break;

                case MSG_MEDIA_CONTROLLER_HIDE:
                    mediaControllerHide();
                    break;

                case MSG_MEDIA_PERENT:
                    int percent = (int) msg.obj;
                    setProgressController(percent);
                    break;
                default:
                    break;
            }
        }
    };


    public VideoDialog(Context context) {
        super(context);
        initView(context);
    }

    public VideoDialog(Context context, int theme) {
        super(context, theme);
        initView(context);
    }

    protected VideoDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView(context);
    }

    private void initView(Context context){
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_video,null);
        setContentView(view);
    }

    public  void  initValue(String url){
        this.url = url;
        initMediaController();
        resetProgressAndTimer();
    }

    private void initMediaController() {
        mContainer = (RelativeLayout) findViewById(R.id.container);
        mVideoView = (AutoVideoView) findViewById(R.id.surfaceview);
        mBotton_Controller = (LinearLayout) findViewById(R.id.bottom_media_controller);
        mText_Current = (TextView) findViewById(R.id.text_currentpostion);
        mText_Durtion = (TextView) findViewById(R.id.text_durtionposition);
        mPro_Buffer = (ProgressBar) findViewById(R.id.image_buffer);
        mImage_PlayOrPause = (ImageView) findViewById(R.id.image_playorpause);
        mImage_PlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    mImage_PlayOrPause.setImageResource(R.drawable.jc_click_play_selector);
                    mIsHandPause = true;
                } else {
                    mVideoView.start();
                    mImage_PlayOrPause.setImageResource(R.drawable.jc_click_pause_selector);
                    mIsHandPause = false;
                }
            }
        });

        mSeekBar = (SeekBar) findViewById(R.id.progress);

        int width = (int) (ScreenUtils.getWidth(mContext)*0.7);
        int height = (int) (ScreenUtils.getHeight(mContext)*0.7);

        mContainer.getLayoutParams().width = (int) width;
        mContainer.getLayoutParams().height = (int) height;

        playMedia(url, "");
    }

    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            mHandler.sendEmptyMessage(MSG_MEDIA_ERROR);
            return false;
        }
    };

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (mp.getDuration() != -1 && mp.getCurrentPosition() >= mp.getDuration() - 1000) {
                cancleControllerTimer();
                mImage_PlayOrPause.setImageResource(R.drawable.jc_click_play_selector);
                mediaControllerShow();
            }

        }
    };

    private MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    mPro_Buffer.setVisibility(View.VISIBLE);
                    if (mImage_PlayOrPause.getVisibility() == View.VISIBLE) {
                        mImage_PlayOrPause.setVisibility(View.GONE);
                    }
                    break;

                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    if (mVideoView.isPlaying() || mIsHandPause) {
                        mPro_Buffer.setVisibility(View.GONE);
                    }
                    break;

                default:
                    break;
            }
            return true;
        }
    };

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (mIsBackPrepared) {
                mVideoView.pause();
                mIsBackPrepared = false;
            } else {
                mHandler.sendEmptyMessage(MSG_SURFACE_START);
            }
            if (mMediaPlayer == null) {
                mMediaPlayer = mp;
                mMediaPlayer.setOnInfoListener(mOnInfoListener);
                mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
            }
        }

    };

    private MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if (mServerTimer != null && percent > 0) {

                Message m =  Message.obtain();
                m.obj = percent;
                m.what = MSG_MEDIA_PERENT;
                mHandler.sendMessage(m);
            }
        }
    };

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mHandler.sendEmptyMessage(MSG_SURFACE_DESTORY);
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    private void setListaner() {
        mVideoView.setOnTouchListener(mOnTouchListener);
        mVideoView.setOnErrorListener(mOnErrorListener);
        mVideoView.setOnCompletionListener(mOnCompletionListener);
        mVideoView.setOnPreparedListener(mOnPreparedListener);
    }

    public void playMedia(String path, String name) {
        if (!NetWorkUtil.isNetWorkEnable(mContext)) {
            Toast.makeText(mContext, R.string.network_is_unable, Toast.LENGTH_SHORT).show();
            return;
        }
        Message msg = Message.obtain();
        msg.obj = path;
        msg.what = MSG_SURFACE_PREPARE;
        mHandler.sendMessage(msg);
    }

    public void setDataSource(String path) {
        if (TextUtils.isEmpty(path)) return;
        mPro_Buffer.setVisibility(View.VISIBLE);
        mImage_PlayOrPause.setVisibility(View.GONE);
        resetProgressAndTimer();
        mVideoView.requestFocus();
        mVideoView.setBackgroundColor(mContext.getResources().getColor(R.color.videobackcolor));
        try {
            setListaner();
            File localFile = new File(path);
            if (localFile.isFile()) {
                mVideoView.setVideoURI(Uri.fromFile(localFile));
            } else {
                mVideoView.setVideoPath(path);
            }
            mVideoView.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void setProgressAndTime(int current, int duration, int progress, int secProgress) {
        Log.i("Progress", "current:"+current+"--duration:"+duration+"--progress"+progress);
        mSeekBar.setProgress(progress > 0 ? progress : 0);
        if (secProgress > 0) {
            mSeekBar.setSecondaryProgress(secProgress);
        }
        mText_Current.setText(TimeUtils.stringForTime(current));
        mText_Durtion.setText(TimeUtils.stringForTime(duration));
    }

    private void mediaControllerHide() {
        mBotton_Controller.setVisibility(View.GONE);
        mImage_PlayOrPause.setVisibility(View.GONE);
        cancleControllerTimer();
    }

    private void mediaControllerShow() {
        cancleControllerTimer();
        mBotton_Controller.setVisibility(View.VISIBLE);
        mImage_PlayOrPause.setVisibility(mPro_Buffer.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        mImage_PlayOrPause.setImageResource(mVideoView.isPlaying() ? R.drawable.jc_click_pause_selector : R.drawable.jc_click_play_selector);
        startProgressTimer();
        startControllerShowOrHide();
    }

    public int getDuration() {
        int du = mVideoView.getDuration();
        int duration = du != -1 ? du : mDuration;  //home键恢复后 有可能拿不到总长度值 故
        return duration;
    }

    private void resetProgressAndTimer() {
        mDuration = 0;
        mSeekBar.setProgress(0);
        mSeekBar.setSecondaryProgress(0);
        mText_Current.setText("00:00");
        mText_Durtion.setText("00:00");
    }

    private void startProgressTimer() {
        cancleControllerTimer();
        mServerTimer = new Timer();
        mServerTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    mHandler.sendEmptyMessage(MSG_UPDATE_PROGRESS);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 500);
    }

    private void startControllerShowOrHide() {
        mControllerTimer = new Timer();
        mControllerTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(MSG_MEDIA_CONTROLLER_HIDE);
            }
        }, 3000, 1);
    }

    private void cancleControllerTimer() {
        if (mServerTimer != null) {
            mServerTimer.cancel();
            mServerTimer = null;
        }
        if (mControllerTimer != null) {
            mControllerTimer.cancel();
            mControllerTimer = null;
        }
    }

    private void setProgressController(int percent) {
        int currentPostion = 0;
        int duration = -1;
        try {
            currentPostion = mVideoView.getCurrentPosition();
            duration = getDuration();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        int progress = currentPostion * 100 / (duration == 0 ? 1 : duration);
        setProgressAndTime(currentPostion, duration, progress, percent);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mVideoView != null) {
            mVideoView = null;
        }
        cancleControllerTimer();
    }
}

package com.dingning.cardzs.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dingning.cardzs.R;


/**
 * by Allen
 */
public class MediaController extends FrameLayout implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private MediaControlImpl mMediaControl;

    private ImageView mPlayImg;//播放按钮
    private SeekBar mProgressSeekBar;//播放进度条
    private TextView mTvPlayTime, mTvAllTime;//播放时间
    private LinearLayout mSk;


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
        mMediaControl.onProgress(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mMediaControl.onTrackingTouch(seekBar, true, -1);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mMediaControl.onTrackingTouch(seekBar, false, System.currentTimeMillis());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_play_status) {
            mMediaControl.onPlayTurn();
        }
    }

    public void setProgressBar(int progress, int max) {
        if (progress < 0) progress = 0;
        mProgressSeekBar.setProgress(progress);
        mProgressSeekBar.setMax(max);
    }

    public void setPlayState(PlayState playState) {
        mPlayImg.setImageResource(playState.equals(PlayState.PLAY) ? R.drawable.video_pause : R.drawable.video_play);
    }


    public void setTimeTxt(int nowSecond, int allSecond) {
        setPlayProgressTxt(nowSecond);
        setAllProgressTxt(allSecond);
    }

    public void setPlayProgressTxt(int playProgressTxt) {
        mTvPlayTime.setText(getPlaySecond(playProgressTxt));
    }

    public void setAllProgressTxt(int allSecond) {
        mTvAllTime.setText(getPlaySecond(allSecond));
    }

    public void playFinish(int allTime) {
        mProgressSeekBar.setProgress(0);
        setTimeTxt(0, allTime);
        setPlayState(PlayState.PAUSE);
    }

    public void setMediaControl(MediaControlImpl mediaControl) {
        mMediaControl = mediaControl;
    }

    public MediaController(Context context) {
        super(context);
        initView(context);
    }

    public MediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public MediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.portrait_video_media_controller, this);


        mPlayImg = (ImageView) findViewById(R.id.iv_play_status);
        mProgressSeekBar = (SeekBar) findViewById(R.id.sk);
        mTvPlayTime = (TextView) findViewById(R.id.play_time);
        mTvAllTime = (TextView) findViewById(R.id.all_time);

        initData();
    }

    private void initData() {
        mPlayImg.setOnClickListener(this);
        mProgressSeekBar.setOnSeekBarChangeListener(this);

        setPlayState(PlayState.PAUSE);
    }

    private String generateTime(int time) {
        int seconds = time % 60;
        int minutes = time / 60;
        int hours = time / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    private String getPlaySecond(int playSecond) {
        String playSecondStr = "00:00";
        if (playSecond > 0) {
            playSecondStr = generateTime(playSecond);
        }
        return playSecondStr;
    }

    private String getAllSecond(int allSecond) {

        String allSecondStr = "00:00";
        if (allSecond > 0) {
            allSecondStr = generateTime(allSecond);
        }
        return allSecondStr;
    }

    /**
     * 播放状态 播放 暂停
     */
    public enum PlayState {
        PLAY, PAUSE
    }

    public enum ProgressState {
        START, DOING, STOP
    }


    public interface MediaControlImpl {
        void onPlayTurn();

        void onProgress(int progress);

        void onTrackingTouch(SeekBar seekBar, boolean startSeek, long currentTime);

    }

}

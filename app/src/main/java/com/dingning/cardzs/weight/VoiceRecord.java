package com.dingning.cardzs.weight;

import android.content.Context;
import android.os.Looper;

import com.czt.mp3recorder.MP3Recorder;
import com.czt.mp3recorder.OnRecordListener;
import com.dingning.cardzs.listener.RecorderInterface;
import com.dingning.cardzs.utils.VideoFile;

import java.io.File;
import java.io.IOException;

/**
 * Created by Allen on 2016/12/23.
 */

public class VoiceRecord implements OnRecordListener{

    private VideoFile videoFile;
    private File mp3File;
    private RecorderInterface listener;
    private Context context;

    private long startTime;
    private int time;
    private long endTime;
    private MP3Recorder recorder;

    public VoiceRecord(Context context , RecorderInterface listener) {

        this.context = context;
        this.listener = listener;
        initialize();
    }

    private void initialize() {

        recorder = new MP3Recorder(this);
    }

    public void startRecording() {
        try {
            startTime = System.currentTimeMillis();
            videoFile = new VideoFile(context, "", VideoFile.VOICE);
            mp3File = new File(videoFile.getFullPath());
            recorder.start(mp3File);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void stopRecording(){
        recorder.stop();
        endTime = System.currentTimeMillis();
        time = (int)Math.ceil((endTime - startTime) /1000);

    }

    public void release(){

        if(null != recorder){
            recorder.release();
        }
    }

    @Override
    public void onStartRecord() {
        listener.onRecordingStarted();
    }

    @Override
    public void onStopRecord() {
        listener.onRecordingStopped(videoFile, time);
    }
}

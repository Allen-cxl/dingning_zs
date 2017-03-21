package com.dingning.cardzs.weight;


import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dingning.cardzs.listener.RecorderInterface;
import com.dingning.cardzs.utils.VideoFile;

import java.io.IOException;

public class CameraRecord {

    private static final String TAG ="CameraRecord";
    private Context context;
    private ViewGroup view;
    private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mediaRecorder;
    private VideoFile videoFile;
    private String fileName;

    private RecorderInterface listener;
    private int quality = CamcorderProfile.QUALITY_480P;


    public  CameraRecord(Context context, String fileName, ViewGroup view, RecorderInterface listener) {

        this.context = context;
        this.view = view;
        this.fileName = fileName;
        this.listener = listener;
        initialize();
    }

    private void initialize() {

        mPreview = new CameraPreview(context, mCamera);

        view.addView(mPreview);
    }

    public void openCamera() {

        if (mCamera == null) {

            releaseCamera();

            // if the front facing camera does not exist
            int cameraId = findBackFacingCamera();

            mCamera = Camera.open(cameraId);
            mPreview.refreshCamera(mCamera);
            reloadQualities(cameraId);
        }
    }

    public void startRecord() {

        videoFile = new VideoFile(context, fileName, VideoFile.VIDEO);

        if (!prepareMediaRecorder()) {
            Toast.makeText(context, "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
            return;
        }
        // work on UiThread for better performance
        mediaRecorder.start();
    }

    public void stopRecord() {

        try{
            mediaRecorder.setOnErrorListener(null);
            mediaRecorder.setOnInfoListener(null);
            mediaRecorder.setPreviewDisplay(null);
          //  mediaRecorder.stop(); // stop the recording
        }catch(RuntimeException stopException){
            //handle cleanup here
        }

        releaseMediaRecorder(); // release the MediaRecorder object
        listener.onRecordingStopped(videoFile, 0);
    }

    private boolean prepareMediaRecorder() {

        mediaRecorder = new MediaRecorder();

        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

       /* if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (cameraFront) {
                mediaRecorder.setOrientationHint(270);
            } else {
                mediaRecorder.setOrientationHint(90);
            }
        }*/

        mediaRecorder.setProfile(CamcorderProfile.get(quality));

        mediaRecorder.setOutputFile(videoFile.getFullPath());
//        mediaRecorder.setMaxDuration(CameraConfig.MAX_DURATION_RECORD); // Set max duration 60 sec.
//        mediaRecorder.setMaxFileSize(CameraConfig.MAX_FILE_SIZE_RECORD); // Set max file size 50M

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.i(TAG, "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
            return false;
        } catch (IOException e) {
            Log.i(TAG, "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
            return false;
        }
        return true;

    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
          //  mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock(); // lock camera for later use
        }

    }


    public void releaseAllResources() {
        releaseMediaRecorder();
        releaseCamera();
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the back facing camera
        // get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        // for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    private void reloadQualities(int idCamera) {

        int maxQualitySupported = CamcorderProfile.QUALITY_480P;

        if (CamcorderProfile.hasProfile(idCamera, CamcorderProfile.QUALITY_480P)) {

            maxQualitySupported = CamcorderProfile.QUALITY_480P;
        }
        if (CamcorderProfile.hasProfile(idCamera, CamcorderProfile.QUALITY_720P)) {

            maxQualitySupported = CamcorderProfile.QUALITY_720P;
        }
        if (CamcorderProfile.hasProfile(idCamera, CamcorderProfile.QUALITY_1080P)) {

            maxQualitySupported = CamcorderProfile.QUALITY_1080P;
        }
        if (CamcorderProfile.hasProfile(idCamera, CamcorderProfile.QUALITY_2160P)) {

            maxQualitySupported = CamcorderProfile.QUALITY_2160P;
        }

        if (!CamcorderProfile.hasProfile(idCamera, quality)) {
            quality = maxQualitySupported;
        }

    }

}

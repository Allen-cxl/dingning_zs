package com.dingning.cardzs.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Locale;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import android_serialport_api.SerialPort;

public class ScanService extends Service {

    private static final String TAG = "ScanService";

    public static final int RECEIVE_DATA = 0010;

    private SerialPort mSerialPort;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private Intent sendIntent;
    private StringBuffer sb = new StringBuffer();

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RECEIVE_DATA:
                    String data = (String) msg.obj;
                    sendIntent = new Intent(
                            "android.intent.action.hal.barcodescanner.scandata");
                    sendIntent.putExtra("scanData", data);
                    sendBroadcast(sendIntent);
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String type = intent.getStringExtra("type");
        if ("on".equals(type)) {
            if (mSerialPort == null) {
                try {

                    String portname = "/dev/ttyS1";
                    int Baudrate = 57600;
                    if ((portname.length() == 0) || (Baudrate == -1)) {
                        throw new InvalidParameterException();
                    }
                    mSerialPort = new SerialPort(new File(portname), Baudrate,0);
                    openSerial();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else if ("off".equals(type)) {
            closeSerial();

        }

        return START_REDELIVER_INTENT;
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    byte[] buffer = new byte[150];
                    if (mInputStream == null)
                        return;
                    int size = mInputStream.read(buffer);
                    if (size <= 0)
                        continue;
                    String str1 = bytesToHex(buffer,size).trim();
                    Log.i("serial_port_pull",str1);
                    if (("04".equalsIgnoreCase(str1)))
                        continue;

                    if(!("04".equalsIgnoreCase(str1))){
                        sb.append(str1);
                    }

                    if(sb.length() == 8){
                        Message localMessage = mHandler.obtainMessage();
                        localMessage.what = RECEIVE_DATA;
                        localMessage.obj = sb.toString();
                        mHandler.sendMessage(localMessage);
                        sb.setLength(0);
                    }

                    Thread.sleep(50);//延时50ms
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void openSerial() {
        mInputStream = mSerialPort.getInputStream();
        mReadThread = new ReadThread();
        mReadThread.start();
    }

    private void closeSerial() {
        if (mReadThread != null)
            mReadThread.interrupt();
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }

    public String bytesToHex(byte[] buffer, int size) {
        String output = "";
        for (int i = 0; i < size; i++) {
            output += String.format("%02x", new Object[] { buffer[i] })
                    .toUpperCase(Locale.getDefault());
        }
        return output;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeSerial();
        Log.i("serial_port_close"," close");
    }

}

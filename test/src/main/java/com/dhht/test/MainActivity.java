package com.dhht.test;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.serialport.SerialPort;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import dhht.android.serialportutil.SerialPortBuilder;
import dhht.android.serialportutil.SerialPortService;

public class MainActivity extends AppCompatActivity {

    private SerialPort mSerialPort;
    private InputStream mInStream = null;
    private OutputStream mOutStream = null;

    private SerialPortService serialPortService;

    String DEVICE_GATE = "/dev/ttyS4";
    int BAUDRATE_GATE = 9600;

    String DEVICE_IDCARD = "/dev/ttyUSB6";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        try {
            SerialPortService serialPortService = (new SerialPortBuilder()).setTimeOut(100L).setBaudrate(115200).setDevicePath(DEVICE_IDCARD).createService();
//            Log.d("TEST", "isSuccess==============" + isSuccesss);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        init();

//        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
////
////        int width = wm.getDefaultDisplay().getWidth();
////
////        int height = wm.getDefaultDisplay().getHeight();
//
//        Point point = new Point();
//
//        wm.getDefaultDisplay().getSize(point);
//
//        int width = point.x;
//
//        int height = point.y;
//
//
//
//        Log.d("TEST", "width==========" + width);
//        Log.d("TEST", "height==========" + height);
    }


    public void init() {
//        try {
//            if (mInStream != null && mOutStream != null && mSerialPort != null) {
//                return;
//            }
//            mSerialPort = new SerialPort(new File(DEVICE_GATE), BAUDRATE_GATE);
//            mInStream = mSerialPort.getInputStream();
//            mOutStream = mSerialPort.getOutputStream();
//
//            serialPortService = new SerialPortBuilder()
//                    .setTimeOut(100L)
//                    .setBaudrate(BAUDRATE_GATE)
//                    .setDevicePath(DEVICE_GATE)
//                    .createService();
//            serialPortService.isOutputLog(true);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


}

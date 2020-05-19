/*
 * Copyright 2009 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http: /**www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.serialport;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class SerialPort {
    private static final String TAG = "=====SerialPort";
    private static SerialPort sInstance;

    public static SerialPort getInstance() {
        synchronized (SerialPort.class) {
            if (sInstance == null) {
                sInstance = new SerialPort();
            }
            return sInstance;
        }
    }

    static {
        System.loadLibrary("serial_port");
    }

    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;


//    public SerialPort(Context context, File device, int baudrate) throws SecurityException, IOException {
//        if (!device.canRead() || !device.canWrite()) {
//            Toast.makeText(context, "开启身份证串口失败", Toast.LENGTH_SHORT).show();
////            try {
////                Process su;
////                su = Runtime.getRuntime().exec("su");
////                String cmd = "chmod 777 " + device.getAbsolutePath();
////                su.getOutputStream().write(cmd.getBytes());
////                su.getOutputStream().flush();
////                int waitFor = su.waitFor();
////                boolean canRead = device.canRead();
////                boolean canWrite = device.canWrite();
////                if (waitFor != 0 || !canRead || !canWrite) {
////                    throw new SecurityException();
////                }
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
//        } else {
//            mFd = open(device.getAbsolutePath(), baudrate);
//            if (mFd == null) {
//                Log.d("serial", "串口开启失败------");
//                throw new IOException();
//            }
//            //LogUtil.d(TAG,"串口开启成功");
//            mFileInputStream = new FileInputStream(mFd);
//            mFileOutputStream = new FileOutputStream(mFd);
//        }
//    }

    public boolean initSerialPort(File device, int baudrate) throws SecurityException, IOException {
        if (!device.canRead() || !device.canWrite()) {
            return false;
        } else {
            mFd = open(device.getAbsolutePath(), baudrate);
            if (mFd == null) {
                Log.d("serial", "串口开启失败------");
                throw new IOException();
            }
            //LogUtil.d(TAG,"串口开启成功");
            mFileInputStream = new FileInputStream(mFd);
            mFileOutputStream = new FileOutputStream(mFd);
            return true;
        }
    }

    /**
     * JNI
     */
    private native static FileDescriptor open(String path, int baudrate);

    /**
     * 关闭串口
     */
    public void closePort() {
        if (this.mFd != null) {
            try {
                this.close();
                this.mFd = null;
                this.mFileInputStream = null;
                this.mFileOutputStream = null;
                //LogUtil.d(TAG,"串口关闭成功");
            } catch (Exception e) {
                Log.d("serial", "串口关闭异常------" + e.getMessage());
            }
        }
    }

    public InputStream getInputStream() {
        return mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    public native void close();
}

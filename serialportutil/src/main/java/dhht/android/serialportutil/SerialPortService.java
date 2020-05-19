package dhht.android.serialportutil;

import android.content.Context;
import android.os.SystemClock;
import android.serialport.SerialPort;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * @author HanPei
 * @date 2018/12/26  下午2:42
 */
public class SerialPortService implements ISerialPortService {

    private static SerialPortService sInstance;

    /**
     * 尝试读取数据间隔时间
     */
    private static int RE_READ_WAITE_TIME = 10;

    /**
     * 读取返回结果超时时间
     */
    private Long mTimeOut = 100L;
    /**
     * 串口地址
     */
    private String mDevicePath;

    /**
     * 波特率
     */
    private int mBaudrate;

    private SerialPort mSerialPort;
    private InputStream mInputStream;

    public InputStream getInputStream() {
        return mInputStream;
    }

    public void setInputStream(InputStream inputStream) {
        mInputStream = inputStream;
    }

    public OutputStream getOutputStream() {
        return mOutputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        mOutputStream = outputStream;
    }

    private OutputStream mOutputStream;

    public static SerialPortService getInstance() {
        synchronized (SerialPortService.class) {
            if (sInstance == null) {
                sInstance = new SerialPortService();
            }
            return sInstance;
        }
    }

    /**
     * 初始化串口
     *
     * @param devicePath 串口地址
     * @param baudrate   波特率
     * @param timeOut    数据返回超时时间
     * @throws IOException 打开串口出错
     */
    public boolean initSerialPortService(String devicePath, int baudrate, Long timeOut) throws IOException {
        mTimeOut = timeOut;
        mDevicePath = devicePath;
        mBaudrate = baudrate;
        File file = new File(mDevicePath);
//        mSerialPort = new SerialPort(context,file, mBaudrate);
        mSerialPort = SerialPort.getInstance();
        boolean isSuccess = mSerialPort.initSerialPort( file, mBaudrate);
        if (!isSuccess) {
            return false;
        }
        mInputStream = mSerialPort.getInputStream();
        mOutputStream = mSerialPort.getOutputStream();
        return true;
    }

    @Override
    public byte[] sendData(byte[] data) {
        synchronized (SerialPortService.this) {
            try {
                if (mInputStream == null) {
                    return null;
                }
                int available = mInputStream.available();
                byte[] returnData;
                if (available > 0) {
                    returnData = new byte[available];
                    mInputStream.read(returnData);
                }
                mOutputStream.write(data);
                mOutputStream.flush();
//                LogUtil.e("发送数据-------" + Arrays.toString(data));
                Long time = System.currentTimeMillis();
                //暂存每次返回数据长度，不变的时候为读取完数据
                int receiveLeanth = 0;
                while (System.currentTimeMillis() - time < mTimeOut) {
                    available = mInputStream.available();
                    if (available > 0 && available == receiveLeanth) {
                        //由于有些数据传输中途会停顿，这里也停顿一会，确保数据接受完整
                        Thread.sleep(50);
                        available = mInputStream.available();
                        if (available == receiveLeanth) {
                            returnData = new byte[available];
                            mInputStream.read(returnData);
//                            LogUtil.e("接收--数据-------" + Arrays.toString(returnData));
                            return returnData;
                        } else {
                            receiveLeanth = available;
                        }
                    } else {
                        receiveLeanth = available;
                    }
                    SystemClock.sleep(RE_READ_WAITE_TIME);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public byte[] sendData(String date) {
        try {
            return sendData(ByteStringUtil.hexStrToByteArray(date));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * q清除串口多余数据并发送数据
     *
     * @param data
     */
    public synchronized void clearInputBufferAndSendData(byte[] data) {
        try {
            if (mInputStream.available() > 0) {
                mInputStream.skip(mInputStream.available());
            }
            mOutputStream.write(data);
            mOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取串口数据
     *
     * @param length  指定读取数据长度
     * @param timeout 指定超时时间
     * @return
     */
    public synchronized byte[] readData(int length, int timeout) throws Exception {
        byte[] data = new byte[length];
        long startTime = System.currentTimeMillis();
        while (mInputStream.available() < length) {
            if ((System.currentTimeMillis() - startTime) > timeout) {
                throw new Exception("读取超时");
            }
            Thread.sleep(20);
        }
        mInputStream.read(data, 0, length);
        return data;
    }

    /**
     * 发送数据+接收数据
     *
     * @param data
     * @param length
     * @param timeout
     * @return
     */
    public synchronized byte[] sendDataAndReadResponse(byte[] data, int length, int timeout) throws Exception {
        clearInputBufferAndSendData(data);
        return readData(length, timeout);
    }

    public byte[] readData(byte delimiter, int maxLen, int timeout) throws Exception {
        byte[] data = new byte[maxLen];
        int readedLen = 0;
        long startTime = System.currentTimeMillis();
        while (true) {
            if (mSerialPort.getInputStream().available() <= 0) {
                Thread.sleep(20);
            }
            if ((System.currentTimeMillis() - startTime) > timeout) {
                throw new Exception("读取超时");
            }

            mSerialPort.getInputStream().read(data, readedLen, 1);
            if (delimiter == data[readedLen]) {
                break;
            }

            readedLen++;

            if (readedLen == maxLen) {
                throw new Exception("超过限定长度");
            }
        }
        return data;
    }

    @Override
    public void close() {
        if (mSerialPort != null) {
            mSerialPort.closePort();
        }
        mInputStream = null;
        mOutputStream = null;
    }


    public void isOutputLog(boolean debug) {
        LogUtil.isDebug = debug;
    }


}

package com.frizzle.bigeye.face;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.frizzle.bigeye.util.CameraHelper;

/**
 * author: LWJ
 * date: 2020/8/10$
 * description
 */
public class OpenCVJni {
    static {
        System.loadLibrary("native-lib");
    }
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private static final int CHECK_FACE = 1;

    private long self;
    private final CameraHelper mCameraHelper;

    private Face mFace;

    public OpenCVJni(String path,String seeta,CameraHelper cameraHelper) {
        self = init(path,seeta);
        mCameraHelper = cameraHelper;
        mHandlerThread = new HandlerThread("track");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message message){
                //运行在子线程
                mFace = native_detector(self, (byte[]) message.obj, mCameraHelper.getCameraId(), CameraHelper.WIDTH, CameraHelper.HEIGHT);
            }
        };
    }

    public Face getFace() {
        return mFace;
    }

    public void startTrack() {
        native_satrt(self);
    }

    //检测人脸 耗时操作 开启子线程 这里使用HandlerThread多次开启子线程
    public void detector(byte[] data) {
        //先移除 为了避免摄像头数据获取很快,检测的数据还没有检测完毕,防止检测旧的数据
        mHandler.removeMessages(CHECK_FACE);
        Message message = mHandler.obtainMessage(CHECK_FACE);
        message.obj = data;
        mHandler.sendMessage(message);
    }

    //初始化检测器追踪器
    private native long init(String path,String seeta);

    //开启检测器追踪器
    private native void native_satrt(long self) ;

    //检测人脸
    private native Face native_detector(long self, byte[] data, int cameraId, int width, int height);
}

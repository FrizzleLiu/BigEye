package com.frizzle.bigeye.util;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * @author yxc
 * @date 2018/11/2
 */
public class CameraHelper implements Camera.PreviewCallback {

    private static final String TAG = "CameraHelper";
    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;

    private int mCameraId;
    private Camera mCamera;
    private byte[] buffer;
    private Camera.PreviewCallback mPreviewCallback;
    SurfaceHolder mSurfaceHolder;
    private SurfaceTexture mSurfaceTexture;

    public CameraHelper(int cameraId){
        mCameraId = cameraId;
    }

    public void setPreviewDisplay(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;
    }

    public void switchCamera(){
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
            mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }else{
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        stopPreview();
        startPreview(mSurfaceTexture);
    }

    public int getCameraId(){
        return mCameraId;
    }

    public void startPreview(SurfaceTexture surfaceTexture) {
        mSurfaceTexture = surfaceTexture;
        try {
            mCamera = Camera.open(mCameraId);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewFormat(ImageFormat.NV21);
            parameters.setPreviewSize(WIDTH, HEIGHT);

            mCamera.setParameters(parameters);
            buffer = new byte[WIDTH * HEIGHT *3/2];

            mCamera.addCallbackBuffer(buffer);
            mCamera.setPreviewCallbackWithBuffer(this);

            mCamera.setPreviewTexture(surfaceTexture);

//            mCamera.setPreviewDisplay(mSurfaceHolder);

            mCamera.startPreview();
        } catch (IOException e) {
            Log.e(TAG, "start preview failed");
            e.printStackTrace();
        }
    }

    public void stopPreview() {
        if (mCamera != null){
            //预览数据回调接口
            mCamera.setPreviewCallback(null);
            //停止预览
            mCamera.stopPreview();
            //释放镜头
            mCamera.release();
            mCamera = null;
        }
    }

    public void setPreviewCallback(Camera.PreviewCallback callback){
        mPreviewCallback = callback;
    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        mPreviewCallback.onPreviewFrame(data, camera);
        camera.addCallbackBuffer(buffer);
    }

}
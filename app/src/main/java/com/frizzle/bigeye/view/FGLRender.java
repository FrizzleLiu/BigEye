package com.frizzle.bigeye.view;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Environment;

import com.frizzle.bigeye.face.OpenCVJni;
import com.frizzle.bigeye.filter.BigEyeFilter;
import com.frizzle.bigeye.filter.CameraFilter;
import com.frizzle.bigeye.filter.ScreenFilter;
import com.frizzle.bigeye.util.CameraHelper;
import com.frizzle.bigeye.util.Utils;

import java.io.File;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: LWJ
 * date: 2020/7/29$
 * description
 */
public class FGLRender implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener, Camera.PreviewCallback {
    private FGLView mView;
    private CameraHelper mCameraHelper;
    private SurfaceTexture mSurfaceTexture;
    private float[] mtx = new float[16];//获取摄像头矩阵所需的四个顶点
    private int[] mTextures;
    private CameraFilter mCameraFilter;
    private ScreenFilter mScreenFilter;
    private BigEyeFilter mBigEyeFilter;
    private OpenCVJni openCVJni;
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private File lbpcascade_frontalface= new File(new File(Environment.getExternalStorageDirectory(), "lbpcascade_frontalface.xml").getAbsolutePath());
    private File seeta_fa_v1 = new File(new File(Environment.getExternalStorageDirectory(), "seeta_fa_v1.1.bin").getAbsolutePath());
    public FGLRender(FGLView view) {
        mView=view;
        init();
    }

    private void init() {
        Utils.copyAssets(mView.getContext(),"lbpcascade_frontalface.xml",lbpcascade_frontalface.getAbsolutePath());
        Utils.copyAssets(mView.getContext(),"seeta_fa_v1.1.bin", seeta_fa_v1.getAbsolutePath());
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        mCameraHelper = new CameraHelper(cameraId);
        mTextures = new int[1];
        //GPU创建纹理
        GLES20.glGenTextures(mTextures.length,mTextures,0);
        //SurfaceTexture相当于与摄像头绑定,用于采集摄像头数据
        //纹理Id与SurfaceTexture绑定
        mSurfaceTexture = new SurfaceTexture(mTextures[0]);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        //获取摄像头的矩阵->可以让摄像头的数据不会变形
        mSurfaceTexture.getTransformMatrix(mtx);
        mCameraFilter = new CameraFilter(mView.getContext());
        mScreenFilter = new ScreenFilter(mView.getContext());
        mBigEyeFilter = new BigEyeFilter(mView.getContext());
        mCameraFilter.setMatrix(mtx);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        mCameraHelper.startPreview(mSurfaceTexture);
        mCameraHelper.setPreviewCallback(this);
        mCameraFilter.onReady(width,height);
        mScreenFilter.onReady(width,height);
        mBigEyeFilter.onReady(width,height);
        openCVJni = new OpenCVJni(lbpcascade_frontalface.getAbsolutePath(), seeta_fa_v1.getAbsolutePath(),mCameraHelper);
        openCVJni.startTrack();
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        //获取一帧数据会回调该方法
        //先清空GPU中的数据
        //清空为什么颜色
        GLES20.glClearColor(0,0,0,0);
        //执行清空
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //获取摄像头数据
        mSurfaceTexture.updateTexImage();
        mSurfaceTexture.getTransformMatrix(mtx);
        mCameraFilter.setMatrix(mtx);
        //不同滤镜效果可叠加,使用责任链模式
        //获取上个责任链的纹理Id
        int id = mCameraFilter.onDrawFrame(mTextures[0]);
        //..责任链模式可以在这加各种滤镜,最终交给mScreenFilter去做显示(运用到SurfaceView中)
        mBigEyeFilter.setFace(openCVJni.getFace());
        id= mBigEyeFilter.onDrawFrame(id);
        mScreenFilter.onDrawFrame(id);

    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mView.requestRender();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (openCVJni!=null){
            openCVJni.detector(data);
        }
    }
}

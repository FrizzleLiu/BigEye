package com.frizzle.bigeye.filter;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.frizzle.bigeye.R;
import com.frizzle.bigeye.util.OpenGLUtils;

/**
 * author: LWJ
 * date: 2020/8/6$
 * description摄像头滤镜 主要用于获取摄像头数据 创建FBO 在FBO添加特效
 */
public class CameraFilter extends AbstractFilter {
    //FBO
    int[] mFramBuffer;
    private int[] mFramBufferTextures;
    private float[] mMatrix;

    public CameraFilter(Context context) {
        super(context, R.raw.camera_vertex, R.raw.camera_frag);
    }

    @Override
    protected void initCoordinate() {
        //坐标转换
        mTexttureBuffer.clear();
        //正常的坐标系四个顶点的坐标
//        float[] TEXTURE={
//                0.0f,0.0f,
//                1.0f,0.0f,
//                0.0f,1.0f,
//                1.0f,1.0f
//        };
        //由于摄像头采集的数据是颠倒的,需要先旋转90°,再做镜像处理
        float[] TEXTURE={
                1.0f, 1.0f,//右下
                1.0f, 0.0f,// 左下
                0.0f, 1.0f,// 右上
                0.0f, 0.0f,// 左上
        };
        mTexttureBuffer.put(TEXTURE);

    }

    public void onReady(int width,int height){
        super.onReady(width,height);
        //创建FBO(缓冲区),需要转换成纹理进行后续操作
        mFramBuffer=new int[1];
        GLES20.glGenFramebuffers(1,mFramBuffer,0);
        //实例化一个纹理 和FBO绑定
        mFramBufferTextures = new int[1];
        OpenGLUtils.glGenTexture(mFramBufferTextures);
        //绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,mFramBufferTextures[0]);
        //绑定FBO
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,mFramBuffer[0]);
        //设置纹理显示的纤度,宽度,和高度
        GLES20. glTexImage2D(GLES20.GL_TEXTURE_2D, 0,GLES20.GL_RGBA, mWidth,mHeight,0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,null);
        //纹理和FBO联系
        GLES20. glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D,
                mFramBufferTextures[0], 0) ;
        //解绑
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);
        //解绑
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
    }

    //重写父类该方法的原因
    //1 CameraFilter与摄像头交互应该使用GLES11Ext的Sample采样器 不是GL_TEXTURE_2D采样器
    //2 返回值应该是FBO中的纹理Id 而不是原始的纹理Id 如果返回原始的纹理id相当于该滤镜什么都没做直接把摄像头数据丢给了下个滤镜
    //3 不能将数据直接显示在SurfaceView中,而是渲染到FBO中(缓冲区,不可见)
    //4 变换矩阵,激活图层,解绑
    @Override
    public int onDrawFrame(int textureId) {
        //设置显示窗口
        GLES20.glViewport(0,0,mWidth,mHeight);
        //告诉GPU将数据渲染到FBO中,不调用默认GL_SurfaceView
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,mFramBuffer[0]);
        //使用着色器
        GLES20.glUseProgram(mProgram);
        //置为初始状态
        mVertexBuffer.position(0);
        //给vPosition赋值
        GLES20.glVertexAttribPointer(vPosition,2,GLES20.GL_FLOAT,false,0,mVertexBuffer);
        //激活可用状态
        GLES20.glEnableVertexAttribArray(vPosition);

        //将mTexttureBuffer的值赋值给OpenGL的vCoord
        mTexttureBuffer.position(0);
        //给vPosition赋值
        GLES20.glVertexAttribPointer(vCoord,2,GLES20.GL_FLOAT,false,0,mTexttureBuffer);
        GLES20.glEnableVertexAttribArray(vCoord);

        //变换矩阵
        GLES20.glUniformMatrix4fv(vMatrix,1,false,mMatrix,0);
        //激活图层
        GLES20.glActiveTexture(GLES20. GL_TEXTURE) ;

        //将GPU的片元着色器的采样器与Java层的SurfaceTexture绑定
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,textureId);

        //获取vTextture
        GLES20.glUniform1i(vTextture,0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        //解绑
        GLES20. glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0) ;
        GLES20. glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0) ;

        return mFramBufferTextures[0];
    }

    public void setMatrix(float[] mtx) {
        mMatrix = mtx;
    }
}

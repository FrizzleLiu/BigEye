package com.frizzle.bigeye.filter;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.frizzle.bigeye.R;

import javax.microedition.khronos.opengles.GL;

/**
 * author: LWJ
 * date: 2020/8/18$
 * description
 * 人脸美颜滤镜
 */
public class BeautyFilter extends AbstractFrameFilter{
    private int width;
    private int height;
    public BeautyFilter(Context context) {
        super(context, R.raw.base_vertex, R.raw.beauty_frag);
        width = GLES20.glGetUniformLocation(mProgram,"width");
        height = GLES20.glGetUniformLocation(mProgram,"height");
    }

    @Override
    protected void initCoordinate() {
        //坐标转换
        mTexttureBuffer.clear();
        //正常的坐标系四个顶点的坐标 CameraFilter中已经处理过了,不做调整
        float[] TEXTURE={
                0.0f,0.0f,
                1.0f,0.0f,
                0.0f,1.0f,
                1.0f,1.0f
        };
//        //由于摄像头采集的数据是颠倒的,调整处理
//        float[] TEXTURE={
//                1.0f, 1.0f,//右下
//                1.0f, 0.0f,// 左下
//                0.0f, 1.0f,// 右上
//                0.0f, 0.0f,// 左上
//        };
        mTexttureBuffer.put(TEXTURE);
    }

    @Override
    public int onDrawFrame(int textureId) {
        //设置显示窗口
        GLES20.glViewport(0,0,mWidth,mHeight);
        //告诉GPU将数据渲染到FBO中,不调用默认GL_SurfaceView
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,mFramBuffer[0]);
        //使用着色器
        GLES20.glUseProgram(mProgram);
        //赋值片元着色器中的 width和height
        GLES20.glUniform1i(width,mWidth);
        GLES20.glUniform1i(height,mHeight);
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
        //激活图层
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0) ;

        //将GPU的片元着色器的采样器与Java层的SurfaceTexture绑定
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId);

        //获取vTextture
        GLES20.glUniform1i(vTextture,0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        //解绑
        GLES20. glBindTexture(GLES20.GL_TEXTURE_2D, 0) ;
        GLES20. glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0) ;
        return mFramBufferTextures[0];
    }
}

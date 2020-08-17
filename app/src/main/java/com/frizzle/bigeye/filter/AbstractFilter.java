package com.frizzle.bigeye.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.frizzle.bigeye.util.OpenGLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * author: LWJ
 * date: 2020/8/4$
 * description基础滤镜
 */
public abstract class AbstractFilter {
    //顶点着色器
    protected int mVertexShaderId;
    //片元着色器
    protected int mFragmentShaderId;

    protected FloatBuffer mTexttureBuffer;
    protected FloatBuffer mVertexBuffer;
    //纹理
    protected int vTextture;
    //矩阵
    protected int vMatrix;
    //片元着色器的接收矩阵
    protected int vCoord;
    protected int vPosition;
    protected int mProgram;
    protected int mWidth;
    protected int mHeight;

    public void onReady(int width,int height){
        mWidth = width;
        mHeight = height;
    }

    public AbstractFilter(Context context, int mVertexShaderId, int mFragmentShaderId) {
        this.mVertexShaderId = mVertexShaderId;
        this.mFragmentShaderId = mFragmentShaderId;
        //摄像头获取的数据是二维数据,有四个顶点 Float类型占四个字节
        //顶点着色器的四个顶点
        mVertexBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexBuffer.clear();
        float[] VERTEX = {
                -1.0f, -1.0f,
                1.0f, -1.0f,
                -1.0f, 1.0f,
                1.0f, 1.0f
        };
        //Java中的坐标转换成OpenGL中的坐标
        mVertexBuffer.put(VERTEX);

        //片元着色器
        mTexttureBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTexttureBuffer.clear();
        float[] TEXTTURE = {
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f
        };
        mTexttureBuffer.put(TEXTTURE);
        initilize(context);
        initCoordinate();
    }

    /**
     * @param context
     * 初始化顶点着色器,片元着色器 Program等
     */
    private void initilize(Context context) {
        String vertexShader = OpenGLUtils.readRawTextFile(context, mVertexShaderId);
        String fragmentShader = OpenGLUtils.readRawTextFile(context, mFragmentShaderId);
        mProgram = OpenGLUtils.loadProgram(vertexShader,fragmentShader);
        //获取vPosition
        //获得着色器中的attribute. 变量position 的索引值
        //顶点着色器使用glGetAttribLocation 片元着色器使用glGetUniformLocation
        vPosition = GLES20.glGetAttribLocation(mProgram,"vPosition");
        vCoord = GLES20.glGetAttribLocation(mProgram,"vCoord");
        vMatrix = GLES20.glGetUniformLocation(mProgram,"vMatrix");
        vTextture = GLES20.glGetUniformLocation(mProgram,"vTextture");
    }


    /**
     * @param textureId
     * 渲染
     */
    public int onDrawFrame(int textureId) {
        //设置显示窗口
        GLES20.glViewport(0,0,mWidth,mHeight);
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

        //激活采样器
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //将GPU的片元着色器的采样器与Java层的SurfaceTexture绑定
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId);

        //获取vTextture
        GLES20.glUniform1i(vTextture,0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        return textureId;
    }

    //初始化额外顶点
    protected abstract void initCoordinate();
}

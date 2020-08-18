package com.frizzle.bigeye.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.frizzle.bigeye.R;
import com.frizzle.bigeye.face.Face;
import com.frizzle.bigeye.util.OpenGLUtils;

/**
 * author: LWJ
 * date: 2020/8/17$
 * description
 */
public class StickFilter extends AbstractFrameFilter {
    private Bitmap mBitmap;
    private int[] mTextureId;
    private Face mFace;


    public void setmFace(Face mFace) {
        this.mFace = mFace;
    }

    public StickFilter(Context context) {
        super(context, R.raw.base_vertex, R.raw.base_frag);
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ears);
    }

    @Override
    protected void initCoordinate() {

    }

    @Override
    public void onReady(int width, int height) {
        super.onReady(width, height);
        mTextureId = new int[1];
        //在GPU中开辟一块内存用于加载Bitmap(贴纸)
        OpenGLUtils.glGenTexture(mTextureId);
        //如果是gif可以考虑采用帧动画,将每一帧的bitmap与纹理id绑定,就是循环调用下面的几个api
        //纹理Id与bitmap绑定
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId[0]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
        //解绑纹理id
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    @Override
    public int onDrawFrame(int textureId) {
        if (mFace == null){
            return textureId;
        }
        //设置显示窗口
        GLES20.glViewport(0, 0, mWidth, mHeight);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,mFramBuffer[0]);
        //使用着色器
        GLES20.glUseProgram(mProgram);
        //置为初始状态
        mVertexBuffer.position(0);
        //给vPosition赋值
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        //激活可用状态
        GLES20.glEnableVertexAttribArray(vPosition);
        //激活采样器
        GLES20.glActiveTexture(GLES20.GL_TEXTURE);
        //将GPU的片元着色器的采样器与Java层的SurfaceTexture绑定
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        //获取vTextture
        GLES20.glUniform1i(vTextture, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        //解绑
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);
        //上一个滤镜绘制效果
        //将mTexttureBuffer的值赋值给OpenGL的vCoord
//        mTexttureBuffer.position(0);
//        //给vPosition赋值
//        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, mTexttureBuffer);
//        GLES20.glEnableVertexAttribArray(vCoord);
        onDrowStick();
        return mFramBufferTextures[0];

    }

    /**
     * 绘制贴纸Bitmap
     * 这里实现的最基础的功能
     * 可能会人脸在镜头中会不断变化,在不断地检测,贴纸可能存在"抖动"现象
     * 可以通过计算解决"抖动现象" 比如记录当前显示贴纸的坐标,下次计算的坐标在距离当前坐标xx范围内不改变位置
     */
    private void onDrowStick() {
        //开启混合模式 将图片进行混合
        GLES20.glEnable(GLES20.GL_BLEND);
        //类似于图层混合模式 这里选用的是全部,透明度也要带上的
        GLES20.glBlendFunc(GLES20.GL_ONE,GLES20.GL_ONE_MINUS_CONSTANT_ALPHA);
        //人脸的宽高
        float x = mFace.faceRects[0]/mFace.imgWidth*mWidth;
        float y = mFace.faceRects[1]/mFace.imgHeight*mHeight;
        int yResult = (int) (y -(float) mFace.height / mFace.imgHeight* mHeight);
        GLES20.glViewport((int) x,yResult,(int) ((float)mFace.width/mFace.imgWidth*mWidth),(int)((float)mFace.height/mFace.imgHeight*mHeight));
        //设置显示窗口
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,mFramBuffer[0]);
        //使用着色器
        GLES20.glUseProgram(mProgram);
        //置为初始状态
        mVertexBuffer.position(0);
        //给vPosition赋值
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        //激活可用状态
        GLES20.glEnableVertexAttribArray(vPosition);
        //激活采样器
        GLES20.glActiveTexture(GLES20.GL_TEXTURE);
        //将GPU的片元着色器的采样器与Java层的SurfaceTexture绑定
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId[0]);
        //获取vTextture
        GLES20.glUniform1i(vTextture, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        //解绑
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);
        GLES20.glDisable(GLES20.GL_BLEND);
    }
}

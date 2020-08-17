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
public abstract class AbstractFrameFilter extends AbstractFilter {
    //FBO
    int[] mFramBuffer;
    protected int[] mFramBufferTextures;

    public AbstractFrameFilter(Context context, int mVertexShaderId, int mFragmentShaderId) {
        super(context, mVertexShaderId, mFragmentShaderId);
    }

    public void onReady(int width, int height) {
        super.onReady(width, height);
        //创建FBO(缓冲区),需要转换成纹理进行后续操作
        mFramBuffer = new int[1];
        GLES20.glGenFramebuffers(1, mFramBuffer, 0);
        //实例化一个纹理 和FBO绑定
        mFramBufferTextures = new int[1];
        OpenGLUtils.glGenTexture(mFramBufferTextures);
        //绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFramBufferTextures[0]);
        //绑定FBO
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFramBuffer[0]);
        //设置纹理显示的纤度,宽度,和高度
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mWidth, mHeight, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        //纹理和FBO联系
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D,
                mFramBufferTextures[0], 0);
        //解绑
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        //解绑
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    //删除纹理
    private void destroyFrameBuffers() {
        // 删除fbo的纹理
        if (mFramBufferTextures != null) {
            GLES20.glDeleteTextures(1, mFramBufferTextures, 0);
            mFramBufferTextures = null;
        }
        //删除fbo
        if (mFramBuffer != null) {
            GLES20.glDeleteFramebuffers(1, mFramBuffer, 0);
            mFramBuffer = null;
        }
    }
}

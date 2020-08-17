package com.frizzle.bigeye.filter;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.frizzle.bigeye.R;
import com.frizzle.bigeye.face.Face;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * author: LWJ
 * date: 2020/8/14$
 * description
 */
public class BigEyeFilter extends AbstractFrameFilter{
    private int left_eye;
    private int right_eye;
    private FloatBuffer left;
    private FloatBuffer right;
    private Face mFace;

    public BigEyeFilter(Context context) {
        super(context,  R.raw.camera_vertex, R.raw.bigeye_frag);
        left_eye = GLES20.glGetUniformLocation(mProgram,"left_eye");
        right_eye = GLES20.glGetUniformLocation(mProgram,"right_eye");
        left = ByteBuffer.allocateDirect(2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        right = ByteBuffer.allocateDirect(2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

    }

    public void setFace(Face face){
        mFace = face;
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
        if (mFace == null){
            return textureId;
        }
        GLES20.glViewport(0,0,mWidth, mHeight) ;
        GLES20.glBindFramebuffer(GLES20. GL_FRAMEBUFFER, mFramBuffer[0]);
        GLES20.glUseProgram(mProgram) ;

        mVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(vPosition,2,GLES20.GL_FLOAT,false,0,mVertexBuffer);
        GLES20.glEnableVertexAttribArray(vPosition);

        mTexttureBuffer.position(0);
        GLES20.glVertexAttribPointer(vCoord,2,GLES20.GL_FLOAT,false,0,mTexttureBuffer);
        GLES20.glEnableVertexAttribArray(vCoord);

        float[] landmarks = mFace.faceRects;
        //左眼
        float x = landmarks[0]/mFace.imgWidth;
        float y = landmarks[1]/mFace.imgHeight;
        left.clear();
        left.put(x);
        left.put(y);
        left.position(0);
        GLES20.glUniform2fv(left_eye,1,left);
        //右眼
        x = landmarks[2]/mFace.imgWidth;
        y = landmarks[3]/mFace.imgHeight;
        right.clear();
        right.put(x);
        right.put(y);
        right.position(0);
        GLES20.glUniform2fv(right_eye,1,right);

        //激活图层
        GLES20.glActiveTexture(GLES20. GL_TEXTURE) ;

        //将GPU的片元着色器的采样器与Java层的SurfaceTexture绑定
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId);

        //获取vTextture
        GLES20.glUniform1i(vTextture,0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        //解绑
        GLES20. glBindTexture(GLES20.GL_TEXTURE_2D, 0) ;
        GLES20. glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0) ;
        //返回FBO ID
        return mFramBufferTextures[0];
    }
}

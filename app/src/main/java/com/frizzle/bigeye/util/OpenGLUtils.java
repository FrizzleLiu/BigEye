package com.frizzle.bigeye.util;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.microedition.khronos.opengles.GL10;

/**
 * author: LWJ
 * date: 2020/8/4$
 * description
 */
public class OpenGLUtils {
    public static String readRawTextFile(Context context, int rawId) {
        try {
            InputStream inputStream = context.getResources().openRawResource(rawId);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();
            String data = null;
            while ((data = br.readLine()) != null) {
                if (!data.contains("//")){
                    stringBuffer.append(data);
                    stringBuffer.append("\n");
                }
            }
            inputStream.close();
            br.close();
            return stringBuffer.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static int loadProgram(String vSource, String fSuorce) {
        int mProgram;
        //创建顶点着色器
        int vShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        //编译
        GLES20.glShaderSource(vShader,vSource);
        GLES20.glCompileShader(vShader);

        int[] status = new int[1];
        GLES20.glGetShaderiv(vShader,GLES20.GL_COMPILE_STATUS,status,0);
        if (status[0] != GLES20.GL_TRUE){
            //创建失败
            throw new IllegalStateException("load vertex shader :" + GLES20.glGetShaderInfoLog(vShader));
        }

        //创建片元着色器
        int fShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        //编译
        GLES20.glShaderSource(fShader,fSuorce);
        GLES20.glCompileShader(fShader);
        status = new int[1];
        GLES20.glGetShaderiv(fShader,GLES20.GL_COMPILE_STATUS,status,0);
        if (status[0] != GLES20.GL_TRUE){
            //创建失败
            throw new IllegalStateException("load fragment shader :" + GLES20.glGetShaderInfoLog(vShader));
        }

        //将顶点着色器和片元着色器交给统一程序管理
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram,vShader);
        GLES20.glAttachShader(mProgram,fShader);
        GLES20.glLinkProgram(mProgram);
        GLES20.glGetShaderiv(mProgram,GLES20.GL_COMPILE_STATUS,status,0);
        if (status[0] != GLES20.GL_TRUE){
            //创建失败
            throw new IllegalStateException("link program :" + GLES20.glGetShaderInfoLog(vShader));
        }
        GLES20.glDeleteShader(vShader);
        GLES20.glDeleteShader(fShader);
        return mProgram;
    }

    //生成纹理用于操作FBO
    public static void glGenTexture(int[] textures) {
        GLES20.glGenTextures(textures.length,textures,0);
        for (int i = 0; i < textures.length; i++) {
            //配置纹理
            //绑定纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textures[i]);
            //纹理缩放效果
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
            //设置纹理环绕方向 GL_TEXTURE_WRAP_S代表x方向,GL_TEXTURE_WRAP_T代表Y方向
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_REPEAT);
            //解绑纹理 只是通知GPU不再使用,并不会销毁
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
        }
    }
}

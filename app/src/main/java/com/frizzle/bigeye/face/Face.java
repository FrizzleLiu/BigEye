package com.frizzle.bigeye.face;

/**
 * author: LWJ
 * date: 2020/8/12$
 * description
 */
public class Face {
    public float[] faceRects;
    //人脸宽高
    public int width;
    public int height;

    //送去检测的图像宽高
    public int imgWidth;
    public int imgHeight;

    public Face(int width, int height, int imgWidth, int imgHeight,float[] faceRects) {
        this.faceRects = faceRects;
        this.width = width;
        this.height = height;
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
    }
}

#extension GL_OES_EGL_image_external : require

precision mediump float;
//采样点的坐标
varying vec2 aCoord;
//color输入
uniform samplerExternalOES vTexture;
void main() {
    //变量接收像素值
    //texture2D:采祥器采集aCoord的像素
    //赋值给gl_FragColor就可以了
    gl_FragColor = texture2D(vTexture, aCoord);
}


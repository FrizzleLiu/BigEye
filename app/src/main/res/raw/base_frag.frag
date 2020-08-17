//精度
precision mediump float;
//采样点的坐标
varying vec2 aCoord;
//采样器不是从android的surfaceTexure中的纹理采数据了，所以不再需要android的扩展纹理采样器了
//使用正常的sampler2D
uniform sampler2D vTexture;

void main(){
    //变量接收像素值
    //texture2D:采祥器采集aCoord的像素
    //赋值给gl_FragColor就可以了
    gl_FragColor = texture2D(vTexture, aCoord);
}


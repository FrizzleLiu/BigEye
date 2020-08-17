//把顶点坐标给这个变量确定要画画的形状
attribute vec4 vPosition;
// 接收纹理坐标， 接收采样器采样图片的坐标
//摄像头采集的数据是二维的 不用和矩阵相乘了，接收一个点只要2个float就可以了，所以是vec2
attribute vec2 vCoord;
//传给片元着色器像素点
varying vec2 aCoord;
void main() {
    //内置变量gL_Position，我们把顶点数据赋值给这个变量openg1就知道它要画什么形状了
    gl_Position = vPosition;
    //进过测试和设备有关(有些设备直接就采集不到图像，有些呢则会镜像)
    aCoord = vCoord;
}

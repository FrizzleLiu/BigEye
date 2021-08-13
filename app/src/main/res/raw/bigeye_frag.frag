//精度
precision mediump float;
//采样点的坐标
varying vec2 aCoord;
//左眼
uniform vec2 left_eye;
//右眼
uniform vec2 right_eye;
//采样器不是从android的surfaceTexure中的纹理采数据了，所以不再需要android的扩展纹理采样器了
//使用正常的sampler2D
uniform sampler2D vTexture;

//实现公式: 得出需要采集的改变后的点距离眼睛中心点的位置
float fs (float r, float rmax){
    //放大系数
    float a = 0.9;
    return (1.0-(r/rmax-1.0)*(r/rmax-1.0))*a;
}

vec2 newCoord(vec2 coord ,vec2 eye ,float rmax){
    vec2 p = coord;
    float r = distance (coord,eye);
    //改变顶点位置
    if(r<rmax)
    {
       float fsr = fs(r,rmax);
        //根据下面关系求p
        //(p-eye)/(coord-eye) = fsr/r;
        p = fsr*(coord - eye) + eye;
    }
    return p;
}

void main(){
    float rmax = distance(left_eye,right_eye)/4.0;
    //左边眼睛
    vec2 p = newCoord(aCoord,left_eye,rmax);
    //右边眼睛
    p = newCoord(p,right_eye,rmax);
    //变量接收像素值
    //texture2D:采祥器采集aCoord的像素
    //赋值给gl_FragColor就可以了
    gl_FragColor = texture2D(vTexture, p);
}


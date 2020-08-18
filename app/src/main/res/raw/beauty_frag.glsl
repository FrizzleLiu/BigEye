//精度
precision mediump float;
//采样点的坐标
varying vec2 aCoord;
//采样器不是从android的surfaceTexure中的纹理采数据了,所以不再需要android的扩展纹理采样器了
//使用正常的sampler2D
uniform mediump sampler2D vTexture;
uniform int width;
uniform int height;
//20采样点
vec2 blurCoordinates [20] ;

void main(){
    //高斯模糊
    vec2 singleStepOffset = vec2(1.0/float(width),1.0/float(height));
    blurCoordinates[0] = aCoord.xy + singleStepOffset*vec2(0.0,-10.0);
    blurCoordinates[1] = aCoord.xy + singleStepOffset * vec2(0.0, 10.0);
    blurCoordinates[2] = aCoord.xy + singleStepOffset * vec2(-10.0, 0.0);
    blurCoordinates[3] = aCoord.xy + singleStepOffset * vec2(10.0, 0.0);
    blurCoordinates[4] = aCoord.xy + singleStepOffset * vec2(5.0, -8.0);
    blurCoordinates[5] = aCoord.xy + singleStepOffset * vec2(5.0, 8.0);
    blurCoordinates[6] = aCoord.xy + singleStepOffset * vec2(-5.0, 8.0);
    blurCoordinates[7] = aCoord.xy + singleStepOffset * vec2(-5.0, -8.0);
    blurCoordinates[8] = aCoord.xy + singleStepOffset * vec2(8.0, -5.0);
    blurCoordinates[9] = aCoord.xy + singleStepOffset * vec2(8.0, 5.0);
    blurCoordinates[10] = aCoord.xy + singleStepOffset * vec2(-8.0, 5.0);
    blurCoordinates[11] = aCoord.xy + singleStepOffset * vec2(-8.0, -5.0);
    blurCoordinates[12] = aCoord.xy + singleStepOffset * vec2(0.0, -6.0);
    blurCoordinates[13] = aCoord.xy + singleStepOffset * vec2(0.0, 6.0);
    blurCoordinates[14] = aCoord.xy + singleStepOffset * vec2(6.0, 0.0);
    blurCoordinates[15] = aCoord.xy + singleStepOffset * vec2(-6.0, 0.0);
    blurCoordinates[16] = aCoord.xy + singleStepOffset * vec2(-4.0, -4.0);
    blurCoordinates[17] = aCoord.xy + singleStepOffset * vec2(-4.0, 4.0);
    blurCoordinates[18] = aCoord.xy + singleStepOffset * vec2(4.0, -4.0) ;
    blurCoordinates[19] = aCoord.xy + singleStepOffset * vec2(4.0, 4.0);
    //求平均值 包括中心点的21个点的平均值
    //算出中心点的颜色值
    vec4 currentColor = texture2D(vTexture,aCoord);
    vec3 totalRGB = currentColor.rgb;
    for(int i = 0; i < 20; i++){
        totalRGB += texture2D(vTexture,blurCoordinates[i].xy).rgb;
    }
    //需要考虑中心点的透明度
    vec4 blur = vec4(totalRGB*1.0/21.0,currentColor.a);
    //高反差 原图减去模糊的图
    vec4 highPassColor = currentColor - blur;
    //增加对比度 取值在0-1之间
    highPassColor.r = clamp(2.0*highPassColor.r*highPassColor.r*24.0,0.0,1.0);
    highPassColor.g = clamp(2.0*highPassColor.g*highPassColor.g*24.0,0.0,1.0);
    highPassColor.b = clamp(2.0*highPassColor.b*highPassColor.b*24.0,0.0,1.0);

    //将高斯模糊 高反差 增加对比度的效果叠加

    vec3 r = mix(currentColor.rgb,blur.rgb,0.2);
    gl_FragColor = vec4(r,1.0);
    //变量接收像素值
    //texture2D:采祥器采集aCoord的像素
    //赋值给gl_FragColor就可以了
    //    gl_FragColor = texture2D(vTexture, aCoord);
}


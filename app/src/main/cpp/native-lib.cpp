#include <jni.h>
#include <string>
#include "FaceTrack.h"
//初始化检测器和跟踪器
extern "C"
JNIEXPORT jlong JNICALL
Java_com_frizzle_bigeye_face_OpenCVJni_init(JNIEnv *env, jobject thiz, jstring path_,
                                            jstring seeta_) {
    const char *path = env->GetStringUTFChars(path_, 0);
    const char *seeta = env->GetStringUTFChars(seeta_, 0);

    FaceTrack *faceTrack = new FaceTrack(path,seeta);


    env->ReleaseStringUTFChars(path_,path);
    env->ReleaseStringUTFChars(seeta_,seeta);

    return reinterpret_cast<jlong>(faceTrack);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_frizzle_bigeye_face_OpenCVJni_native_1satrt(JNIEnv *env, jobject thiz, jlong self) {
    if (self == 0){
        return;
    }
    FaceTrack *me = reinterpret_cast<FaceTrack *>(self);
    me->startTracking();
}

//人脸检测  检测出人眼的位置
extern "C"
JNIEXPORT jobject JNICALL
Java_com_frizzle_bigeye_face_OpenCVJni_native_1detector(JNIEnv *env, jobject thiz, jlong self,
                                                        jbyteArray data_, jint camera_id, jint width,
                                                        jint height) {
    if (self == 0){
        return NULL;
    }
    jbyte *data = env->GetByteArrayElements(data_,NULL);
    FaceTrack *me = (FaceTrack *)self;
    Mat src(height+height /2,width,CV_8UC1,data);
    //将NV21转换成RGB
    cvtColor(src,src,COLOR_YUV2RGBA_NV21);

    if(camera_id == 1 ){
        //前置摄像头 逆时针旋转90°
        rotate(src,src,ROTATE_90_COUNTERCLOCKWISE);
        //1水平镜像翻转  0垂直镜像翻转
        flip(src,src,1);
    } else {
        //后置摄像头 顺时针旋转90°
        rotate(src,src,ROTATE_90_CLOCKWISE);
    }
    //灰度
    Mat gray;
    cvtColor(src,gray,COLOR_RGBA2GRAY);
    //直方图均衡化，提升对比度
    equalizeHist(gray,gray);
    //检测
    vector<Rect2f> rects = me->decetor(gray);
    env->ReleaseByteArrayElements(data_,data,0);
    width = src.cols;
    height = src.rows;
    int rectSize = rects.size();
    //反射将关键点的数据 返回给Java层
    if (rectSize) {
        jclass clazz = env->FindClass("com/frizzle/bigeye/face/Face");
        jmethodID construct = env->GetMethodID(clazz, "<init>", "(IIII[F)V");
        int size = rectSize * 2;
        //创建java的float 数组
        jfloatArray floatArray = env->NewFloatArray(size);
        for (int i = 0, j = 0; i < size; j++) {
            float f[2] = {rects[j].x, rects[j].y};
            env->SetFloatArrayRegion(floatArray, i, 2, f);
            i += 2;
        }
            Rect2f faceRect = rects[0];
            int face_width = faceRect.width;
            int face_height = faceRect.height;
            jobject face = env->NewObject(clazz, construct, face_width, face_height, width, height,
                                          floatArray);
            return face;
        }
        return NULL;

}
//
// Created by tpson on 2020/8/12.
//

#include "FaceTrack.h"
#include "macro.h"

FaceTrack::FaceTrack(const char *path,const char *seeta) {
    if (tracker) {
        tracker->stop();
        delete tracker;
        tracker = 0;
    }
    //智能指针
    Ptr<CascadeClassifier> classifier = makePtr<CascadeClassifier>(path);
    //创建一个检测器
    Ptr<CascadeDetectorAdapter> mainDetector = makePtr<CascadeDetectorAdapter>(classifier);
    Ptr<CascadeClassifier> classifier1 = makePtr<CascadeClassifier>(path);
    //创建一个跟踪器
    Ptr<CascadeDetectorAdapter> trackingDetector = makePtr<CascadeDetectorAdapter>(classifier1);
    //参数
    DetectionBasedTracker::Parameters DetectorParams;
    tracker = makePtr<DetectionBasedTracker>(mainDetector, trackingDetector, DetectorParams);
    faceAlignment = makePtr<seeta::FaceAlignment>(seeta);
}

void FaceTrack::startTracking() {
    if (tracker){
        tracker->run();
    }
}

vector<Rect2f> FaceTrack::decetor(Mat src) {
    std:: vector<Rect> faces;
    std:: vector<Rect2f> rects;
    //开始检测
    tracker->process(src);
    //获取结果
    tracker->getObjects(faces);
    seeta::FacialLandmark points[5];
    if (faces.size()){
        //遍历人脸,对每个人眼定位放大
        Rect face = faces[0];
        //人脸区域
        rects.push_back(Rect2f(face.x,face.y,face.width,face.height));
        seeta:: ImageData image_data(src.cols,src.rows) ;
        image_data.data=src.data;
        //待检测的区域,人脸区域检测人眼
        seeta::FaceInfo faceInfo;
        seeta::Rect bbox;
        bbox.x = face.x;
        bbox.y = face.y;
        bbox.width = face. width;
        bbox.height = face. height;
        faceInfo.bbox = bbox;
        faceAlignment->PointDetectLandmarks(image_data,faceInfo,points);
        for (int i = 0; i < 5; ++i) {
            //五个关键点分别是 左眼（0）、右眼（1）、鼻子（2）、嘴巴左边（3）、嘴巴右边（4）
            rects.push_back(Rect2f(points[i].x,points[i].y,0,0));
            if (i==0){
                LOGE("左眼坐标 x: %ld  y: %ld",faces[0].x,faces[0].y);
            }
            if (i==1){
                LOGE("右眼坐标 x: %ld y: %ld",faces[1].x,faces[1].y);
            }
        }
    }
    return rects;
}

//
// Created by tpson on 2020/8/12.
//

#ifndef BIGEYE_FACETRACK_H
#define BIGEYE_FACETRACK_H
#include "opencv2/opencv.hpp"
#include "face_alignment.h"
using namespace cv;
using namespace std;
class CascadeDetectorAdapter : public DetectionBasedTracker::IDetector {
public:
    CascadeDetectorAdapter(cv::Ptr<cv::CascadeClassifier> detector) :
            IDetector(),
            Detector(detector) {
        CV_Assert(detector);
    }

    void detect(const cv::Mat &Image, std::vector<cv::Rect> &objects) {
        Detector->detectMultiScale(Image, objects, scaleFactor, minNeighbours, 0, minObjSize,
                                   maxObjSize);
    }

    virtual ~CascadeDetectorAdapter() {

    }

private:
    CascadeDetectorAdapter();

    cv::Ptr<cv::CascadeClassifier> Detector;
};



class FaceTrack {
public:
    FaceTrack(const char *path,const char *seeta);

    void startTracking();

    vector<Rect2f> decetor(Mat src);

private:
    Ptr<DetectionBasedTracker> tracker;
    Ptr<seeta::FaceAlignment> faceAlignment;
};


#endif //BIGEYE_FACETRACK_H

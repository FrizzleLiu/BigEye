#ifndef DNPLAYER_MACRO_H
#define DNPLAYER_MACRO_H

#include <android/log.h>


#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,"OPENCV",__VA_ARGS__)

#define DELETE(obj) if(obj){ delete obj; obj = 0; }

#endif //DNPLAYER_MACRO_H

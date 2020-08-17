
cmake_minimum_required(VERSION 3.4.1)

add_library( # Sets the name of the library.
        native-lib
        SHARED
        src/main/cpp/FaceTrack.cpp
        src/main/cpp/native-lib.cpp)
#导入头文件 同../../../../src/main/cpp/include
include_directories(src/main/cpp/include)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}")
find_library( # Sets the name of the path variable.
        log-lib

        log)

add_subdirectory(src/main/cpp/FaceAlignment)
include_directories(src/main/cpp/FaceAlignment/include)
target_link_libraries( # Specifies the target library.
        native-lib
        opencv_java4
        ${log-lib}
        android
        # Links the target library to the log library
        # included in the NDK.
        seeta_fa_lib
       )
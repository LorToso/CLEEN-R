#include <CLEENR_jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/contrib/detection_based_tracker.hpp>

#include <string>
#include <vector>

#include <android/log.h>

#define LOG_TAG "FaceDetection/DetectionBasedTracker"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

using namespace std;
using namespace cv;

JNIEXPORT void JNICALL Java_com_cleenr_cleenr_MainActivity_nativeDetect(JNIEnv * jenv, jclass, jlong thiz, jlong imageGray, jlong faces)
{
    LOGD("Java_com_cleenr_cleenr_MainActivity_nativeDetect enter");

    LOGD("Java_com_cleenr_cleenr_MainActivity_nativeDetect exit");
}
JNIEXPORT void JNICALL Java_com_cleenr_cleenr_MainActivity_nativeErode(JNIEnv * jenv, jclass, jlong imageAddress)
{
	Mat * pImage = (Mat*) imageAddress;
}


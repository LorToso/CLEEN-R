#include <CLEENR_jni.h>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/contrib/detection_based_tracker.hpp>
#include "opencv2/objdetect/objdetect.hpp"
#include "opencv2/features2d/features2d.hpp"

#include <string>
#include <vector>

#include <android/log.h>

#define LOG_TAG "FaceDetection/DetectionBasedTracker"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

using namespace std;
using namespace cv;

JNIEXPORT void JNICALL Java_com_cleenr_cleenr_ObjectDetector_findContours(
		JNIEnv *, jclass, jlong imageAdr, jlong contoursAdr) {
	Mat * pImage = (Mat*) imageAdr;
	Mat * pContours = (Mat*) contoursAdr;

	vector<Rect> allRects;
	findAllRects(pImage, allRects);

}

void findAllRects(cv::Mat * pImage, std::vector<cv::Rect> & allRects)
{

	bool wasOnWhitePixel = false;
	int rectBeginningX = 0;
	for(int r=0; r < pImage->rows; r++)
	{
		for(int c=0; c < pImage->cols; c++)
		{
			uchar value = pImage->at<uchar>(r,c);
			if(value > 0)				// IS ON WHITE PIXEL
			{
				if(wasOnWhitePixel)		// WAS ALREADY ON WHITE PIXEL
				{
					continue;
				}
				else					// CAME FROM BLACK PIXEL
				{
					rectBeginningX = c;
					wasOnWhitePixel = true;
				}
			}
			else
			{							// IS ON BLACK PIXEL
				if(wasOnWhitePixel)// CAME FROM WHITE PIXEL
				{
					// this creates a rectangle which is too high by 1 pixel and goes down by 1 pixel too much
					// this is due to creating a sufficently overlapping area
					Rect r(c,r-1,rectBeginningX-c,3);
					allRects.push_back(r);
					wasOnWhitePixel = false;
				}
				else					// WAS ALREADY ON BLACK PIXEL
				{
					continue;
				}
			}
		}
	}
}

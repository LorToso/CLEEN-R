#include <CLEENR_jni.h>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/contrib/detection_based_tracker.hpp>
#include "opencv2/objdetect/objdetect.hpp"
#include "opencv2/features2d/features2d.hpp"

#include <string>
#include <set>
#include <list>

#include <android/log.h>

#define LOG_TAG "FaceDetection/DetectionBasedTracker"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

using namespace std;
using namespace cv;

JNIEXPORT void JNICALL
Java_com_cleenr_cleenr_ObjectDetector_findContours(JNIEnv *, jclass,
		jlong imageAdr, jlong contoursAdr)
{
	Mat * pImage = (Mat*) imageAdr;
	Mat * pContours = (Mat*) contoursAdr;


	//pContours->release();


	vector<vector<Rect> > allRects;
	vector<Rect> combinedRects;

	findAllRects(pImage, allRects);
	int s = 0;
	for (int i = 0; i < allRects.size(); i++)
	{
		vector<Rect> & r = allRects[i];
		s += r.size();
	}
	LOGD("Found %d Rects", s);
	combineRects(allRects, combinedRects);
	LOGD("Found %d combined Rects", combinedRects.size());

	allRects.clear();
	combinedRects.clear();

	//pContours->zeros(Size(combinedRects.size(), 4), DataType<int>::type);

	/*for(int i=0; i<combinedRects.size(); i++)
	 {
	 Rect & r = combinedRects[i];
	 pContours->at<int>(i,0) = r.x;
	 pContours->at<int>(i,1) = r.y;
	 pContours->at<int>(i,2) = r.width;
	 pContours->at<int>(i,3) = r.height;
	 }*/
}

void findAllRects(cv::Mat * pImage, vector<vector<Rect> > & allRects)
{
	for (int row = 0; row < pImage->rows; row++)
	{
		vector<Rect> rectsInRow;

		findRectsInRow(pImage, rectsInRow, row);

		allRects.push_back(rectsInRow);
	}
}

void findRectsInRow(cv::Mat * pImage, vector<Rect> & rectsInRow, int row)
{
	bool wasOnWhitePixel = false;
	int rectBeginningX = 0;

	for (int col = 0; col < pImage->cols; col++)
	{
		uchar value = pImage->at<uchar>(row, col);

		if (value > 0)					// IS ON WHITE PIXEL
		{
			if (wasOnWhitePixel)		// WAS ALREADY ON WHITE PIXEL
			{
				continue;
			}
			else						// CAME FROM BLACK PIXEL
			{
				rectBeginningX = col;
				wasOnWhitePixel = true;
			}
		}
		else
		{								// IS ON BLACK PIXEL
			if (wasOnWhitePixel)		// CAME FROM WHITE PIXEL
			{
				wasOnWhitePixel = false;
				int rectWidth = col - rectBeginningX;
				if (rectWidth > 2)
				{
					// this creates a rectangle which is too high by 1 pixel
					// this is due to creating a sufficently overlapping area
					Rect rec(col, row, rectWidth, 2);
					rectsInRow.push_back(rec);
				}
				//LOGD("Found Rect");
			}
			else						// WAS ALREADY ON BLACK PIXEL
			{
				continue;
			}
		}
	}
}
void combineRects(vector<vector<Rect> > & allRects, vector<Rect> & combinedRects)
{
	for (int row = allRects.size() - 1; row > 0; row--)
	{
		vector<Rect> & thisRowRects = allRects[row];
		vector<Rect> & upperRowRects = allRects[row - 1];

		combineRows(thisRowRects, upperRowRects, combinedRects);
		//LOGD("Combined rows %d And %d", row, row-1);
	}

}

void combineRows(vector<Rect> & lowerRow, vector<Rect> & upperRow, vector<Rect> & combinedRects)
{
	for (int i = 0; i < lowerRow.size(); i++)
	{
		Rect & rectInThisRow = lowerRow[i];
		bool rectHadCombinations = false;

		for (int j = 0; j < upperRow.size(); j++)
		{
			Rect & rectInUpperRow = upperRow[i];
			if ((rectInThisRow & rectInUpperRow).area() > 0)
			{
				rectHadCombinations = true;
				rectInUpperRow |= rectInThisRow;
			}
		}

		if (!rectHadCombinations)
		{
			combinedRects.push_back(rectInThisRow);
		}
	}
}

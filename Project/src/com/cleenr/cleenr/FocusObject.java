package com.cleenr.cleenr;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

public class FocusObject {
	public static final double sHorizonalCentrationTolerance = 1.05;
	public static final double sVerticalCentrationTolerance = 1.05;

	public final static double sColorSimilarity = 1.2;
	public final static double sAreaSimilarity = 1.1;
	public final static double sPositionSimilarity = 1.1;
	
	private Mat mRGBA;
	private Rect mArea;
	private Point mCenter;
	private Scalar mCoreColor;

	public FocusObject() {
		mRGBA = new Mat();
		mArea = new Rect();
		mCenter = new Point();
	}

	private FocusObject(Mat rgba, Rect area) {
		mRGBA = rgba;
		mArea = area;
		mCenter = calcCenter(area);
		mCoreColor = Core.mean(mRGBA);
	}

	public static FocusObject createFromRect(Rect area) {
		Mat submat = CleenrImage.getInstance().mInputFrame.submat(area);
		Mat rgba = submat.clone();

		return new FocusObject(rgba, area);
	}

	public static ArrayList<FocusObject> createFromRects(ArrayList<Rect> boundingRects) {
		ArrayList<FocusObject> focusObjects = new ArrayList<FocusObject>();
		for (Rect rect : boundingRects)
			focusObjects.add(FocusObject.createFromRect(rect));
		return focusObjects;
	}

	private Point calcCenter(Rect area) {
		return new Point(area.x + area.width / 2, area.y + area.height / 2);
	}

	public Rect getRect() {
		return mArea;
	}

	public Point getCenter() {
		return mCenter;
	}

	public Scalar getMeanColor() {
		return mCoreColor;
	}

	public boolean isInRange() {
		return false;
	}

	public boolean isHorizontallyCentered() {
		int imageWidth = (int) CleenrImage.getInstance().getFrameSize().width;
		int imageHeight = (int) CleenrImage.getInstance().getFrameSize().height;
		int minimumValidArea = (int) ((2 - sHorizonalCentrationTolerance) * imageWidth / 2);
		int maximumValidArea = (int) (sHorizonalCentrationTolerance * imageWidth / 2);

		Rect validArea = new Rect(minimumValidArea, 0, maximumValidArea - minimumValidArea, imageHeight);
		return validArea.contains(mCenter);
	}

	public boolean isVerticallyCentered() {
		int imageWidth = (int) CleenrImage.getInstance().getFrameSize().width;
		int imageHeight = (int) CleenrImage.getInstance().getFrameSize().height;
		int minimumValidArea = (int) ((2 - sVerticalCentrationTolerance) * imageHeight / 2);
		int maximumValidArea = (int) (sVerticalCentrationTolerance * imageHeight / 2);

		Rect validArea = new Rect(0, minimumValidArea, imageWidth, maximumValidArea - minimumValidArea);
		return validArea.contains(mCenter);
	}

	public boolean haveSimilarPosition(FocusObject otherObject) {
		return CleenrUtils.rectContainsWithDelta(otherObject.getRect(), mCenter, sPositionSimilarity)
				|| CleenrUtils.rectContainsWithDelta(mArea, otherObject.getCenter(), sPositionSimilarity);

	}

	public boolean haveSimilarSize(FocusObject otherObject) {
		double thisArea = mArea.area();
		double otherArea = otherObject.getRect().area();

		return (thisArea * sAreaSimilarity > otherArea && thisArea * (2 - sAreaSimilarity) < otherArea);
	}

	public boolean haveSimilarColor(FocusObject otherObject) {

		double minR = mCoreColor.val[0] * (2 - sColorSimilarity);
		double maxR = mCoreColor.val[0] * sColorSimilarity;
		double minG = mCoreColor.val[1] * (2 - sColorSimilarity);
		double maxG = mCoreColor.val[1] * sColorSimilarity;
		double minB = mCoreColor.val[2] * (2 - sColorSimilarity);
		double maxB = mCoreColor.val[2] * sColorSimilarity;

		double[] otherColors = otherObject.getMeanColor().val;
		// TODO: THIS SHOULD BE HSV, NOT RGB

		return 	(otherColors[0] > minR && otherColors[0] < maxR) 
				&& (otherColors[1] > minG && otherColors[1] < maxG) 
				&& (otherColors[2] > minB && otherColors[2] < maxB);

	}
}

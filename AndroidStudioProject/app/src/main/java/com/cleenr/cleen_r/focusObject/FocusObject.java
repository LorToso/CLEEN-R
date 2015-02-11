package com.cleenr.cleen_r.focusObject;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import com.cleenr.cleen_r.CleenrImage;
import com.cleenr.cleen_r.CleenrUtils;


public abstract class FocusObject {
	public static final double sHorizonalCentrationTolerance = 1.05;
	public static final double sVerticalCentrationTolerance = 1.05;

	public final static double sColorSimilarity = 1.2;
	public final static double sAreaSimilarity = 1.1;
	public final static double sPositionSimilarity = 1.1;
	
	public static FocusObject createFromRect(Rect area) {
		Mat submat = CleenrImage.getInstance().mInputFrame.submat(area);
		Mat rgba = submat.clone();

		return new ValidFocus(rgba, area);
	}
	public static ArrayList<FocusObject> createFromRects(ArrayList<Rect> boundingRects) {
		ArrayList<FocusObject> focusObjects = new ArrayList<FocusObject>();
		for (Rect rect : boundingRects)
			focusObjects.add(FocusObject.createFromRect(rect));
		return focusObjects;
	}

	public boolean isHorizontallyCentered() {
		int imageWidth = (int) CleenrImage.getInstance().getFrameSize().width;
		int imageHeight = (int) CleenrImage.getInstance().getFrameSize().height;
		int minimumValidArea = (int) ((2 - sHorizonalCentrationTolerance) * imageWidth / 2);
		int maximumValidArea = (int) (sHorizonalCentrationTolerance * imageWidth / 2);

		Rect validArea = new Rect(minimumValidArea, 0, maximumValidArea - minimumValidArea, imageHeight);
		return validArea.contains(getCenter());
	}

	public boolean isVerticallyCentered() {
		int imageWidth = (int) CleenrImage.getInstance().getFrameSize().width;
		int imageHeight = (int) CleenrImage.getInstance().getFrameSize().height;
		int minimumValidArea = (int) ((2 - sVerticalCentrationTolerance) * imageHeight / 2);
		int maximumValidArea = (int) (sVerticalCentrationTolerance * imageHeight / 2);

		Rect validArea = new Rect(0, minimumValidArea, imageWidth, maximumValidArea - minimumValidArea);
		return validArea.contains(getCenter());
	}

	public boolean haveSimilarPosition(FocusObject otherObject) {
		return CleenrUtils.rectContainsWithDelta(otherObject.getRect(), getCenter(), sPositionSimilarity)
				|| CleenrUtils.rectContainsWithDelta(getRect(), otherObject.getCenter(), sPositionSimilarity);

	}

	public boolean haveSimilarSize(FocusObject otherObject) {
		double thisArea = getRect().area();
		double otherArea = otherObject.getRect().area();

		return (thisArea * sAreaSimilarity > otherArea && thisArea * (2 - sAreaSimilarity) < otherArea);
	}

	public boolean haveSimilarColor(FocusObject otherObject) {
		double[] coreColor = getMeanColor().val;
		double minR = coreColor[0] * (2 - sColorSimilarity);
		double maxR = coreColor[0] * sColorSimilarity;
		double minG = coreColor[1] * (2 - sColorSimilarity);
		double maxG = coreColor[1] * sColorSimilarity;
		double minB = coreColor[2] * (2 - sColorSimilarity);
		double maxB = coreColor[2] * sColorSimilarity;

		double[] otherColors = otherObject.getMeanColor().val;
		// TODO: THIS SHOULD BE HSV, NOT RGB

		return 	(otherColors[0] > minR && otherColors[0] < maxR) 
				&& (otherColors[1] > minG && otherColors[1] < maxG) 
				&& (otherColors[2] > minB && otherColors[2] < maxB);

	}
	
	
	public abstract Rect getRect();
	public abstract Point getCenter();
	public abstract Scalar getMeanColor();
	public abstract boolean isInRange();
	public abstract String toString();
	public abstract boolean isValidFocus();
}

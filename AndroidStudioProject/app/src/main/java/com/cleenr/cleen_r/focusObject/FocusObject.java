package com.cleenr.cleen_r.focusObject;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import com.cleenr.cleen_r.CleenrBrain;
import com.cleenr.cleen_r.CleenrImage;
import com.cleenr.cleen_r.CleenrUtils;
import com.cleenr.cleen_r.objectCategorisation.Category;
import com.cleenr.cleen_r.objectCategorisation.Color;
import com.cleenr.cleen_r.objectCategorisation.Shape;


public abstract class FocusObject {
    public static final double sHorizonalCentrationTolerance = 1.20;
    public static final double sVerticalCentrationTolerance = 1.20;

    public final static double sColorSimilarityHue = 1.2;
    public final static double sColorSimilaritySaturation = 1.2;
    public final static double sColorSimilarityValue = 1.4;

    public final static double sAreaSimilarity = 1.1;
    public final static double sPositionSimilarity = 1.15;

    public static FocusObject createFromContour(MatOfPoint contour) {
        Rect rect = Imgproc.boundingRect(contour);
        Mat subMatrix = CleenrImage.getInstance().mInputFrame.submat(rect);

        Mat rgba = new Mat();
        subMatrix.copyTo(rgba);

        return new ValidFocus(rgba, rect, contour);
    }

    public static ArrayList<FocusObject> createFromContours(ArrayList<MatOfPoint> boundingContours) {
        ArrayList<FocusObject> focusObjects = new ArrayList<>();
        for (MatOfPoint contour : boundingContours)
            focusObjects.add(FocusObject.createFromContour(contour));
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
        double[] coreColor = getMeanColorHSV().val;
        double minH = coreColor[0] * (2 - sColorSimilarityHue);
        double maxH = coreColor[0] * sColorSimilarityHue;
        double minS = coreColor[1] * (2 - sColorSimilaritySaturation);
        double maxS = coreColor[1] * sColorSimilaritySaturation;
        double minV = coreColor[2] * (2 - sColorSimilarityValue);
        double maxV = coreColor[2] * sColorSimilarityValue;

        double[] otherColors = otherObject.getMeanColorHSV().val;

        return (otherColors[0] > minH && otherColors[0] < maxH)
                && (otherColors[1] > minS && otherColors[1] < maxS)
                && (otherColors[2] > minV && otherColors[2] < maxV);

    }

    public Category getCategory()
    {
        return new Category(getShapeCategorisation(), getColorCategorisation());
    }


    public abstract Shape getShapeCategorisation();
    public abstract Color getColorCategorisation();

    public abstract Rect getRect();

    public abstract Point getCenter();

    public abstract Scalar getMeanColorRGBA();

    public abstract Scalar getMeanColorHSV();

    public abstract boolean isInRange();

    public abstract String toString();

    public abstract boolean isValidFocus();

    public abstract boolean isSearchedObject(CleenrBrain brain);
}

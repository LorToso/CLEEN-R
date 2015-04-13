package com.cleenr.cleen_r.focusObject;

import com.cleenr.cleen_r.objectCategorisation.Color;
import com.cleenr.cleen_r.objectCategorisation.Shape;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class ValidFocus extends FocusObject {
    private final Rect mRect;
    private final MatOfPoint mContour;
    private final Point mCenter;
    private final Scalar mCoreColorRGBA;
    private final Scalar mCoreColorHSV;

    protected ValidFocus(Mat rgba, Rect rect, MatOfPoint contour) {
        Mat hsv = new Mat(rgba.size(), CvType.CV_8SC3);
        Imgproc.cvtColor(rgba, hsv, Imgproc.COLOR_RGB2HSV);

        this.mContour = contour;
        this.mRect = rect;
        this.mCenter = calcCenter(rect);
        this.mCoreColorRGBA = Core.mean(rgba);
        this.mCoreColorHSV = Core.mean(hsv);
    }


    private Point calcCenter(Rect area) {
        return new Point(area.x + area.width / 2, area.y + area.height / 2);
    }

    @Override
    public Shape getShapeCategorisation() {
        return Shape.getObjectShape(mContour);
    }

    @Override
    public Color getColorCategorisation() {
        return Color.getObjectColor(mCoreColorHSV);
    }

    public Rect getRect() {
        return mRect;
    }

    public Point getCenter() {
        return mCenter;
    }

    public Scalar getMeanColorRGBA() {
        return mCoreColorRGBA;
    }

    public Scalar getMeanColorHSV() {
        return mCoreColorHSV;
    }

    public String toString() {
        return "FocusObject at " + getCenter() + ". Width = " + getRect().width + ". Height = " + getRect().height + ". Category: " + getCategory() + ". HSVColor: " + mCoreColorHSV;
    }

    @Override
    public boolean isValidFocus() {
        return true;
    }

    public MatOfPoint getContour() {
        return mContour;
    }
}

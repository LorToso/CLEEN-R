package com.cleenr.cleen_r.focusObject;

import com.cleenr.cleen_r.objectCategorisation.Color;
import com.cleenr.cleen_r.objectCategorisation.Shape;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class ValidFocus extends FocusObject {
    private Rect mArea;
    private Point mCenter;
    private Scalar mCoreColorRGBA;
    private Scalar mCoreColorHSV;

    protected ValidFocus(Mat rgba, Rect area) {

        Mat hsv = new Mat(rgba.size(), CvType.CV_8SC3);
        Imgproc.cvtColor(rgba, hsv, Imgproc.COLOR_RGB2HSV);

        mArea = area;
        mCenter = calcCenter(area);
        mCoreColorRGBA = Core.mean(rgba);
        mCoreColorHSV = Core.mean(hsv);
    }


    private Point calcCenter(Rect area) {
        return new Point(area.x + area.width / 2, area.y + area.height / 2);
    }

    @Override
    public Shape getShapeCategorisation() {
        return null;
    }

    @Override
    public Color getColorCategorisation() {
        return null;
    }

    public Rect getRect() {
        return mArea;
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
        return "FocusObject at " + getCenter() + ". Width = " + getRect().width + ". Height = " + getRect().height + ". Mean Color: " + getMeanColorRGBA() + ".";
    }

    @Override
    public boolean isValidFocus() {
        return true;
    }
}

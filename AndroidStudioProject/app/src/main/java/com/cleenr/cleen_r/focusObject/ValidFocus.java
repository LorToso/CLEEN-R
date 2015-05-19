package com.cleenr.cleen_r.focusObject;

import android.util.Log;

import com.cleenr.cleen_r.CleenrBrain;
import com.cleenr.cleen_r.CleenrImage;
import com.cleenr.cleen_r.Globals;
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

    public boolean isInRange() {
        int maximumDistance = 6; // TODO
        return getDistanceInCentimeter() < maximumDistance;
    }

    private double getDistanceInCentimeter() {


        double alpha = Math.toRadians(30);
        double streuung = Math.toRadians(20);

        int cameraWidth = 3; //cm
        int modelHeight = 10; //cm

        double cameraX = cameraWidth * Math.sin(alpha);
        double cameraY = cameraWidth * Math.cos(alpha) + modelHeight;

        double frameHeight = CleenrImage.getInstance().getFrameSize().height;
        double objectPositionRatio = mRect.br().y / frameHeight;

        double distance = Math.tan(alpha+(2*streuung*objectPositionRatio-streuung))*cameraY + cameraX;


        Log.d("Distance", "" + distance);

        // Not totally correct yet.

        return distance;
    }

    public String toString() {
        return String.format("FocusObject at %s. Width = %d . Height = %d. Category: %s. HSVColor: %s",
                             getCenter().toString(), getRect().width, getRect().height,
                             getCategory().toString(), mCoreColorHSV.toString());
    }

    @Override
    public boolean isValidFocus() {
        return true;
    }

    @Override
    public boolean isSearchedObject(CleenrBrain brain)
    {
        if (Globals.searchCategories.containsKey(getCategory()))
        {
            Log.d("searched object", "Found searched object!");
            return true;
        }
        return false;
    }

    public MatOfPoint getContour() {
        return mContour;
    }
}

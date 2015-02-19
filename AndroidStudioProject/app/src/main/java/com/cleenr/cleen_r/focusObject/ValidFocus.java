package com.cleenr.cleen_r.focusObject;

import android.util.Log;

import com.cleenr.cleen_r.CleenrImage;

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

	public boolean isInRange() {
        int maximumDistance = 5; // TODO
		return getDistanceInCentimeter() < maximumDistance ;
	}
    private double getDistanceInCentimeter()
    {


        double alpha = Math.toRadians(30);
        double streuung = Math.toRadians(20);

        int cameraWidth = 3; //cm
        int modelHeight = 5; //cm

        double cameraX = cameraWidth*Math.sin(alpha);
        double cameraY = cameraWidth*Math.cos(alpha)+modelHeight;

        //double tMid = cameraY/Math.sin(alpha);
        //double dMid = cameraX + tMid*Math.cos(alpha);

        double tTop = cameraY/Math.sin(alpha-streuung);
        double dTop = cameraX + tTop*Math.cos(alpha-streuung);

        double tBot = cameraY/Math.sin(alpha+streuung);
        double dBot = cameraX + tBot*Math.cos(alpha+streuung);

        double frameHeight = CleenrImage.getInstance().getFrameSize().height;

        double distance = (1-(mArea.br().y/frameHeight))*dTop+(mArea.br().y/frameHeight)*(dBot);
        Log.d("Distance","" +distance);

        // Not totally correct yet.

        return distance;
    }

	public String toString()
	{
		return "FocusObject at " +  getCenter() + ". Width = " + getRect().width + ". Height = " + getRect().height + ". Mean Color: " + getMeanColorRGBA() + ".";
	}

	@Override
	public boolean isValidFocus() {
		return true;
	}
}

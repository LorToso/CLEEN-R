package com.cleenr.cleenr.robotcontrolunits;

import org.opencv.core.Point;

import com.cleenr.cleenr.CleenrImage;
import com.cleenr.cleenr.focusObject.FocusObject;

import android.util.Log;

public class TempControlUnit implements RobotControlUnit
{

	

	public boolean isClawClosed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void turnRight() {
		try {
			Log.d("ControlUnit", "Turning right");
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void closeClaw() {
		try {
			Log.d("ControlUnit", "Closing claw");
			Thread.sleep(250);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void driveForward() {
		try {
			Log.d("ControlUnit", "Driving forward");
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean hasObjectInClaw() {
		// TODO Auto-generated method stub
		return false;
	}

	public void openClaw() {
		// TODO
		try {
			Log.d("ControlUnit", "Opening claw");
			Thread.sleep(250);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void turnLeft() {
		try {
			Log.d("ControlUnit", "Turning left");
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void centerObject(FocusObject focusObject) {
		Point focusCenter = focusObject.getCenter();

		int imageWidth = (int) CleenrImage.getInstance().getFrameSize().width;
		double minimumValidArea = (2-FocusObject.sHorizonalCentrationTolerance) *  imageWidth/ 2;
		double maximumValidArea = FocusObject.sHorizonalCentrationTolerance * imageWidth / 2;
		
		// CAREFUL!!!! Point(0|0) is on the Bottom right, because FUCK YOU
		if (focusCenter.x < minimumValidArea)
			turnLeft();
		else if(focusCenter.x > maximumValidArea)
			turnRight();
	}
	
}

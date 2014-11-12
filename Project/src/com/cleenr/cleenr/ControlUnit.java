package com.cleenr.cleenr;

import android.util.Log;

public class ControlUnit 
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
	
}

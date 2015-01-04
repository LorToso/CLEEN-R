package com.cleenr.cleenr;

import org.opencv.core.Mat;

import com.cleenr.cleenr.focusObject.FocusObject;
import com.cleenr.cleenr.focusObject.FocusObjectDetector;
import com.cleenr.cleenr.focusObject.NoFocus;

import android.util.Log;

public class CLEENRBrain {
	public CleenrImage mCleenrImage;

	public boolean isCamerainitialized;
	private FocusObject mFocusedObject = new NoFocus();
	private FocusObjectDetector mFocusObjectFinder = new FocusObjectDetector();
	private RobotWorker mWorkLoop;


	public CLEENRBrain() {
		mCleenrImage = CleenrImage.getInstance();
		mWorkLoop = new RobotWorker(this);
		new Thread(mWorkLoop).start();
	}

	public Mat onCameraFrame(Mat inputFrame) {
		mCleenrImage.changeFrame(inputFrame);
		
		findFocus();
		drawFocus();
		printFocus();

		isCamerainitialized = true;
		
		System.gc();
		return mCleenrImage.mOutputFrame;
		//return inputFrame;
	}


	private void findFocus() {
		focusObject(mFocusObjectFinder.findFocusTarget(mFocusedObject));
	}
	private void drawFocus() {
		CleenrUtils.drawFocusObject(mCleenrImage.mOutputFrame, mFocusedObject);
	}
	private void printFocus(){
		Log.d("FocusObject", mFocusedObject.toString());
	}
		
	private void focusObject(FocusObject focusedObject) {
		mFocusedObject = focusedObject;
	}

	public FocusObject getFocusedObject() {
		return mFocusedObject;
	}
}

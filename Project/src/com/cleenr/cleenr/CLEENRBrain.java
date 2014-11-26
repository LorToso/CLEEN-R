package com.cleenr.cleenr;

import org.opencv.core.Mat;

import android.util.Log;

public class CLEENRBrain {
	private static boolean bIsCameraInitialized = false;

	public CleenrImage mCleenrImage;

	private FocusObject mFocusedObject = new FocusObject();
	private FocusObjectDetector mFocusObjectFinder = new FocusObjectDetector();
	private WorkLoop mWorkLoop;


	public CLEENRBrain() {
		mCleenrImage = CleenrImage.getInstance();
		mWorkLoop = new WorkLoop(this);
		new Thread(mWorkLoop).start();
	}

	public Mat onCameraFrame(Mat inputFrame) {
		mCleenrImage.changeFrame(inputFrame);
		
		findFocus();
		drawFocus();

		bIsCameraInitialized = true;
		System.gc();
		return mCleenrImage.mOutputFrame;
	}

	private void findFocus() {
		FocusObject newFocus = mFocusObjectFinder.findFocusTarget(mFocusedObject);
		focusObject(newFocus);
	}

	private void drawFocus() {
		if (mFocusedObject != null)
			CleenrUtils.drawFocusObject(mCleenrImage.mOutputFrame, mFocusedObject);
	}

	private void focusObject(FocusObject focusedObject) {

		if (mFocusedObject == null) {
			if (focusedObject == null) {
				Log.d("FocusObject", "No focus found.");
				return;
			}
			mFocusedObject = focusedObject;
			Log.d("FocusObject", "New Focus - Center: " + focusedObject.getCenter() + " Color: " + focusedObject.getMeanColor());
			return;
		}

		if (focusedObject == null)
			Log.d("FocusObject", "Focus lost.");
		else
			Log.d("FocusObject", "Object moved:\t" + mFocusedObject.getCenter() + "\t ->\t " + focusedObject.getCenter());

		mFocusedObject = focusedObject;

	}

	public static boolean isCameraInitialized() {
		return bIsCameraInitialized;
	}

	public FocusObject getFocusedObject() {
		return mFocusedObject;
	}
}

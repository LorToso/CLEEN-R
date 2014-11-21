package com.cleenr.cleenr;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Mat;

import android.content.Context;
import android.util.Log;

public class CLEENRBrain implements Runnable {
	private Context mContext;
	private boolean bIsCameraInitialized = false;

	private Thread workThread;

	private FocusedObject mFocusedObject = new FocusedObject();
	private FocusObjectFinder mFocusObjectFinder = new FocusObjectFinder();
	private ControlUnit mControlUnit = new ControlUnit();

	private WorkPhase mWorkPhase = WorkPhase.INITIALIZING;

	public static Mat outputFrame;
	
	public CLEENRBrain(Context context) {
		mContext = context;
		workThread = new Thread(this);
		workThread.start();
	}

	public Mat onCameraFrame(Mat inputFrame) {
		outputFrame = inputFrame.clone();
		
		FocusedObject focus = mFocusObjectFinder.findFocusTarget(outputFrame, mFocusedObject);
		focusObject(focus);
		CleenrUtils.drawRect(outputFrame, mFocusedObject);
		bIsCameraInitialized = true;
		return outputFrame;
	}

	private void focusObject(FocusedObject focusedObject) {

		if (mFocusedObject == null) {
			if (focusedObject == null) {
				Log.d("FocusObject", "No focus found.");
				return;
			}
			mFocusedObject = focusedObject;
			Log.d("FocusObject",
					"New Focus - Center: " + focusedObject.getCenter()
							+ " Color: " + focusedObject.getMeanColor());
			return;
		}

		if (focusedObject == null)
			Log.d("FocusObject", "Focus lost.");
		else
			Log.d("FocusObject", "Object moved:\t" + mFocusedObject.getCenter()
					+ "\t ->\t " + focusedObject.getCenter());

		mFocusedObject = focusedObject;

	}

	@Override
	public void run() {
		//workLoop();
	}

	private void workLoop() {

		while (!bIsCameraInitialized)
			Thread.yield();

		mWorkPhase = WorkPhase.SEARCHING_OBJECT;

		while (true) {
			switch (mWorkPhase) {
			case SEARCHING_OBJECT:
				searchObject();
				break;
			case GOING_TO_OBJECT:
				driveTowardsObject();
				break;
			case PICKING_UP_OBJECT:
				pickUpObject();
				break;
			case SEARCHING_DESTINATION:
				searchDestination();
				break;
			case GOING_TO_DESTINATION:
				driveTowardsDestination();
				break;
			case DROPPING_OFF_OBJECT:
				dropOffObject();
				break;
			default:
				break;
			}
		}
	}

	private void searchObject() {
		int maximumTurnRate = 100; // TODO: Make this static

		int currentTurnRate = 0;
		while (mFocusedObject == null) {
			mControlUnit.turnRight();
			if (++currentTurnRate > maximumTurnRate) {
				// TODO: What if no object was found after 1 complete Rotation?
				// mFocusObjectFinder.lowerCriteria();
				currentTurnRate = 0;
			}
		}
		mWorkPhase = WorkPhase.GOING_TO_OBJECT;
	}

	private void driveTowardsObject() {

		FocusedObject tempFocus = mFocusedObject;

		if (tempFocus == null) {
			mWorkPhase = WorkPhase.SEARCHING_OBJECT;
			return;
		}

		if (tempFocus.isInRange()) {
			mWorkPhase = WorkPhase.PICKING_UP_OBJECT;
			return;
		}

		if (!tempFocus.isHorizontallyCentered()) {
			mControlUnit.centerObject(tempFocus);
			return;
		}

		mControlUnit.driveForward();

	}

	private void pickUpObject() {
		mControlUnit.centerObject(mFocusedObject);
		mControlUnit.closeClaw();
		if (!mControlUnit.hasObjectInClaw()) {
			mControlUnit.openClaw();
			mWorkPhase = WorkPhase.SEARCHING_OBJECT;
			return;
		}
		mWorkPhase = WorkPhase.SEARCHING_DESTINATION;
	}

	private void searchDestination() {
		// TODO
	}

	private void driveTowardsDestination() {
		// TODO
	}

	private void dropOffObject() {
		// TODO
	}
}

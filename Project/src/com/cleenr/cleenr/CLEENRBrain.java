package com.cleenr.cleenr;


import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Mat;

import android.content.Context;
import android.util.Log;


public class CLEENRBrain implements Runnable
{
	private Context mContext;
	
	private Thread workThread;
	
	private FocusedObject 		mFocusedObject 		= new FocusedObject();
	private FocusObjectFinder 	mFocusObjectFinder 	= new FocusObjectFinder();
	private ControlUnit			mControlUnit		= new ControlUnit();
	
	private WorkPhase			mWorkPhase 			= WorkPhase.INITIALIZING;
	
	public CLEENRBrain(Context context)
	{
		mContext = context;
		workThread = new Thread(this);
		workThread.start();
	}

    
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		Mat outputFrame = inputFrame.rgba();
		focusObject(mFocusObjectFinder.findFocusTarget(inputFrame, mFocusedObject));
		CleenrUtils.drawRect(outputFrame, mFocusedObject);
		return outputFrame;
	}
	
	private void focusObject(FocusedObject focusedObject)
	{
		if(focusedObject != null)
			Log.d("FOCUSED OBJECT", "New Focus - Center: " + focusedObject.getCenter() + " Color: " + focusedObject.getMeanColor());
		mFocusedObject = focusedObject;
	}


	@Override
	public void run() {
		workLoop();
	}


	private void workLoop() 
	{
		mWorkPhase = WorkPhase.SEARCHING_OBJECT;
		
		while(true)
		{
			switch(mWorkPhase)
			{
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
	private void searchObject()
	{
		int maximumTurnRate = 100;		// TODO: Make this static
		
		int currentTurnRate = 0;
		while(mFocusedObject == null) 
		{
			mControlUnit.turnRight();
			if(++currentTurnRate > maximumTurnRate)
			{
				mFocusObjectFinder.lowerCriteria();
				currentTurnRate = 0;
			}	
		}
		mWorkPhase = WorkPhase.GOING_TO_OBJECT;
	}
	private void driveTowardsObject()
	{
		if(mFocusedObject == null)
		{
			mWorkPhase = WorkPhase.SEARCHING_OBJECT;
			return;
		}
		if(mFocusedObject.isInRange())
		{
			mWorkPhase = WorkPhase.PICKING_UP_OBJECT;
			return;
		}
		/*if(!mFocusedObject.isCentered())
		{
			// TODO Center Object
			return;
		}*/
		mControlUnit.driveForward();
		
	}
	private void pickUpObject()
	{
		// TODO
	}
	private void searchDestination()
	{
		// TODO
	}
	private void driveTowardsDestination()
	{
		// TODO
	}
	private void dropOffObject()
	{
		// TODO
	}
}

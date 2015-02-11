package com.cleenr.cleenr;

import com.cleenr.cleenr.robotcontrolunits.RobotControlUnit;
import com.cleenr.cleenr.robotcontrolunits.TempControlUnit;
import com.cleenr.cleenr.workphase.Idle;
import com.cleenr.cleenr.workphase.SearchingObject;
import com.cleenr.cleenr.workphase.WorkPhase;

public class RobotWorker implements Runnable{
	private WorkPhase mWorkPhase 				= new Idle(this);
	private RobotControlUnit mRobotControlUnit 	= new TempControlUnit();
	private CLEENRBrain mBrain;
	
	public RobotWorker(CLEENRBrain brain)
	{
		mBrain = brain;
	}
	
	public void switchWorkphase(WorkPhase newWorkPhase)
	{
		mWorkPhase = newWorkPhase;
	}
	
	@Override
	public void run() {
		workLoop();
	}

	private void workLoop() 
	{
		waitForFirstFrame();

		switchWorkphase(new SearchingObject(this));

		while (true) {
			mWorkPhase.executeWork(mBrain.getFocusedObject(), mRobotControlUnit);
		}
	}

	private void waitForFirstFrame() {
		while(!mBrain.isCamerainitialized)
			Thread.yield();
	}
}

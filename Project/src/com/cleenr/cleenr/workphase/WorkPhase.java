package com.cleenr.cleenr.workphase;

import com.cleenr.cleenr.RobotWorker;
import com.cleenr.cleenr.focusObject.FocusObject;
import com.cleenr.cleenr.robotcontrolunits.RobotControlUnit;

public abstract class WorkPhase {
	RobotWorker mRobotWorker;
	public WorkPhase(RobotWorker worker)
	{
		mRobotWorker = worker;
	}
	public abstract void executeWork(FocusObject focusObject, RobotControlUnit controlUnit);
}
	/*INITIALIZING,
	SEARCHING_OBJECT,
	GOING_TO_OBJECT,
	PICKING_UP_OBJECT,
	SEARCHING_DESTINATION,
	GOING_TO_DESTINATION,
	DROPPING_OFF_OBJECT*/

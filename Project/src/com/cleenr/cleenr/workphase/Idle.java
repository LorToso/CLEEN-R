package com.cleenr.cleenr.workphase;

import com.cleenr.cleenr.RobotWorker;
import com.cleenr.cleenr.focusObject.FocusObject;
import com.cleenr.cleenr.robotcontrolunits.RobotControlUnit;

public class Idle extends WorkPhase {

	public Idle(RobotWorker worker) {
		super(worker);
	}

	@Override
	public void executeWork(FocusObject focusObject, RobotControlUnit controlUnit) {
		// Nothing to do
	}


}

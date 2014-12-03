package com.cleenr.cleenr.workphase;

import com.cleenr.cleenr.RobotWorker;
import com.cleenr.cleenr.focusObject.FocusObject;
import com.cleenr.cleenr.robotcontrolunits.RobotControlUnit;

public class DroppingOffObject extends WorkPhase {

	public DroppingOffObject(RobotWorker worker) {
		super(worker);
	}

	@Override
	public void executeWork(FocusObject focusObject, RobotControlUnit controlUnit) {
		controlUnit.openClaw();
		mRobotWorker.switchWorkphase(new Idle(mRobotWorker));
	}


}

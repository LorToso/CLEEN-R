package com.cleenr.cleenr.workphase;

import com.cleenr.cleenr.RobotWorker;
import com.cleenr.cleenr.focusObject.FocusObject;
import com.cleenr.cleenr.robotcontrolunits.RobotControlUnit;

public class PickingUpObject extends WorkPhase {

	public PickingUpObject(RobotWorker worker) {
		super(worker);
	}

	@Override
	public void executeWork(FocusObject focusObject, RobotControlUnit controlUnit) {
		controlUnit.closeClaw();

		if (controlUnit.hasObjectInClaw()) {
			controlUnit.openClaw();
			mRobotWorker.switchWorkphase(new SearchingObject(mRobotWorker));
			return;
		}
		mRobotWorker.switchWorkphase(new SearchingDestination(mRobotWorker));
	}


}

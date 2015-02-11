package com.cleenr.cleenr.workphase;

import com.cleenr.cleenr.RobotWorker;
import com.cleenr.cleenr.focusObject.FocusObject;
import com.cleenr.cleenr.focusObject.NoFocus;
import com.cleenr.cleenr.robotcontrolunits.RobotControlUnit;

public class GoingToObject extends WorkPhase {

	public GoingToObject(RobotWorker worker) {
		super(worker);
	}

	@Override
	public void executeWork(FocusObject focusObject, RobotControlUnit controlUnit) {
		if (!focusObject.isValidFocus()) {
			mRobotWorker.switchWorkphase(new SearchingObject(mRobotWorker));
			return;
		}

		if (focusObject.isInRange()) {
			mRobotWorker.switchWorkphase(new PickingUpObject(mRobotWorker));
			return;
		}

		if (!focusObject.isHorizontallyCentered()) {
			controlUnit.centerObject(focusObject);
			return;
		}

		controlUnit.driveForward();
	}


}

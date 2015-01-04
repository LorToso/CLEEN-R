package com.cleenr.cleenr.workphase;

import com.cleenr.cleenr.RobotWorker;
import com.cleenr.cleenr.focusObject.FocusObject;
import com.cleenr.cleenr.robotcontrolunits.RobotControlUnit;

public class SearchingObject extends WorkPhase {

	public SearchingObject(RobotWorker worker) {
		super(worker);
	}

	@Override
	public void executeWork(FocusObject focusObject, RobotControlUnit controlUnit) {
				
		if(!focusObject.isValidFocus())
		{
			controlUnit.turnRight();
			return;
		}
		mRobotWorker.switchWorkphase(new GoingToObject(mRobotWorker));
	}


}

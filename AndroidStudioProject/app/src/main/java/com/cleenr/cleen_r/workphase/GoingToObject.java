package com.cleenr.cleen_r.workphase;

import com.cleenr.cleen_r.RobotWorker;
import com.cleenr.cleen_r.focusObject.FocusObject;
import com.cleenr.cleen_r.focusObject.NoFocus;
import com.cleenr.cleen_r.robotcontrolunits.RobotControlUnit;

public class GoingToObject extends WorkPhase {

    public GoingToObject(RobotWorker worker) {
        super(worker);
    }

    @Override
    public void executeWork(FocusObject focusObject, RobotControlUnit controlUnit) {
        if (!focusObject.isValidFocus()) {
            // keep moving
            //mRobotWorker.switchWorkphase(new SearchingObject(mRobotWorker));
            return;
        }

        if (focusObject.isInRange()) {
            controlUnit.stopMoving();
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

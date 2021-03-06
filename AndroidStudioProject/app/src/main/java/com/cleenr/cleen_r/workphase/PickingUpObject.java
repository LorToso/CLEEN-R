package com.cleenr.cleen_r.workphase;

import com.cleenr.cleen_r.CleenrBrain;
import com.cleenr.cleen_r.RobotWorker;
import com.cleenr.cleen_r.focusObject.FocusObject;
import com.cleenr.cleen_r.robotcontrolunits.RobotControlUnit;

public class PickingUpObject extends WorkPhase {

    public PickingUpObject(RobotWorker worker) {
        super(worker);
    }

    @Override
    public void executeWork(FocusObject focusObject, RobotControlUnit controlUnit, CleenrBrain brain) {
        controlUnit.closeClaw();
        mRobotWorker.switchWorkphase(new GoingToDestination(mRobotWorker));
    }


}

package com.cleenr.cleen_r.workphase;

import com.cleenr.cleen_r.CleenrBrain;
import com.cleenr.cleen_r.RobotWorker;
import com.cleenr.cleen_r.focusObject.FocusObject;
import com.cleenr.cleen_r.robotcontrolunits.RobotControlUnit;

public class DroppingOffObject extends WorkPhase {

    public DroppingOffObject(RobotWorker worker) {
        super(worker);
    }

    @Override
    public void executeWork(FocusObject focusObject, RobotControlUnit controlUnit, CleenrBrain brain) {
        controlUnit.openClaw();
        mRobotWorker.switchWorkphase(new Idle(mRobotWorker));
    }


}

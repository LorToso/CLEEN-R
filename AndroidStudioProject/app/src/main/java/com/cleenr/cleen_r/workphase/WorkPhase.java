package com.cleenr.cleen_r.workphase;

import com.cleenr.cleen_r.RobotWorker;
import com.cleenr.cleen_r.focusObject.FocusObject;
import com.cleenr.cleen_r.robotcontrolunits.RobotControlUnit;

public abstract class WorkPhase {
    RobotWorker mRobotWorker;

    public WorkPhase(RobotWorker worker) {
        mRobotWorker = worker;
    }

    public abstract void executeWork(FocusObject focusObject, RobotControlUnit controlUnit);
}

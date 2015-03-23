package com.cleenr.cleen_r.workphase;

import com.cleenr.cleen_r.RobotWorker;
import com.cleenr.cleen_r.focusObject.FocusObject;
import com.cleenr.cleen_r.objectCategorisation.Category;
import com.cleenr.cleen_r.robotcontrolunits.RobotControlUnit;


public class CategorisingObject extends WorkPhase {

    public CategorisingObject(RobotWorker worker) {
        super(worker);
    }

    @Override
    public void executeWork(FocusObject focusObject, RobotControlUnit controlUnit) {

        Category category = focusObject.getCategory();

        mRobotWorker.switchWorkphase(new SearchingDestination(mRobotWorker));
    }


}

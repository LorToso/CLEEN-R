package com.cleenr.cleen_r;

import org.opencv.core.Mat;

import com.cleenr.cleen_r.focusObject.FocusObject;
import com.cleenr.cleen_r.focusObject.FocusObjectDetector;
import com.cleenr.cleen_r.focusObject.NoFocus;
import com.cleenr.cleen_r.nxt.NxtTalker;
import com.cleenr.cleen_r.objectCategorisation.Category;
import com.cleenr.cleen_r.objectCategorisation.Color;
import com.cleenr.cleen_r.objectCategorisation.Shape;
import com.cleenr.cleen_r.robotcontrolunits.PositionTracker;

import android.graphics.Point;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CleenrBrain {
    public       boolean         isCamerainitialized;
    public final NxtTalker       mNxtTalker;
    public final PositionTracker mPositionTracker;

    private FocusObject         mFocusedObject     = new NoFocus();
    private FocusObjectDetector mFocusObjectFinder = new FocusObjectDetector();
    private Thread      mRobotWorkerThread;
    private RobotWorker mWorkLoop;
    private Map<Category, Point> searchCategories = new HashMap<>();

    public CleenrBrain(NxtTalker nxtTalker)
    {
        mNxtTalker = nxtTalker;
        mPositionTracker = new PositionTracker();
        mWorkLoop = new RobotWorker(this);
        mRobotWorkerThread = new Thread(mWorkLoop);

        // testing values
        searchCategories.put(new Category(Shape.SPHERE, Color.YELLOW), new Point(0, 0));
    }

    public Map<Category, Point> getSearchCategories()
    {
        return searchCategories;
    }

    public Mat onCameraFrame(Mat inputFrame)
    {
        CleenrImage.getInstance().changeFrame(inputFrame);

        findFocus();
        drawFocus();
        printFocus();

        isCamerainitialized = true;

        //System.gc();
        return CleenrImage.getInstance().mOutputFrame;
        //return inputFrame;
    }

    public void onResume()
    {
        Log.d("onResume", "Resuming workerthread");
        mRobotWorkerThread = new Thread(mWorkLoop);
        mRobotWorkerThread.start();
    }
    public void onPause()
    {
        Log.d("onPause", "Stopping workerthread");
        mWorkLoop.stopOnNextTurn();

        try {
            mRobotWorkerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.interrupted();
        }
    }

    private void findFocus() {
        focusObject(mFocusObjectFinder.findFocusTarget(mFocusedObject));
    }

    private void drawFocus() {
        CleenrUtils.drawFocusObject(CleenrImage.getInstance().mOutputFrame, mFocusedObject);
    }

    private void printFocus() {
        Log.d("FocusObject", mFocusedObject.toString());
    }

    private void focusObject(FocusObject focusedObject) {
        mFocusedObject = focusedObject;
    }

    public FocusObject getFocusedObject() {
        return mFocusedObject;
    }
}

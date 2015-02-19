package com.cleenr.cleen_r.robotcontrolunits;

import org.opencv.core.Point;

import com.cleenr.cleen_r.CleenrImage;
import com.cleenr.cleen_r.focusObject.FocusObject;
import com.cleenr.cleen_r.nxt.NxtTalker;

import android.util.Log;

public class NxtControlUnit implements RobotControlUnit {

    private final NxtTalker mNxtTalker;

    private RobotAction lastAction = null;

    private final byte CLAW_MOTOR = NxtTalker.MOTOR_PORT_A;
    private final byte LEFT_WHEEL_MOTOR = NxtTalker.MOTOR_PORT_C;
    private final byte RIGHT_WHEEL_MOTOR = NxtTalker.MOTOR_PORT_B;

    private final byte MOTOR_SPEED = 20; // -100 to 100
    private final byte MOTOR_SPEED_SLOW = 10; // -100 to 100

    private final long SLEEP_TIME_TURNING = 50;
    private final long SLEEP_TIME_DRIVING = 100;

    public NxtControlUnit(NxtTalker nxtTalker) {
        mNxtTalker = nxtTalker;
    }

    public boolean isClawClosed() {
        // TODO Auto-generated method stub
        return false;
    }

    public void turnRight() {
        try {
            Log.d("ControlUnit", "Turning right");
            mNxtTalker.setMotorSpeed(LEFT_WHEEL_MOTOR, MOTOR_SPEED);
            mNxtTalker.setMotorSpeed(RIGHT_WHEEL_MOTOR, (byte) (-1 * MOTOR_SPEED));
            Thread.sleep(SLEEP_TIME_TURNING);
            mNxtTalker.setMotorSpeed(NxtTalker.MOTOR_PORT_ALL, (byte) 0);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        lastAction = RobotAction.TURN_RIGHT;
    }

    public void closeClaw() {
        try {
            Log.d("ControlUnit", "Closing claw");
            mNxtTalker.setMotorSpeed(CLAW_MOTOR, MOTOR_SPEED);
            Thread.sleep(250);
            mNxtTalker.setMotorSpeed(NxtTalker.MOTOR_PORT_ALL, (byte) 0);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        lastAction = RobotAction.CLOSE_CLAW;
    }

    public void driveForward() {
        try {
            Log.d("ControlUnit", "Driving forward");
            mNxtTalker.setMotorSpeed(LEFT_WHEEL_MOTOR, MOTOR_SPEED);
            mNxtTalker.setMotorSpeed(RIGHT_WHEEL_MOTOR, MOTOR_SPEED);
            Thread.sleep(SLEEP_TIME_DRIVING);
            mNxtTalker.setMotorSpeed(NxtTalker.MOTOR_PORT_ALL, (byte) 0);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        lastAction = RobotAction.DRIVE_FORWARD;
    }

    public boolean hasObjectInClaw() {
        // TODO Auto-generated method stub
        return false;
    }

    public void openClaw() {
        // TODO
        try {
            Log.d("ControlUnit", "Opening claw");
            mNxtTalker.setMotorSpeed(CLAW_MOTOR, (byte) (-1 * MOTOR_SPEED));
            Thread.sleep(250);
            mNxtTalker.setMotorSpeed(NxtTalker.MOTOR_PORT_ALL, (byte) 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lastAction = RobotAction.OPEN_CLAW;
    }

    public void turnLeft() {
        try {
            Log.d("ControlUnit", "Turning left");
            mNxtTalker.setMotorSpeed(LEFT_WHEEL_MOTOR, (byte) (-1 * MOTOR_SPEED));
            mNxtTalker.setMotorSpeed(RIGHT_WHEEL_MOTOR, MOTOR_SPEED);
            Thread.sleep(SLEEP_TIME_TURNING);
            mNxtTalker.setMotorSpeed(NxtTalker.MOTOR_PORT_ALL, (byte) 0);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        lastAction = RobotAction.TURN_LEFT;
    }

    public void centerObject(FocusObject focusObject) {
        Point focusCenter = focusObject.getCenter();

        int imageWidth = (int) CleenrImage.getInstance().getFrameSize().width;
        double minimumValidArea = (2 - FocusObject.sHorizonalCentrationTolerance) * imageWidth / 2;
        double maximumValidArea = FocusObject.sHorizonalCentrationTolerance * imageWidth / 2;

        // CAREFUL!!!! Point(0|0) is on the Bottom right, because FUCK YOU
        if (focusCenter.x < minimumValidArea)
            turnLeftSlowly();
        else if (focusCenter.x > maximumValidArea)
            turnRightSlowly();
    }

    @Override
    public void repeatLastAction() {
        switch (lastAction)
        {
            case TURN_LEFT:
                turnLeft();
                break;
            case TURN_RIGHT:
                turnRight();
                break;
            case TURN_LEFT_SLOWLY:
                turnLeftSlowly();
                break;
            case TURN_RIGHT_SLOWLY:
                turnRightSlowly();
                break;
            case OPEN_CLAW:
                openClaw();
                break;
            case CLOSE_CLAW:
                closeClaw();
                break;
            default:
                break;
        }
    }

    private void turnRightSlowly() {
        try {
            Log.d("ControlUnit", "Turning right");
            mNxtTalker.setMotorSpeed(LEFT_WHEEL_MOTOR, MOTOR_SPEED_SLOW);
            mNxtTalker.setMotorSpeed(RIGHT_WHEEL_MOTOR, (byte) (-1 * MOTOR_SPEED_SLOW));
            Thread.sleep(SLEEP_TIME_TURNING);
            mNxtTalker.setMotorSpeed(NxtTalker.MOTOR_PORT_ALL, (byte) 0);
            Thread.sleep(200);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        lastAction = RobotAction.TURN_RIGHT_SLOWLY;
    }


    private void turnLeftSlowly() {
        try {
            Log.d("ControlUnit", "Turning left");
            mNxtTalker.setMotorSpeed(LEFT_WHEEL_MOTOR, (byte) (-1 * MOTOR_SPEED_SLOW));
            mNxtTalker.setMotorSpeed(RIGHT_WHEEL_MOTOR, MOTOR_SPEED_SLOW);
            Thread.sleep(SLEEP_TIME_TURNING);
            mNxtTalker.setMotorSpeed(NxtTalker.MOTOR_PORT_ALL, (byte) 0);
            Thread.sleep(200);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        lastAction = RobotAction.TURN_LEFT_SLOWLY;

    }

}

package com.cleenr.cleen_r.robotcontrolunits;

import android.graphics.PointF;
import android.util.Log;

import com.cleenr.cleen_r.CleenrImage;
import com.cleenr.cleen_r.Utils;
import com.cleenr.cleen_r.focusObject.FocusObject;
import com.cleenr.cleen_r.nxt.NxtTalker;

import org.opencv.core.Point;

public class NxtControlUnit implements RobotControlUnit
{
    private final NxtTalker       mNxtTalker;
    private final PositionTracker mPosTracker;

    private long          movementStartingTime = System.currentTimeMillis();
    private RobotMovement currentMovement      = RobotMovement.STOP;
    private byte          currentMotorSpeed    = 0;

    private final byte CLAW_MOTOR        = NxtTalker.MOTOR_PORT_A;
    private final byte LEFT_WHEEL_MOTOR  = NxtTalker.MOTOR_PORT_C;
    private final byte RIGHT_WHEEL_MOTOR = NxtTalker.MOTOR_PORT_B;

    private final byte MOTOR_SPEED         = 50; // -100 to 100
    private final byte MOTOR_SPEED_TURNING = 30; // -100 to 100
    private final byte MOTOR_SPEED_SLOW    = 20; // -100 to 100
    private final byte MOTOR_SPEED_CLAW    = 30; // -100 to 100

    public static final byte SYNC_MODE_TURNING = NxtTalker.MOTOR_REG_MODE_NONE;
    public static final byte SYNC_MODE_DRIVING = NxtTalker.MOTOR_REG_MODE_NONE;

    public NxtControlUnit(NxtTalker nxtTalker, PositionTracker posTracker)
    {
        mNxtTalker = nxtTalker;
        mPosTracker = posTracker;
    }

    public void turnRight()
    {
        Log.d("ControlUnit", "Turning right");
        mNxtTalker.setMotorSpeed(LEFT_WHEEL_MOTOR, MOTOR_SPEED_TURNING, SYNC_MODE_TURNING);
        mNxtTalker.setMotorSpeed(RIGHT_WHEEL_MOTOR, (byte) (-1 * MOTOR_SPEED_TURNING), SYNC_MODE_TURNING);
        refreshMovement(RobotMovement.TURN_RIGHT, MOTOR_SPEED_TURNING);
    }

    public void closeClaw()
    {
        try
        {
            Log.i("ControlUnit", "Closing claw");
            mNxtTalker.setMotorSpeed(CLAW_MOTOR, (byte) (-1 * MOTOR_SPEED_CLAW));
            Thread.sleep(1000);
            mNxtTalker.setMotorSpeed(NxtTalker.MOTOR_PORT_ALL, (byte) 0);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void driveForward()
    {
        Log.d("ControlUnit", "Driving forward");
        mNxtTalker.setMotorSpeed(LEFT_WHEEL_MOTOR, MOTOR_SPEED, SYNC_MODE_DRIVING);
        mNxtTalker.setMotorSpeed(RIGHT_WHEEL_MOTOR, MOTOR_SPEED, SYNC_MODE_DRIVING);
        refreshMovement(RobotMovement.DRIVE_FORWARD, MOTOR_SPEED);
    }

    public void driveBackward()
    {
        Log.d("ControlUnit", "Driving backward");
        mNxtTalker.setMotorSpeed(LEFT_WHEEL_MOTOR, (byte) (-1 * MOTOR_SPEED), SYNC_MODE_DRIVING);
        mNxtTalker.setMotorSpeed(RIGHT_WHEEL_MOTOR, (byte) (-1 * MOTOR_SPEED), SYNC_MODE_DRIVING);
        refreshMovement(RobotMovement.DRIVE_BACKWARD, MOTOR_SPEED);
    }

    public boolean isMoving()
    {
        return currentMovement != RobotMovement.STOP;
    }

    public void openClaw()
    {
        try
        {
            Log.i("ControlUnit", "Opening claw");
            mNxtTalker.setMotorSpeed(CLAW_MOTOR, MOTOR_SPEED_CLAW);
            Thread.sleep(1000);
            mNxtTalker.setMotorSpeed(NxtTalker.MOTOR_PORT_ALL, (byte) 0);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void turnLeft()
    {
        Log.d("ControlUnit", "Turning left");
        mNxtTalker.setMotorSpeed(LEFT_WHEEL_MOTOR, (byte) (-1 * MOTOR_SPEED_TURNING), SYNC_MODE_TURNING);
        mNxtTalker.setMotorSpeed(RIGHT_WHEEL_MOTOR, MOTOR_SPEED_TURNING, SYNC_MODE_TURNING);
        refreshMovement(RobotMovement.TURN_LEFT, MOTOR_SPEED_TURNING);
    }

    public void centerObject(FocusObject focusObject)
    {
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

    private void turnRightSlowly()
    {
        Log.d("ControlUnit", "Turning right");
        mNxtTalker.setMotorSpeed(LEFT_WHEEL_MOTOR, MOTOR_SPEED_SLOW, SYNC_MODE_TURNING);
        mNxtTalker.setMotorSpeed(RIGHT_WHEEL_MOTOR, (byte) (-1 * MOTOR_SPEED_SLOW), SYNC_MODE_TURNING);
        refreshMovement(RobotMovement.TURN_RIGHT_SLOWLY, MOTOR_SPEED_SLOW);
    }


    private void turnLeftSlowly()
    {
        Log.d("ControlUnit", "Turning left");
        mNxtTalker.setMotorSpeed(LEFT_WHEEL_MOTOR, (byte) (-1 * MOTOR_SPEED_SLOW), SYNC_MODE_TURNING);
        mNxtTalker.setMotorSpeed(RIGHT_WHEEL_MOTOR, MOTOR_SPEED_SLOW, SYNC_MODE_TURNING);
        refreshMovement(RobotMovement.TURN_LEFT_SLOWLY, MOTOR_SPEED_SLOW);
    }

    public void facePoint(PointF targetPoint)
    {
        double x = mPosTracker.getX();
        double y = mPosTracker.getY();
        // (radians from the y-axes clockwise)
        double robotAngle = mPosTracker.getAngle();

        double facingVectorX = targetPoint.x - x;
        double facingVectorY = targetPoint.y - y;

        double targetAngle = Utils.normalizeAngle(Math.atan(facingVectorX / facingVectorY));
        double radiansToTurn = Utils.normalizeAngle(targetAngle - robotAngle);
        if (facingVectorY < 0)
            radiansToTurn = Utils.normalizeAngle(radiansToTurn + Math.PI);

        double degreesPerSecond = PositionTracker.MAX_RADIANS_PER_SECOND * (MOTOR_SPEED_TURNING / 100.0);

        if (radiansToTurn >= Math.PI)
        {
            // turning left is faster
            radiansToTurn = 2.0 * Math.PI - radiansToTurn;
            turnLeft();
        }
        else
        {
            // turning right is faster
            turnRight();
        }

        double timeToTurn = radiansToTurn / degreesPerSecond;

        Log.i(
                "point facing", String.format(
                        "target angle: %.3f\u00b0, to turn: %.3f\u00b0, time to turn: %.3f",
                        Math.toDegrees(targetAngle), Math.toDegrees(radiansToTurn), timeToTurn));

        try
        {
            Thread.sleep((long) (timeToTurn * 1000.0));
        }
        catch (InterruptedException ex)
        { }
        stopMoving();
    }

    public void driveToPoint(PointF targetPoint)
    {
        facePoint(targetPoint);

        double x = mPosTracker.getX();
        double y = mPosTracker.getY();
        double distanceX = targetPoint.x - x;
        double distanceY = targetPoint.y - y;
        double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);

        double metersPerSecond = PositionTracker.MAX_METERS_PER_SECOND * (MOTOR_SPEED / 100.0);
        double timeToDrive = distance / metersPerSecond;

        Log.i(
                "driving to point", String.format(
                        "distance: %.3f, time to drive: %.3f", distance, timeToDrive)
        );

        driveForward();
        try
        {
            Thread.sleep((long) (timeToDrive * 1000.0));
        }
        catch (InterruptedException ex)
        { }
        stopMoving();
    }

    @Override
    public void stopMoving()
    {
        Log.d("ControlUnit", "Stopping");
        mNxtTalker.setMotorSpeed(NxtTalker.MOTOR_PORT_ALL, (byte) 0);
        refreshMovement(RobotMovement.STOP, (byte) 0);
    }

    private void refreshMovement(RobotMovement nextMovement, byte motorSpeed)
    {
        long now = System.currentTimeMillis();
        long timePassed = now - movementStartingTime;

        if (currentMovement == RobotMovement.STOP)
        {
            // starting to move, haven't travelled any distance yet
            movementStartingTime = now;
            currentMovement = nextMovement;
            currentMotorSpeed = motorSpeed;
            return;
        }

        // still moving in the same direction, changing direction or stopping
        // either way, add the previous movement to the position
        switch (currentMovement)
        {
            case DRIVE_FORWARD:
                mPosTracker.addMovement(PositionTracker.MOVEMENT_DIRECTION.FORWARD, currentMotorSpeed, timePassed);
                break;
            case DRIVE_BACKWARD:
                mPosTracker.addMovement(PositionTracker.MOVEMENT_DIRECTION.BACKWARD, currentMotorSpeed, timePassed);
                break;
            case TURN_LEFT:
            case TURN_LEFT_SLOWLY:
                mPosTracker.addMovement(PositionTracker.MOVEMENT_DIRECTION.LEFT, currentMotorSpeed, timePassed);
                break;
            case TURN_RIGHT:
            case TURN_RIGHT_SLOWLY:
                mPosTracker.addMovement(PositionTracker.MOVEMENT_DIRECTION.RIGHT, currentMotorSpeed, timePassed);
                break;
        }
        movementStartingTime = now;
        currentMovement = nextMovement;
        currentMotorSpeed = motorSpeed;
    }
}

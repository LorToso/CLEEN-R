package com.cleenr.cleen_r;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cleenr.cleen_r.nxt.ChooseDeviceActivity;
import com.cleenr.cleen_r.nxt.NxtTalker;
import com.cleenr.cleen_r.robotcontrolunits.NxtControlUnit;
import com.cleenr.cleen_r.robotcontrolunits.PositionTracker;
import com.cleenr.cleen_r.robotcontrolunits.RobotControlUnit;

import java.util.Timer;
import java.util.TimerTask;


public class ManualControlActivity extends ActionBarActivity
{
    private static final int REQUEST_ENABLE_BT  = 1;
    private static final int REQUEST_FIND_BRICK = 2;

    private BluetoothAdapter mBluetoothAdapter = null;
    private String           mDeviceAddress    = null;
    private NxtTalker        mNXTTalker        = null;
    private RobotControlUnit mRobotControlUnit = null;
    private PositionTracker  mPosTracker       = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_control);

        mNXTTalker = new NxtTalker();
        mPosTracker = new PositionTracker();
        mRobotControlUnit = new NxtControlUnit(mNXTTalker, mPosTracker);

        Button forwardButton = (Button) findViewById(R.id.button_moveForward);
        Button backwardButton = (Button) findViewById(R.id.button_moveBackward);
        Button leftButton = (Button) findViewById(R.id.button_moveLeft);
        Button rightButton = (Button) findViewById(R.id.button_moveRight);
        Button returnToStartingPointButton = (Button) findViewById(R.id.button_return_to_starting_point);

        forwardButton.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mRobotControlUnit.driveForward();
                    }
                }
        );

        backwardButton.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mRobotControlUnit.driveBackward();
                    }
                }
        );

        leftButton.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mRobotControlUnit.turnLeft();
                    }
                }
        );

        rightButton.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mRobotControlUnit.turnRight();
                    }
                }
        );

        returnToStartingPointButton.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mRobotControlUnit.returnToStartingPoint();
                    }
                }
        );

        Timer timer = new Timer("refreshDisplay timer");
        timer.scheduleAtFixedRate(new RefreshPositionDisplayTimerTask(), 0, 100);
    }

    private class RefreshPositionDisplayTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            refreshPositionDisplay();
        }
    }

    private void refreshPositionDisplay()
    {
        runOnUiThread(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        TextView xView = (TextView) findViewById(R.id.textView_x);
                        TextView yView = (TextView) findViewById(R.id.textView_y);
                        TextView angleView = (TextView) findViewById(R.id.textView_angle);

                        xView.setText(String.format("%.2f", mPosTracker.getX()));
                        yView.setText(String.format("%.2f", mPosTracker.getY()));
                        angleView.setText(String.format("%.2f", mPosTracker.getAngle()));
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manual_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_connect:
                findBrick();
                return true;
            case R.id.action_disconnect:
                if (mNXTTalker != null)
                    mNXTTalker.stop();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void findBrick()
    {
        if (!isBluetoothAvailable())
            return;

        if (enableBluetooth())
            startBrickFindingActivity();
    }

    private void startBrickFindingActivity()
    {
        Intent intent = new Intent(this, ChooseDeviceActivity.class);
        startActivityForResult(intent, REQUEST_FIND_BRICK);
    }

    private boolean isBluetoothAvailable()
    {
        if (mBluetoothAdapter != null)
            return true;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter != null)
            return true;

        Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
        return false;
    }

    private boolean enableBluetooth()
    {
        if (mBluetoothAdapter.isEnabled())
            return true;

        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case REQUEST_ENABLE_BT:
                if (resultCode != Activity.RESULT_OK)
                {
                    Toast.makeText(this, "Bluetooth could not be enabled", Toast.LENGTH_LONG).show();
                    break;
                }
                startBrickFindingActivity();
                break;
            case REQUEST_FIND_BRICK:
                if (resultCode != Activity.RESULT_OK)
                {
                    break;
                }
                String address = data.getExtras().getString(ChooseDeviceActivity.EXTRA_DEVICE_ADDRESS);
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                //Toast.makeText(this, address, Toast.LENGTH_LONG).show();
                mDeviceAddress = address;
                mNXTTalker.connect(device);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}

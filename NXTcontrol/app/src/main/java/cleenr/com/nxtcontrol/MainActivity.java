package cleenr.com.nxtcontrol;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jfedor.nxtremotecontrol.ChooseDeviceActivity;
import org.jfedor.nxtremotecontrol.NXTTalker;

public class MainActivity extends Activity
{
    public static final int MESSAGE_TOAST = 1;
    public static final int MESSAGE_STATE_CHANGE = 2;

    public static final String TOAST = "toast";

    public byte mLeftMotorValue;
    public byte mRightMotorValue;
    public byte mGripperMotorValue;

    private int mState;
    private BluetoothAdapter mBluetoothAdapter;
    private String mDeviceAddress = null;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_STATE_CHANGE:
                    mState = msg.arg1;
                    displayState();
                    break;
            }
        }
    };

    private final NXTTalker mNXTTalker = new NXTTalker(mHandler);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        VerticalMotorSpeedSlider leftSlider =
                (VerticalMotorSpeedSlider)findViewById(R.id.seekBar_leftMotor);
        VerticalMotorSpeedSlider rightSlider =
                (VerticalMotorSpeedSlider)findViewById(R.id.seekBar_rightMotor);
        MotorSpeedSlider gripperSlider =
                (MotorSpeedSlider)findViewById(R.id.seekBar_gripperMotor);

        final TextView leftMotorValueTextView =
                (TextView)findViewById(R.id.textView_motorLeftValue);
        final TextView rightMotorValueTextView =
                (TextView)findViewById(R.id.textView_motorRightValue);
        final TextView gripperMotorValueTextView =
                (TextView)findViewById(R.id.textView_gripperMotorValue);

        leftSlider.setOnSeekBarChangeListener(
                new MotorSliderChangeListener(leftMotorValueTextView, mNXTTalker, NXTTalker.MOTOR_PORT_B));

        rightSlider.setOnSeekBarChangeListener(
                new MotorSliderChangeListener(rightMotorValueTextView, mNXTTalker, NXTTalker.MOTOR_PORT_C));

        gripperSlider.setOnSeekBarChangeListener(
                new MotorSliderChangeListener(gripperMotorValueTextView, mNXTTalker, NXTTalker.MOTOR_PORT_A));

        final Button connectBtn =
                (Button)findViewById(R.id.button_connect);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findBrick();
            }
        });
    }

    private void findBrick()
    {
        Intent intent = new Intent(this, ChooseDeviceActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK) {
            String address = data.getExtras().getString(ChooseDeviceActivity.EXTRA_DEVICE_ADDRESS);
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            //Toast.makeText(this, address, Toast.LENGTH_LONG).show();
            mDeviceAddress = address;
            mNXTTalker.connect(device);
        }
    }

    private void displayState()
    {
        final TextView connectedTextView =
                (TextView)findViewById(R.id.textView_connected);
        final Button connectBtn =
                (Button)findViewById(R.id.button_connect);
        String text = "";
        boolean enButton = false;
        switch (mState)
        {
            case NXTTalker.STATE_NONE:
                text = "not connected";
                enButton = true;
                break;
            case NXTTalker.STATE_CONNECTING:
                text = "connecting...";
                enButton = false;
                break;
            case NXTTalker.STATE_CONNECTED:
                text = "connected";
                enButton = false;
                break;
        }
        connectedTextView.setText(text);
        connectBtn.setEnabled(enButton);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

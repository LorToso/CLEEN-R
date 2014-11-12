package cleenr.com.nxtcontrol;

import android.widget.SeekBar;
import android.widget.TextView;

import lejos.nxt.Motor;
import lejos.nxt.NXTMotor;
import lejos.nxt.remote.NXTCommand;
import lejos.nxt.remote.RemoteMotor;
import lejos.pc.comm.NXTCommandConnector;
import lejos.pc.comm.NXTConnectionState;
import lejos.pc.comm.NXTConnector;

/**
 * Created by hudini on 11.11.2014.
 */
public class MotorSliderChangeListener implements SeekBar.OnSeekBarChangeListener {

    private TextView valueTextView;
    private NXTConnector nxtConn;
    private RemoteMotor motor;

    public MotorSliderChangeListener(TextView valueTextView, NXTConnector nxtConn, RemoteMotor motor)
    {
        this.valueTextView = valueTextView;
        this.nxtConn = nxtConn;
        this.motor = motor;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        int motorPerc = i * 2 - 100; // -100% to 100%
        valueTextView.setText(motorPerc + "%");
        if (!isNxtConnected())
            return;

        if (motorPerc == 0)
        {
            motor.stop();
            return;
        }

        motor.setPower(motorPerc);
        motor.setSpeed((int)motor.getMaxSpeed());
        motor.forward();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private boolean isNxtConnected()
    {
        return (nxtConn.getNXTInfo().connectionState ==
                NXTConnectionState.PACKET_STREAM_CONNECTED);
    }
}

package cleenr.com.nxtcontrol;

import android.widget.SeekBar;
import android.widget.TextView;

import org.jfedor.nxtremotecontrol.NXTTalker;

/**
 * Created by hudini on 11.11.2014.
 */
public class MotorSliderChangeListener implements SeekBar.OnSeekBarChangeListener {

    private TextView mValueTextView;
    private NXTTalker mNXTTalker;
    private byte mMotorPort;

    public MotorSliderChangeListener(TextView valueTextView, NXTTalker talker, byte motorPort) {
        mValueTextView = valueTextView;
        mNXTTalker = talker;
        mMotorPort = motorPort;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (!isNxtConnected())
            return;

        byte motorPerc = (byte) (i * 2 - 100); // -100% to 100%
        mValueTextView.setText(motorPerc + "%");
        mNXTTalker.setMotorSpeed(mMotorPort, mMotorPort, NXTTalker.MOTOR_REG_MODE_SYNC);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private boolean isNxtConnected() {
        return mNXTTalker.getState() == NXTTalker.STATE_CONNECTED;
    }
}

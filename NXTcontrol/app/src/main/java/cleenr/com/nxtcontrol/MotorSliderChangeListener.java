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
    private int mMotorPort;
    private MainActivity mActivity;

    public MotorSliderChangeListener(TextView valueTextView, NXTTalker talker, int motorPort, MainActivity activity)
    {
        mValueTextView = valueTextView;
        mNXTTalker = talker;
        mMotorPort = motorPort;
        mActivity = activity;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        byte motorPerc = (byte) (i * 2 - 100); // -100% to 100%
        mValueTextView.setText(motorPerc + "%");

        byte leftMotorValue = 0, rightMotorValue = 0, gripperMotorValue = 0;

        switch(mMotorPort)
        {
            case 0:
                leftMotorValue = motorPerc;
                mActivity.mLeftMotorValue = motorPerc;
                rightMotorValue = mActivity.mRightMotorValue;
                gripperMotorValue = mActivity.mGripperMotorValue;
                break;
            case 1:
                rightMotorValue = motorPerc;
                mActivity.mRightMotorValue = motorPerc;
                leftMotorValue = mActivity.mLeftMotorValue;
                gripperMotorValue = mActivity.mGripperMotorValue;
                break;
            case 2:
                gripperMotorValue = motorPerc;
                mActivity.mGripperMotorValue = motorPerc;
                leftMotorValue = mActivity.mLeftMotorValue;
                rightMotorValue = mActivity.mRightMotorValue;
                break;
        }

        if (!isNxtConnected())
            return;

        mNXTTalker.motors3(leftMotorValue, rightMotorValue, gripperMotorValue, false, true);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private boolean isNxtConnected()
    {
        return mNXTTalker.getState() == NXTTalker.STATE_CONNECTED;
    }
}

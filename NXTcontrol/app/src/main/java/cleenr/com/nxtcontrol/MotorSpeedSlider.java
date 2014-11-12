package cleenr.com.nxtcontrol;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * Created by hudini on 11.11.2014.
 */
public class MotorSpeedSlider extends SeekBar {

    public MotorSpeedSlider(Context context) {
        super(context);
    }

    public MotorSpeedSlider(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MotorSpeedSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getActionMasked() != MotionEvent.ACTION_UP)
            return super.onTouchEvent(event);

        setProgress(50);
        return true;
    }
}

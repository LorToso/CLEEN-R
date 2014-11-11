package cleenr.com.nxtcontrol;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by hudini on 11.11.2014.
 */
public class VerticalMotorSpeedSlider extends VerticalSeekBar {

    public VerticalMotorSpeedSlider(Context context) {
        super(context);
    }

    public VerticalMotorSpeedSlider(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalMotorSpeedSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getActionMasked() != MotionEvent.ACTION_UP)
            return super.onTouchEvent(event);

        setProgress(50);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
        return true;
    }
}

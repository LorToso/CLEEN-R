package cleenr.com.nxtcontrol;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import lejos.nxt.Motor;
import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTCommandConnector;
import lejos.pc.comm.NXTConnectionState;
import lejos.pc.comm.NXTConnector;


public class MainActivity extends Activity {

    private static final NXTConnector nxtConn = new NXTConnector();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                new MotorSliderChangeListener(leftMotorValueTextView, nxtConn, Motor.B));

        rightSlider.setOnSeekBarChangeListener(
                new MotorSliderChangeListener(rightMotorValueTextView, nxtConn, Motor.C));

        gripperSlider.setOnSeekBarChangeListener(
                new MotorSliderChangeListener(gripperMotorValueTextView, nxtConn, Motor.A));

        nxtConn.setDebug(true);
        nxtConn.addLogListener(new NXTCommLogListener() {
            public void logEvent(String arg0) {
                Log.e("NXJ log:", arg0);
            }

            public void logEvent(Throwable arg0) {
                Log.e("NXJ log:", arg0.getMessage(), arg0);
            }
        });

        final Button connectBtn = (Button)findViewById(R.id.button_connect);
        final TextView connectedTextView = (TextView)findViewById(R.id.textView_connected);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nxtConn.connectTo())
                {
                    connectedTextView.setText("connected to:" + nxtConn.getNXTInfo().name);
                    connectBtn.setEnabled(false);
                    NXTCommandConnector.setNXTCommand(new NXTCommand(nxtConn.getNXTComm()));
                }
                else
                {
                    connectedTextView.setText("could not connect");
                }
            }
        });
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

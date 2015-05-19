package com.cleenr.cleen_r;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;


public class ZoneSettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone_setting);

        final Spinner colorSpinner = (Spinner) findViewById(R.id.spinner_color);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.colors, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        colorSpinner.setAdapter(adapter);

        final RadioButton radioSphere = (RadioButton) findViewById(R.id.radioButton_sphere);
        final RadioButton radioRect = (RadioButton) findViewById(R.id.radioButton_rect);

        final ZoneSettingView zoneSettingView = (ZoneSettingView) findViewById(R.id.zoneSettingView);
        zoneSettingView.setControls(colorSpinner, radioSphere, radioRect);

        Button clearButton = (Button) findViewById(R.id.button_clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Globals.searchCategories.clear();
                zoneSettingView.refresh();
            }
        });
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        setResult(RESULT_OK);
        finish();
    }
}

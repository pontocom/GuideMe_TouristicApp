package me.serrao.guidemeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity  {

    protected SharedPreferences appData;
    protected String defaultURL = "http://192.168.10.129:3000";
    protected String mURL;
    protected int mRange;
    protected SeekBar sbRange;
    protected TextView tvRange;
    protected EditText etLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        appData = getSharedPreferences("GuidemeAppData", 0);
        mRange = appData.getInt("RANGE", 10);
        mURL = appData.getString("URL", defaultURL);

        sbRange = (SeekBar) findViewById(R.id.sbRange);
        tvRange = (TextView) findViewById(R.id.tvRange);
        etLocation = (EditText) findViewById(R.id.etLocation);

        sbRange.setProgress(mRange);
        tvRange.setText(mRange + " Km");
        etLocation.setText(mURL);

        sbRange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvRange.setText(progress + " Km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mRange = seekBar.getProgress();
            }
        });
    }

    public void onCancel(View view) {
        finish();
    }

    public void onSave(View view) {
        defaultURL = etLocation.getText().toString();

        SharedPreferences.Editor editor = appData.edit();
        editor.putInt("RANGE", mRange);
        editor.putString("URL", defaultURL);
        editor.commit();

        finish();
    }
}

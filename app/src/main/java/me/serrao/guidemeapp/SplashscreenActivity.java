package me.serrao.guidemeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

public class SplashscreenActivity extends AppCompatActivity implements LocationListener {
    protected ProgressBar progressBar;
    protected LocationManager locationManager;
    protected double latt, logt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        ActivityCompat.requestPermissions(SplashscreenActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
   }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null) {
            latt = location.getLatitude();
            logt = location.getLongitude();

            Log.i("GUIDEME", "Latitude = " + latt + " Longitude = " + logt);

            locationManager.removeUpdates(this);

            progressBar.setVisibility(View.INVISIBLE);

            Intent i = new Intent(SplashscreenActivity.this, MainActivity.class);
            i.putExtra("latitude", latt);
            i.putExtra("longitude", logt);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, this);
                    Log.i("GUIDEME", "Waiting for location updates...");
                } catch (Exception e) {
                    Log.i("GUIDEME", "Unable to obtain location...");
                }
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

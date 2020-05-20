package me.serrao.guidemeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    protected double latt, logt;
    protected SharedPreferences appData;
    protected String defaultURL = "http://192.168.10.129:3000";
    protected String mURL;
    protected int mRange;
    protected ProgressBar pbGetPOI;
    protected GoogleMap myMap;
    protected Map<Marker, Integer> hmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = getIntent();
        latt = i.getDoubleExtra("latitude", 0);
        logt = i.getDoubleExtra("longitude", 0);

        Log.i("GUIDEME", "Location received LAT = " + latt + ", LOG = " + logt);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.myMap);
        supportMapFragment.getMapAsync(this);

        appData = getSharedPreferences("GuidemeAppData", 0);

        pbGetPOI = (ProgressBar) findViewById(R.id.pbGetPoi);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("GUIDE", "I am on OnMAPREADY!");

        myMap = googleMap;

        LatLng myPos = new LatLng(latt, logt);
        googleMap.setMyLocationEnabled(true);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 13));

        loadAllPois();

        myMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.i("GUIDEME", "Click on marker with ID = " + hmap.get(marker));
                Intent i = new Intent(MainActivity.this, DetailsActivity.class);
                i.putExtra("IDPOI", hmap.get(marker));
                startActivity(i);
            }
        });
    }

    public void clickSettings(View view) {
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }

    public void loadAllPois() {
        mRange = appData.getInt("RANGE", 10);
        mURL = appData.getString("URL", defaultURL);

        String endpointURL = mURL + "/poi/range/" + latt + "/" + logt + "/" + mRange * 1000;
        Log.i("GUIDEME", "RANGE = " + mRange);
        Log.i("GUIDEME", "URL = " + endpointURL);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, endpointURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("GUIDEME", "Response = " + response);
                pbGetPOI.setVisibility(View.INVISIBLE);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.getString("status").compareTo("OK") == 0) {
                        JSONArray poi = jsonObject.getJSONArray("poi");
                        hmap = new HashMap<Marker, Integer>();

                        for(int i = 0; i < poi.length(); i++) {
                            JSONObject jsonpoi = poi.getJSONObject(i);
                            Log.i("GUIDEME", "-> " + jsonpoi.getString("name"));

                            Marker marker = myMap.addMarker(new MarkerOptions()
                                    .title(jsonpoi.getString("name"))
                                    .snippet(jsonpoi.getString("address"))
                                    .position(new LatLng(jsonpoi.getDouble("latt"), jsonpoi.getDouble("logt"))));

                            hmap.put(marker, jsonpoi.getInt("id"));
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Some error occured while getting POI information!", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.i("GUIDEME", "Error while processing JSON ->" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pbGetPOI.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "Some error occured while getting POI information!", Toast.LENGTH_SHORT).show();
                Log.i("GUIDEME", "An error has occured ->" + error.getMessage());
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

        pbGetPOI.setVisibility(View.VISIBLE);
    }

    public void onRefresh(View view) {
        if(hmap != null) {
            if(!hmap.isEmpty()) {
                for(Map.Entry<Marker,Integer> entry: hmap.entrySet()) {
                    entry.getKey().remove();
                }
                loadAllPois();
            } else {
                loadAllPois();
            }
        }
    }
}

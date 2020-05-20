package me.serrao.guidemeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONObject;

public class DetailsActivity extends AppCompatActivity {

    protected TextView poiNome;
    protected TextView poiAddress;
    protected TextView poiDescription;
    protected ImageView poiImage;
    protected ProgressBar pbDetails;
    protected int idPOI;
    protected SharedPreferences appData;
    protected String defaultURL = "http://192.168.10.129:3000";
    protected String mURL;
    protected int mRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        poiNome = (TextView) findViewById(R.id.tvNomePOI);
        poiAddress = (TextView) findViewById(R.id.tvAddressPOI);
        poiDescription = (TextView) findViewById(R.id.tvDescriptionPOI);
        poiImage = (ImageView) findViewById(R.id.ivImagePOI);
        pbDetails = (ProgressBar) findViewById(R.id.pbDetails);

        Intent i = getIntent();
        idPOI = i.getIntExtra("IDPOI", 0);

        appData = getSharedPreferences("GuidemeAppData", 0);
        mRange = appData.getInt("RANGE", 10);
        mURL = appData.getString("URL", defaultURL);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, mURL + "/poi/" + idPOI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("GUIDEME", "RESPONSE = " + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("status").compareTo("OK") == 0) {
                        JSONObject poi = jsonObject.getJSONArray("poi").getJSONObject(0);

                        poiNome.setText(poi.getString("name"));
                        poiAddress.setText(poi.getString("address"));
                        poiDescription.setText(poi.getString("description"));

                        Glide.with(DetailsActivity.this)
                                .load(poi.getString("image"))
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        pbDetails.setVisibility(View.INVISIBLE);
                                        return false;
                                    }
                                })
                                .into(poiImage);

                   } else {
                        Toast.makeText(DetailsActivity.this, "Some error occured while getting POI data!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.i("GUIDEME", "Error = " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pbDetails.setVisibility(View.INVISIBLE);
                Toast.makeText(DetailsActivity.this, "Some error occured while getting POI data!", Toast.LENGTH_SHORT).show();
                Log.i("GUIDEME", "ERROR = " + error.toString());
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void onClose(View view) {
        finish();
    }
}

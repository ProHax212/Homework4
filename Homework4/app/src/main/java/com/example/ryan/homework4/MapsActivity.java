package com.example.ryan.homework4;
//Elizabeth is the best!  She's super caring!
//The Logo for LocatorGator was NOT made by me(Ryan Comer), it was found on superbwallpapers.com - https://www.google.com/search?q=alligator+detective&rlz=1C1ASUT_enUS623US623&espv=2&biw=1920&bih=919&source=lnms&tbm=isch&sa=X&ei=S8P3VMWoMtCcyQT7jIGgCg&ved=0CAYQ_AUoAQ#imgdii=_&imgrc=UTMUzl2vsrWr5M%253A%3B2OPsxYjloU4cjM%3Bhttp%253A%252F%252Fcdn.superbwallpapers.com%252Fwallpapers%252Ffunny%252Fcrocodile-detective-26705-1920x1200.jpg%3Bhttp%253A%252F%252Fwww.superbwallpapers.com%252Ffunny%252Fcrocodile-detective-26705%252F%3B1920%3B1200
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_location);
        //setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */

    public void findLocation(View view)
    {

        EditText text = (EditText)findViewById(R.id.locationEditText);
        String message = text.getText().toString();
        String url ="https://maps.googleapis.com/maps/api/geocode/json?address=";
        String[] address = message.split(" ");
        String append = "";

        for(int i = 0; i < address.length; i++)
        {
            if(i != 0) append += "+";
            append += address[i];
        }

        append += "&key=" + "AIzaSyBmoBCUEBFSq7oP9tgTbo9dupDbHKgOKAI";
        url += append;

        try {
            getLatLongFromAddress(url);
        }
        catch(JSONException e)
        {

        }

    }

    public void getLatLongFromAddress(String url) throws JSONException{

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseJson(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    public void parseJson(String response)
    {

        double latitude, longitude;
        Intent intent = new Intent(this, ChangeMapActivity.class);
        String county = "";

        try {
            JSONObject json = new JSONObject(response);
            JSONArray jsonArray = json.getJSONArray("results");
            latitude = jsonArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
            longitude = jsonArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");

            jsonArray = jsonArray.getJSONObject(0).getJSONArray("address_components");
            for(int i = 0; i < jsonArray.length(); i++)
            {
                if(jsonArray.getJSONObject(i).getJSONArray("types").getString(0).equals("administrative_area_level_2"))
                {
                    county = jsonArray.getJSONObject(i).getString("short_name");
                }
            }

            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            intent.putExtra("county", county);

            RadioButton normal = (RadioButton)findViewById(R.id.radioButtonNormal);
            RadioButton hybrid = (RadioButton)findViewById(R.id.radioButtonHybrid);
            RadioButton terrain = (RadioButton)findViewById(R.id.radioButtonTerrain);

            if(normal.isChecked())
            {
               intent.putExtra("map_type", "normal");
            }else if(hybrid.isChecked())
            {
                intent.putExtra("map_type", "hybrid");
            }else if(terrain.isChecked())
            {
                intent.putExtra("map_type", "terrain");
            }

            startActivity(intent);
        }
        catch (JSONException e)
        {

        }

    }

    public String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapMain))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(35, 103)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }
}

package com.example.ryan.homework4;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.RadioButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ChangeMapActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient apiClient;
    private Marker lastLocation;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        apiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        apiClient.connect();

        final Double longitude = intent.getDoubleExtra("longitude", 15.0);
        final Double latitude = intent.getDoubleExtra("latitude", 15.0);
        final String county = intent.getStringExtra("county");
        String mapType = intent.getStringExtra("map_type");

        setContentView(R.layout.activity_maps);

        setUpMapIfNeeded();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Location").snippet(county));
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Location myLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
                if(myLocation != null)
                {
                    if(lastLocation != null)
                    {
                        lastLocation.remove();
                    }
                    lastLocation = mMap.addMarker(new MarkerOptions().position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())).title("Your Location"));
                }

                return true;
            }
        });

        if(mapType.equals("normal")) mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        else if(mapType.equals("hybrid")) mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        else if(mapType.equals("terrain")) mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

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

    }

    @Override
    public void onConnected(Bundle bundle) {
        Location myLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        System.out.println("Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("Suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println("Failed");
    }
}
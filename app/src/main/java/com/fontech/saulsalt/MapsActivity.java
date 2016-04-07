package com.fontech.saulsalt;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private LocationManager locationManager;
    private TextView LongitudeText,LatitudeText;
    private Button SearchButton;

    private GoogleMap mMap;
    private Location mostRecentLocation = null;
    private Marker markerMe;
    private LinearLayout TopLayout;
    private MenuItem action_location,action_no_location;
    private LatLng currentLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        TopLayout = (LinearLayout)findViewById(R.id.TopLayout);
        LongitudeText = (TextView)findViewById(R.id.LongitudeText);
        LatitudeText = (TextView)findViewById(R.id.LatitudeText);
        SearchButton = (Button)findViewById(R.id.SearchButton);

        mapFragment.getMapAsync(this);
        TopLayout.setVisibility(View.GONE);
        SearchButton.setOnClickListener(listener);
    }
    private Button.OnClickListener listener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Double lat = Double.parseDouble(LatitudeText.getText().toString());
                Double lon = Double.parseDouble(LongitudeText.getText().toString());
                LatLng latLng = new LatLng(lat, lon);
                MarkerOptions markerOpt = new MarkerOptions();
                markerOpt.position(latLng);
                markerOpt.title("here");
                markerMe = mMap.addMarker(markerOpt);
                mMap.addPolyline(new PolylineOptions().add(latLng, currentLatLng).width(5).color(Color.GRAY));
            }catch (Exception e){
                Toast.makeText(MapsActivity.this,"Invalid number!!",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    };
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(24.990000, 121.500000);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        getLocation();
    }

    private void getLocation() {//取得裝置的GPS位置資料
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = locationManager.getBestProvider(criteria, true);
        //In order to make sure the device is getting the location, request updates.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        locationManager.requestLocationUpdates(provider, 5000, 5, this);
        mostRecentLocation = locationManager.getLastKnownLocation(provider);
        LatLng latLng = new LatLng(mostRecentLocation.getLatitude(), mostRecentLocation.getLongitude());
        cameraFocusOnMe(latLng);
        currentLatLng = latLng;

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_menu, menu);
        action_location = menu.findItem(R.id.action_location);
        action_no_location = menu.findItem(R.id.action_no_location);
        action_no_location.setVisible(false);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_gps) {
            cameraFocusOnMe(currentLatLng);
        }else if(id == R.id.action_location){
            TopLayout.setVisibility(View.VISIBLE);
            action_location.setVisible(false);
            action_no_location.setVisible(true);
        }else if(id == R.id.action_no_location){
            TopLayout.setVisibility(View.GONE);
            action_location.setVisible(true);
            action_no_location.setVisible(false);
            mMap.clear();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onStop() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(this);
        super.onStop();
    }

    private void cameraFocusOnMe(LatLng latLng ){
        CameraPosition camPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(16)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));
    }


    @Override
    public void onLocationChanged(Location location) {
        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d("latLng",String.valueOf(currentLatLng.longitude) +", "+String.valueOf(currentLatLng.latitude));
//        cameraFocusOnMe(latLng);
//        showMarkerMe(latLng);
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

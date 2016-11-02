package au.com.wsit.project08.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import au.com.wsit.project08.Google.GoogleServicesHelper;
import au.com.wsit.project08.Parse.ParseApiHelper;
import au.com.wsit.project08.R;
import au.com.wsit.project08.service.LocationAlarm;
import au.com.wsit.project08.service.LocationService;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleServicesHelper.GoogleCallback

{
    public static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap mGoogleMap;
    private SupportMapFragment mapFragment;
    private GoogleServicesHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHelper = new GoogleServicesHelper(this, this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mHelper.connect();

        // Start the location alarm
        LocationAlarm alarm = new LocationAlarm(this);
        alarm.startLocationTracking();

        getLocationList();

    }

    private void getLocationList()
    {
        ParseApiHelper helper = new ParseApiHelper();
        helper.getLocations(new ParseApiHelper.GetLocationsCallback()
        {
            @Override
            public void result(ArrayList<Location> locations)
            {
                for (Location location : locations)
                {
                    Log.i(TAG, "Locations we've been: " + location.toString());

                }
            }

            @Override
            public void onFail(String msg)
            {
                Log.i(TAG, "Failed to get past locations");
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mGoogleMap = googleMap;
    }

    private void setMarkers(Location location)
    {
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        MarkerOptions options  = new MarkerOptions()
                .position(latLng)
                .title("Current Location");

        mGoogleMap.addMarker(options);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

    }


    @Override
    public void LocationChanged(Location location)
    {
        setMarkers(location);
    }

    @Override
    public void onConnected(Location location)
    {
        setMarkers(location);
    }

    @Override
    public void permissionResult(boolean result)
    {
        // Failed permissions, get access from user
        ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, 123);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            Log.i(TAG, "Location permission granted");
            // Now get the data
            mHelper = new GoogleServicesHelper(this, this);
            mHelper.connect();

        }
        else
        {
            Log.i(TAG, "Location permission denied");

        }
    }
}

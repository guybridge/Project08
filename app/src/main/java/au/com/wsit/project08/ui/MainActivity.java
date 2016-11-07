package au.com.wsit.project08.ui;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.drive.query.Filter;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Date;

import au.com.wsit.project08.Google.GoogleServicesHelper;
import au.com.wsit.project08.Parse.ParseApiHelper;
import au.com.wsit.project08.R;
import au.com.wsit.project08.service.LocationAlarm;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleServicesHelper.GoogleCallback,
        FilterFragment.Callback

{
    public static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap mGoogleMap;
    private SupportMapFragment mapFragment;
    private GoogleServicesHelper mHelper;
    private Location mCurrentLocation;
    private Button mFilterButton;
    private ImageButton mCurrentLocationButton;
    private ProgressBar mFilterLoading;
    private TextView mFilterDates;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHelper = new GoogleServicesHelper(this, this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFilterButton = (Button) findViewById(R.id.filterButton);
        mCurrentLocationButton = (ImageButton) findViewById(R.id.currentLocationButton);
        mFilterDates = (TextView) findViewById(R.id.datesView);
        mFilterDates.setVisibility(View.INVISIBLE);
        mFilterLoading = (ProgressBar) findViewById(R.id.markerProgressLoading);
        mFilterLoading.setVisibility(View.INVISIBLE);

        mFilterButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                android.app.FragmentManager fm = getFragmentManager();
                FilterFragment filterFragment = new FilterFragment();
                filterFragment.show(fm, "FilterFragment");
            }
        });

        mCurrentLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                LatLng current = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(current, 14.0f);
                mGoogleMap.animateCamera(cameraUpdate);
            }
        });

        // Start the location alarm
        LocationAlarm alarm = new LocationAlarm(this);
        alarm.startLocationTracking();

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mHelper.disconnect();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mHelper.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mGoogleMap = googleMap;
    }

    private void setFilterMarkers(ArrayList<Location> places)
    {
        for (Location location : places)
        {
            setCurrentLocation(location);
        }
    }

    // Sets a marker on the location passed in.
    private void setCurrentLocation(Location location)
    {
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        Date date = new Date(location.getTime());
        String markerTitle = date.toString();

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(markerTitle);

        mGoogleMap.addMarker(options);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

    }


    @Override
    public void LocationChanged(Location location)
    {
        setCurrentLocation(location);
        mCurrentLocation = location;
    }

    @Override
    public void onConnected(Location location)
    {
        setCurrentLocation(location);
        mCurrentLocation = location;
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

    // Filter fragment callback with the date ranges
    @Override
    public void result(Date sourceDate, Date endDate)
    {
        mFilterLoading.setVisibility(View.VISIBLE);
        Log.i(TAG, "Got filter fragment callback");
        // TODO: Sort results
        Log.i(TAG, "Selected date ranges are: " + "source: " + sourceDate.getTime() + " end: " + endDate.getTime());
        mFilterDates.setVisibility(View.VISIBLE);
        mFilterDates.setText(sourceDate + "\n" + endDate);
        ParseApiHelper filter = new ParseApiHelper();
        filter.filterResults(sourceDate, endDate, new ParseApiHelper.FilterCallback()
        {
            @Override
            public void result(ArrayList<Location> locations)
            {
                mFilterLoading.setVisibility(View.INVISIBLE);
                Log.i(TAG, "Got " + locations.size() + " locations");
                Toast.makeText(MainActivity.this, "Got " + locations.size() + " locations", Toast.LENGTH_LONG).show();
                setFilterMarkers(locations);
            }

            @Override
            public void onFail(String msg)
            {
                Log.i(TAG, "Unable to get date ranges: " + msg);
            }
        });

    }
}

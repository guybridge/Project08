package au.com.wsit.project08.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import au.com.wsit.project08.Google.GoogleServicesHelper;
import au.com.wsit.project08.Parse.ParseApiHelper;

/**
 * Created by guyb on 2/11/16.
 */
public class LocationService extends IntentService implements
        GoogleServicesHelper.GoogleCallback
{
    private static final String TAG = LocationService.class.getSimpleName();
    private GoogleServicesHelper mHelper;

    public LocationService()
    {
        super("LocationService");
        setIntentRedelivery(true);

    }

    public LocationService(String name)
    {
        super(name);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i(TAG, "Location service started : )");

        try
        {
            mHelper = new GoogleServicesHelper(this, this);
            mHelper.connect();
        }
        catch(NullPointerException e)
        {
            Log.i(TAG, "Unable to connect google services helper " + e.getMessage());
        }

        return Service.START_NOT_STICKY;

    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Log.i(TAG, "onHandleIntent called");
    }

    private void saveLocation(Location location)
    {
        ParseApiHelper helper = new ParseApiHelper(getApplicationContext());
        helper.addLocation(location, new ParseApiHelper.AddLocationCallback()
        {
            @Override
            public void onSuccess()
            {
                Log.i(TAG, "Saved new location to backend");
            }

            @Override
            public void onFail(String msg)
            {
                Log.i(TAG, "Problem saving location to backend " + msg);
            }
        });
    }

    @Override
    public void LocationChanged(Location location)
    {
        Log.i(TAG, "Location changed");
        saveLocation(location);

    }

    @Override
    public void onConnected(Location location)
    {
        Log.i(TAG, "Google API client is connected");
        saveLocation(location);
    }

    @Override
    public void permissionResult(boolean result)
    {
        Log.i(TAG, "Permission denied: user needs to grant location permission");
    }
}

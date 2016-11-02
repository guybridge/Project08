package au.com.wsit.project08.Parse;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.analytics.Tracker;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import au.com.wsit.project08.utils.TrackerConstants;

/**
 * Helper method to do Parse backend functions
 */
public class ParseApiHelper
{
    private static final String TAG = ParseApiHelper.class.getSimpleName();


    // Callback for adding a location to the Parse backend
    public interface AddLocationCallback
    {
        void onSuccess();
        void onFail(String msg);
    }
    // Callback for getting a list of locations
    public interface GetLocationsCallback
    {
        void result(ArrayList<Location> locations);
        void onFail(String msg);

    }

    public void addLocation(Location location, final AddLocationCallback callback)
    {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Log.i(TAG, "Saving location: " + location.toString() + " to Parse backend");

        ParseObject addLocation = new ParseObject(TrackerConstants.LOCATION_CLASS_NAME);
        addLocation.put(TrackerConstants.KEY_LATITUDE, latitude);
        addLocation.put(TrackerConstants.KEY_LONGITUDE, longitude);
        addLocation.saveInBackground(new SaveCallback()
        {
            @Override
            public void done(ParseException e)
            {
                if(e == null)
                {

                    callback.onSuccess();
                }
                else
                {

                    callback.onFail(e.getMessage());
                }

            }
        });
    }

    public void getLocations(final GetLocationsCallback callback)
    {
        ParseQuery<ParseObject> pastLocations = ParseQuery.getQuery(TrackerConstants.LOCATION_CLASS_NAME);
        pastLocations.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> objects, ParseException e)
            {
                // Create an array list of locations to send back
                ArrayList<Location> locationsList = new ArrayList<Location>();
                if (e == null)
                {
                    for (ParseObject pastLocation : objects)
                    {
                        double latitude = pastLocation.getDouble(TrackerConstants.KEY_LATITUDE);
                        double longitude = pastLocation.getDouble(TrackerConstants.KEY_LONGITUDE);

                        Location locationItem = new Location("Location");
                        locationItem.setLatitude(latitude);
                        locationItem.setLongitude(longitude);

                        locationsList.add(locationItem);
                    }

                    callback.result(locationsList);
                }
                else
                {
                    callback.onFail(e.getMessage());
                }
            }
        });
    }
}

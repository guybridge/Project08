package au.com.wsit.project08.Parse;

import android.location.Location;
import android.util.Log;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import au.com.wsit.project08.service.LocationService;
import au.com.wsit.project08.utils.Distance;
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

    public interface FilterCallback
    {
        void result(ArrayList<Location> locations);
        void onFail(String msg);
    }

    // Adds a location to the backend, check if the current location and last location are too close.
    // If they are too close then we don't check in
    public void addLocation(final Location location, final AddLocationCallback callback)
    {
        // Get the latLng from the location object
        final double latitude = location.getLatitude();
        final double longitude = location.getLongitude();

        // Debug
        Log.i(TAG, "Saving location: " + location.toString() + " to Parse backend");

        // First check the last location saved
        ParseQuery<ParseObject> lastLocation = ParseQuery.getQuery(TrackerConstants.LOCATION_CLASS_NAME);
        lastLocation.addDescendingOrder("createdAt");
        lastLocation.getFirstInBackground(new GetCallback<ParseObject>()
        {
            @Override
            public void done(ParseObject object, ParseException e)
            {
                Date lastCheckIn = object.getCreatedAt();
                double lastLat = object.getDouble(TrackerConstants.KEY_LATITUDE);
                double lastLong = object.getDouble(TrackerConstants.KEY_LONGITUDE);

                Log.i(TAG, "The last location tracked was at: " + lastCheckIn.toString());
                Log.i(TAG, "The latLng for this location is: " + lastLat
                + "," + lastLong);

                double destLatitude = location.getLatitude();
                double destLongitude = location.getLongitude();
                float distanceTohere = Distance.findDistanceBetween(lastLat, lastLong, destLatitude, destLongitude);
                Log.i(TAG, "The distance to here is: " + distanceTohere + " meters");

                // If the distance is greater to here then do the check in, otherwise don't worry.
                if(distanceTohere > 50)
                {
                    Log.i(TAG, "Distance is greater than parameters, going to check user in.");
                    // Add location to backend
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
                else
                {
                    Log.i(TAG, "Distance between current location and last location are too close.");
                    callback.onFail("Distance between current location and last location are too close.");
                }

            }
        });


    }

    // Get a list of the past locations from the Parse backend
    public void getLocations(final GetLocationsCallback callback)
    {
        // Create a parse query object
        ParseQuery<ParseObject> pastLocations = ParseQuery.getQuery(TrackerConstants.LOCATION_CLASS_NAME);
        // Sort descending by date
        pastLocations.addDescendingOrder("createdAt");
        pastLocations.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> objects, ParseException e)
            {
                // Create an array list of locations to send back
                ArrayList<Location> locationsList = new ArrayList<Location>();
                if (e == null)
                {
                    // Loop through the Past locations
                    for (ParseObject pastLocation : objects)
                    {
                        // Get the latitude and longitude from the past location
                        double latitude = pastLocation.getDouble(TrackerConstants.KEY_LATITUDE);
                        double longitude = pastLocation.getDouble(TrackerConstants.KEY_LONGITUDE);
                        long time = pastLocation.getCreatedAt().getTime();

                        // Store in a Location object
                        Location locationItem = new Location("Location");
                        locationItem.setLatitude(latitude);
                        locationItem.setLongitude(longitude);
                        locationItem.setTime(time);

                        // Add the location to the ArrayList of locations
                        locationsList.add(locationItem);
                    }

                    // Send the arrarList back
                    callback.result(locationsList);
                }
                else
                {
                    callback.onFail(e.getMessage());
                }
            }
        });
    }

    // Filter items by date
    public void filterResults(Date startDate, Date endDate, final FilterCallback callback)
    {
        Log.i(TAG, "Filtering results");
        ParseQuery<ParseObject> filterQuery = ParseQuery.getQuery(TrackerConstants.LOCATION_CLASS_NAME);

        // Add 24 hours to the endTime
        Date midnight = new Date(endDate.getTime() + 86400000);
        Date startDateTimeOffset = new Date(startDate.getTime() + 28800000);

        //YYYY-MM-DDTHH:mm:ss.SSSZ
        Log.i(TAG, "Start date is: " + startDateTimeOffset);
        Log.i(TAG, "End date is: " + midnight);

        filterQuery.whereGreaterThan("createdAt", startDate);
        filterQuery.whereLessThan("createdAt", midnight);
        filterQuery.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> objects, ParseException e)
            {
                ArrayList<Location> locationsList = new ArrayList<Location>();
                for (ParseObject pastLocation : objects)
                {
                    double latitude = pastLocation.getDouble(TrackerConstants.KEY_LATITUDE);
                    double longitude = pastLocation.getDouble(TrackerConstants.KEY_LONGITUDE);
                    long time = pastLocation.getCreatedAt().getTime();

                    // Store in a Location object
                    Location locationItem = new Location("Location");
                    locationItem.setLatitude(latitude);
                    locationItem.setLongitude(longitude);
                    locationItem.setTime(time);

                    // Add the location to the ArrayList of locations
                    locationsList.add(locationItem);
                }

                callback.result(locationsList);
            }
        });
    }
}

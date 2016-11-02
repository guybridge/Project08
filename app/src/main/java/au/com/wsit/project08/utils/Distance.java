package au.com.wsit.project08.utils;

import android.location.Location;

/**
 * Created by guyb on 2/11/16.
 */
public class Distance
{
    // Find the distance between two locations
    // The input parameters are the destination
    public static float findDistanceBetween(double sourceLat, double sourceLong, double destLat, double destLong)
    {
        Location userLocation = new Location("User Location");
        userLocation.setLatitude(sourceLat);
        userLocation.setLongitude(sourceLong);

        Location destinationLocation = new Location("Destination Location");
        destinationLocation.setLatitude(destLat);
        destinationLocation.setLongitude(destLong);

        float distance = userLocation.distanceTo(destinationLocation);

        return distance;
    }
}

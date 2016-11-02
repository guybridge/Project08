package au.com.wsit.project08.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by guyb on 2/11/16.
 */
public class LocationAlarm
{
    private static final String TAG = LocationAlarm.class.getSimpleName();
    private AlarmManager mAlarmManager;
    private PendingIntent mPendingAlarmIntent;
    private Context mContext;

    public LocationAlarm(Context context)
    {
        mContext = context;
    }

    public void startLocationTracking()
    {
        Log.i(TAG, "LocationAlarm starting");
        // Get a reference to alarmManager
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent locationService = new Intent(mContext, LocationService.class);
        // Create the pending intent
        mPendingAlarmIntent = PendingIntent.getService(mContext, 0, locationService, 0);
        // Set the alarm to fire every 30 minutes
        mAlarmManager.setInexactRepeating
                (AlarmManager.ELAPSED_REALTIME,
                        AlarmManager.INTERVAL_HALF_HOUR,
                        AlarmManager.INTERVAL_HALF_HOUR,
                        mPendingAlarmIntent);


    }
}

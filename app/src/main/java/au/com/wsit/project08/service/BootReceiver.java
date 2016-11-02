package au.com.wsit.project08.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by guyb on 2/11/16.
 */
public class BootReceiver extends BroadcastReceiver
{
    private static final String TAG = BootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            LocationAlarm alarm = new LocationAlarm(context);
            // Start location tracking on boot
            Log.i(TAG, "onBoot: Starting location tracking");
            alarm.startLocationTracking();
        }
    }
}

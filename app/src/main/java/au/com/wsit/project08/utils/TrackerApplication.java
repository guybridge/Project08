package au.com.wsit.project08.utils;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by guyb on 2/11/16.
 */
public class TrackerApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("uDt4S0JLd5cYXaARgpMdgiwVl1Bm23pTk04hkqFi")
                .clientKey("RMgIadQmwD6bfJNuTP6SWgUxxJNo3mUxJj8tbyco")
                .server("https://parseapi.back4app.com")
                .build());


    }
}

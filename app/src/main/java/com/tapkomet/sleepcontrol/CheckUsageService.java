package com.tapkomet.sleepcontrol;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.Display;

import java.util.Calendar;

/**
 * Created by buster on 14-Mar-18.
 */

public class CheckUsageService extends IntentService
{

    static boolean already_started = false;

    public CheckUsageService()
    {
        super("CheckUsageService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {
        if (already_started) return;

        try
        {
            while (true)
            {
                Thread.sleep(60000);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int wake_hour = preferences.getInt("wake_hour", 7);
                int wake_minute = preferences.getInt("wake_minute", 0);
                int sleep_hour = preferences.getInt("sleep_hour", 7);
                int sleep_minute = preferences.getInt("sleep_minute", 0);

                Calendar rightNow = Calendar.getInstance();
                int current_hour = rightNow.get(Calendar.HOUR_OF_DAY);
                int current_minute = rightNow.get(Calendar.MINUTE);

                if (
                    current_hour >= wake_hour &&
                    current_minute >= wake_minute &&
                    current_hour <= sleep_hour &&
                    current_minute <= sleep_minute)
                {
                    // stop checking if phone is turned off or not
                    break;
                }
                else
                {
                    boolean is_phone_used = false;
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH)
                    {
                        DisplayManager dm = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
                        for (Display display : dm.getDisplays())
                            if (display.getState() != Display.STATE_OFF)
                                is_phone_used = true;
                    }
                    else
                    {
                        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                        is_phone_used = pm.isScreenOn();
                    }

                    if (is_phone_used)
                    {
                        int woke_minutes = preferences.getInt("woke_mins", 0);
                        woke_minutes ++;

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("woke_mins", woke_minutes);
                    }
                }
            }

            stopSelf();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }
}

package com.tapkomet.sleepcontrol;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TimePicker;
import android.view.View;
import android.widget.Toast;


import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity
{
    AppCompatActivity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int wake_hour = preferences.getInt("wake_hour", 7);
        int wake_minute = preferences.getInt("wake_minute", 0);

        int sleep_hour = preferences.getInt("sleep_hour", 7);
        int sleep_minute = preferences.getInt("sleep_minute", 0);

        int woke_mins = preferences.getInt("woke_mins", 0);
        if (woke_mins > 0)
        {
            Toast.makeText(this, "You were awake for " + woke_mins + " minutes last night!",  Toast.LENGTH_LONG).show();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            ((TimePicker)findViewById(R.id.waketime)).setHour(wake_hour);
            ((TimePicker)findViewById(R.id.waketime)).setMinute(wake_minute);
            ((TimePicker)findViewById(R.id.sleeptime)).setHour(sleep_hour);
            ((TimePicker)findViewById(R.id.sleeptime)).setMinute(sleep_minute);
        }

        setTimers();

        ((TimePicker)findViewById(R.id.waketime)).setOnTimeChangedListener(new TimePicker.OnTimeChangedListener()
        {
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute)
            {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("wake_hour", hourOfDay);
                editor.putInt("wake_minute", minute);
                editor.apply();
            }
        });

        ((TimePicker)findViewById(R.id.sleeptime)).setOnTimeChangedListener(new TimePicker.OnTimeChangedListener()
        {
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute)
            {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("sleep_hour", hourOfDay);
                editor.putInt("sleep_minute", minute);
                editor.apply();
            }
        });

        findViewById(R.id.settime_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setTimers();
            }
        });

    }

    public void setTimers()
    {
        Calendar time = Calendar.getInstance();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int hour = preferences.getInt("wake_hour", 7);
        int minute = preferences.getInt("wake_minute", 0);

        int sleep_hour = preferences.getInt("sleep_hour", 7);
        int sleep_minute = preferences.getInt("sleep_minute", 0);

        time.set(time.get(Calendar.YEAR), time.get(Calendar.MONTH), time.get(Calendar.DAY_OF_MONTH), hour, minute);

        if (time.compareTo(Calendar.getInstance()) < 0)
            time.add(Calendar.DAY_OF_MONTH, 1);
        WakeAlarmActivity.alarmReceiver.setAlarm(activity.getApplicationContext(), time);

        time.set(time.get(Calendar.YEAR), time.get(Calendar.MONTH), time.get(Calendar.DAY_OF_MONTH), sleep_hour, sleep_minute);

        if (time.compareTo(Calendar.getInstance()) < 0)
            time.add(Calendar.DAY_OF_MONTH, 1);
        SleepAlarmActivity.sleepAlarmReceiver.setAlarm(activity.getApplicationContext(), time);

    }
}

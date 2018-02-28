package com.tapkomet.sleepcontrol;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TimePicker;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity
{
    AppCompatActivity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        int wake_hour = sharedPref.getInt("wake_hour", 7);
        int wake_minute = sharedPref.getInt("wake_minute", 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            ((TimePicker)findViewById(R.id.waketime)).setHour(wake_hour);
            ((TimePicker)findViewById(R.id.waketime)).setMinute(wake_minute);
        }

        setTimer();

        ((TimePicker)findViewById(R.id.waketime)).setOnTimeChangedListener(new TimePicker.OnTimeChangedListener()
        {
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute)
            {
                SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("wake_hour", hourOfDay);
                editor.putInt("wake_minute", minute);
                editor.commit();

                setTimer();
            }
        });
    }

    public void setTimer()
    {
        Calendar time = Calendar.getInstance();

        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        int hour = sharedPref.getInt("wake_hour", 7);
        int minute = sharedPref.getInt("wake_minute", 0);

        time.set(time.get(Calendar.YEAR), time.get(Calendar.MONTH), time.get(Calendar.DAY_OF_MONTH), hour, minute);

        if (time.compareTo(Calendar.getInstance()) < 0)
            time.add(Calendar.DAY_OF_MONTH, 1);
        AlarmActivity.alarmReceiver.setAlarm(activity.getApplicationContext(), time);
    }
}

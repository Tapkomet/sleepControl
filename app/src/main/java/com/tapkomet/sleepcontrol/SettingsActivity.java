package com.tapkomet.sleepcontrol;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
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
        if (woke_mins > 0) Toast.makeText(this, "You were awake for " + woke_mins + " minutes last night!",  Toast.LENGTH_LONG).show();

        ((EditText)findViewById(R.id.wake_hours)).setText(Integer.valueOf(wake_hour).toString());
        ((EditText)findViewById(R.id.wake_mins)).setText(Integer.valueOf(wake_minute).toString());
        ((EditText)findViewById(R.id.sleep_hours)).setText(Integer.valueOf(sleep_hour).toString());
        ((EditText)findViewById(R.id.sleep_mins)).setText(Integer.valueOf(sleep_minute).toString());

        setTimers();

        findViewById(R.id.settime_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean time_conversion_error = false;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();

                int wake_h = Integer.valueOf(((EditText)findViewById(R.id.wake_hours)).getText().toString());
                if (wake_h < 24) editor.putInt("wake_hour", wake_h);
                else time_conversion_error = true;

                int wake_m = Integer.valueOf(((EditText)findViewById(R.id.wake_mins)).getText().toString());
                if (wake_m < 60) editor.putInt("wake_minute", wake_m);
                else time_conversion_error = true;

                int sleep_h = Integer.valueOf(((EditText)findViewById(R.id.sleep_hours)).getText().toString());
                if (sleep_h < 24) editor.putInt("sleep_hour", sleep_h);
                else time_conversion_error = true;

                int sleep_m = Integer.valueOf(((EditText)findViewById(R.id.sleep_mins)).getText().toString());
                if (sleep_m < 60) editor.putInt("sleep_minute", sleep_m);
                else time_conversion_error = true;

                editor.commit();

                if (time_conversion_error)
                    Toast.makeText(getApplicationContext(), "Invalid time entered!",  Toast.LENGTH_LONG).show();
                else
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

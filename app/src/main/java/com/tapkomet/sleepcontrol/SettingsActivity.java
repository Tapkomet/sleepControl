package com.tapkomet.sleepcontrol;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.LinearLayout;
import android.view.View;
import android.widget.Toast;
import com.facebook.ads.*;


import java.io.File;
import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity
{
    AppCompatActivity activity = this;

    private AdView adView;

    static int currentSnoozes;
    static int totalSnoozes;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setTimers();

        findViewById(R.id.choose_audio).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                startActivityForResult(intent, 5);
            }});

        findViewById(R.id.set_settings).setOnClickListener(new View.OnClickListener()
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

                editor.putBoolean("skip_monday", ((CheckBox)findViewById(R.id.skip_monday)).isChecked());
                editor.putBoolean("skip_tuesday", ((CheckBox)findViewById(R.id.skip_tuesday)).isChecked());
                editor.putBoolean("skip_wednesday", ((CheckBox)findViewById(R.id.skip_wednesday)).isChecked());
                editor.putBoolean("skip_thursday", ((CheckBox)findViewById(R.id.skip_thursday)).isChecked());
                editor.putBoolean("skip_friday", ((CheckBox)findViewById(R.id.skip_friday)).isChecked());
                editor.putBoolean("skip_saturday", ((CheckBox)findViewById(R.id.skip_saturday)).isChecked());
                editor.putBoolean("skip_sunday", ((CheckBox)findViewById(R.id.skip_sunday)).isChecked());

                Spinner spinner = findViewById(R.id.snoozes);
                String selected = spinner.getSelectedItem().toString();
                totalSnoozes = Integer.parseInt(selected);
                currentSnoozes = totalSnoozes;

                editor.commit();

                if (time_conversion_error)
                    Toast.makeText(getApplicationContext(), "Invalid time entered!",  Toast.LENGTH_LONG).show();
                else
                {
                    Toast.makeText(getApplicationContext(), "New settings are set.", Toast.LENGTH_LONG).show();
                    setTimers();
                }
            }
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int wake_hour = preferences.getInt("wake_hour", 7);
        int wake_minute = preferences.getInt("wake_minute", 0);

        int sleep_hour = preferences.getInt("sleep_hour", 7);
        int sleep_minute = preferences.getInt("sleep_minute", 0);
        int grace = preferences.getInt("grace", 5);
        int totalSnoozes = preferences.getInt("totalSnoozes", 3);

        ((EditText)findViewById(R.id.wake_hours)).setText(Integer.valueOf(wake_hour).toString());
        ((EditText)findViewById(R.id.wake_mins)).setText(Integer.valueOf(wake_minute).toString());
        ((EditText)findViewById(R.id.sleep_hours)).setText(Integer.valueOf(sleep_hour).toString());
        ((EditText)findViewById(R.id.sleep_mins)).setText(Integer.valueOf(sleep_minute).toString());
        ((EditText)findViewById(R.id.grace)).setText(Integer.valueOf(grace).toString());
        ((Spinner)findViewById(R.id.snoozes)).setSelection(totalSnoozes);
        ((CheckBox)findViewById(R.id.skip_monday)).setChecked(preferences.getBoolean("skip_monday", false));
        ((CheckBox)findViewById(R.id.skip_tuesday)).setChecked(preferences.getBoolean("skip_tuesday", false));
        ((CheckBox)findViewById(R.id.skip_wednesday)).setChecked(preferences.getBoolean("skip_wednesday", false));
        ((CheckBox)findViewById(R.id.skip_thursday)).setChecked(preferences.getBoolean("skip_thursday", false));
        ((CheckBox)findViewById(R.id.skip_friday)).setChecked(preferences.getBoolean("skip_friday", false));
        ((CheckBox)findViewById(R.id.skip_saturday)).setChecked(preferences.getBoolean("skip_saturday", true));
        ((CheckBox)findViewById(R.id.skip_sunday)).setChecked(preferences.getBoolean("skip_sunday", true));

        // Instantiate an AdView view
        adView = new AdView(this, "YOUR_PLACEMENT_ID", AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        adView.loadAd();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
    {
        if (resultCode == Activity.RESULT_OK && requestCode == 5)
        {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null)
            {
                Log.d("RINGTONE", uri.toString());
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("ringtone", uri.toString());
                editor.commit();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
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

package com.tapkomet.sleepcontrol;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.View;
import android.widget.Toast;
import com.facebook.share.widget.ShareDialog;
import com.facebook.share.model.ShareLinkContent;
import android.net.Uri;
import com.facebook.ads.*;


import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity
{
    AppCompatActivity activity = this;

    static final int POINTS_TO_NEXT_LEVEL = 100;

    int level;
    int points;
    int streak;

    ShareDialog shareDialog;

    private AdView adView;

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

        level = preferences.getInt("level", 1);
        points = preferences.getInt("points", 0);
        streak = preferences.getInt("streak", 0);

        if (preferences.getBoolean("slept", false))
        {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("slept", false);

            int woke_mins = preferences.getInt("woke_mins", 0);
            if (woke_mins > 1)
            {
                Toast.makeText(this, "You were awake for " + woke_mins + " minutes last night! You lose 10 points!", Toast.LENGTH_LONG).show();

                streak = 0;
                editor.putInt("streak", 0);

                points -= 10;
                if (points < 0) points = 0;
                editor.putInt("points", points);

                editor.putInt("woke_mins", 0);
            }
            else
            {
                Toast.makeText(this, "You were sleeping like a baby last night! You got 10 points!", Toast.LENGTH_LONG).show();

                streak ++;
                editor.putInt("streak", streak);

                points += 10;
                if (points > POINTS_TO_NEXT_LEVEL)
                {
                    points -= POINTS_TO_NEXT_LEVEL;
                    level ++;
                }
                editor.putInt("points", points);
                editor.putInt("level", level);
            }

            editor.commit();
        }

        ((TextView)findViewById(R.id.level)).setText("LEVEL: " + level);
        ((TextView)findViewById(R.id.points)).setText("POINTS: " + points + "/" + POINTS_TO_NEXT_LEVEL);
        ((ProgressBar)findViewById(R.id.progress)).setMax(POINTS_TO_NEXT_LEVEL);
        ((ProgressBar)findViewById(R.id.progress)).setProgress(points);
        ((TextView)findViewById(R.id.streak)).setText("SLEEP STREAK: " + streak + " DAYS");

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
        shareDialog = new ShareDialog(this);
        findViewById(R.id.share_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                facebookShare();
            }
        });

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
    public void facebookShare(){
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.tt2kgames.xcomew"))
                .setQuote("So far I've stuck to the sleeping schedule for "+
                        streak+" days in a row and has earned "+points+" points")
                .build();
        shareDialog.show(content);
    }
}

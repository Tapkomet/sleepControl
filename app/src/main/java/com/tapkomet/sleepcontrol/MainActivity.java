package com.tapkomet.sleepcontrol;

import android.content.*;
import android.icu.util.Calendar;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;
import com.facebook.ads.*;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

public class MainActivity extends AppCompatActivity
{
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
        performSetup();


    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Toast.makeText(this, "Welcome back!", Toast.LENGTH_LONG).show();
        performSetup();
    }

    /*@Override
    protected void onPause()
    {
        super.onPause();
        Toast.makeText(this, "Pause?!", Toast.LENGTH_LONG).show();
    }*/

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    public void facebookShare(){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Toast.makeText(this, "Slept:" +preferences.getBoolean("slept", false), Toast.LENGTH_LONG).show();

        /*ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.tt2kgames.xcomew"))
                .setQuote("So far I've stuck to the sleeping schedule for "+
                        streak+" days in a row and has earned "+points+" points")
                .build();
        shareDialog.show(content);*/
    }

    public void performSetup(){
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        level = preferences.getInt("level", 1);
        points = preferences.getInt("points", 0);
        streak = preferences.getInt("streak", 0);

        boolean skip_day = false;

        Calendar calendar;
        int day = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
        {
            calendar = Calendar.getInstance();
            day = calendar.get(Calendar.DAY_OF_WEEK);
        }
        switch (day) {
            case Calendar.MONDAY: if (preferences.getBoolean("skip_monday", false)) skip_day = true; break;
            case Calendar.TUESDAY: if (preferences.getBoolean("skip_tuesday", false)) skip_day = true; break;
            case Calendar.WEDNESDAY: if (preferences.getBoolean("skip_wednesday", false)) skip_day = true; break;
            case Calendar.THURSDAY: if (preferences.getBoolean("skip_thursday", false)) skip_day = true; break;
            case Calendar.FRIDAY: if (preferences.getBoolean("skip_friday", false)) skip_day = true; break;
            case Calendar.SATURDAY: if (preferences.getBoolean("skip_saturday", false)) skip_day = true; break;
            case Calendar.SUNDAY: if (preferences.getBoolean("skip_sunday", false)) skip_day = true; break;
        }

        if (skip_day)
        {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("slept", false);
            editor.commit();
        }
        else
        {
            if (preferences.getBoolean("slept", false))
            {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("slept", false);

                int woke_mins = preferences.getInt("woke_mins", 0);
                int grace = preferences.getInt("grace", 1);
                if (woke_mins > grace)
                {
                    Toast.makeText(this, "You were awake for " + woke_mins + " minutes last night! You lose 10 points!", Toast.LENGTH_LONG).show();

                    streak = 0;
                    editor.putInt("streak", 0);

                    points -= 10;
                    if (points < 0) points = 0;
                    editor.putInt("points", points);

                    editor.putInt("woke_mins", 0);
                } else
                {
                    Toast.makeText(this, "You were sleeping like a baby last night! You got 10 points!", Toast.LENGTH_LONG).show();

                    streak++;
                    editor.putInt("streak", streak);

                    points += 10;
                    if (points > POINTS_TO_NEXT_LEVEL)
                    {
                        points -= POINTS_TO_NEXT_LEVEL;
                        level++;
                    }
                    editor.putInt("points", points);
                    editor.putInt("level", level);
                }

                editor.commit();
            }
        }

        ((TextView)findViewById(R.id.level)).setText("LEVEL: " + level);
        ((TextView)findViewById(R.id.points)).setText("POINTS: " + points + "/" + POINTS_TO_NEXT_LEVEL);
        ((ProgressBar)findViewById(R.id.progress)).setMax(POINTS_TO_NEXT_LEVEL);
        ((ProgressBar)findViewById(R.id.progress)).setProgress(points);
        ((TextView)findViewById(R.id.streak)).setText("SLEEP STREAK: " + streak + " DAYS");

        shareDialog = new ShareDialog(this);
        findViewById(R.id.share_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                facebookShare();
            }
        });

        findViewById(R.id.settings_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
                MainActivity.this.startActivity(myIntent);
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
}

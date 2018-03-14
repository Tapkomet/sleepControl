package com.tapkomet.sleepcontrol;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class SleepAlarmActivity extends AppCompatActivity
{
    Timer sound_timer = new Timer();

    static SleepAlarmReceiver sleepAlarmReceiver = new SleepAlarmReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_alarm);


        sound_timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
                } catch (Exception e) { e.printStackTrace(); }
            }
        },
        0, 1000);

        findViewById(R.id.sleep_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        sound_timer.cancel();
        sound_timer.purge();

        new CheckUsageService().startService(new Intent());

        super.onBackPressed();
    }

    @Override
    protected void onStop()
    {
        sound_timer.cancel();
        sound_timer.purge();

        new CheckUsageService().startService(new Intent());

        super.onStop();
    }
}

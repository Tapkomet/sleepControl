package com.tapkomet.sleepcontrol;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class SleepAlarmActivity extends AppCompatActivity
{
    Timer sound_timer = new Timer();

    Ringtone r;

    public static SleepAlarmReceiver sleepAlarmReceiver = new SleepAlarmReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_alarm);

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
        wakeLock.acquire();

        sound_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    Uri notification = Uri.parse(preferences.getString("ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString()));
                    r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
                } catch (Exception e) { e.printStackTrace(); }
            }
        },
        0);

        findViewById(R.id.sleep_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                r.stop();
                finish();
            }
        });
    }

    public void stopProcess()
    {
        sound_timer.cancel();
        sound_timer.purge();

        startService((new Intent(this, CheckUsageService.class)));

        KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
        keyguardLock.disableKeyguard();
    }

    @Override
    public void onBackPressed()
    {
        stopProcess();
        super.onBackPressed();
    }

    @Override
    protected void onStop()
    {
        stopProcess();
        super.onStop();
    }
}

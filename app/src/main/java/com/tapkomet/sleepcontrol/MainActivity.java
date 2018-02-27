package com.tapkomet.sleepcontrol;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity
{
    final Context context = this;
    Timer sound_timer = new Timer();

    AlarmReceiver alarmReceiver = new AlarmReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        ((Button) findViewById(R.id.wake_button)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        ((Button) findViewById(R.id.snooze_button)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Calendar time = Calendar.getInstance();
                time.add(Calendar.SECOND, 6);
                alarmReceiver.setAlarm(context, time);

                finish();
                System.exit(0);
            }
        });
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        sound_timer.cancel();
        sound_timer.purge();
    }
}

package com.tapkomet.sleepcontrol;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class SleepAlarmActivity extends AppCompatActivity
{
    final Context context = this;

    Timer sound_timer = new Timer();
    public static SleepAlarmReceiver sleepAlarmReceiver = new SleepAlarmReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evening_alarm);


        sound_timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    System.out.println("Running");
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
        Calendar time = Calendar.getInstance();
        time.add(Calendar.MINUTE, 5);
        sleepAlarmReceiver.setAlarm(context, time);

        super.onBackPressed();
    }

    @Override
    protected void onStop()
    {
        sound_timer.cancel();
        sound_timer.purge();

        super.onStop();
    }
}

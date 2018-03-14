package com.tapkomet.sleepcontrol;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class WakeAlarmActivity extends AppCompatActivity
{
    final Context context = this;

    Timer sound_timer = new Timer();
    public static WakeAlarmReceiver alarmReceiver = new WakeAlarmReceiver();

    int solution;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_alarm);

        makePuzzle();

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

        findViewById(R.id.wake_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String solution_text = ((EditText) findViewById(R.id.solution_text)).getText().toString();

                if (solution_text.length() == 0)
                    Toast.makeText(context, "Solve the puzzle please!", Toast.LENGTH_SHORT).show();
                else
                {
                    int written_solution = Integer.parseInt(solution_text);

                    if (solution == written_solution)
                        finish();
                    else
                    {
                        ((EditText) findViewById(R.id.solution_text)).setText("");
                        Toast.makeText(context, "Wrong solution, try again!", Toast.LENGTH_SHORT).show();
                        makePuzzle();
                    }
                }
            }
        });

        findViewById(R.id.snooze_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Calendar time = Calendar.getInstance();
                time.add(Calendar.MINUTE, 5);
                alarmReceiver.setAlarm(context, time);

                finish();
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        Calendar time = Calendar.getInstance();
        time.add(Calendar.MINUTE, 5);
        alarmReceiver.setAlarm(context, time);

        super.onBackPressed();
    }

    @Override
    protected void onStop()
    {
        sound_timer.cancel();
        sound_timer.purge();

        super.onStop();
    }

    void makePuzzle()
    {
        int sign = (int) (Math.random() * 3);

        int one = 1 + (int) (Math.random() * 20);
        int two = 1 + (int) (Math.random() * 20);

        switch (sign)
        {
            case 1:
                solution = one - two;
                ((TextView)findViewById(R.id.puzzle)).setText("" + one + " - " + two + " =");
                break;

            case 2:
                solution = one * two;
                ((TextView)findViewById(R.id.puzzle)).setText("" + one + " * " + two + " =");
                break;

            default:
                solution = one + two;
                ((TextView)findViewById(R.id.puzzle)).setText("" + one + " + " + two + " =");
                break;
        }
    }
}
package com.tapkomet.sleepcontrol;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class SleepAlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent main = new Intent(context, SleepAlarmActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(main);
    }

    public void setAlarm(Context context, Calendar time)
    {
        System.out.println("Sleep alarm: "+time);
        Intent intent = new Intent(context, SleepAlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 192837, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get the AlarmManager service
        AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), sender);
    }
}

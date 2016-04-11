package com.cmpe.boun.buyemek;

/**
 * Created by cagatay on 05.03.2016.
 */

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class InitAlarms extends IntentService {

    private static final String TAG = "InitAlarms";

    static final int NOTIF_HOUR1 = 3;
    static final int NOTIF_MIN1 = 1;

    static final int NOTIF_HOUR2 = 3;
    static final int NOTIF_MIN2 = 3;

    public InitAlarms() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent arg0) {
    }

    public static void setAlarms(Context context) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        PendingIntent pendingIntent;
        AlarmManager alarmManager;
        Intent intentsOpen = new Intent(context, TimeAlarm.class);
        intentsOpen.setAction("com.cmpe.boun.buyemek.InitAlarms");
        pendingIntent = PendingIntent.getBroadcast(context,111, intentsOpen, 0);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar now = Calendar.getInstance();

        Calendar lunchTime = Calendar.getInstance();
        lunchTime.set(Calendar.MINUTE, NOTIF_MIN1);
        lunchTime.set(Calendar.HOUR_OF_DAY, NOTIF_HOUR1);
        if (lunchTime.compareTo(now) < 0) { // lunchTime < now
            lunchTime.add(Calendar.DATE,1);
        }
        Log.d("LunchTime: ", df.format(lunchTime.getTime()));

        Calendar dinnerTime = Calendar.getInstance();
        dinnerTime.set(Calendar.MINUTE, NOTIF_MIN2);
        dinnerTime.set(Calendar.HOUR_OF_DAY, NOTIF_HOUR2);
        if (dinnerTime.compareTo(now) < 0) { // dinnerTime < now
            dinnerTime.add(Calendar.DATE,1);
        }
        Log.d("DinnerTime: ", df.format(dinnerTime.getTime()));


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, lunchTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, dinnerTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Log.d(TAG + "_lunch", df.format(lunchTime.getTime()));
        Log.d(TAG + "_dinner", df.format(dinnerTime.getTime()));


    }

}

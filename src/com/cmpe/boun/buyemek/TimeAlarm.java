package com.cmpe.boun.buyemek;

/**
 * Created by cagatay on 05.03.2016.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

public class TimeAlarm extends BroadcastReceiver {

    private static final String TAG = "TimeAlarm";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "Received broadcast intent: " + intent.getAction());

        boolean wantsNotif = SaveSharedPreference.getNotificationStatus(context);
        Log.d("User wants notif:", wantsNotif + " ");
        Log.d("intent.getAction():", intent.getAction());
        if (wantsNotif) {

            DataHandler db = new DataHandler(context);
            ArrayList<Meal> todays = db.getTodaysMeals();

            Calendar lunchTime = Calendar.getInstance();
            lunchTime.set(Calendar.YEAR,Calendar.MONTH,Calendar.DAY_OF_MONTH,InitAlarms.NOTIF_HOUR1,InitAlarms.NOTIF_MIN1 +5);

            Meal currentMeal = null;
            Log.d("size of todays:", ""+todays.size());
            if (lunchTime.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()) {
                if (todays.size()>0 && todays.get(0).time.contains(Meal.MEAL1_TIME)) {
                    currentMeal = todays.get(0);
                }
                else if (todays.size()>1 && todays.get(1).time.contains(Meal.MEAL1_TIME)) {
                    currentMeal = todays.get(1);
                }
            }
            else {
                if (todays.size()>0 && todays.get(0).time.contains(Meal.MEAL2_TIME)) {
                    currentMeal = todays.get(0);
                }
                else if (todays.size()>1 && todays.get(1).time.contains(Meal.MEAL2_TIME)) {
                    currentMeal = todays.get(1);
                }
            }


            if (currentMeal != null) {
                Log.d("current meal for notif:", currentMeal.toString());
                String contextTitle = currentMeal.time.substring(0,1).toUpperCase() + currentMeal.time.substring(1) + " Yemegi";
                String contextText = currentMeal.getJustMeals();

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(contextTitle)
                        .setContentText(contextText);

                Intent resultIntent = new Intent(context, MainActivity.class);

                PendingIntent resultPendingIntent = PendingIntent.getActivity(context,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
                mBuilder.setAutoCancel(true);
                mBuilder.setDefaults(Notification.DEFAULT_SOUND);
                mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
                int mNotificationId = 001;
                NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotifyMgr.notify(mNotificationId, mBuilder.build());

            }
        }

    }
}

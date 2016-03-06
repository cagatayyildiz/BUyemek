package com.cmpe.boun.buyemek;

/**
 * Created by cagatay on 05.03.2016.
 */

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartupReceiver extends BroadcastReceiver {

    private static final String TAG = "StartupReceiver";
    NotificationManager nm;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "Received broadcast intent: " + intent.getAction());
        InitAlarms.setAlarms(context);
    }
}

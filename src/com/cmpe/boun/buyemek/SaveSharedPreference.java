package com.cmpe.boun.buyemek;

/**
 * Created by cagatay on 05.03.2016.
 */

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * Used for storing application specific data in the phone memory
 * Values are specific to the current user
 *
 */
public class SaveSharedPreference {

    static final String NOTIFICATIONS = "notifications";
    static final String NOTIFICATIONS_SET = "notifications_set";
    static final String APP_CREATED = "app_created";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setNotificationStatus(Context ctx, boolean notificationStatus)
    {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(NOTIFICATIONS, notificationStatus);
        editor.commit();
    }

    public static boolean getNotificationStatus(Context ctx)
    {
        return getSharedPreferences(ctx).getBoolean(NOTIFICATIONS, false);
    }

    public static void removeNotificationStatus(Context ctx){
        Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(NOTIFICATIONS);
        editor.commit();
    }

    public static void updateNotificationStatus(Context ctx, boolean newStatus){
        Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(NOTIFICATIONS);
        editor.commit();
        editor.putBoolean(NOTIFICATIONS, newStatus);
        editor.commit();
    }


    public static void setNotificationSetStatus(Context ctx, boolean notificationSetStatus)
    {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(NOTIFICATIONS_SET, notificationSetStatus);
        editor.commit();
    }

    public static boolean getNotificationSetStatus(Context ctx)
    {
        return getSharedPreferences(ctx).getBoolean(NOTIFICATIONS_SET,false);
    }

    public static void removeNotificationSetStatus(Context ctx){
        Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(NOTIFICATIONS_SET);
        editor.commit();
    }

    public static void updateNotificationSetStatus(Context ctx, boolean newStatus){
        Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(NOTIFICATIONS_SET);
        editor.commit();
        editor.putBoolean(NOTIFICATIONS_SET, newStatus);
        editor.commit();
    }


    public static void setAppInstalledFlag(Context ctx, boolean notificationStatus)
    {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(APP_CREATED, notificationStatus);
        editor.commit();
    }

    public static boolean getAppInstalledFlag(Context ctx)
    {
        return getSharedPreferences(ctx).getBoolean(APP_CREATED, false);
    }

    public static void removeAppInstalledFlag(Context ctx){
        Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(APP_CREATED);
        editor.commit();
    }

    public static void updateAppInstalledFlag(Context ctx, boolean newStatus){
        Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(APP_CREATED);
        editor.commit();
        editor.putBoolean(APP_CREATED, newStatus);
        editor.commit();
    }

}

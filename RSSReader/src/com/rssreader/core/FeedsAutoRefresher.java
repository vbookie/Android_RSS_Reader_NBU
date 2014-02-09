package com.rssreader.core;

import com.rssreader.R;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Class used to set and trigger refresh of the feeds.
 * 
 * @author Viktor Bukurov
 * @version 1.0
 * @since 2014-02-10
 */
public class FeedsAutoRefresher extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(intent);
	}

	/**
	 * Schedules the autorefresh of feeds.
	 * 
	 * @param context the application context.
	 */
	public static void SetAlarm(Context context)
    {
        Intent intent = new Intent(context, FeedsAutoRefresher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);       
        alarmManager.cancel(pendingIntent);
        
        long refreshTime = getRefreshTimeRate(context);
        if (refreshTime > 0)
        	alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
        			System.currentTimeMillis(),
        			1000 * 60 * refreshTime, // Milliseconds * Seconds * Minutes
        			pendingIntent); 
    }

    /**
     * Cancels any schedules of autorefreshing the feeds.
     * 
     * @param context the application context.
     */
    public static void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, FeedsAutoRefresher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
    
    /**
     * Gets the refresh time interval specified in the preferences.
     * 
     * @param context the application context.
     * @return the auto-refresh time interval.
     */
    private static long getRefreshTimeRate(Context context) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		String refreshTimePrefKey = context.getString(R.string.pref_refresh_rate_key);
		String defaultRefreshInterval = context.getString(R.string.pref_refresh_rate_interval_default);
		String refreshTimeString = pref.getString(refreshTimePrefKey, defaultRefreshInterval);
		long refreshTime = Long.parseLong(refreshTimeString);
		return refreshTime;
	}
}

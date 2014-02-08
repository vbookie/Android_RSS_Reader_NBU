package com.rssreader.core;

import com.rssreader.R;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;

public class FeedsAutoRefresher extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(intent);
	}

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

    public static void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, FeedsAutoRefresher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
    
    private static long getRefreshTimeRate(Context context) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		String refreshTimePrefKey = context.getString(R.string.pref_refresh_rate_key);
		String defaultRefreshInterval = context.getString(R.string.pref_refresh_rate_interval_default);
		String refreshTimeString = pref.getString(refreshTimePrefKey, defaultRefreshInterval);
		long refreshTime = Long.parseLong(refreshTimeString);
		return refreshTime;
	}
}

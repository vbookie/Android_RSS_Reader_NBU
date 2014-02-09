package com.rssreader.core;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Used to refresh feeds in the background.
 * 
 * @author Viktor Bukurov
 * @version 1.0
 * @since 2014-02-10
 */
public class FeedsService extends IntentService {	
	private static boolean inProgress = false;
	
	/**
	 * @return whether a refresh is in progress.
	 */
	public static boolean isInProgress() {
		return inProgress;
	}
	
	/**
	 * Creates an instance of FeedsService class.
	 */
	public FeedsService() {
		super("FeedsService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		super.setIntentRedelivery(false);
		inProgress = false;
		Log.d("FeedsService", "Service CREATED.");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("FeedsService", "Service START COMMAND");
		if (!isInProgress()) {
			inProgress = true;
			super.onStartCommand(intent, flags, startId);
		} 
		
		return Service.START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		inProgress = false;
		Log.d("FeedsService", "Service DESTROYED");
	}
	
	/**
	 * @return whether there is an Internet connection or not.
	 */
	public boolean hasInternetConnection() {
		ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = cm.getActiveNetworkInfo();
		if (network != null && network.isConnected()) {		
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected void onHandleIntent(Intent arg0) {
		FeedRefresher refresher = new FeedRefresher(getApplicationContext());
		refresher.refreshFeeds();
		inProgress = false;
		FeedsAutoRefresher.completeWakefulIntent(arg0);
	}
	
}

package com.rssreader.core;

import java.util.ArrayList;
import java.util.Set;

import com.rssreader.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

/**
 * @author Viktor Bukurov
 * 
 */
public class FeedsManager {
	private Context context;
	private ArrayList<Feed> feeds;
	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public FeedsManager(Context context) {
		this.setContext(context);
		this.feeds = new ArrayList<Feed>();
	}
	
	public boolean hasInternetConnection() {
		ConnectivityManager cm = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = cm.getActiveNetworkInfo();
		if (network != null && network.isConnected()) {		
			return true;
		} else {
			return false;
		}
	}

	public ArrayList<FeedItem> getAllFeedItems() {
		ArrayList<FeedItem> feedItems = new ArrayList<FeedItem>();
		for (Feed feed : this.feeds) {
			feedItems.addAll(feed.getFeedItems());
		}
		return feedItems;
	}
	
	public void initializeFeeds() {
		 
	}
	
	public void refreshFeeds() {
		
	}
	
	private Set<String> getFeedUrls() {
		String feedsKey = this.context.getString(R.string.pref_feeds_key);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.context);
		Set<String> set = pref.getStringSet(feedsKey, null);
		return set;
	}
}


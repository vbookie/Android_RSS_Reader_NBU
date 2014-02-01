package com.rssreader.core;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author Viktor Bukurov
 *
 */
public class Feed {
	/* FIELDS */
	private FeedType feedType;
	private String title;
	private String link;
	private String description;
	private Date lastUpdatedDate;
	private ArrayList<FeedItem> feedItems; // TODO: change it to linked list

	/* CONSTRUCTORS */
	/**
	 * Creates a new instance of Feed class with specific type which represents a single Feed.
	 * 
	 * @param type The type of the feed.
	 */
	public Feed(FeedType type){
		this.feedType = type;		
		this.title = null;
		this.link = null;
		this.description = null;
		this.setLastUpdatedDate(new Date());
		this.feedItems = new ArrayList<FeedItem>();
	}

	/* GETTERS AND SETTERS */
	public FeedType getFeedType() {
		return feedType;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}
	
	/* PUBLIC METHODS */
	
	/**
	 * Adds a feed item to this feed.
	 * 
	 * @param item The FeedItem to add.
	 */
	public void addFeedItem(FeedItem item) {
		this.feedItems.add(item);
	}
	
	public ArrayList<FeedItem> getFeedItems() {
		return this.feedItems;
	}
}

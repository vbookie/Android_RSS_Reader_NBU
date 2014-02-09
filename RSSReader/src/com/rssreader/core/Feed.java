package com.rssreader.core;

import java.util.Date;
import java.util.LinkedList;

/**
 * Represents a feed that contains multiple feed items.
 * 
 * @author Viktor Bukurov
 * @version 1.0
 * @since 2014-02-10
 */
public class Feed {
	/* FIELDS */
	private FeedType feedType;
	private String title;
	private String link;
	private String description;
	private Date lastUpdatedDate;
	private LinkedList<FeedItem> feedItems;

	/* CONSTRUCTORS */
	/**
	 * Creates a new instance of Feed class with specific type.
	 * 
	 * @param type The type of the feed.
	 */
	public Feed(FeedType type){
		this.feedType = type;		
		this.title = null;
		this.link = null;
		this.description = null;
		this.setLastUpdatedDate(new Date());
		this.feedItems = new LinkedList<FeedItem>();
	}

	/* GETTERS AND SETTERS */
	
	/**
	 * @return the type of the feed.
	 */
	public FeedType getFeedType() {
		return feedType;
	}
	
	/**
	 * @return the title of the feed.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the new title for the feed.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the link to the website the feed belongs to.
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @param link the link to the website the feed belongs to.
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * @return the description of the feed.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description of the feed.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the last modified date of the feed.
	 */
	public Date getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	/**
	 * @param lastUpdatedDate the last modified date of the feed.
	 */
	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}
	
	/**
	 * @return the feed items that belong to the feed.
	 */
	public LinkedList<FeedItem> getFeedItems() {
		return this.feedItems;
	}
	
	/* PUBLIC METHODS */
	
	/**
	 * Adds a feed item that belongs to this feed.
	 * 
	 * @param item The FeedItem to add.
	 */
	public void addFeedItem(FeedItem item) {
		this.feedItems.add(item);
	}
}

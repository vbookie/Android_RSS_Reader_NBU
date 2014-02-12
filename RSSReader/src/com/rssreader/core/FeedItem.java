package com.rssreader.core;

import java.util.Date;

import com.rssreader.utils.Summarizer;

/**
 * Represents a single feed item.
 * 
 * @author Viktor Bukurov
 * @version 1.0
 * @since 2014-02-10
 */
public class FeedItem {
	/* FIELDS */
	private String title;
	private String description;
	private String link;
	private String guid;
	private String content;
	private String summary;
	private Date publicationDate;
	
	/* CONSTRUCTORS */
	
	/**
	 * Creates an instance of FeedItem class.
	 */
	public FeedItem() {
		this.publicationDate = new Date();
	}
	
	/* GETTERS AND SETTERS */
	
	/**
	 * @return the title of the feed item.
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * @param title the title of the feed item.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * @return the description of the feed item.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param description the description of the feed item.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return the link to the article on the web.
	 */
	public String getLink() {
		return link;
	}
	
	/**
	 * @param link the link to the article on the web.
	 */
	public void setLink(String link) {
		this.link = link;
	}
	
	/**
	 * @return the guid of the feed item. (only for RSS feeds)
	 */
	public String getGuid() {
		return guid;
	}
	
	/**
	 * @param guid the guid of the feed item. (only for RSS feeds)
	 */
	public void setGuid(String guid) {
		this.guid = guid;
	}

	/**
	 * @return the text content of the article.
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the text content of the article.
	 */
	public void setContent(String content) {
		this.content = content;
	}
	
	/**
	 * @return the publication date of the article.
	 */
	public Date getPublicationDate() {
		return publicationDate;
	}

	/**
	 * @param publicationDate the publication date of the article.
	 */
	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}

	/**
	 * @return the summary of the feed item.
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * @param summary the summary of the feed item.
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * Generates a summary for the feed item based on it's content or description.
	 * 
	 * @param summarizer the summarizer to generate the summary.
	 */
	public void generateSummary(Summarizer summarizer) {
		String text = this.content;
		if (text == null || text.trim().isEmpty())
			text = this.description;
		this.summary = summarizer.summarize(text);
	}
}

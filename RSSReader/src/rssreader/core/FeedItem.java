package rssreader.core;

import java.util.Date;

/**
 * @author Viktor Bukurov
 *
 */
public class FeedItem {
	/* FIELDS */
	private String title;
	private String description;
	private String link;
	private String guid;
	private String content;
	private Date publicationDate;
	
	/* CONSTRUCTORS */
	
	/**
	 * Creates an instance of FeedItem class which represents a single feed item.
	 */
	public FeedItem() {
		this.publicationDate = new Date();
	}
	
	/* GETTERS AND SETTERS */
	
	/**
	 * Gets the title of this feed item.
	 * @return The title.
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Sets the title of this feed item.
	 * 
	 * @param title The title.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getLink() {
		return link;
	}
	
	public void setLink(String link) {
		this.link = link;
	}
	
	public String getGuid() {
		return guid;
	}
	
	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public Date getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}
}

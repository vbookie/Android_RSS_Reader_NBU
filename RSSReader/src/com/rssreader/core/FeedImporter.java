package com.rssreader.core;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.net.Uri;

/**
 * @author Viktor Bukurov
 *
 */
class FeedImporter {
	private final URL url;
	private final Uri uri;
	private final Context context;
	
	public FeedImporter(String feedUrl, Context context) throws MalformedURLException {
		this.url = new URL(feedUrl);
		this.uri = Uri.parse(feedUrl);
		this.context = context;
	}
	
	public File importFeed() throws IOException
	{
		File file = this.getTempFile();
		
		HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();
		int responseCode = connection.getResponseCode();
		String contentType = connection.getContentType();
		InputStream in = new BufferedInputStream(connection.getInputStream());
		
		if (responseCode == HttpURLConnection.HTTP_OK && contentType != null) {
			OutputStream outputStream = this.context.openFileOutput(file.getName(), Context.MODE_PRIVATE);
			try {
				byte[] buffer = new byte[8192];
				while(in.read(buffer) != -1) {
					outputStream.write(buffer);
				}
			}
			finally {
				in.close();
				outputStream.close();
			}
		}
		return file;
	}
	
	private File getTempFile() throws IOException {
		String fileName = this.uri.getHost() + "." + this.uri.getLastPathSegment();	
	    File file = null;
	    
	    String[] files = this.context.fileList();	    
	    for (int i = 0; i < files.length; i++) {
	    	if (files[i].startsWith(fileName)) {
	    		file = new File(files[i]);
	    		return file;
	    	}
	    }
	    
	    // If cache file doesn't exist a new temp file will be created
	    file = File.createTempFile(fileName, ".xml", this.context.getCacheDir());
	    return file;
	}
}

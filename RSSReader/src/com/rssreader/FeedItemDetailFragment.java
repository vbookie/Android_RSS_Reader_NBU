package com.rssreader;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class FeedItemDetailFragment extends Fragment {
	public static final String ITEM_ARG = "item_arguments";
	public static final String ARG_ITEM_ID = "item_id";
	public static final String ARG_ITEM_TITLE = "item_title";
	public static final String ARG_ITEM_DATE= "item_date";
	public static final String ARG_ITEM_DESCRIPTION= "item_description";
	public static final String ARG_ITEM_CONTENT = "item_content";
	public static final String ARG_ITEM_LINK = "item_link";

	private Bundle itemData = null;
	
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public FeedItemDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			itemData = getArguments();	
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_feeditem_detail,
				container, false);

		if (this.itemData != null) {
			((TextView) rootView.findViewById(R.id.item_detail_headline))
				.setText(itemData.getString(ARG_ITEM_TITLE));
			
			((TextView) rootView.findViewById(R.id.item_detail_dateline))
				.setText(itemData.getString(ARG_ITEM_DATE));
			
			String content = itemData.getString(ARG_ITEM_CONTENT);
			if (content == null || content.trim().isEmpty()) {
				content = itemData.getString(ARG_ITEM_DESCRIPTION);
			}
			((WebView) rootView.findViewById(R.id.item_detail_content_web))
				.loadData(content, "text/html", null);
			
			Button button = (Button) rootView.findViewById(R.id.item_detail_button_browser);
			button.setOnClickListener(new View.OnClickListener() {
				    @Override
				    public void onClick(View v) {
				        String link = itemData.getString(ARG_ITEM_LINK);
				        Intent i = new Intent(Intent.ACTION_VIEW);
				        i.setData(Uri.parse(link));
				        startActivity(i);
				    }
			});
		}

		return rootView;
	}
}

package com.rssreader;

import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.util.AttributeSet;

public class FeedsPreference extends Preference {

	public FeedsPreference(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public FeedsPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public FeedsPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onClick() {
		Context context = this.getContext();
		Intent intent = new Intent(context, SettingsActivity.class);
		context.startActivity(intent);
	}
}

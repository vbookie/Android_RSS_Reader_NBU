package com.rssreader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

public class SettingsSingleFeedActivity extends Activity {
	TextView descriptionView;
	EditText editTextField;

	String originalUrl;
	int position;
	boolean isInEditMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_single_feed);
		setupActionBar();
		
		this.editTextField = (EditText) this.findViewById(R.id.settings_single_feed_url);
		
		Intent intent = this.getIntent();
		if (intent.hasExtra("url")) {
			// Prepare activity for Feed Edit
			this.isInEditMode = true;
			this.originalUrl = intent.getStringExtra("url");
			this.position = intent.getIntExtra("position", -1);
			this.editTextField.setText(originalUrl);
			this.setTitle(R.string.title_activity_settings_edit_feed);
			this.descriptionView = (TextView) this.findViewById(R.id.settings_single_feed_description);
			descriptionView.setText(R.string.settings_single_feed_description_edit);
		} else {
			this.isInEditMode = false;
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(false);
		//getActionBar().hide();
	}

	public void saveButtonClicked(View view) {
		String url = this.editTextField.getText().toString();
		
		// Checks for empty URL
		if (url.trim().isEmpty()) {
			String message = getString(R.string.settings_feed_alert_empty);
			showAlert(message);
			return;
		}
		
		// Checks for invalid URL
		if (url.equals(getString(R.string.settings_single_feed_default_url))) {
			String message = getString(R.string.settings_feed_alert_invalid);
			showAlert(message);
			return;
		}
		try {
			@SuppressWarnings("unused")
			URL realUrl = new URL(url);
		} catch (MalformedURLException e) {
			String message = getString(R.string.settings_feed_alert_invalid);
			showAlert(message);
			return;
		}
		
		// Checks for duplicate URL
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		String feedsKey = getString(R.string.pref_feeds_key);
		Set<String> set = pref.getStringSet(feedsKey, null);
		if (set != null && set.contains(url)) {
			if (!(this.isInEditMode && url.equals(originalUrl))) {
				String message = getString(R.string.settings_feed_alert_duplicate);
				showAlert(message);
				return;
			}
		}
		
		// Send result
		Intent returnIntent = new Intent();
		returnIntent.putExtra("url", url);
		if (isInEditMode)
			returnIntent.putExtra("position", this.position);
		setResult(RESULT_OK, returnIntent);
		finish();
	}
	
	private void showAlert(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
		       //.setTitle(R.string.dialog_title);
			.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // User clicked OK button
	           }
	       });
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public void cancelButtonClicked(View view) {
		Intent returnIntent = new Intent();
		setResult(RESULT_CANCELED, returnIntent);
		finish();
	}
}

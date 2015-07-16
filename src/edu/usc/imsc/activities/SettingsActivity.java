package edu.usc.imsc.activities;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import edu.usc.imsc.R;
import edu.usc.imsc.util.Tools;

public class SettingsActivity extends PreferenceActivity {
	private boolean exitApp;


	// private static HashMap<String, Boolean> settings ;
	public static boolean isChangeSettings = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.layout.preferences);
		setTitle(R.string.settings_title);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences prefs,
					String key) {
				Log.d(Tools.TAG, SettingsActivity.class.getName() + " preference changed");
				isChangeSettings = true;
			}
		};

		prefs.registerOnSharedPreferenceChangeListener(listener);
		
		//	Trajectory
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(Tools.TAG, SettingsActivity.class.getName() + " itemid " + item.getItemId());
		isChangeSettings = true;
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onContentChanged() {
		Log.i(Tools.TAG, SettingsActivity.class.getName() + " onContentChanged");
		super.onContentChanged();
	}

	@Override
	public void onBackPressed() {
		Log.i(Tools.TAG,  SettingsActivity.class.getName() + " onBackPressed");
		exitApp = false;
		super.onBackPressed();
	}

	@Override
	public void onPanelClosed(int featureId, Menu menu) {
		Log.i(Tools.TAG,  SettingsActivity.class.getName() + " onPanelClosed");
		super.onPanelClosed(featureId, menu);
	}

	@Override
	protected void onPause() {
		Log.i(Tools.TAG, SettingsActivity.class.getName() + " onPause");
		if (exitApp) {

		} else {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			boolean btram = prefs.getBoolean("enable_tram_route_pref", false);
			String stram = prefs.getString("favorite_tram_route", "unknown");

			Log.i(Tools.TAG,  SettingsActivity.class.getName() + " tram_pref_enabled =" + btram + "\ttramid =" + stram);
		}
		super.onPause();
	}

	@Override
	protected void onRestart() {
		Log.i(Tools.TAG,  SettingsActivity.class.getName() +  " onRestart");

		super.onRestart();
	}

	@Override
	protected void onResume() {
		exitApp = false;
		Log.i(Tools.TAG,  SettingsActivity.class.getName() + " onResume");

		super.onResume();
	}

	@Override
	protected void onStart() {
		Log.i(Tools.TAG,  SettingsActivity.class.getName() + " onStart");
		super.onStart();
	}

	@Override
	protected void onUserLeaveHint() {
		Log.i(Tools.TAG, SettingsActivity.class.getName() + " onUserLeaveHint");
		super.onUserLeaveHint();
	}
}

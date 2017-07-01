package info.softsolution.ebele.activity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import info.softsolution.ebele.R;
import info.softsolution.ebele.controller.AppController;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener
{
	private static final String TAG = SettingsActivity.class.getSimpleName();
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		updatePreferences();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		updatePreference(key);
	}
	
	private void updatePreferences()
	{
//		updatePreference(AppController.TIME_OPTION);
//		updatePreference(AppController.DATE_RANGE);
//		updatePreference(AppController.DATE_FORMAT);
//		updatePreference(AppController.RINGTONE_PREF);
	}
	
	private void updatePreference(String key)
	{
		Preference pref = findPreference(key);
		if(pref instanceof ListPreference)
		{
			ListPreference listPref = (ListPreference)pref;
			pref.setSummary(listPref.getEntry());
			return;
		}
		
/*
		if(AppController.RINGTONE_PREF.equals(key))
		{
			Uri ringtoneUri = Uri.parse(AppController.getRingtone());
			Ringtone ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
			if(ringtone != null)
			{
				pref.setSummary(ringtone.getTitle(this));
			}
		}
*/
	}
}

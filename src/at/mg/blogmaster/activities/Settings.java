package at.mg.blogmaster.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import at.mg.blogmaster.R;

public class Settings extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings);
	}

}

import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.example.acer.voice.R;

public class SettingsFragment extends PreferenceFragmentCompat{

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_visualizer);
    }
}



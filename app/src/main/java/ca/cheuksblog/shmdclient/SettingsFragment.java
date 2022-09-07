package ca.cheuksblog.shmdclient;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder()
                .build();
        Toolbar toolbar = new Toolbar(getContext());

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        PreferenceManager.getDefaultSharedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(((prefs, s) -> {
                    if (s.equals("server_ip")) {
                        SHMDApi.getInstance().setIp(prefs.getString(s, "10.0.0.125"));
                    } else if (s.equals("server_port")) {
                        SHMDApi.getInstance().setPort(prefs.getString(s, "3030"));
                    }
                }));
    }
}
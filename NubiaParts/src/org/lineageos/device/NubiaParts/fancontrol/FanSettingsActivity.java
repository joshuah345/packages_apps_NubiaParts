package org.lineageos.device.NubiaParts.fancontrol;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;

import org.lineageos.device.NubiaParts.fancontrol.FanSettings;

public class FanSettingsActivity extends AppCompatActivity {

    private static final String TAG = FanSettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(android.R.id.content, FanSettings.class, null)
                    .commit();
        }

    }
}


package org.lineageos.device.NubiaParts.gamekeys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import org.lineageos.device.NubiaParts.gamekeys.Constants;

public class ScreenStateReceiver extends BroadcastReceiver {
    private static final String TAG = "ScreenStateReceiver";
 
    private boolean userEnabledGameKeys(Context context){
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(
                Constants.PREF_KEY, Context.MODE_PRIVATE);
        return prefs.getBoolean(Constants.USER_ENABLE_GAME_KEY_PREF, false);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (userEnabledGameKeys(context)) {
            if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                Log.d(TAG, "Screen ON, wake up game keys");
                KeyController.wake();
            } else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                Log.d(TAG, "Screen OFF, put game keys to sleep");
                KeyController.sleep();
            }
        }
    }
}


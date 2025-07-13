package org.lineageos.device.NubiaParts.fancontrol;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class ScreenStateReceiver extends BroadcastReceiver {

    private boolean mFollowScreen;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(
                Constants.FAN_PREF_NAME, Context.MODE_PRIVATE);
        boolean mFollowScreen = prefs.getBoolean(Constants.SCREEN_STATE_FAN_KEY, false);

        if (mFollowScreen) {
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                FanController.toggle(false);
            } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                FanController.toggle(true);
            }
        }

    }

}

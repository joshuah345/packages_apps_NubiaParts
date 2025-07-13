package org.lineageos.device.NubiaParts.fancontrol;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class ChargingMonitor extends BroadcastReceiver {

    private final String TAG = this.getClass().getSimpleName();

    private boolean willBoostOnCharge;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(
                Constants.FAN_PREF_NAME, Context.MODE_PRIVATE);
        willBoostOnCharge = prefs.getBoolean(Constants.FAN_CHARGING_BOOST_KEY, false);

        if (willBoostOnCharge) {
            if (Intent.ACTION_POWER_CONNECTED.equals(intent.getAction())) {
                FanController.setSpeed(context, Constants.FAN_SPEED_MAX);
                LockManager.lockFan(context.getApplicationContext(),
                        Constants.FAN_SPEED_MAX, TAG);
            } else if (Intent.ACTION_POWER_DISCONNECTED.equals(intent.getAction())) {
                FanController.applyUserSpeed(context);
            }
        }

    }

}

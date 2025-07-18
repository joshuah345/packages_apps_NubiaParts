package org.lineageos.device.NubiaParts.fancontrol;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.util.Log;

public class ChargingMonitor extends BroadcastReceiver {

    private final String TAG = this.getClass().getSimpleName();

    private boolean willBoostOnCharge;
    private boolean fanBoosted = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        IntentFilter batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, batteryFilter);

        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(
                Constants.FAN_PREF_NAME, Context.MODE_PRIVATE);
        willBoostOnCharge = prefs.getBoolean(Constants.FAN_CHARGING_BOOST_KEY, false);

        if (isCharging && willBoostOnCharge) {
            LockManager.lockFan(context, Constants.FAN_SPEED_MAX, TAG);
            FanController.setSpeed(context, Constants.FAN_SPEED_MAX);
            fanBoosted = true;
        } else if (!isCharging || fanBoosted) {
            if (LockManager.getLockReason(context).equals(TAG)) {
                LockManager.unlockFan(context);
            }
            FanController.applyUserSpeed(context);
            fanBoosted = false;
        }
    }
}

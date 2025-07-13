package org.lineageos.device.NubiaParts.fancontrol;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;


public class FanService extends Service {

    boolean userWantsFanEnabled;
    boolean followScreenState;
    boolean willChargeBoost;

    private ScreenStateReceiver mScreenStateReceiver;
    private ChargingMonitor mChargingMonitor;

    Context context = this;

    public final String TAG = this.getClass().getSimpleName();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            if (Constants.INTENT_FAN_START.equals(action)) {
                Log.d(TAG, "Boot has completed, restoring fan service!");
            }

            if (Constants.INTENT_FAN_RELOAD.equals(action)) {
                Log.d(TAG, "Reloading fan service!");
                reload();
            }

            if (Constants.INTENT_FAN_STOP.equals(action)) {
                Log.d(TAG, "STOP intent caught!");
                stopAll();
                stopSelf();
            }

        } else {
            Log.w(TAG, "Intent or action is null, Starting service normally");
            reload();
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void reload() {
        SharedPreferences prefs = getApplicationContext()
                .getSharedPreferences(Constants.FAN_PREF_NAME, Context.MODE_PRIVATE);

        willChargeBoost = prefs.getBoolean(Constants.FAN_CHARGING_BOOST_KEY, false);
        followScreenState = prefs.getBoolean(Constants.SCREEN_STATE_FAN_KEY, false);
        mScreenStateReceiver = new ScreenStateReceiver();
        mChargingMonitor = new ChargingMonitor();
        controlFgAppService(true);
        Log.d(TAG, "Started ForegroundAppService");
        LockManager.unlockFan(getApplicationContext());

        if (willChargeBoost) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_POWER_CONNECTED);
            filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            registerReceiver(mChargingMonitor, filter);
            Log.d(TAG, "Started ChargingMonitor");
        } else {
            try {
                context.unregisterReceiver(mChargingMonitor);
                Log.d(TAG, "Attempted to unregister ChargingMonitor");
            } catch (IllegalArgumentException e) {
                // receiver wasn't registered yet
            }
        }

        if (followScreenState) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(mScreenStateReceiver, filter);
        } else {
            try {
                context.unregisterReceiver(mScreenStateReceiver);
            } catch (IllegalArgumentException e) {
                // receiver wasn't registered yet
            }
        }
        // Ready to run fan
        FanController.toggle(true);
        Log.d(TAG, "Fan toggled ON");
    }

    private void controlFgAppService(boolean mode) {
        if (mode) {
            // start fg service
            if (!ForegroundAppService.isRunning)
                context.startService(new Intent(context, ForegroundAppService.class));
        } else {
            if (ForegroundAppService.isRunning)
                context.stopService(new Intent(context, ForegroundAppService.class));
        }
    }

    private void stopAll() {
        FanController.toggle(false);
        Log.d(TAG, "Disabling fan...");
        LockManager.unlockFan(context);
        Log.d(TAG, "Clearing all existing fan locks..");
        controlFgAppService(false);
        try {
            context.unregisterReceiver(mScreenStateReceiver);
        } catch (IllegalArgumentException e) {
            // receiver wasn't registered yet
        }
        try {
            context.unregisterReceiver(mChargingMonitor);
        } catch (IllegalArgumentException e) {
            // receiver wasn't registered yet
        }
    }

}
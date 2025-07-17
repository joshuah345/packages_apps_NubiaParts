package org.lineageos.device.NubiaParts.fancontrol;

import static android.app.Service.START_STICKY;

import static java.lang.Integer.parseInt;

import android.app.ActivityTaskManager;
import android.app.TaskStackListener;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import org.lineageos.device.NubiaParts.fancontrol.LockManager;

public class ForegroundAppService extends Service {

    private static final String TAG = "ForegroundAppService";
    private static final boolean DEBUG = false;

    private String mPreviousApp;
    private SharedPreferences prefs;

    public static boolean isRunning = false;

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
             prefs = context.getApplicationContext().getSharedPreferences(
                    Constants.FAN_PREF_NAME, Context.MODE_PRIVATE);
             mPreviousApp = " ";
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.d(TAG, "Starting service");
        try {
            ActivityTaskManager.getService().registerTaskStackListener(mTaskListener);
        } catch (RemoteException e) {
            Log.d(TAG, "Unable to register TaskStackListener");
        }
        registerReceiver();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        try {
            this.unregisterReceiver(mIntentReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ActivityTaskManager.getService().unregisterTaskStackListener(mTaskListener);
        } catch (RemoteException e) {
            // listener wasn't registered
        }
        super.onDestroy();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        this.registerReceiver(mIntentReceiver, filter);
    }

    public void cancelAllToasts() {

        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                Toast dummyToast = Toast.makeText(
                        getApplicationContext(),
                        "",
                        Toast.LENGTH_SHORT
                );
                dummyToast.cancel();
            } catch (Exception e) {
                Log.e("ToastUtils", "Failed to cancel Toasts", e);
            }
        });
    }

    public String getAppNameFromPackage(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
            return pm.getApplicationLabel(appInfo).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null; // Package not found
        }
    }

    private void showToast(String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }
    private final TaskStackListener mTaskListener = new TaskStackListener() {
        @Override
        public void onTaskStackChanged() {
            try {
                final ActivityTaskManager.RootTaskInfo focusedTask =
                        ActivityTaskManager.getService().getFocusedRootTaskInfo();
                if (focusedTask != null && focusedTask.topActivity != null) {
                    ComponentName taskComponentName = focusedTask.topActivity;
                    String foregroundApp = taskComponentName.getPackageName();
                    Log.d(TAG,"NEW foreground app" + foregroundApp);
                    if (!foregroundApp.equals(mPreviousApp)) {
                        if (prefs.contains(foregroundApp)) {
                            Log.d(TAG, foregroundApp + " found in speed list");
                            String fanSpeed = prefs.getString(foregroundApp, null);
                            if (fanSpeed != null) {
                                Log.d(TAG, "Setting fan speed for " + foregroundApp);
                                FanController.setSpeed(getApplicationContext(), parseInt(fanSpeed));
                                cancelAllToasts();
                                showToast("Set fan speed " + fanSpeed + " for "
                                + getAppNameFromPackage(getApplicationContext(), foregroundApp));
                            }
                        } else {
                            if (!FanController.getSpeed().equals(FanController.getUserSpeed(getApplicationContext())))
                            {
                                FanController.applyUserSpeed(getApplicationContext());
                                cancelAllToasts();
                                showToast("Reset fan speed to " + FanController.getUserSpeed(getApplicationContext()));
                            }
                        }
                        mPreviousApp = foregroundApp;
                    }
                }
            } catch (Exception e) {}
        }
    };
}
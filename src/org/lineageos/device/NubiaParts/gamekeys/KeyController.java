package org.lineageos.device.NubiaParts.gamekeys;

import android.util.Log;

import org.lineageos.device.NubiaParts.gamekeys.Constants;

public class KeyController {

    private static final String TAG = KeyController.class.getSimpleName();

    public static void wake() {
        Utils.writeValue(Constants.LEFT_SHOULDER, String.valueOf(Constants.WAKE_MODE_INT));
        Log.d(TAG, Constants.LEFT_SHOULDER + " value is " + Utils.readLine(Constants.LEFT_SHOULDER));
        Utils.writeValue(Constants.RIGHT_SHOULDER, String.valueOf(Constants.WAKE_MODE_INT));
        Log.d(TAG, Constants.RIGHT_SHOULDER + " value is " + Utils.readLine(Constants.RIGHT_SHOULDER));
    }

    public static void sleep() {
        Utils.writeValue(Constants.LEFT_SHOULDER, String.valueOf(Constants.SLEEP_MODE_INT));
        Log.d(TAG, Constants.LEFT_SHOULDER + " value is " + Utils.readLine(Constants.LEFT_SHOULDER));
        Utils.writeValue(Constants.RIGHT_SHOULDER, String.valueOf(Constants.SLEEP_MODE_INT));
        Log.d(TAG, Constants.RIGHT_SHOULDER + " value is " + Utils.readLine(Constants.RIGHT_SHOULDER));
    }
}

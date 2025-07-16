package org.lineageos.device.NubiaParts.gamekeys;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.content.Intent;
import android.util.Log;

import org.lineageos.device.NubiaParts.gamekeys.Constants;
import org.lineageos.device.NubiaParts.gamekeys.KeyController;
import org.lineageos.device.NubiaParts.gamekeys.ScreenStateReceiver;

public class GameKeyTileService extends TileService {

    private final Context context = this;
    private static boolean isRegistered = false;

    
    private static final String TAG = GameKeyTileService.class.getSimpleName();


    private void registerScreenReceiver(Context context) {
        ScreenStateReceiver screenReceiver = new ScreenStateReceiver();
        if (!isRegistered) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(screenReceiver, filter);
            isRegistered = true;
            Log.d(TAG, "Screen receiver registered");
        }
    }

    private void unregisterScreenReceiver(Context context) {
        ScreenStateReceiver screenReceiver = new ScreenStateReceiver();
        if (isRegistered) {
            try {
                unregisterReceiver(screenReceiver);
                isRegistered = false;
                Log.d(TAG, "Screen receiver unregistered");
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "Receiver was not registered: " + e.getMessage());
            }
        }
    }

    @Override
    public void onStartListening() {
         SharedPreferences prefs = getApplicationContext().getSharedPreferences(
            Constants.PREF_KEY, Context.MODE_PRIVATE);
        Tile tile = getQsTile();
        tile.setLabel("Shoulder Buttons");
        if (Utils.readLine(Constants.LEFT_SHOULDER) != null 
        && Utils.readLine(Constants.RIGHT_SHOULDER) != null) {
            if (prefs.getBoolean(Constants.USER_ENABLE_GAME_KEY_PREF, false)) {
                tile.setState(Tile.STATE_ACTIVE);
                tile.setSubtitle("On");  
            } else {
                tile.setState(Tile.STATE_INACTIVE);
                tile.setSubtitle("Off");  
            }
        } else {
            tile.setState(Tile.STATE_UNAVAILABLE);
        }
        tile.updateTile();
    }

    @Override
    public void onClick() {
            SharedPreferences prefs = getApplicationContext().getSharedPreferences(
            Constants.PREF_KEY, Context.MODE_PRIVATE);
            Tile tile = getQsTile();
            if (tile.getState() == Tile.STATE_ACTIVE) {
                prefs.edit().putBoolean(Constants.USER_ENABLE_GAME_KEY_PREF, false).apply();
                KeyController.sleep();
                unregisterScreenReceiver(this);        
                tile.setSubtitle("Off");  
                tile.setState(Tile.STATE_INACTIVE);
            } else {
                prefs.edit().putBoolean(Constants.USER_ENABLE_GAME_KEY_PREF, true).apply();
                KeyController.wake();
                registerScreenReceiver(this);
                tile.setSubtitle("On");  
                tile.setState(Tile.STATE_ACTIVE);
            }
            tile.updateTile();
    }

}


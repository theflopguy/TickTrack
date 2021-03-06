package com.theflopguyproductions.ticktrack.startup.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.theflopguyproductions.ticktrack.utils.helpers.PowerSaverHelper;

public class OptimiserService extends Service {

    public static final String ACTION_BATTERY_OPTIMISE_CHECK_START = "ACTION_BATTERY_OPTIMISE_CHECK_START";
    public static final String ACTION_BATTERY_OPTIMISE_CHECK_STOP = "ACTION_BATTERY_OPTIMISE_CHECK_STOP";

    private Handler refreshHandler = new Handler();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {

            String action = intent.getAction();

            assert action != null;
            switch (action) {
                case ACTION_BATTERY_OPTIMISE_CHECK_START:
                    refreshHandler.post(refreshRunnable);
                    break;
                case ACTION_BATTERY_OPTIMISE_CHECK_STOP:
                    Intent broadcastIntent = new Intent(getApplicationContext(), BatteryOptimiseReceiver.class);
                    broadcastIntent.setAction(BatteryOptimiseReceiver.ACTION_OPTIMISING_DONE_CHECK);
                    sendBroadcast(broadcastIntent);
                    stopSelf();
                    onDestroy();
                    break;
            }
        }
        return START_STICKY;
    }

    Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            if(PowerSaverHelper.getIfAppIsWhiteListedFromBatteryOptimizations(getApplicationContext(), getPackageName())
                    .equals(PowerSaverHelper.WhiteListedInBatteryOptimizations.NOT_WHITE_LISTED)){
                refreshHandler.postDelayed(refreshRunnable,250);
            } else {

                Intent stopServiceIntent = new Intent(getApplicationContext(), OptimiserService.class);
                stopServiceIntent.setAction(OptimiserService.ACTION_BATTERY_OPTIMISE_CHECK_STOP);
                startService(stopServiceIntent);
                refreshHandler.removeCallbacks(refreshRunnable);
            }
        }
    };

}
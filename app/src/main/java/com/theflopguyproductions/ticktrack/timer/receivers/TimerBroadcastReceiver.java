package com.theflopguyproductions.ticktrack.timer.receivers;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.theflopguyproductions.ticktrack.timer.data.TimerData;
import com.theflopguyproductions.ticktrack.timer.quick.QuickTimerData;
import com.theflopguyproductions.ticktrack.timer.service.TimerRingService;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class TimerBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_TIMER_BROADCAST = "ACTION_TIMER_BROADCAST";
    public static final String ACTION_QUICK_TIMER_BROADCAST = "ACTION_QUICK_TIMER_BROADCAST";

    private ArrayList<TimerData> timerDataArrayList = new ArrayList<>();
    private ArrayList<QuickTimerData> quickTimerData = new ArrayList<>();
    private int timerIDInteger, quickTimerIDInteger;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Objects.equals(intent.getAction(), ACTION_TIMER_BROADCAST)){

            TickTrackDatabase tickTrackDatabase = new TickTrackDatabase(context);

            timerIDInteger = intent.getIntExtra("timerIntegerID",-1);
            timerDataArrayList = tickTrackDatabase.retrieveTimerList();

            int position = getCurrentTimerPosition();
            if(position!=-1){
                if(timerDataArrayList.get(position).isTimerOn() && !timerDataArrayList.get(position).isTimerPause() || timerDataArrayList.get(position).isTimerRinging()){
                    System.out.println("RECEIVED TIMER FIRST");
                    if((SystemClock.elapsedRealtime() - timerDataArrayList.get(position).getTimerAlarmEndTimeInMillis() >= 0)){

                        System.out.println("RECEIVED TIMER BROADCAST");
                        timerDataArrayList.get(position).setTimerRinging(true);
                        timerDataArrayList.get(position).setTimerNotificationOn(false);
                        timerDataArrayList.get(position).setTimerEndedTimeInMillis(SystemClock.elapsedRealtime());
                        timerDataArrayList.get(position).setTimerStartTimeInMillis(-1);
                        tickTrackDatabase.storeTimerList(timerDataArrayList);
                        System.out.println("TIMER BROADCAST RINGER SERVICE GOT");

                        if(!isMyServiceRunning(TimerRingService.class, context)){
                            startNotificationService(context);
                        }
                    }
                }
            }
        }
        else if (Objects.equals(intent.getAction(), ACTION_QUICK_TIMER_BROADCAST)){
            TickTrackDatabase tickTrackDatabase = new TickTrackDatabase(context);

            quickTimerIDInteger = intent.getIntExtra("timerIntegerID",0);
            quickTimerData = tickTrackDatabase.retrieveQuickTimerList();

            int position = getCurrentQuickTimerPosition();
            if(position!=-1){
                quickTimerData.get(position).setTimerRinging(true);
                quickTimerData.get(position).setTimerNotificationOn(false);
                quickTimerData.get(position).setTimerEndedTimeInMillis(SystemClock.elapsedRealtime());
                quickTimerData.get(position).setTimerStartTimeInMillis(-1);
                tickTrackDatabase.storeQuickTimerList(quickTimerData);
                System.out.println("QUICK TIMER BROADCAST RINGER SERVICE GOT");
                if(!isMyServiceRunning(TimerRingService.class, context)){
                    startNotificationService(context);
                }
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private int getCurrentTimerPosition(){
        for(int i = 0; i < timerDataArrayList.size(); i ++){
            if(timerDataArrayList.get(i).getTimerIntID()==timerIDInteger){
                return i;
            }
        }
        return -1;
    }

    private int getCurrentQuickTimerPosition(){
        for(int i = 0; i < quickTimerData.size(); i ++){
            if(quickTimerData.get(i).getTimerIntID()==quickTimerIDInteger){
                return i;
            }
        }
        return -1;
    }

    private void startNotificationService(Context context) {
        Intent intent = new Intent(context, TimerRingService.class);
        intent.setAction(TimerRingService.ACTION_ADD_TIMER_FINISH);
        context.startService(intent);
    }

}

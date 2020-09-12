package com.theflopguyproductions.ticktrack.application;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.theflopguyproductions.ticktrack.timer.receivers.TimerBroadcastReceiver;
import com.theflopguyproductions.ticktrack.utils.firebase.FirebaseHelper;

import java.util.Objects;

public class TickTrack extends Application {

    public static final String COUNTER_NOTIFICATION = "TICK_TRACK_COUNTER";
    public static final String STOPWATCH_NOTIFICATION = "TICK_TRACK_STOPWATCH";
    public static final String TIMER_RUNNING_NOTIFICATION = "TIMER_RUNNING_NOTIFICATION";
    public static final String TIMER_MISSED_NOTIFICATION = "TIMER_MISSED_NOTIFICATION";
    public static final String TIMER_COMPLETE_NOTIFICATION = "TIMER_COMPLETE_NOTIFICATION";
    public static final String GENERAL_NOTIFICATION = "TICK_TRACK_GENERAL";
    public static final String MISCELLANEOUS_NOTIFICATION = "MISCELLANEOUS_NOTIFICATION";
    public static final String DATA_BACKUP_RESTORE_NOTIFICATION = "DATA_BACKUP_RESTORE_NOTIFICATION";

    public static final int COUNTER_NOTIFICATION_ID = 1;
    public static final int TIMER_RUNNING_NOTIFICATION_ID = 2;
    public static final int TIMER_RINGING_NOTIFICATION_ID = 3;
    public static final int STOPWATCH_NOTIFICATION_ID = 4;
    public static final int TIMER_MISSED_NOTIFICATION_ID = 5;
    public static final int BACKUP_RESTORE_NOTIFICATION_ID = 6;
    public static final int MISCELLANEOUS_NOTIFICATION_ID = 7;
    public static final int GENERAL_NOTIFICATION_ID = 8;

    public static final String COUNTER_NOTIFICATION_DESCRIPTION = "No Sound, Counter feature";
    public static final String STOPWATCH_NOTIFICATION_DESCRIPTION = "No Sound, Stopwatch feature";
    public static final String TIMER_RUNNING_NOTIFICATION_DESCRIPTION = "No Sound, Ongoing Timer feature";
    public static final String TIMER_MISSED_NOTIFICATION_DESCRIPTION = "Make Sound, Missed Timers feature";
    public static final String TIMER_COMPLETE_NOTIFICATION_DESCRIPTION = "Make Sound, Elapsed Timer feature [Required]";
    public static final String GENERAL_NOTIFICATION_DESCRIPTION = "Make Sound, General App Alerts";
    public static final String MISCELLANEOUS_NOTIFICATION_DESCRIPTION = "Make Sound, Important App Alerts";
    public static final String DATA_BACKUP_RESTORE_NOTIFICATION_DESCRIPTION = "No Sound, Backup/Restore feature [Required]";


    @Override
    public void onCreate() {
        super.onCreate();

        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context. NOTIFICATION_SERVICE ) ;
            createCounterChannel(mNotificationManager);
            createGeneralChannel(mNotificationManager);
            createStopwatchChannel(mNotificationManager);
            createTimerChannel(mNotificationManager);
            createTimerCompleteChannel(mNotificationManager);
            createMiscellaneousChannel(mNotificationManager);
            createDataBackupRestoreChannel(mNotificationManager);
            createTimerMissedNotificationChannel(mNotificationManager);

        }

        ComponentName receiver = new ComponentName(this, TimerBroadcastReceiver.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        setupCrashAnalyticsBasicData();

    }

    private void setupCrashAnalyticsBasicData() {
        if(new FirebaseHelper(this).isUserSignedIn()){
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if(account!=null){
                FirebaseCrashlytics.getInstance().setUserId(Objects.requireNonNull(account.getEmail()));
            }
            FirebaseCrashlytics.getInstance().setCustomKey("isUserSignedIn", true);
        } else {
            FirebaseCrashlytics.getInstance().setCustomKey("isUserSignedIn", false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createTimerMissedNotificationChannel(NotificationManager mNotificationManager) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes. CONTENT_TYPE_SONIFICATION )
                .setUsage(AudioAttributes. USAGE_ALARM )
                .build() ;

        int importance = NotificationManager.IMPORTANCE_HIGH ;
        NotificationChannel notificationChannel = new
                NotificationChannel( TIMER_MISSED_NOTIFICATION , "TickTrack Missed Timers" , importance) ;
        notificationChannel.enableLights( true ) ;
        notificationChannel.setLightColor(Color. RED ) ;
        notificationChannel.enableVibration( true ) ;
        notificationChannel.setVibrationPattern( new long []{ 100 , 100 , 100 , 100}) ;
        notificationChannel.setDescription(TIMER_MISSED_NOTIFICATION_DESCRIPTION);

//            notificationChannel.setSound(sound , audioAttributes) ;

        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(notificationChannel) ;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createDataBackupRestoreChannel(NotificationManager mNotificationManager) {

        int importance = NotificationManager. IMPORTANCE_LOW ;
        NotificationChannel notificationChannel = new
                NotificationChannel( DATA_BACKUP_RESTORE_NOTIFICATION , "TickTrack Backup" , importance) ;
        notificationChannel.setDescription(DATA_BACKUP_RESTORE_NOTIFICATION_DESCRIPTION);
        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(notificationChannel) ;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createMiscellaneousChannel(NotificationManager mNotificationManager) {

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes. CONTENT_TYPE_SONIFICATION )
                .setUsage(AudioAttributes. USAGE_ALARM )
                .build() ;

        int importance = NotificationManager. IMPORTANCE_HIGH ;
        NotificationChannel notificationChannel = new
                NotificationChannel( MISCELLANEOUS_NOTIFICATION , "TickTrack Miscellaneous" , importance) ;
        notificationChannel.enableLights( true ) ;
        notificationChannel.setLightColor(Color. YELLOW ) ;
        notificationChannel.enableVibration( true ) ;
        notificationChannel.setDescription(MISCELLANEOUS_NOTIFICATION_DESCRIPTION);
        notificationChannel.setVibrationPattern( new long []{ 200 , 200 , 200 , 200 }) ;

//            notificationChannel.setSound(sound , audioAttributes) ;

        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(notificationChannel) ;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createCounterChannel(NotificationManager mNotificationManager) {

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes. CONTENT_TYPE_SONIFICATION )
                .setUsage(AudioAttributes. USAGE_ALARM )
                .build() ;

        int importance = NotificationManager. IMPORTANCE_HIGH ;
        NotificationChannel notificationChannel = new
                NotificationChannel( COUNTER_NOTIFICATION , "TickTrack Counter" , importance) ;
        notificationChannel.enableLights( true ) ;
        notificationChannel.setLightColor(Color. BLUE ) ;
        notificationChannel.enableVibration( true ) ;
        notificationChannel.setDescription(COUNTER_NOTIFICATION_DESCRIPTION);
        notificationChannel.setVibrationPattern( new long []{ 100 , 100 , 100}) ;

//            notificationChannel.setSound(sound , audioAttributes) ;

        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(notificationChannel) ;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createTimerChannel(NotificationManager mNotificationManager) {

        int importance = NotificationManager.IMPORTANCE_LOW ;
        NotificationChannel notificationChannel = new
                NotificationChannel(TIMER_RUNNING_NOTIFICATION, "TickTrack Ongoing Timer" , importance) ;
        notificationChannel.setDescription(TIMER_RUNNING_NOTIFICATION_DESCRIPTION);
        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(notificationChannel) ;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createTimerCompleteChannel(NotificationManager mNotificationManager) {

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes. CONTENT_TYPE_SONIFICATION )
                .setUsage(AudioAttributes. USAGE_ALARM )
                .build() ;

        int importance = NotificationManager. IMPORTANCE_HIGH ;
        NotificationChannel notificationChannel = new
                NotificationChannel(TIMER_COMPLETE_NOTIFICATION, "TickTrack Elapsed Timer" , importance) ;
        notificationChannel.enableLights( true ) ;
        notificationChannel.setLightColor(Color. GREEN ) ;
        notificationChannel.enableVibration( true ) ;
        notificationChannel.setDescription(TIMER_COMPLETE_NOTIFICATION_DESCRIPTION);
        notificationChannel.setVibrationPattern( new long []{ 200 , 200 , 200 , 200 , 200 , 200 , 200 , 200 , 200 }) ;

//            notificationChannel.setSound(sound , audioAttributes) ;

        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(notificationChannel) ;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createStopwatchChannel(NotificationManager mNotificationManager) {

        int importance = NotificationManager.IMPORTANCE_MIN ;
        NotificationChannel notificationChannel = new
                NotificationChannel( STOPWATCH_NOTIFICATION , "TickTrack Stopwatch" , importance) ;
        notificationChannel.setDescription(STOPWATCH_NOTIFICATION_DESCRIPTION);
        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(notificationChannel) ;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createGeneralChannel(NotificationManager mNotificationManager) {

        int importance = NotificationManager. IMPORTANCE_HIGH ;
        NotificationChannel notificationChannel = new
                NotificationChannel( GENERAL_NOTIFICATION , "TickTrack Notification" , importance) ;
        notificationChannel.enableLights( true ) ;
        notificationChannel.setLightColor(Color. BLUE ) ;
        notificationChannel.enableVibration( true ) ;
        notificationChannel.setVibrationPattern( new long []{ 100 , 100, 100}) ;
        notificationChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription(GENERAL_NOTIFICATION_DESCRIPTION);
        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(notificationChannel) ;

    }

}

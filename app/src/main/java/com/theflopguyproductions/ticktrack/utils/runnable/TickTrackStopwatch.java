package com.theflopguyproductions.ticktrack.utils.runnable;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.theflopguyproductions.ticktrack.stopwatch.StopwatchData;
import com.theflopguyproductions.ticktrack.stopwatch.StopwatchLapData;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackProgressBar;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class TickTrackStopwatch {

    private ArrayList<StopwatchData> stopwatchDataArrayList;
    private ArrayList<StopwatchLapData> stopwatchLapData;
    private TextView hourMinuteText, milliSecondText;
    private long stopwatchRetrievedStartTime, stopwatchDurationElapsed, differenceValue = 0;
    private Handler stopwatchHandler, progressBarHandler;
    private TickTrackDatabase tickTrackDatabase;
    private TickTrackProgressBar progressBar;
    private Context context;

    public TickTrackStopwatch(Context context){
        this.context = context;
        tickTrackDatabase = new TickTrackDatabase(context);
        stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();
        stopwatchLapData = tickTrackDatabase.retrieveStopwatchLapData();
        stopwatchHandler = new Handler();
        progressBarHandler = new Handler();

        if(!(stopwatchDataArrayList.size() > 0)){
            StopwatchData stopwatchData = new StopwatchData();
            stopwatchData.setPause(false);
            stopwatchData.setRunning(false);
            stopwatchData.setLastLapEndTimeInMillis(0);
            stopwatchData.setStopwatchTimerStartTimeInMillis(-1);
            stopwatchData.setStopwatchTimerStartTimeInRealTimeMillis(-1);
            stopwatchDataArrayList.add(stopwatchData);
            tickTrackDatabase.storeStopwatchData(stopwatchDataArrayList);
            stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();
        }


    }

    private final Runnable stopwatchRunnable = this::stopwatchRunnable;
    private final Runnable progressBarRunnable = this::progressBarRunnable;

    private void stopwatchRunnable(){
        if (!stopwatchDataArrayList.get(0).isRunning() || stopwatchDataArrayList.get(0).isPause()) {
            stopwatchHandler.removeCallbacks(stopwatchRunnable);
            return;
        }
        long elapsedRealTime = SystemClock.elapsedRealtime()+differenceValue;
        stopwatchDurationElapsed = elapsedRealTime - stopwatchRetrievedStartTime;
        updateTextView();
        stopwatchHandler.postDelayed(stopwatchRunnable, 1);
    }

    private void updateTextView() {
        if (hourMinuteText != null) {
            String hourMinute = getFormattedHourMinute(stopwatchDurationElapsed, this.hourMinuteText);
            this.hourMinuteText.setText(hourMinute);
        }
        if(milliSecondText != null){
            String milliSecond = getFormattedMillisecond(stopwatchDurationElapsed);
            this.milliSecondText.setText(milliSecond);
        }
    }

    long currentValue = 0, maxProgress = 10000;
    private float getCurrentStep(long currentValue, long maxLength){
        return ((currentValue-0f)/(maxLength-0f)) *(1f-0f)+0f;
    }
    private void progressBarRunnable(){
        if(progressBar!=null){
            if (stopwatchDataArrayList.get(0).isPause()) {
                progressBarHandler.removeCallbacks(progressBarRunnable);
                stopwatchDataArrayList.get(0).setProgressValue(currentValue);
                return;
            }
            if(currentValue<10000 && stopwatchDataArrayList.get(0).getProgressValue() != -1){
                currentValue += 50;
                progressBar.setProgress(getCurrentStep(currentValue, maxProgress));
            } else {
                currentValue = 0;
            }
            progressBarHandler.postDelayed(progressBarRunnable, 10);
            stopwatchDataArrayList.get(0).setProgressValue(currentValue);
        }
    }

    public void start(){
        if (stopwatchDataArrayList.get(0).isRunning())
            throw new IllegalStateException("Already Started");
        else {

            stopwatchRetrievedStartTime = SystemClock.elapsedRealtime();
            stopwatchDurationElapsed = 0;
            differenceValue = 0;

            stopwatchHandler.post(stopwatchRunnable);

            stopwatchLapData.clear();

            stopwatchDataArrayList.get(0).setRunning(true);
            stopwatchDataArrayList.get(0).setPause(false);
            stopwatchDataArrayList.get(0).setLastLapEndTimeInMillis(0);
            stopwatchDataArrayList.get(0).setStopwatchTimerStartTimeInMillis(System.currentTimeMillis());
            stopwatchDataArrayList.get(0).setStopwatchTimerStartTimeInRealTimeMillis(SystemClock.elapsedRealtime());
            tickTrackDatabase.storeStopwatchData(stopwatchDataArrayList);
            stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();

        }
    }

    public void pause(){
        if(stopwatchDataArrayList.get(0).isPause()){
            throw new IllegalStateException("Already Paused");

        } else if(!stopwatchDataArrayList.get(0).isRunning()){
            throw new IllegalStateException("Not Started");
        } else {

            System.out.println("PAUSED STOPWATCH");
            stopwatchHandler.removeCallbacks(stopwatchRunnable);
            progressBarHandler.removeCallbacks(progressBarRunnable);

            stopwatchDataArrayList.get(0).setLastPauseValueInMillis(stopwatchDurationElapsed);
            stopwatchDataArrayList.get(0).setRunning(true);
            stopwatchDataArrayList.get(0).setPause(true);
            stopwatchDataArrayList.get(0).setLastPauseTimeRealTimeInMillis(SystemClock.elapsedRealtime());
            stopwatchDataArrayList.get(0).setLastPauseTimeInMillis(System.currentTimeMillis());
            tickTrackDatabase.storeStopwatchData(stopwatchDataArrayList);
            stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();
            updateTextView();
            //TODO HANDLER PROGRESSBAR SHIT

        }
    }

    public void resume() {
        if(!stopwatchDataArrayList.get(0).isPause()){
            throw new IllegalStateException("Not Paused");
        } else if (!stopwatchDataArrayList.get(0).isRunning()){
            throw new IllegalStateException("Not Started");
        } else {

            System.out.println("RESUME STOPWATCH");

            differenceValue = stopwatchDataArrayList.get(0).getLastPauseValueInMillis();
            stopwatchRetrievedStartTime = SystemClock.elapsedRealtime();

            stopwatchHandler.post(stopwatchRunnable);

            stopwatchDataArrayList.get(0).setPause(false);
            stopwatchDataArrayList.get(0).setRunning(true);
            tickTrackDatabase.storeStopwatchData(stopwatchDataArrayList);
            stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();

            if(currentValue>0){
                progressBarHandler.post(progressBarRunnable);
            }
        }
    }

//    private long getPauseDifference() {
//        if(stopwatchDataArrayList.get(0).isPause() && stopwatchDataArrayList.get(0).isRunning()){
//            return stopwatchDataArrayList.get(0).getLastPauseTimeRealTimeInMillis() - stopwatchDataArrayList.get(0).getStopwatchTimerStartTimeInRealTimeMillis();
//        }
//        return 0;
//    }

    public void stop(){
        if(!stopwatchDataArrayList.get(0).isRunning()){
            throw new IllegalStateException("Not Started");
        } else {

            progressBar.setProgress(0);
            currentValue = 0;
            stopwatchHandler.removeCallbacks(stopwatchRunnable);
            progressBarHandler.removeCallbacks(progressBarRunnable);

            stopwatchDataArrayList.get(0).setRunning(false);
            stopwatchDataArrayList.get(0).setPause(false);
            stopwatchDataArrayList.get(0).setStopwatchTimerStartTimeInMillis(-1);
            stopwatchDataArrayList.get(0).setStopwatchTimerStartTimeInRealTimeMillis(-1);
            stopwatchDataArrayList.get(0).setLastPauseTimeRealTimeInMillis(-1);
            stopwatchDataArrayList.get(0).setLastPauseTimeInMillis(-1);
            stopwatchDataArrayList.get(0).setLastLapEndTimeInMillis(0);
            stopwatchDataArrayList.get(0).setNotification(false);
            tickTrackDatabase.storeStopwatchData(stopwatchDataArrayList);
            stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();

            if(this.hourMinuteText!=null) {
                this.hourMinuteText.setText("00");
                this.hourMinuteText.setTextSize(72);
            }
            if(this.milliSecondText!=null)
                this.milliSecondText.setText("00");

            stopwatchLapData.clear();
            tickTrackDatabase.storeLapNumber(0);
            tickTrackDatabase.storeLapData(stopwatchLapData);

        }
    }

    public void lap(){
        if(!stopwatchDataArrayList.get(0).isRunning()){
            throw new IllegalStateException("Not Started");
        } else {

            long lapTime = stopwatchDurationElapsed - stopwatchDataArrayList.get(0).getLastLapEndTimeInMillis();
            stopwatchDataArrayList.get(0).setLastLapEndTimeInMillis(stopwatchDurationElapsed);

            int currentLapNumber = tickTrackDatabase.retrieveLapNumber();
            if(!(currentLapNumber >= 0)){
                currentLapNumber = 0;
            }

            StopwatchLapData stopwatchLapData = new StopwatchLapData();
            stopwatchLapData.setLapNumber(currentLapNumber+1);
            stopwatchLapData.setElapsedTimeInMillis(stopwatchDurationElapsed);
            stopwatchLapData.setLapTimeInMillis(lapTime);
            this.stopwatchLapData.add(stopwatchLapData);
            tickTrackDatabase.storeLapData(this.stopwatchLapData);
            tickTrackDatabase.storeLapNumber(currentLapNumber+1);

            currentValue = 0;
            progressBarHandler.removeCallbacks(progressBarRunnable);
            progressBarHandler.post(progressBarRunnable);
        }
    }

    public void setGraphics(@Nullable TextView hourMinute, TextView milliSecond, TickTrackProgressBar tickTrackProgressBar) {
        this.hourMinuteText = hourMinute;
        this.milliSecondText = milliSecond;
        this.progressBar = tickTrackProgressBar;
    }

    static String getFormattedMillisecond(long elapsedTime) {
        int milliseconds = (int) ((elapsedTime % 1000) / 10);
        NumberFormat f = new DecimalFormat("00");
        return f.format(milliseconds);
    }

    static String getFormattedHourMinute(long elapsedTime, TextView hourMinute) {
        final StringBuilder displayTime = new StringBuilder();
        int seconds = (int) ((elapsedTime / 1000) % 60);
        int minutes = (int) (elapsedTime / (60 * 1000) % 60);
        int hours = (int) (elapsedTime / (60 * 60 * 1000));
        NumberFormat f = new DecimalFormat("00");
        if (minutes == 0) {
            displayTime.append(f.format(seconds));
            hourMinute.setTextSize(72);
        }
        else if (hours == 0) {
            displayTime.append(f.format(minutes)).append(":").append(f.format(seconds));
            hourMinute.setTextSize(54);
        }
        else {
            displayTime.append(hours).append(":").append(f.format(minutes)).append(":").append(f.format(seconds));
            hourMinute.setTextSize(38);
        }
        return displayTime.toString();
    }

    public void setupPauseValues() {
        stopwatchDurationElapsed = stopwatchDataArrayList.get(0).getLastPauseValueInMillis();
        currentValue = stopwatchDataArrayList.get(0).getProgressValue();
        updateTextView();
    }

    public void setupResumeValues(){
        differenceValue = getDifference();
        stopwatchRetrievedStartTime = SystemClock.elapsedRealtime();
        stopwatchHandler.post(stopwatchRunnable);

        stopwatchDataArrayList.get(0).setPause(false);
        stopwatchDataArrayList.get(0).setRunning(true);
        tickTrackDatabase.storeStopwatchData(stopwatchDataArrayList);
        stopwatchDataArrayList = tickTrackDatabase.retrieveStopwatchData();

        if(stopwatchDataArrayList.get(0).getProgressValue()>0){
            currentValue = stopwatchDataArrayList.get(0).getProgressValue();
            progressBarHandler.post(progressBarRunnable);
        }
    }
    private long getDifference() {
        if(stopwatchDataArrayList.get(0).getStopwatchTimerStartTimeInRealTimeMillis()<SystemClock.elapsedRealtime()){
            return SystemClock.elapsedRealtime() - stopwatchDataArrayList.get(0).getStopwatchTimerStartTimeInRealTimeMillis();
        }
        return 0;
    }
    public void onStopCalled() {
        stopwatchHandler.removeCallbacks(stopwatchRunnable);
        progressBarHandler.removeCallbacks(progressBarRunnable);
    }
}

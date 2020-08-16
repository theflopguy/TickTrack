package com.theflopguyproductions.ticktrack.stopwatch;

public class StopwatchData {

    boolean isRunning, isPause, isNotification;
    long lastLapEndTimeInMillis, recentRealTimeInMillis, recentLocalTimeInMillis, lastUpdatedValueInMillis;
    long progressValue;

    public long getProgressValue() {
        return progressValue;
    }

    public void setProgressValue(long progressValue) {
        this.progressValue = progressValue;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
        System.out.println(running+"<<<<RUNNNING");
    }

    public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean pause) {
        isPause = pause;
        System.out.println(pause+"<<<<PAUSE");
    }

    public boolean isNotification() {
        return isNotification;
    }

    public void setNotification(boolean notification) {
        isNotification = notification;
    }

    public long getRecentRealTimeInMillis() {
        return recentRealTimeInMillis;
    }

    public void setRecentRealTimeInMillis(long recentRealTimeInMillis) {
        this.recentRealTimeInMillis = recentRealTimeInMillis;
    }

    public long getRecentLocalTimeInMillis() {
        return recentLocalTimeInMillis;
    }

    public void setRecentLocalTimeInMillis(long recentLocalTimeInMillis) {
        this.recentLocalTimeInMillis = recentLocalTimeInMillis;
    }

    public long getLastLapEndTimeInMillis() {
        return lastLapEndTimeInMillis;
    }

    public void setLastLapEndTimeInMillis(long lastLapEndTimeInMillis) {
        this.lastLapEndTimeInMillis = lastLapEndTimeInMillis;
    }

    public long getLastUpdatedValueInMillis() {
        return lastUpdatedValueInMillis;
    }

    public void setLastUpdatedValueInMillis(long lastUpdatedValueInMillis) {
        this.lastUpdatedValueInMillis = lastUpdatedValueInMillis;
    }
}
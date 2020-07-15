package com.theflopguyproductions.ticktrack.ui.counter;

public class CounterData {
    String lastUpdateTimeStamp;
    String countValue;
    String counterLabel;
    int labelColor;

    public int getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(int labelColor) {
        this.labelColor = labelColor;
    }

    public String getLastUpdateTimeStamp() {
        return lastUpdateTimeStamp;
    }

    public void setLastUpdateTimeStamp(String lastUpdateTimeStamp) {
        this.lastUpdateTimeStamp = lastUpdateTimeStamp;
    }

    public String getCountValue() {
        return countValue;
    }

    public void setCountValue(String countValue) {
        this.countValue = countValue;
    }

    public String getCounterLabel() {
        return counterLabel;
    }

    public void setCounterLabel(String counterLabel) {
        this.counterLabel = counterLabel;
    }
}
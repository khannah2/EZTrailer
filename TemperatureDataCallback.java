package com.eztrailermonitor.eztrailermonitor;

public interface TemperatureDataCallback {
    public void onTemperatureDataChanged(byte[] currentReadings, boolean[] changedReadings);
}

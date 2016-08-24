package com.eztrailermonitor.eztrailermonitor;

/*
 * This interface provides BLE device information to the main activity.
 */
public interface ScannedDeviceCallback {
    public void onDeviceDetected(String deviceName, String deviceID);
}

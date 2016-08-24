package com.eztrailermonitor.eztrailermonitor;

import android.content.Context;

/*
 * This class holds data for a single device.
 */
public class DeviceData {

    int deviceIndex;
    String device;
    String mode;
    boolean enabled;

    public DeviceData(int deviceIndex) {
        this.deviceIndex = deviceIndex;
        this.device = "";
        this.mode = "";
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void enable(boolean enabled) {
        this.enabled = enabled;
    }

    private String getPath(String device, String varName) {
        return device + "/" + varName;
    }

    public void loadSettings(Context context) {
        device = UserSettings.getStringValue(context, getPath("device" + Integer.toString(deviceIndex), "device"), "");
        mode = UserSettings.getStringValue(context, getPath("device" + Integer.toString(deviceIndex), "mode"), "");
        enabled = UserSettings.getBoolValue(context, getPath("device" + Integer.toString(deviceIndex), "enabled"), false);
    }

    public void saveSettings(Context context) {
        UserSettings.setStringValue(context, getPath("device" + Integer.toString(deviceIndex), "device"), device);
        UserSettings.setStringValue(context, getPath("device" + Integer.toString(deviceIndex), "mode"), mode);
        UserSettings.setBoolValue(context, getPath("device" + Integer.toString(deviceIndex), "enabled"), enabled);
    }

    public int getDeviceIndex() {
        return deviceIndex;
    }

    public String getDevice() {
        return device;
    }

    public String getMode() {
        return mode;
    }

    public boolean isEnabled() {
        return enabled;
    }
}

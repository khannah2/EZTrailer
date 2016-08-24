package com.eztrailermonitor.eztrailermonitor;

import android.content.Context;

/*
 * This class holds data for a list of Arduino devices.
 */
public class DeviceList {

    public static final int NUM_DEVICES = 6;
    public static final int NO_MORE_ROOM = -99;

    private DeviceData[] deviceData =  new DeviceData[NUM_DEVICES];
    private static DeviceList instance;
    private boolean loaded;

    public static DeviceList getInstance() {
        if (instance == null) {
            instance = new DeviceList();
        }
        return instance;
    }

    private DeviceList() {
        loaded = false;
        for (int dev = 0; dev < NUM_DEVICES; dev++) {
            deviceData[dev] = new DeviceData(dev);
        }
    }

    public void loadSettingsIfNeeded(Context context) {
        if (!loaded) {
            loadSettings(context);
        }
    }

    public void loadSettings(Context context) {
        loaded = true;
        for (int dev = 0; dev < NUM_DEVICES; dev++) {
            deviceData[dev].loadSettings(context);
        }
    }

    public void saveSettings(Context context) {
        for (int dev = 0; dev < NUM_DEVICES; dev++) {
            deviceData[dev].saveSettings(context);
        }
    }

    public int getDeviceIndex(String deviceID) {
        int avail = NO_MORE_ROOM;
        for (int dev = 0; dev < NUM_DEVICES; dev++) {
            DeviceData dd = deviceData[dev];
            if (!dd.getDevice().isEmpty()) {
                if (dd.getDevice().equals(deviceID)) {
                    return dev;
                }
            } else {
                if (avail == NO_MORE_ROOM) {
                    avail = -(dev+1);
                }
            }
        }
        return avail;
    }

    public int addDevice(Context context, String deviceID) {
        int dev = getDeviceIndex(deviceID);
        if (dev >= 0) {
            return dev;
        }
        if (dev != NO_MORE_ROOM) {
            dev = (-dev) - 1;
            DeviceData dd = deviceData[dev];
            dd.setDevice(deviceID);
            deviceData[dev] = dd;
            saveSettings(context);
        }
        return dev;
    }

    public DeviceData getDevice(int deviceIndex) {
        if (deviceIndex < 0 || deviceIndex >= NUM_DEVICES) {
            return null;
        }
        if (deviceData[deviceIndex].getDevice().isEmpty()) {
            return null;
        }

        return deviceData[deviceIndex];
    }

}

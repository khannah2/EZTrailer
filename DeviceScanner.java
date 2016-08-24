package com.eztrailermonitor.eztrailermonitor;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import utils.AppConstants;

/*
 * This class scans for BLE devices, looking for EZTrailer devices.
 */
public class DeviceScanner extends ScanCallback {

    private final int MIN_RSSI = -70;

    private BluetoothLeScanner scanner;
    private ScannedDeviceCallback callback;
    private boolean scanning;

    public void startScan(ScannedDeviceCallback callback) {
        if (!scanning) {
            try {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if (adapter != null) {
                    if (!adapter.isEnabled()) {
                        adapter.enable();
                    }
                    scanner = adapter.getBluetoothLeScanner();
                    if (scanner != null) {
                        scanning = true;
                        this.callback = callback;
                        ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
                        List<ScanFilter> filters = new ArrayList<ScanFilter>();
                        Log.d(AppConstants.LOG_TAG, "Starting BLE scan");
                        scanner.startScan(filters, settings, this);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopScan() {
        if (scanning) {
            try {
                scanning = false;
                callback = null;
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if (adapter != null) {
                    scanner = adapter.getBluetoothLeScanner();
                    if (scanner != null) {
                        Log.d(AppConstants.LOG_TAG, "Stopping BLE scan");
                        scanner.stopScan(this);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        for (ScanResult sr : results) {
            onScanResult(ScanSettings.CALLBACK_TYPE_ALL_MATCHES, sr);
        }
    }

    @Override
    public void onScanFailed (int errorCode) {
        Log.d(AppConstants.LOG_TAG, "BLE scan failed, error " + Integer.toString(errorCode));
    }

    @Override
    public void onScanResult (int callbackType, ScanResult result) {
        if (callbackType != ScanSettings.CALLBACK_TYPE_MATCH_LOST) {
            if (result.getRssi() >= MIN_RSSI) {
                if (callback != null) {
                    BluetoothDevice device = result.getDevice();
                    String address = device.getAddress();
                    String name = device.getName();
                    Log.d(AppConstants.LOG_TAG, "BLE device seen: " + address + " / " + name);
                    if (device != null) {
                        if (name.contains("EZT")) {
                            callback.onDeviceDetected(name, address);
                        }
                    }
                }
            }
        }
    }
}

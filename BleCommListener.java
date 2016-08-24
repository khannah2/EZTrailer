package com.eztrailermonitor.eztrailermonitor;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import utils.AppConstants;

/*
 * This class schedules data communication with BLE devices (i.e., Arduino units).
 */
public class BleCommListener implements BleManager.BleManagerListener {

    // Service Constants
    private static final String UUID_SERVICE = "97A37B64-3656-4B99-89D6-9D63EC44677B";
    private static final String UUID_TEMP0 = "A497AB98-EFA8-4BE3-9E89-E42E7D70D647";
    private static final String UUID_TEMP1 = "B9B56AF4-A94E-407B-8777-3E35F70AA9ED";
    private static final String UUID_TEMP2 = "7C036622-79B9-4F0D-9EC6-E804FCC3C6AB";
    private static final String UUID_TEMP3 = "F0C21ECC-31B7-41C5-83EC-49D4411B70DD";
    private static final String UUID_TEMP4 = "3CBF986E-3762-4C33-AE16-7885CFAF0856";
    private static final String UUID_TEMP5 = "23252F16-EA53-4D6A-911D-A517F9557B55";

    private BleManager bleManager;
    private BluetoothGattService uartService;
    private Handler handler;
    private Context context;
    private static BleCommListener instance;
    private TemperatureDataCallback tdc;

    public byte[] temperature = new byte[6]; // the incoming temperature readings

    private BleCommListener(Context context) {
        super();
        this.context = context;
        handler = new Handler(context.getMainLooper());
        bleManager = BleManager.getInstance(context);
        bleManager.setBleListener(this);
    }

    public static BleCommListener getInstance(Context context) {
        if (instance == null) {
            instance = new BleCommListener(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onConnected() {
        Log.d(AppConstants.LOG_TAG, "Connected to " + bleManager.getConnectedDeviceAddress());
    }

    @Override
    public void onConnecting() {
        Log.d(AppConstants.LOG_TAG, "Connecting");
    }

    @Override
    public void onDisconnected() {
        Log.d(AppConstants.LOG_TAG, "Disconnected");
    }

    @Override
    public void onServicesDiscovered() {
        Log.d(AppConstants.LOG_TAG, "Services discovered");
        handler.post(new Runnable() {
            @Override
            public void run() {
                uartService = bleManager.getGattService(UUID_SERVICE);
                if (uartService != null) {
                    bleManager.enableNotification(uartService, UUID_TEMP0, true);
                    bleManager.enableNotification(uartService, UUID_TEMP1, true);
                    bleManager.enableNotification(uartService, UUID_TEMP2, true);
                    bleManager.enableNotification(uartService, UUID_TEMP3, true);
                    bleManager.enableNotification(uartService, UUID_TEMP4, true);
                    bleManager.enableNotification(uartService, UUID_TEMP5, true);
                }
            }
        });
    }

    //Getting Values from Bluetooth:
    @Override
    public void onDataAvailable(BluetoothGattCharacteristic characteristic) {
        if (characteristic.getService().getUuid().toString().equalsIgnoreCase(UUID_SERVICE)) {
            boolean[] changed = new boolean[6];
            if (characteristic.getUuid().toString().equalsIgnoreCase(UUID_TEMP0)) {
                byte[] data = characteristic.getValue();
                Log.d(AppConstants.LOG_TAG, "Temp0: " + Byte.toString(data[0]));
                changed[0] = (temperature[0] != data[0]);
                temperature[0] = data[0];
            } else if (characteristic.getUuid().toString().equalsIgnoreCase(UUID_TEMP1)) {
                byte[] data = characteristic.getValue();
                Log.d(AppConstants.LOG_TAG, "Temp1: " + Byte.toString(data[0]));
                changed[1] = (temperature[1] != data[0]);
                temperature[1] = data[0];
            } else if (characteristic.getUuid().toString().equalsIgnoreCase(UUID_TEMP2)) {
                byte[] data = characteristic.getValue();
                Log.d(AppConstants.LOG_TAG, "Temp2: " + Byte.toString(data[0]));
                changed[2] = (temperature[2] != data[0]);
                temperature[2] = data[0];
            } else if (characteristic.getUuid().toString().equalsIgnoreCase(UUID_TEMP3)) {
                byte[] data = characteristic.getValue();
                Log.d(AppConstants.LOG_TAG, "Temp3: " + Byte.toString(data[0]));
                changed[3] = (temperature[3] != data[0]);
                temperature[3] = data[0];
            } else if (characteristic.getUuid().toString().equalsIgnoreCase(UUID_TEMP4)) {
                byte[] data = characteristic.getValue();
                Log.d(AppConstants.LOG_TAG, "Temp4: " + Byte.toString(data[0]));
                changed[4] = (temperature[4] != data[0]);
                temperature[4] = data[0];
            } else if (characteristic.getUuid().toString().equalsIgnoreCase(UUID_TEMP5)) {
                byte[] data = characteristic.getValue();
                Log.d(AppConstants.LOG_TAG, "Temp5: " + Byte.toString(data[0]));
                changed[5] = (temperature[5] != data[0]);
                temperature[5] = data[0];
            }
            tdc.onTemperatureDataChanged(temperature, changed);
        }
    }

    @Override
    public void onDataAvailable(BluetoothGattDescriptor descriptor) {
    }

    @Override
    public void onDataWritten(BluetoothGattCharacteristic characteristic) {
    }

    @Override
    public void onReadRemoteRssi(int rssi) {
    }

    public void connect(String deviceID, TemperatureDataCallback tdc) {
        this.tdc = tdc;
        bleManager.connect(context, deviceID);
    }

    public void disconnect() {
        bleManager.disconnect();
    }

}

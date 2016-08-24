package com.eztrailermonitor.eztrailermonitor;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import utils.Androidutils;

//import android.preference.PreferenceManager;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.EditText;

/**
 * Created by Thiru on 19-02-2016.
 */


public class MainActivity extends Activity {

    public static final String TAG = "EZTrailer";

    // Button btnOn, btnOff;
    TextView txtString, txtStringLength, sensorView0, sensorView1, sensorView2, sensorView3, sensorView4, sensorView5, axle1Dif, axle2Dif, axle3Dif, axlecount;
    ImageView trailer;

    Handler bluetoothIn;

    final int handlerState = 0;                        //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;

    int savedTempDif;
    String saveddiff;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        // read shared preferenes to compare to measured results
        //added by Keith Hannah 2/21/2016

        //   final int savedTempDif;

        SharedPreferences sharedPreferences = getSharedPreferences("TrailerData", Context.MODE_PRIVATE);
        String savedmax = sharedPreferences.getString("maxtempvalue", "");
        String savedmin = sharedPreferences.getString("mintempvalue", "");
        final String axlecount = sharedPreferences.getString("axlesmode", "");
        saveddiff = sharedPreferences.getString("temp_dif", "");
        //savedMaxInt = Integer.parseInt(savedmax);
        //savedMinInt = Integer.parseInt(savedmin);

        try {
            savedTempDif = Integer.parseInt(saveddiff);
            System.out.println("check temp difference......."+saveddiff);

        } catch (Exception e) {

        }


//        //Link the buttons and textViews to respective views
//        btnOn = (Button) findViewById(R.id.buttonOn);
//        btnOff = (Button) findViewById(R.id.buttonOff);
        txtString = (TextView) findViewById(R.id.txtString);
        txtStringLength = (TextView) findViewById(R.id.testView1);
        sensorView0 = (TextView) findViewById(R.id.sensorView0);
        sensorView1 = (TextView) findViewById(R.id.sensorView1);
        sensorView2 = (TextView) findViewById(R.id.sensorView2);
        sensorView3 = (TextView) findViewById(R.id.sensorView3);
        sensorView4 = (TextView) findViewById(R.id.sensorView4);
        sensorView5 = (TextView) findViewById(R.id.sensorView5);
        axle1Dif = (TextView) findViewById(R.id.axle1Dif);
        axle2Dif = (TextView) findViewById(R.id.axle2Dif);
        axle3Dif = (TextView) findViewById(R.id.axle3Dif);
        // trailer = (ImageView) findViewById(R.id.tripallgreen);


        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {                                     //if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);                                      //keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        txtString.setText("Data Received = " + dataInPrint);
                        int dataLength = dataInPrint.length();                          //get length of data received
                        txtStringLength.setText("String Length = " + String.valueOf(dataLength));

                        if (recDataString.charAt(0) == '#')                             //if it starts with # we know it is what we are looking for
                        {
                            String sensor0 = recDataString.substring(1, 5);             //get sensor value from string between indices 1-5
                            String sensor1 = recDataString.substring(7, 11);            //same again...
                            String sensor2 = recDataString.substring(13, 17);
                            String sensor3 = recDataString.substring(19, 23);
                            String sensor4 = recDataString.substring(25, 29);
                            String sensor5 = recDataString.substring(31, 35);

                            sensorView0.setText(" Sensor 0 Temp = " + Androidutils.convertvalue(sensor0) + "F");    //update the textviews with sensor values
                            sensorView1.setText(" Sensor 1 Temp = " + Androidutils.convertvalue(sensor1) + "F");
                            sensorView2.setText(" Sensor 2 Temp = " + Androidutils.convertvalue(sensor2 )+ "F");
                            sensorView3.setText(" Sensor 3 Temp = " +Androidutils.convertvalue(sensor3)+ "F");
                            sensorView4.setText(" Sensor 4 Temp = " + Androidutils.convertvalue(sensor4) + "F");
                            sensorView5.setText(" Sensor 5 Temp = " + Androidutils.convertvalue(sensor5) + "F");
                            double ints0;
                            double ints1;
                            double ints2;
                            double ints3;
                            double ints4;
                            double ints5;
                            double axle1difval;
                            double axle2difval;
                            double axle3difval;
                            double dif;


                            ints0 = Double.parseDouble(sensor0);
                            ints1 = Double.parseDouble(sensor1);
                            ints2 = Double.parseDouble(sensor2);
                            ints3 = Double.parseDouble(sensor3);
                            ints4 = Double.parseDouble(sensor4);
                            ints5 = Double.parseDouble(sensor5);


                            // added by Keith Hannah
                            // convert temp difference to precentage
                            axle1difval = 100 * (Math.abs((ints0 - ints1) / (ints0 + ints1)));
                            String axle1difstring = Double.toString(axle1difval);
                            axle2difval = 100 * (Math.abs((ints2 - ints3) / (ints2 + ints3)));
                            String axle2difstring = Double.toString(axle2difval);
                            axle3difval = 100 * (Math.abs((ints4 - ints5) / (ints4 + ints5)));
                            String axle3difstring = Double.toString(axle3difval);


                            if (axle1difval > savedTempDif) {
                                axle1Dif.setTextColor(Color.RED);
                                dif = (ints0 - ints1);
                                if (dif < 0) {

                                    Toast.makeText(getApplicationContext(), "Axle 1 right wheel is hot",
                                            Toast.LENGTH_SHORT).show();

                                    /*if (axlecount == "1") {
                                        trailer.setBackgroundResource(R.drawable.singlerred);


                                    }
                                    ;// single axle right is hot
                                    if (axlecount == "2") {
                                        trailer.setBackgroundResource(R.drawable.twoaxlesfrred);


                                    }//double right is hot

                                    if (axlecount == "3") {
                                        trailer.setBackgroundResource(R.drawable.tripfrred);
                                    }//tripple axle front right is hot.
*/

                                }

                                if (dif > 0) {

                                    Toast.makeText(getApplicationContext(), "Axle 1 left wheel is hot",
                                            Toast.LENGTH_LONG).show();
                                  /*  if (axlecount == "1") {
                                        trailer.setBackgroundResource(R.drawable.singlelred);

                                    }
                                    ;// single axle right is hot
                                    if (axlecount == "2") {
                                        trailer.setBackgroundResource(R.drawable.twoaxlesflred);


                                    }//double right is hot

                                    if (axlecount == "3") {
                                        trailer.setBackgroundResource(R.drawable.tripflred);
                                    }//tripple axle front right is hot.
*/
                                }


                            } else
                                axle1Dif.setTextColor(Color.GREEN);
                            if (axle2difval > savedTempDif) {
                                axle2Dif.setTextColor(Color.RED);
                                dif = (ints2 - ints3);
                                if (dif < 0) {

                                    Toast.makeText(getApplicationContext(), "Axle 2 right wheel is hot",
                                            Toast.LENGTH_SHORT).show();
                                   /* if (axlecount == "2") {
                                        trailer.setBackgroundResource(R.drawable.twoaxlesrrred);

                                    }//double right rear is hot

                                    if (axlecount == "3") {
                                        trailer.setBackgroundResource(R.drawable.tripmrred);
                                    }//tripple axle middle right is hot.*/

                                }
                                if (dif > 0) {

                                    Toast.makeText(getApplicationContext(), "Axle 2 left wheel is hot",
                                            Toast.LENGTH_SHORT).show();


                                  /*  if (axlecount == "2") {
                                        trailer.setBackgroundResource(R.drawable.twoaxlesrlred);


                                    }//double left is hot

                                    if (axlecount == "3") {
                                        trailer.setBackgroundResource(R.drawable.tripmlred);
                                    }//triple axle middle left is hot.*/

                                }
                            } else
                                axle2Dif.setTextColor(Color.GREEN);

                            if (axle3difval > savedTempDif) {
                                axle3Dif.setTextColor(Color.RED);
                                dif = (ints4 - ints5);
                              /*  if (dif < 0) {

                                    Toast.makeText(getApplicationContext(), "Axle 3 right wheel is hot",
                                            Toast.LENGTH_SHORT).show();
                                    trailer.setBackgroundResource(R.drawable.triprrred);
                                }
                                if (dif > 0) {

                                    Toast.makeText(getApplicationContext(), "Axle 3 left wheel is hot",
                                            Toast.LENGTH_SHORT).show();
                                    trailer.setBackgroundResource(R.drawable.triprled);
                                }*/
                            } else
                                axle3Dif.setTextColor(Color.GREEN);


                            axle1Dif.setText("axle 1 difference = " + Androidutils.convertvalue( axle1difstring) + "%");
                            axle2Dif.setText("axle 2 difference = " + Androidutils.convertvalue( axle2difstring)+ "%");
                            axle3Dif.setText("axle 3 difference = " +  Androidutils.convertvalue(axle3difstring) + "%");
//


                        }
                        recDataString.delete(0, recDataString.length());                    //clear all string data
                        // strIncom =" ";
                        dataInPrint = " ";
                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

        // Set up onClick listeners for buttons to send 1 or 0 to turn on/off LED
//        btnOff.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                mConnectedThread.write("0");    // Send "0" via Bluetooth
//                Toast.makeText(getBaseContext(), "Turn off LED", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        btnOn.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                mConnectedThread.write("1");    // Send "1" via Bluetooth
//                Toast.makeText(getBaseContext(), "Turn on LED", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(DevicelistActivity.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        mConnectedThread.write("x");
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if (btAdapter == null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }
}

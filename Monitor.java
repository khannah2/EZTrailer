package com.eztrailermonitor.eztrailermonitor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import utils.Androidutils;
import utils.AppConstants;

public class Monitor extends AppCompatActivity implements ScannedDeviceCallback, TemperatureDataCallback {

    private static final int NEW_DEVICE_SCAN_TIME_MS = 10000;

    ImageView trailer;
    public static String fragTag;

    TextView fLTemp, fRTemp, mLTemp, mRTemp, rLTemp, rRTemp, txtString, txtStringLength;
    TextView txt_axel_1, txt_axel_2, txt_axel_3, txtints0, txtints1, txtaxledif, SavedTempDif;
    TextView axle_one, axle_two, axle_three;

    int fLTempMin = 0; //50;
    int fRTempMin = 0; //55;
    int mLTempMin = 0; //60;
    int mRTempMin = 0;// 65;
    int rLTempMin = 0;//70;
    int rRTempMin = 0;//75;
    int REQUEST_ENABLE_BT = 00000;


    public static final String DEFAULTMAX = "220";
    String savedmax;
    String axlecount;
    int sensorValue = 0;
    BluetoothAdapter mBluetoothAdapter;
    String red = "<font color='#EE0000'>red</font>";
    public Dialog alertProgressDialog;
    TextView close;
    private static final String TAG = "Monitor";
    private static final boolean D = true;


    // declare button for launching website and textview for connection status
    //Button tlbutton;
    TextView textView1;

    // EXTRA string to send on to mainactivity
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Member fields
    private BluetoothAdapter mBtAdapter;

    final int handlerState = 0;                        //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private StringBuilder recDataString = new StringBuilder();

    // String for MAC address
    private static String address;

    int savedTempDif;
    String saveddiff;
    Set<BluetoothDevice> pairedDevices;
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

    private BleCommListener mBleCommListener;
    private DeviceScanner mDeviceScanner;
    private ArrayAdapter<String> mBleDevicesArrayAdapter;
    private Handler handler;
    CountDownTimer mCounter;
    byte[] mValues = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        handler = new Handler(this.getMainLooper());
        mBleCommListener = BleCommListener.getInstance(this);
        mDeviceScanner = new DeviceScanner();

       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        fLTemp = (TextView) findViewById(R.id.fltemp);
        fRTemp = (TextView) findViewById(R.id.frtemp);
        mLTemp = (TextView) findViewById(R.id.mltemp);
        mRTemp = (TextView) findViewById(R.id.mrtemp);
        rLTemp = (TextView) findViewById(R.id.rltemp);
        rRTemp = (TextView) findViewById(R.id.rrtemp);
        //savedTempDif = (TextView) findViewById(R.id.SavedTempDif);

        txt_axel_1 = (TextView) findViewById(R.id.axle_one);
        txt_axel_2 = (TextView) findViewById(R.id.axle_two);
        txt_axel_3 = (TextView) findViewById(R.id.axle_three);
        trailer = (ImageView) findViewById(R.id.tripallgreen);
        //txtStringLength = (TextView) findViewById(R.id.txtStringLength);
        txtString = (TextView) findViewById(R.id.txtString);
        fLTemp.setText("fLTemp" + fLTempMin);
        fRTemp.setText("fRTemp" + fRTempMin);
        mLTemp.setText("mLTemp" + mLTempMin);
        mRTemp.setText("mRTemp" + mRTempMin);
        rLTemp.setText("rLTemp" + rLTempMin);
        rRTemp.setText("rRTemp" + rRTempMin);
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        //triple axle front right is hot.


        // t.setText(Html.fromHtml(first + next));

        //start();
        /*bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                try {
                    SharedPreferences sharedPreferences = getSharedPreferences("TrailerData", Context.MODE_PRIVATE);
                    savedmax = sharedPreferences.getString("maxtempvalue", DEFAULTMAX);
                    sensorValue = Integer.parseInt(savedmax);

                    //  System.out.println("checking.........." + savedmax);
                    String savedmax = sharedPreferences.getString("maxtempvalue", "");
                    String savedmin = sharedPreferences.getString("mintempvalue", "");
                    axlecount = sharedPreferences.getString("axlesmode", "");

                    if (msg.what == handlerState) {                                     //if message is what we want
                        String readMessage = (String) msg.obj;                                // msg.arg1 = bytes from connect thread
                        recDataString.append(readMessage);                                      //keep appending to string until ~
                        int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                        if (endOfLineIndex > 0) {                                           // make sure there data before ~
                            String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                            //txtString.setTextColor(Color.RED);

                            txtString.setText("Data Received = " + dataInPrint);//should set text on screen to datastring
                            int dataLength = dataInPrint.length();                          //get length of data received
                            txtStringLength.setText("String Length = " + String.valueOf(dataLength));

                            if (recDataString.charAt(0) == '#')                             //if it starts with # we know it is what we are looking for
                            {
                                / *String sensor0 = recDataString.substring(1, 5);             //get sensor value from string between indices 1-5
                                String sensor1 = recDataString.substring(7, 11);            //same again...
                                String sensor2 = recDataString.substring(13, 17);
                                String sensor3 = recDataString.substring(19, 23);
                                String sensor4 = recDataString.substring(25, 29);
                                String sensor5 = recDataString.substring(31, 35);* /

                                String sensor0 = recDataString.substring(1, 6);             //get sensor value from string between indices 1-5
                                String sensor1 = recDataString.substring(8, 13);            //same again...
                                String sensor2 = recDataString.substring(15, 20);           // setting all to 6 digits so over 100deg displays
                                String sensor3 = recDataString.substring(22, 27);
                                String sensor4 = recDataString.substring(29, 34);
                                String sensor5 = recDataString.substring(36, 41);


                                fLTempMin = Androidutils.convertvalue(sensor0);
                                fRTempMin = Androidutils.convertvalue(sensor1);
                                mLTempMin = Androidutils.convertvalue(sensor2);
                                mRTempMin = Androidutils.convertvalue(sensor3);
                                rLTempMin = Androidutils.convertvalue(sensor4);
                                rRTempMin = Androidutils.convertvalue(sensor5);


                                fLTemp.setText(" Temp =" + Androidutils.convertvalue(sensor0) + "F");    //update the textviews with sensor values
                                fRTemp.setText(" Temp = " + Androidutils.convertvalue(sensor1) + "F");
                                mLTemp.setText(" Temp = " + Androidutils.convertvalue(sensor2) + "F");
                                mRTemp.setText(" Temp = " + Androidutils.convertvalue(sensor3) + "F");
                                rLTemp.setText(" Temp = " + Androidutils.convertvalue(sensor4) + "F");
                                rRTemp.setText(" 5 Temp = " + Androidutils.convertvalue(sensor5) + "F");
//SavedTempDif.setText(" saved temp Dif" + savedTempDif + "%");

                                ints0 = Double.parseDouble(sensor0);
                                ints1 = Double.parseDouble(sensor1);
                                ints2 = Double.parseDouble(sensor2);
                                ints3 = Double.parseDouble(sensor3);
                                ints4 = Double.parseDouble(sensor4);
                                ints5 = Double.parseDouble(sensor5);


                                // added by Keith Hannah
                                // convert temp difference to percentage
                                axle1difval = 100 * (Math.abs((ints0 - ints1) / (ints0 + ints1)));
                                String axle1difstring = Double.toString(axle1difval);
                                // txtints0.setText("sensor 0 =" + ints0); //keith test
                                // txtints1.setText("sensor 1 =" + ints1);  // keith test

                                //txtaxledif.setText("axle1difval = " + Androidutils.convertvalue(axle1difstring));

                                axle2difval = 100 * (Math.abs((ints2 - ints3) / (ints2 + ints3)));
                                String axle2difstring = Double.toString(axle2difval);
                                axle3difval = 100 * (Math.abs((ints4 - ints5) / (ints4 + ints5)));
                                String axle3difstring = Double.toString(axle3difval);

                                //  start();
                                //if any temp is above the stored difference


                                if (axle1difval > savedTempDif) {
                                    //axle_one.setTextColor(Color.RED);

//                                axle1.setTextColor(Color.RED);
                                    dif = (ints0 - ints1);
                                    if (dif < 0) {

                                        //Toast.makeText(getApplicationContext(), "Axle 1 right wheel is hot",
                                        // Toast.LENGTH_SHORT).show(); temporarily off because of multiple toast


                                        if (axlecount.equals("1")) {
                                            trailer.setBackgroundResource(R.drawable.singlerred);


                                        }
                                        ;// single axle right is hot
                                        if (axlecount.equals("2")) {
                                            trailer.setBackgroundResource(R.drawable.twoaxlesfrred);


                                        }//double right is hot

                                        if (axlecount.equals("3")) {
                                            trailer.setBackgroundResource(R.drawable.tripfrred);
                                        }//tripple axle front right is hot.


                                    }

                                    if (dif > 0) {

                                        //Toast.makeText(getApplicationContext(), "Axle 1 left wheel is hot",    /temporarily off to stop toast
                                        //      Toast.LENGTH_LONG).show();

                                        if (axlecount.equals("1")) {
                                            trailer.setBackgroundResource(R.drawable.singlelred);

                                        }
                                        ;// single axle right is hot
                                        if (axlecount.equals("2")) {
                                            trailer.setBackgroundResource(R.drawable.twoaxlesflred);


                                        }//double right is hot

                                        if (axlecount.equals("3")) {
                                            trailer.setBackgroundResource(R.drawable.tripflred);
                                        }//triple axle front right is hot.


                                    }


                                } else if (axle2difval > savedTempDif) {
                                    //axle_two.setTextColor(Color.RED);
                                    dif = (ints2 - ints3);
                                    if (dif < 0) {

                                        //Toast.makeText(getApplicationContext(), "Axle 2 right wheel is hot",    /temporarily off
                                        //      Toast.LENGTH_SHORT).show();

                                        if (axlecount.equals("2")) {
                                            trailer.setBackgroundResource(R.drawable.twoaxlesrrred);

                                        }//double right rear is hot

                                        if (axlecount.equals("3")) {
                                            trailer.setBackgroundResource(R.drawable.tripmrred);
                                        }//triple axle middle right is hot.


                                    }
                                    if (dif > 0) {

                                        // Toast.makeText(getApplicationContext(), "Axle 2 left wheel is hot",     /temporarily off
                                        //       Toast.LENGTH_SHORT).show();


                                        if (axlecount.equals("2")) {
                                            trailer.setBackgroundResource(R.drawable.twoaxlesrlred);


                                        }//double left is hot

                                        if (axlecount.equals("3")) {
                                            trailer.setBackgroundResource(R.drawable.tripmlred);
                                        }//triple axle middle left is hot.*//*


                                    }
                                } else if (axle3difval > savedTempDif) {
                                    // axle_three.setTextColor(Color.RED);
                                    dif = (ints4 - ints5);

                                    if (dif < 0) {

                                        //        Toast.makeText(getApplicationContext(), "Axle 3 right wheel is hot", / temporarily turn toast off
                                        //              Toast.LENGTH_SHORT).show();
                                        trailer.setBackgroundResource(R.drawable.triprrred);
                                    }
                                    if (dif > 0) {

                                        //  Toast.makeText(getApplicationContext(), "Axle 3 left wheel is hot", temporarily turn toast off
                                        //        Toast.LENGTH_SHORT).show();
                                        trailer.setBackgroundResource(R.drawable.triprled);
                                    }

                                } else

                                    axle_three.setTextColor(Color.BLACK);//doesn't work without it.


                                txt_axel_1.setText("axle 1 difference = " + Androidutils.convertvalue(axle1difstring) + "%");
                                txt_axel_2.setText("axle 2 difference = " + Androidutils.convertvalue(axle2difstring) + "%");
                                txt_axel_3.setText("axle 3 difference = " + Androidutils.convertvalue(axle3difstring) + "%");// * / //keith 3/7


                            }
                            //start();
                            recDataString.delete(0, recDataString.length());                    //clear all string data
                            // strIncom =" ";
                            dataInPrint = " ";
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };*/
        // timer();


    }

   /* public void timer() {
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
              *//* if(sensorValue==55) {
                   sensorValue = sensorValue +10;
               }else
               {
                   sensorValue = sensorValue+ 5;
               }*//*



               System.out.println("sensorValue---------------------"+sensorValue);
                start();
                timer();
            }

        }, 0, 1000000);
    }*/

    @Override
    public void onPause() {
        super.onPause();
        if (mBleCommListener != null) {
            mBleCommListener.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBleCommListener != null && address != null && !address.isEmpty()) {
            mBleCommListener.connect(address, this);
        }
        start();
        mCounter = new CountDownTimer(20000, 100) {

            public void onTick(long millisUntilFinished) {
                if (mValues != null)
                    update(mValues);
            }

            @Override
            public void onFinish() {
                mCounter.start();
            }
        }.start();

    }

    public void start() {

        SharedPreferences sharedPreferences = getSharedPreferences("TrailerData", Context.MODE_PRIVATE);
        savedmax = sharedPreferences.getString("maxtempvalue", DEFAULTMAX);
        sensorValue = Integer.parseInt(savedmax);
        System.out.println("checking.........." + savedmax);
        String savedmax = sharedPreferences.getString("maxtempvalue", "");
        String savedmin = sharedPreferences.getString("mintempvalue", "");
        String axlecount = sharedPreferences.getString("axlesmode", "");
        // String axlecount= chooseaxles.axlesMode;
        saveddiff = sharedPreferences.getString("temp_dif", "");

        if (Androidutils.getContentFromSharedPreference(Monitor.this, AppConstants.SHARED_PREFERENCE_KEY).equals("1")) {
            txt_axel_2.setText("Axle Different = " + axle2difval + "%");  // keith 3/7 to use percent
            txt_axel_1.setText(" ");
            txt_axel_3.setText(" ");
            trailer.setBackgroundResource(R.drawable.singleaxlegreen);
            txt_axel_2.setTextColor(getResources().getColor(R.color.green));
            mLTemp.setText("MLTemp=" + mLTempMin);
            mRTemp.setText("MRTemp=" + mRTempMin);
            rLTemp.setText(" ");
            rRTemp.setText(" ");
            fLTemp.setText(" ");
            fRTemp.setText(" ");
        } else if (Androidutils.getContentFromSharedPreference(Monitor.this, AppConstants.SHARED_PREFERENCE_KEY).equals("2")) {
            txt_axel_1.setText(" "); //changed by Keith
            txt_axel_2.setText("Front Axle Different = " + axle2difval + "%");
            txt_axel_3.setText(" Rear Axle Different = " + axle3difval + "%"); // keith 3/7
            trailer.setBackgroundResource(R.drawable.twoaxlesallgreen);
            txt_axel_2.setTextColor(getResources().getColor(R.color.green));
            txt_axel_3.setTextColor(getResources().getColor(R.color.green));
            mLTemp.setText("MLTemp=" + mLTempMin);
            mRTemp.setText("MRTemp=" + mRTempMin);
            rLTemp.setText("RLTemp=" + rLTempMin);
            rRTemp.setText("RRTemp=" + rRTempMin);
            fLTemp.setText(" ");
            fRTemp.setText(" ");

        } else if (Androidutils.getContentFromSharedPreference(Monitor.this, AppConstants.SHARED_PREFERENCE_KEY).equals("3")) {

            txt_axel_1.setText("Front Axle Different = " + axle1difval + "%");
            txt_axel_2.setText("Middle Axle Different = " + axle2difval + "%");
            txt_axel_3.setText("Rear Axle Different = " + axle3difval + "%");
            trailer.setBackgroundResource(R.drawable.tripaxlesallgreen);
            txt_axel_1.setTextColor(getResources().getColor(R.color.green));
            txt_axel_2.setTextColor(getResources().getColor(R.color.green));
            txt_axel_3.setTextColor(getResources().getColor(R.color.green));
            fLTemp.setText("FLTemp=" + fRTempMin);
            fRTemp.setText("FRTemp=" + fRTempMin);
            mLTemp.setText("MLTemp=" + mLTempMin);
            mRTemp.setText("MRTemp=" + mRTempMin);
            rLTemp.setText("RLTemp=" + rLTempMin);
            rRTemp.setText("RRTemp=" + rRTempMin);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = getSharedPreferences("TrailerData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("maxtempvalue", maxmintemp.DEFAULTMAX);
        editor.putString("mintempvalue", maxmintemp.DEFAULTMIN);
        editor.putString("temp_dif", maxmintemp.DEFDIFFERENCE);
        editor.commit();

        try {
            Androidutils.clearcache(this);
        } catch (Exception e) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_monitor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
           /* Intent intent = new Intent(this, DevicelistActivity.class);
            startActivity(intent);*/
            mBtAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBtAdapter.isEnabled()) {
                scanDevices();
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1100);
            }
        }
        if (id == R.id.AxleCount) {
            Androidutils.addpreferences(Monitor.this, "");
            Intent intent = new Intent(this, chooseaxles.class);
            startActivity(intent);
            finish();
        }
        if (id == R.id.maxmintemp) {
            Intent intent = new Intent(this, maxmintemp.class);
            startActivity(intent);
            finish();
        }
        if (id == R.id.exit) {
            finish();
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void scanDevices() {
        // Inform the user
        handler.post(new Runnable() {
            public void run() {
                Context context = getApplicationContext();
                Toast toast = Toast.makeText(context, "Scanning for EZTrailer devices...", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        // Scan for new devices
        mDeviceScanner.startScan(this);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Monitor.this.mDeviceScanner.stopScan();
                showalert();
            }
        }, NEW_DEVICE_SCAN_TIME_MS);
    }

    public void showalert() {

        if (alertProgressDialog == null) {
            alertProgressDialog = new Dialog(this, android.R.style.Theme_Translucent);
            alertProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertProgressDialog.setContentView(R.layout.device_list);
            alertProgressDialog.setCancelable(true);
            close = (TextView) alertProgressDialog.findViewById(R.id.close);
          /*  textView1 = (TextView) alertProgressDialog.findViewById(R.id.connecting);
            textView1.setTextSize(40);
            textView1.setText(" ");*/

            close.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    alertProgressDialog.dismiss();
                    alertProgressDialog = null;
                }
            });

            // Initialize array adapter for paired devices
            mBleDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

            // Find and set up the ListView for paired devices
            ListView pairedListView = (ListView) alertProgressDialog.findViewById(R.id.paired_devices);
            pairedListView.setAdapter(mBleDevicesArrayAdapter);
            alertProgressDialog.findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);//make title viewable

            // Get the list of BLE devices.
            DeviceList deviceList = DeviceList.getInstance();
            deviceList.loadSettingsIfNeeded(this);

            // Add previously known devices to the array
            int n = 0;
            for (int i = 0; i < deviceList.NUM_DEVICES; i++) {
                DeviceData dd = deviceList.getDevice(i);
                if (dd != null) {
                    n++;
                    mBleDevicesArrayAdapter.add(dd.getDevice() + " (" + dd.getMode() + ")");
                }
            }
            if (n == 0) {
                String noDevices = "NO devices";
                mBleDevicesArrayAdapter.add(noDevices);
            }

            pairedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        String info = ((TextView) view).getText().toString();
                        address = info.substring(0, 17);
                        Log.d(AppConstants.LOG_TAG, "Selected device " + address);
                        mBleCommListener.connect(address, Monitor.this);
                        alertProgressDialog.dismiss();
                        alertProgressDialog = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            alertProgressDialog.show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1100) {
        }
    }

    public boolean checkBTState() {
        boolean ischeck;
        // Check device has Bluetooth and that it is turned on
        mBtAdapter = BluetoothAdapter.getDefaultAdapter(); // CHECK THIS OUT THAT IT WORKS!!!

        if (mBtAdapter == null) {
            Toast.makeText(getBaseContext(), "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
            ischeck = false;
        } else {
            if (mBtAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
                //  showalert();
                ischeck = true;
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1100);
                //  checkBTState();
                ischeck = true;
            }
        }

        return ischeck;
    }


    @Override
    public void onDeviceDetected(String deviceName, String deviceID) {
        DeviceList deviceList = DeviceList.getInstance();
        deviceList.addDevice(this, deviceID);
    }

    @Override
    public void onTemperatureDataChanged(final byte[] currentReadings, boolean[] changedReadings) {

        final String i = String.valueOf(currentReadings[0]);

        mValues = currentReadings;
    }


    void update(final byte[] currentReadings) {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("TrailerData", Context.MODE_PRIVATE);
                savedmax = sharedPreferences.getString("maxtempvalue", DEFAULTMAX);
                sensorValue = Integer.parseInt(savedmax);
                System.out.println("checking.........." + savedmax);
                String savedmax = sharedPreferences.getString("maxtempvalue", "");
                String savedmin = sharedPreferences.getString("mintempvalue", "");
                String axlecount = sharedPreferences.getString("axlesmode", "");
                // String axlecount= chooseaxles.axlesMode;
                saveddiff = sharedPreferences.getString("temp_dif", "");
//




                if (String.valueOf(currentReadings[0]).length() > 0) {
                    fLTempMin = currentReadings[0]; //50;
                    fRTempMin = currentReadings[1]; //55;
                    mLTempMin = currentReadings[2]; //60;
                    mRTempMin = currentReadings[3];// 65;
                    rLTempMin = currentReadings[4];//70;
                    rRTempMin = currentReadings[5];
                    if (Androidutils.getContentFromSharedPreference(Monitor.this, AppConstants.SHARED_PREFERENCE_KEY).equals("1")) {


                        //txt_axel_2.setText("Axel  Difference =" + (mLTempMin - mRTempMin) + "%"); // Keith 3/7
                        axle2difval = Math.abs(mLTempMin - mRTempMin);
                        txt_axel_2.setText("Axle Different = " + axle2difval + "%");  // keith 3/7 to use percent
                        txt_axel_1.setText(" ");
                        txt_axel_3.setText(" ");

                        if (mLTempMin < sensorValue) {
                            mLTemp.setText("MLTemp=" + mLTempMin);
                            trailer.setBackgroundResource(R.drawable.singlelred);
                            txt_axel_2.setTextColor(getResources().getColor(R.color.red));
                            mLTemp.setTextColor(getResources().getColor(R.color.red));
                            mRTemp.setText("MRTemp=" + mRTempMin);
                            //Androidutils.Notify("EZtrailer", "Middle Left Over Heated :" + mLTempMin + "F", Monitor.this);
                            Androidutils.showtoast(Monitor.this, "Middle Left Over Heated :" + mLTempMin + "F");
                            Androidutils.playSound(Monitor.this);

                        } else {
                            trailer.setBackgroundResource(R.drawable.singleaxlegreen);
                            txt_axel_2.setTextColor(getResources().getColor(R.color.green));
                            mLTemp.setText("MLTemp=" + mLTempMin);
                            mRTemp.setText("MRTemp=" + mRTempMin);
                        }
                        if (mRTempMin < sensorValue) {
                            trailer.setBackgroundResource(R.drawable.singlerred);
                            txt_axel_2.setTextColor(getResources().getColor(R.color.red));
                            mRTemp.setText("mRTemp=" + mLTempMin);
                            rLTemp.setTextColor(getResources().getColor(R.color.red));
                            mRTemp.setText("MLTemp=" + mLTempMin);
                            //   Androidutils.Notify("EZtrailer", "Middle Right Over Heated :" + mRTempMin + "F", Monitor.this);

                            Androidutils.showtoast(Monitor.this, "Middle Left Over Heated :" + mLTempMin + "F");
                            Androidutils.playSound(Monitor.this);

                        } else {
                            trailer.setBackgroundResource(R.drawable.singleaxlegreen);
                            txt_axel_2.setTextColor(getResources().getColor(R.color.green));
                            mLTemp.setText("MLTemp=" + mLTempMin);
                            mRTemp.setText("MRTemp=" + mRTempMin);
                        }

                        fLTemp.setText(" ");
                        fRTemp.setText(" ");
                        rLTemp.setText(" ");
                        rRTemp.setText(" ");
                    } else if (Androidutils.getContentFromSharedPreference(Monitor.this, AppConstants.SHARED_PREFERENCE_KEY).equals("2")) {
                        //txt_axel_1.setText("Axel 1 Difference =" + (fLTempMin - fRTempMin) + "% ");
                        txt_axel_1.setText(" "); //changed by Keith
                        //txt_axel_2.setText("Axel 2 Difference =" + (mLTempMin - mRTempMin) + "%");  // keith
                        axle2difval = Math.abs(mLTempMin - mRTempMin); // put into math for absolute 8/16
                        txt_axel_2.setText("Front Axle Different = " + axle2difval + "%");

                        axle3difval = Math.abs(rLTempMin - rRTempMin);// put into math for absolute 8/16
                        //txt_axel_3.setText("Axel 3 Difference =" + (rLTempMin - rRTempMin) + "%");
                        txt_axel_3.setText(" Rear Axle Different = " + axle3difval + "%"); // keith 3/7

                   /* mLTemp = (TextView) findViewById(R.id.mltemp);
                    mRTemp = (TextView) findViewById(R.id.mrtemp);
                    rLTemp = (TextView) findViewById(R.id.rltemp);
                    rRTemp = (TextView) findViewById(R.id.rrtemp);*/
                        if (mLTempMin < sensorValue) {
                            trailer.setBackgroundResource(R.drawable.twoaxlesflred);
                            txt_axel_2.setTextColor(getResources().getColor(R.color.red));
                            mLTemp.setText("MLTemp" + mLTempMin);
                            mLTemp.setTextColor(getResources().getColor(R.color.red));
                            mRTemp.setText("MRTemp=" + mRTempMin);
                            rLTemp.setText("RLTemp=" + rLTempMin);
                            rRTemp.setText("RRTemp=" + rRTempMin);

                            // Androidutils.Notify("EZtrailer", "Middle Left Over Heated :" + mLTempMin + "F", Monitor.this);

                            Androidutils.showtoast(Monitor.this, "Middle Left Over Heated :" + mLTempMin + "F");
                            Androidutils.playSound(Monitor.this);

                        } else {
                            trailer.setBackgroundResource(R.drawable.twoaxlesallgreen);
                            txt_axel_2.setTextColor(getResources().getColor(R.color.green));
                            txt_axel_3.setTextColor(getResources().getColor(R.color.green));
                            mLTemp.setText("MLTemp=" + mLTempMin);
                            mRTemp.setText("MRTemp=" + mRTempMin);
                            rLTemp.setText("RLTemp=" + rLTempMin);
                            rRTemp.setText("RRTemp=" + rRTempMin);
                        }


                        if (mRTempMin < sensorValue) {
                            trailer.setBackgroundResource(R.drawable.twoaxlesfrred);
                            txt_axel_2.setTextColor(getResources().getColor(R.color.red));
                            // mRTemp.setText("");
                            mRTemp.setText("MLTemp" + mRTempMin);
                            mRTemp.setTextColor(getResources().getColor(R.color.red));
                            mLTemp.setText("MRTemp=" + mRTempMin);
                            rLTemp.setText("RLTemp=" + rLTempMin);
                            rRTemp.setText("RRTemp=" + rRTempMin);

                            //   Androidutils.Notify("EZtrailer", "Middle Right Over Heated :" + mRTempMin + "F", Monitor.this);

                            Androidutils.showtoast(Monitor.this, "Middle Right Over Heated :" + mRTempMin + "F");
                            Androidutils.playSound(Monitor.this);

                        } else {
                            trailer.setBackgroundResource(R.drawable.twoaxlesallgreen);
                            txt_axel_2.setTextColor(getResources().getColor(R.color.green));
                            txt_axel_3.setTextColor(getResources().getColor(R.color.green));
                            mLTemp.setText("MLTemp=" + mLTempMin);
                            mRTemp.setText("MRTemp=" + mRTempMin);
                            rLTemp.setText("RLTemp=" + rLTempMin);
                            rRTemp.setText("RRTemp=" + rRTempMin);
                        }
                        if (rLTempMin < sensorValue) {
                           // trailer.setBackgroundResource(R.drawable.twoaxlesrlred);
                           // txt_axel_3.setTextColor(getResources().getColor(R.color.red));
                            rLTemp.setText("rLTemp" + rLTempMin);
                            rLTemp.setTextColor(getResources().getColor(R.color.red));
                            mLTemp.setText("MLTemp=" + mLTempMin);
                            mRTemp.setText("MRTemp=" + mRTempMin);
                            rRTemp.setText("RRTemp=" + rRTempMin);
                            //  Androidutils.Notify("EZtrailer", "Rear Left Over Heated :" + rLTempMin + "F", Monitor.this);

                            Androidutils.showtoast(Monitor.this, "Rear Left Over Heated :" + rLTempMin + "F");
                            Androidutils.playSound(Monitor.this);

                        } else {
                            trailer.setBackgroundResource(R.drawable.twoaxlesallgreen);
                            txt_axel_2.setTextColor(getResources().getColor(R.color.green));
                            txt_axel_3.setTextColor(getResources().getColor(R.color.green));
                            mLTemp.setText("MLTemp=" + mLTempMin);
                            mRTemp.setText("MRTemp=" + mRTempMin);
                            rLTemp.setText("RLTemp=" + rLTempMin);
                            rRTemp.setText("RRTemp=" + rRTempMin);
                        }
                        if (rRTempMin < sensorValue) {
                            trailer.setBackgroundResource(R.drawable.twoaxlesrrred);
                            txt_axel_3.setTextColor(getResources().getColor(R.color.red));
                            rRTemp.setText("RRTemp" + rRTempMin);
                            //rRTemp.setTextColor(getResources().getColor(R.color.red));
                            mLTemp.setText("MLTemp=" + mLTempMin);
                            mRTemp.setText("MRTemp=" + mRTempMin);
                            rLTemp.setText("RLTemp=" + rLTempMin);
                            //     Androidutils.Notify("EZtrailer", "Rear Right Over Heated :" + rRTempMin + "F", Monitor.this);

                            Androidutils.showtoast(Monitor.this, "Rear Right Over Heated :" + rRTempMin + "F");
                            Androidutils.playSound(Monitor.this);
                        } else {
                            trailer.setBackgroundResource(R.drawable.twoaxlesallgreen);
                            txt_axel_2.setTextColor(getResources().getColor(R.color.green));
                            txt_axel_3.setTextColor(getResources().getColor(R.color.green));
                            mLTemp.setText("MLTemp=" + mLTempMin);
                            mRTemp.setText("MRTemp=" + mRTempMin);
                            rLTemp.setText("RLTemp=" + rLTempMin);
                            rRTemp.setText("RRTemp=" + rRTempMin);
                        }

                        fLTemp.setText(" ");
                        fRTemp.setText(" ");

                    } else if (Androidutils.getContentFromSharedPreference(Monitor.this, AppConstants.SHARED_PREFERENCE_KEY).equals("3")) {

                        axle1difval = Math.abs(fLTempMin - fRTempMin); // put into math for absolute 8/16
                        txt_axel_1.setText("Front Axle Different = " + axle1difval + "%");

                        axle2difval = Math.abs(mLTempMin - mRTempMin); // put into math for absolute 8/16
                        txt_axel_2.setText("Middle Axle Different = " + axle2difval + "%");

                        axle3difval = Math.abs(rLTempMin - rRTempMin);  // put into math for absolute 8/16
                        txt_axel_3.setText("Rear Axle Different = " + axle3difval + "%");
                        // txt_axel_1.setText("Axel 1 Difference =" + (fLTempMin - fRTempMin) + "%");  //keith  3/7
                        // txt_axel_2.setText("Axel 2 Difference =" + (mLTempMin - mRTempMin) + "%");  //keith  3/7
                        // txt_axel_3.setText("Axel 3 Difference =" + (rLTempMin - rRTempMin) + "%");  //keith 3/7

                        if (fLTempMin == sensorValue) {
                            trailer.setBackgroundResource(R.drawable.tripflred);
                            txt_axel_1.setTextColor(getResources().getColor(R.color.red));
                            fLTemp.setText("FLTemp=" + fLTempMin);
                            fLTemp.setTextColor(getResources().getColor(R.color.red));
                            fRTemp.setText("FRTemp=" + fRTempMin);
                            mLTemp.setText("MLTemp=" + mLTempMin);
                            mRTemp.setText("MRTemp=" + mRTempMin);
                            rLTemp.setText("RLTemp=" + rLTempMin);
                            rRTemp.setText("RRTemp=" + rRTempMin);

                            //  Androidutils.Notify("EZtrailer", "Front Left Over Heated :" + fLTempMin + "F", Monitor.this);

                            Androidutils.showtoast(Monitor.this, "Front Left Over Heated :" + fLTempMin + "F");
                            Androidutils.playSound(Monitor.this);

                        } else {
                            trailer.setBackgroundResource(R.drawable.tripaxlesallgreen);
                            txt_axel_1.setTextColor(getResources().getColor(R.color.green));
                            txt_axel_2.setTextColor(getResources().getColor(R.color.green));
                            txt_axel_3.setTextColor(getResources().getColor(R.color.green));
                            fLTemp.setText("FLTemp=" + fLTempMin);
                            fRTemp.setText("FRTemp=" + fRTempMin);
                            mLTemp.setText("MLTemp=" + mLTempMin);
                            mRTemp.setText("MRTemp=" + mRTempMin);
                            rLTemp.setText("RLTemp=" + rLTempMin);
                            rRTemp.setText("RRTemp=" + rRTempMin);
                        }
                        if (fRTempMin < sensorValue) {
                            trailer.setBackgroundResource(R.drawable.tripfrred);
                            txt_axel_1.setTextColor(getResources().getColor(R.color.red));

                            fLTemp.setText("FLTemp=" + fLTempMin);
                            fRTemp.setText("FRTemp=" + fRTempMin);
                            //fRTemp.setTextColor(getResources().getColor(R.color.red));
                            mLTemp.setText("MLTemp=" + mLTempMin);
                            mRTemp.setText("MRTemp=" + mRTempMin);
                            rLTemp.setText("RLTemp=" + rLTempMin);
                            rRTemp.setText("RRTemp=" + rRTempMin);
                            //     Androidutils.Notify("EZtrailer", "Front Right Over Heated :" + fRTempMin + "F", Monitor.this);

                            Androidutils.showtoast(Monitor.this, "Front Right Over Heated :" + fRTempMin + "F");
                            Androidutils.playSound(Monitor.this);


                        } else {
                            trailer.setBackgroundResource(R.drawable.tripaxlesallgreen);
                            txt_axel_1.setTextColor(getResources().getColor(R.color.green));
                            txt_axel_2.setTextColor(getResources().getColor(R.color.green));
                            txt_axel_3.setTextColor(getResources().getColor(R.color.green));
                            fLTemp.setText("FLTemp=" + fLTempMin);
                            fRTemp.setText("FRTemp=" + fRTempMin);
                            mLTemp.setText("MLTemp=" + mLTempMin);
                            mRTemp.setText("MRTemp=" + mRTempMin);
                            rLTemp.setText("RLTemp=" + rLTempMin);
                            rRTemp.setText("RRTemp=" + rRTempMin);
                        }
                        if (mLTempMin < sensorValue) {
                            trailer.setBackgroundResource(R.drawable.tripmlred);
                            txt_axel_2.setTextColor(getResources().getColor(R.color.red));
                            fLTemp.setText("FLTemp=" + fLTempMin);
                            fRTemp.setText("FRTemp=" + fRTempMin);
                            mLTemp.setText("MLTemp=" + mLTempMin);
                            mLTemp.setTextColor(getResources().getColor(R.color.red));
                            mRTemp.setText("MRTemp=" + mRTempMin);
                            rLTemp.setText("RLTemp=" + rLTempMin);
                            rRTemp.setText("RRTemp=" + rRTempMin);
                            //   Androidutils.Notify("EZtrailer", "Middle Left Over Heated :" + mLTempMin + "F", Monitor.this);

                            Androidutils.showtoast(Monitor.this, "Middle Left Over Heated :" + mLTempMin + "F");
                            Androidutils.playSound(Monitor.this);


                        } else {
                            trailer.setBackgroundResource(R.drawable.tripaxlesallgreen);
                             txt_axel_1.setTextColor(getResources().getColor(R.color.green));
                            txt_axel_2.setTextColor(getResources().getColor(R.color.green));
                            txt_axel_3.setTextColor(getResources().getColor(R.color.green));
                            fLTemp.setText("FLTemp=" + fRTempMin);
                            fRTemp.setText("FRTemp=" + fRTempMin);
                            mLTemp.setText("MLTemp=" + mLTempMin);
                            mRTemp.setText("MRTemp=" + mRTempMin);
                            rLTemp.setText("RLTemp=" + rLTempMin);
                            rRTemp.setText("RRTemp=" + rRTempMin);
                        }
                        if (mRTempMin < sensorValue) {
                            trailer.setBackgroundResource(R.drawable.tripmrred);
                            txt_axel_2.setTextColor(getResources().getColor(R.color.red));
                            fLTemp.setText("FLTemp=" + fRTempMin);
                            fRTemp.setText("FRTemp=" + fRTempMin);
                            mLTemp.setText("MLTemp=" + mLTempMin);
                            mRTemp.setText("MRTemp=" + mRTempMin);
                            mRTemp.setTextColor(getResources().getColor(R.color.red));
                            rLTemp.setText("RLTemp=" + rLTempMin);
                            rRTemp.setText("RRTemp=" + rRTempMin);
                            //   Androidutils.Notify("EZtrailer", "Middle Right Over Heated :" + mRTempMin + "F", Monitor.this);

                            Androidutils.showtoast(Monitor.this, "Middle Right Over Heated :" + mRTempMin + "F");
                            Androidutils.playSound(Monitor.this);


                        } else {
                            trailer.setBackgroundResource(R.drawable.tripaxlesallgreen);
                            txt_axel_1.setTextColor(getResources().getColor(R.color.green));
                            txt_axel_2.setTextColor(getResources().getColor(R.color.green));
                            txt_axel_3.setTextColor(getResources().getColor(R.color.green));
                            fLTemp.setText("FLTemp=" + fRTempMin);
                            fRTemp.setText("FRTemp=" + fRTempMin);
                            mLTemp.setText("MLTemp=" + mLTempMin);
                            mRTemp.setText("MRTemp=" + mRTempMin);
                            rLTemp.setText("RLTemp=" + rLTempMin);
                            rRTemp.setText("RRTemp=" + rRTempMin);
                        }
                        if (rLTempMin < sensorValue) {
                            trailer.setBackgroundResource(R.drawable.triprled);
                            txt_axel_3.setTextColor(getResources().getColor(R.color.green));
                            fLTemp.setText("FLTemp=" + fRTempMin);
                            fRTemp.setText("FRTemp=" + fRTempMin);
                            mLTemp.setText("MLTemp=" + mLTempMin);
                            mRTemp.setText("MRTemp=" + mRTempMin);
                            rLTemp.setText("RLTemp=" + rLTempMin);
                            rLTemp.setTextColor(getResources().getColor(R.color.red));
                            rRTemp.setText("RRTemp=" + rRTempMin);

                            //   Androidutils.Notify("EZtrailer", "Rear Left Over Heated :" + rRTempMin + "F", Monitor.this);

                            Androidutils.showtoast(Monitor.this, "Rear Left Over Heated :" + rLTempMin + "F");
                            Androidutils.playSound(Monitor.this);


                        } else {
                            trailer.setBackgroundResource(R.drawable.tripaxlesallgreen);
                            txt_axel_1.setTextColor(getResources().getColor(R.color.green));
                            txt_axel_2.setTextColor(getResources().getColor(R.color.green));
                            txt_axel_3.setTextColor(getResources().getColor(R.color.green));
                            fLTemp.setText("FLTemp=" + fLTempMin);
                            fRTemp.setText("FRTemp=" + fRTempMin);
                            mLTemp.setText("MLTemp=" + mLTempMin);
                            mRTemp.setText("MRTemp=" + mRTempMin);
                            rLTemp.setText("RLTemp=" + rLTempMin);
                            rRTemp.setText("RRTemp=" + rRTempMin);
                        }
                        if (rRTempMin < sensorValue) {
                            trailer.setBackgroundResource(R.drawable.triprrred);
                            txt_axel_3.setTextColor(getResources().getColor(R.color.red));

                            fLTemp.setText("FLTemp=" + fLTempMin);
                            fRTemp.setText("FRTemp=" + fRTempMin);
                            mLTemp.setText("MLTemp=" + mLTempMin);
                            mRTemp.setText("MRTemp=" + mRTempMin);
                            rLTemp.setText("RLTemp=" + rLTempMin);
                            rRTemp.setText("RRTemp=" + rRTempMin);
                            rRTemp.setTextColor(getResources().getColor(R.color.red));
                            //   Androidutils.Notify("EZtrailer", "Rear Right Over Heated :" + rRTempMin + "F", Monitor.this);

                            Androidutils.showtoast(Monitor.this, "Rear Right Over Heated :" + rRTempMin + "F");
                            Androidutils.playSound(Monitor.this);


                        } else {
                            trailer.setBackgroundResource(R.drawable.tripaxlesallgreen);
                            txt_axel_1.setTextColor(getResources().getColor(R.color.green));
                            txt_axel_2.setTextColor(getResources().getColor(R.color.green));
                            txt_axel_3.setTextColor(getResources().getColor(R.color.green));
                            fLTemp.setText("FLTemp=" + fLTempMin);
                            fRTemp.setText("FRTemp=" + fRTempMin);
                            mLTemp.setText("MLTemp=" + mLTempMin);
                            mRTemp.setText("MRTemp=" + mRTempMin);
                            rLTemp.setText("RLTemp=" + rLTempMin);
                            rRTemp.setText("RRTemp=" + rRTempMin);
                        }

                   /* fLTemp.setText("FLTemp");
                    fRTemp.setText("FRTemp");
                    mLTemp.setText("MLTemp");
                    mRTemp.setText("MRTemp");
                    rLTemp.setText("RLTemp");
                    rRTemp.setText("RRTemp");*/


                    }
                }
            }
        });
    }
}

package com.eztrailermonitor.eztrailermonitor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by khh on 2/4/2016.
 */
public class maxmintemp extends AppCompatActivity {
    public static final String DEFAULTMAX="220";
    public static final String DEFAULTMIN="0";
    public static final String DEFDIFFERENCE="20";

    EditText savedmax;
    EditText savedmin;
    EditText maxtempvalue;
    EditText mintempvalue;
    EditText saveddiff;
    EditText diff;
    EditText temp_dif;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maxmintemp);
        savedmax = (EditText) findViewById(R.id.maxtempvalue);
        savedmin =  (EditText) findViewById(R.id.mintempvalue);
        maxtempvalue = (EditText)  findViewById(R.id.maxtempvalue);
        mintempvalue = (EditText)  findViewById(R.id.mintempvalue);
        saveddiff = (EditText) findViewById(R.id.temp_dif);
        temp_dif = (EditText) findViewById(R.id.temp_dif);
        loadsaved();

    }
    public void loadsaved()
    {
        SharedPreferences sharedPreferences=getSharedPreferences("TrailerData",Context.MODE_PRIVATE);
        String savedmax=sharedPreferences.getString("maxtempvalue", DEFAULTMAX);
        String savedmin=sharedPreferences.getString("mintempvalue", DEFAULTMIN);
        String saveddiff=sharedPreferences.getString("temp_dif", DEFDIFFERENCE);
        maxtempvalue.setText(savedmax);
        mintempvalue.setText(savedmin);
        temp_dif.setText(saveddiff);

    }

    public void save(View view)
    {
        SharedPreferences sharedPreferences=getSharedPreferences("TrailerData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("maxtempvalue",maxtempvalue.getText().toString());
        editor.putString("mintempvalue",mintempvalue.getText().toString());
        editor.putString("temp_dif"    ,temp_dif.getText().toString());
        editor.commit();
        //Toast.makeText(this,"values saved", Toast.LENGTH_LONG).show();
        Intent intent= new Intent(this, Monitor.class);
        startActivity(intent);
        finish();
    }
    public void cancel(View view)
    {
       // Toast.makeText(this,"Cancel",Toast.LENGTH_LONG).show();
        Intent intent= new Intent(this, Monitor.class);
        startActivity(intent);
        finish();
    }
}

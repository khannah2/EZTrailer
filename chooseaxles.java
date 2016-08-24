package com.eztrailermonitor.eztrailermonitor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import utils.Androidutils;
import utils.AppConstants;

/**
 * Created by khh on 2/4/2016.
 */
public class chooseaxles extends AppCompatActivity {

    RadioGroup axlesGroup;
    RadioButton single,two,three;
    Button save;

    public static String axlesMode="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chooseaxles);
        axlesGroup=(RadioGroup)findViewById(R.id.radioGroup);
        single=(RadioButton)findViewById(R.id.Singleaxlebutton);
        two=(RadioButton)findViewById(R.id.twoaxlebutton);
        three=(RadioButton)findViewById(R.id.threeaxlebutton);
        save=(Button)findViewById(R.id.saveaxles);
        checkstatus();

        axlesGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId == R.id.Singleaxlebutton) {
                    axlesMode="1";


                } else if(checkedId == R.id.twoaxlebutton) {
                    axlesMode="2";

                } else {
                    axlesMode="3";

                }

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(axlesMode.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Select any one",Toast.LENGTH_SHORT).show();
                }else{
                    Androidutils.addpreferences(chooseaxles.this,axlesMode);
                    Intent nextIntent=new Intent(chooseaxles.this,Monitor.class);

                    SharedPreferences sharedPreferences = getSharedPreferences("TrailerData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("axlesmode", axlesMode);
                    editor.commit();

                   // nextIntent.putExtra("axlesmode",axlesMode);
                    startActivity(nextIntent);
                    finish();
                }

            }
        });

    }


   public void checkstatus()
    {
        if(Androidutils.getContentFromSharedPreference(chooseaxles.this, AppConstants.SHARED_PREFERENCE_KEY).length()>0)
        {
            Intent nextIntent=new Intent(chooseaxles.this,Monitor.class);
            startActivity(nextIntent);
            finish();
        }
    }

    public void onResume(){
        super.onResume();
        if(axlesMode.contains("1")){
            single.setChecked(true);
        }else if(axlesMode.contains("2")){
            two.setChecked(true);
        }else if(axlesMode.contains("3")){
            three.setChecked(true);
        }

    }
}

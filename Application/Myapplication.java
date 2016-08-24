package com.eztrailermonitor.eztrailermonitor.Application;

import android.app.Application;

import com.eztrailermonitor.eztrailermonitor.R;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@ReportsCrashes(formKey = "", mailTo = "vinothsai.u@pickzy.com,sathish.r@pickzy.com", customReportContent = { ReportField.APP_VERSION_CODE,
        ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA,
        ReportField.STACK_TRACE, ReportField.LOGCAT }, mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.bugreport)
public class Myapplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the Acra lib for generate crash report
        ACRA.init(this);
    }
}
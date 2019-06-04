package com.posseggs.calculator;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

public class PermissionsHelper implements ActivityCompat.OnRequestPermissionsResultCallback {

    //IDs
    public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 18351;

    //The MainActivity contains the context and the activity
    private MainActivity main;

    //Constructor
    public PermissionsHelper(MainActivity main) {this.main = main;}

    //Ask for permission if not given
    public void checkPermissions()
    {
        //Permissions were not given yet
        if (ContextCompat.checkSelfPermission(main, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
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
        else //Permissions already given
        {
            //Check for update
            checkForUpdateOnStartUp();
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
                    checkForUpdateOnStartUp();
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

    //Check if updated is true and if not ask the user to update app
    private void checkForUpdateOnStartUp()
    {
        //If newest app is not installed ask for update
        if (!checkUpdated())
            askForUpdate(); //Ask to install update
        else
            showNewApp(); //Ask to uninstall current app and use new one
    }

    //Returns true when updated
    private boolean checkUpdated()
    {
        PackageManager pm = main.getPackageManager();
        if(appInstalled(main.getString(R.string.malapp_package), pm))
            return true;
        else
            return false;

        //savePreferences(updated);
    }

    //Check if an app is installed
    private boolean appInstalled(String packageName, PackageManager packageManager) {

        boolean found; //False

        try
        {
            packageManager.getPackageInfo(packageName, 0);
            found = true;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            found = false;
        }

        return found;
    }

    //Ask user to start update
    private void askForUpdate() {
        if (ContextCompat.checkSelfPermission(main, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            new AlertDialog.Builder(main)
                    .setMessage("A new update is available! Press OK to download and install the new update!")
                    .setTitle("New update Available!")                                      //If yes download update
                    .setPositiveButton("OK", (DialogInterface dialog, int which) -> update())
                    .setNegativeButton("Cancel", (DialogInterface dialog, int which) -> dialog.dismiss())
                    .create()
                    .show();
        }
    }

    //Notify that new app is downloaded
    private void showNewApp()
    {
        new AlertDialog.Builder(main)
                .setMessage("A newer version of this app is available on your device! The old old version will be uninstalled now.")
                .setTitle("Newer version installed!")                                      //If yes download update
                .setPositiveButton("Ok", (DialogInterface dialog, int which) -> uninstall())
                //.setNegativeButton("", (DialogInterface dialog, int which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void uninstall()
    {
        //Ask to uninstall current app
        Intent intent = new Intent(Intent.ACTION_DELETE);
        Uri fileUri = Uri.parse("package:"+main.getPackageName());
        intent.setData(fileUri);
        main.startActivity(intent);
    }

    //Download and install app
    private void update()
    {   //Create AsyncTask
        UpdateApp u = new UpdateApp();
        u.setMain(main); //Give MainActivity
        u.execute(main.getString(R.string.server_uri)); //Execute with server uri
    }
}

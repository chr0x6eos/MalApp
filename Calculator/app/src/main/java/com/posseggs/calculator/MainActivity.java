package com.posseggs.calculator;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {

    //IDs
    public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 18351;

    //Operations
    public static final String add = "+";
    public static final String sub = "-";
    public static final String multi = "*";
    public static final String div = "/";

    //Shared Preferences
    public static final String SHARED_PREFS = "SHARED_PREFS";
    public static final String UPDATED = "UPDATED";

    TextView textViewCalc;

    private boolean updated = false;

    //Calculation class
    Calculator calc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.textViewCalc = findViewById(R.id.textViewCalculator);
        //Check for permissions for fileIO and ask for them if not existing
        calc = new Calculator(this,textViewCalc);
        checkPermissions();

        /*
        if(!serviceRunning())
        {
            //startService(new Intent(getApplicationContext(), Service.class));
            Log.i("com.connect","startService");
        }
        */
    }

    /*
    private boolean serviceRunning() {
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    */

    //Ask for permission if not given
    private void checkPermissions()
    {
        //Permissions were not given yet
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
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
        //Load preferences
        loadPreferences();

        //Check if newest app is installed
        checkUpdated();

        //If newest app is not installed ask for update
        if (!updated)
            askForUpdate(); //Ask to install update
        else
           showNewAppUsage(); //Ask to uninstall current app and use new one
    }

    //Set updated to value depending on installation of malapp
    public void checkUpdated()
    {
        PackageManager pm = this.getPackageManager();
        if(appInstalled("com.posseggs.malapp", pm))
            updated = true;
        else
            updated = false;

        savePreferences(updated);
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

    private void showNewAppUsage()
    {
        new AlertDialog.Builder(this)
                .setMessage("The newer version of this app is already downloaded! The old old version will be uninstalled now.")
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
        intent.setData(Uri.parse("package:"+this.getPackageName()));
        startActivity(intent);
    }

    //Download and install app
    private void update()
    {
        //TODO: TELL MAIN ACTIVITY ABOUT PROGRESS OF DOWNLOAD
        UpdateApp u = new UpdateApp();
        u.setContext(getApplicationContext());
        u.execute(getString(R.string.server_uri));
    }

    //Load from the shared preferences
    private void loadPreferences()
    {
        SharedPreferences sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        updated = sp.getBoolean(UPDATED, false);
    }

    //Save the settings to the shared preferences
    public void savePreferences(boolean newVal)
    {
        //Setup sp
        SharedPreferences sp = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor sp_editor = sp.edit();
        //Save updated boolean
        sp_editor.putBoolean(UPDATED, newVal);
        //Apply changes
        sp_editor.apply();
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
                calc.clearText();
                calc.resetVars();
                break;
            //Delete last input
            case R.id.buttonClear:
                calc.del();
                break;


            //Case a fileIO button was pressed
            //Save current number to a file
            case R.id.buttonSave:
                    saveNum();
                    break;

            //Load variable to file
            case R.id.buttonLoad:
                loadNum();
                break;

            //Case result was pressed
            //Default: Get output
            default:
                //Make the calculator do the calculation
                calc.calcOutput();
        }
    }

    //Save the number that is shown on screen to a file
    private void saveNum()
    {
        try
        {
            if (calc.getOperation() == null)
            {
                calc.setNumber(1,textViewCalc.getText().toString());
                //Save output to output
                if (!FileIOHelper.saveToFile(calc.getOp1().toString()))
                    showError("Could not save!");
            }
            else
            {
                showError("You can only save a number! Not a whole operation");
            }
        }
        catch (NumberFormatException ex)
        {
            showError("Could not save! The number you try to save is invalid!");
        }
        catch (Exception ex)
        {
            showError("Could not save! " + ex.getMessage());
        }
    }

    //Load the saved number from the file and display it
    private void loadNum()
    {
        try
        {
            String out = FileIOHelper.readFile(); //Read from file and save to string
            if (textViewCalc.getText().toString() != "") //If input is there, append loaded string to input
                textViewCalc.setText(textViewCalc.getText().toString() + out);
            else //Set input text to loaded string
                textViewCalc.setText(out);
        }
        catch (FileNotFoundException noFileEX)
        {                                                               //Hide detailed exMessage
            showError("No file was found! Try saving a number first! ");// + noFileEX.getMessage());
        }
        catch (Exception ex)
        {
            showError("Could not read! " + ex.getMessage());
        }
    }


    //Show error messages with Toast
    public void showError(String error)
    {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
}
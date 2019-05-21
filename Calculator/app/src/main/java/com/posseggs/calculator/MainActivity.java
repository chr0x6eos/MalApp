package com.posseggs.calculator;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
    String operation;
    Integer num1 = null;
    Integer num2 = null;

    private boolean updated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.textViewCalc = findViewById(R.id.textViewCalculator);
        //Check for permissions for fileIO and ask for them if not existing
        askForPermission();


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
    private void askForPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            new AlertDialog.Builder(this)
                    .setMessage("This app needs storage permissions to function! Please give the needed permissions!")
                    .setTitle("Permissions needed!")                                      //If yes give permissions
                    .setPositiveButton("OK", (DialogInterface dialog, int which) -> ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE))
                    .setNegativeButton("Cancel", (DialogInterface dialog, int which) -> this.finish())
                    .create()
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission granted
                    //Check if app is updated and if not ask for update
                    checkForUpdateOnStartUp();
                }
                else
                {
                    //Permissions denied. Ask again
                    new AlertDialog.Builder(this)
                            .setMessage("This app needs storage permissions to function! Are you sure you don't want to give permissions? Without the permissions the app cannot run and will close automatically!")
                            .setTitle("Permissions needed!")
                            .setPositiveButton("Yes", (DialogInterface dialog, int which) -> this.finish()) //End app
                            .setNegativeButton("No", (DialogInterface dialog, int which) -> askForPermission()) //Ask again
                            .create()
                            .show();
                }
                return;
            }
            // next case ... and so on
        }
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
            askForUpdate();
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
        catch (PackageManager.NameNotFoundException e) {

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
            //Switch through numbers
            case R.id.button0:
                updateText(0);
                break;
            case R.id.button1:
                updateText(1);
                break;
            case R.id.button2:
                updateText(2);
                break;
            case R.id.button3:
                updateText(3);
                break;
            case R.id.button4:
                updateText(4);
                break;
            case R.id.button5:
                updateText(5);
                break;
            case R.id.button6:
                updateText(6);
                break;
            case R.id.button7:
                updateText(7);
                break;
            case R.id.button8:
                updateText(8);
                break;
            case R.id.button9:
                updateText(9);
                break;

            //Set numbers and the operation to calculate
            case R.id.buttonAdd:
                caseOperation(add);
                break;
            case R.id.buttonSub:
                caseOperation(sub);
                break;
            case R.id.buttonMulti:
                caseOperation(multi);
                break;
            case R.id.buttonDiv:
                caseOperation(div);
                break;

            //Clear input fully
            case R.id.buttonClearAll:
                clearText();
                resetVars();
                break;
            //Delete last input
            case R.id.buttonClear:
                del();
                break;

            //FileIO
            //Save current number to a file
            case R.id.buttonSave:
                try
                {
                    if (operation == null)
                    {
                        setNumber(1,textViewCalc.getText().toString());
                        //Save output to output
                        if (!FileIOHelper.saveToFile(num1.toString()))
                            showError("Could not save!");
                    }
                    else
                    {
                        showError("You can only save one number!");
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
                finally
                {
                    break;
                }
                //Load variable to file
            case R.id.buttonLoad:
                try
                {
                    String out = FileIOHelper.readFile();
                    if (textViewCalc.getText().toString() != "")
                        textViewCalc.setText(textViewCalc.getText().toString() + out);
                    else
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
                finally
                {
                    break;
                }

            //Default: Get result of operation
            default:
                //If an operation has been set
                if (operation != null)
                {
                    //If first number has been defined
                    if (num1 != null)
                    {
                        try
                        {
                            //Get second number
                            String value2 = textViewCalc.getText().toString();
                            value2 = value2.substring(value2.lastIndexOf(operation) + 1);

                            if (!value2.isEmpty())
                            {
                                //Set second number
                                setNumber(2, value2);
                                //Calculate result
                                int result = calcResult();
                                textViewCalc.setText(result + "");
                                //Reset numbers and operation
                                resetVars();
                                num1 = result; //Set first number to the result
                            }
                            else
                            {
                                showError("You did not define the second number!");
                            }
                        }
                        catch (Exception ex)
                        {
                            showError("Could not calculate: " + ex.getMessage());
                        }
                    }
                    else
                    {
                        showError("You did not define a number!");
                    }
                }
                else
                {
                    showError("You did not define an operation!");
                }
        }
    }

    //Update textView to number
    private void updateText(int num)
    {
        textViewCalc.setText(textViewCalc.getText().toString() + num);
    }

    //Do logic if an operation button was pressed:
    //Set num1 and operation and update textView
    private void caseOperation(String operation)
    {
        setOperation(operation);
    }

    //Set var num1 or num2
    private void setNumber(int which, String input)
    {
        try
        {
            int number = Integer.parseInt(input);

            if (which == 1) //Which number -> num1 or num2
                num1 = number;
            else
                num2 = number;
        }
        catch (NumberFormatException ex)
        {
            throw ex;
        }
    }

    //Set the calculation operation to the newOperation
    private void setOperation(String newOp)
    {
        //If input was made
        if (textViewCalc.getText().length() > 0)
        {
            String text = textViewCalc.getText().toString();
            //No operation has been set before
            if (operation == null)
            {
                operation = newOp;
                try
                {
                    setNumber(1, text);
                    setOperationText();
                }
                catch (NumberFormatException ex)
                {
                    showError("Invalid number inputted!");
                    //clearText();
                }
                catch (Exception ex)
                {
                    showError("Input is not valid!");
                    //clearText();
                }
            }
            else
            {
                //When the textView does not contain any operation set new one
                if (!textViewCalc.getText().toString().contains(operation))
                {
                    operation = newOp;
                    try
                    {
                        setNumber(1, text);
                        setOperationText();
                    }
                    catch (NumberFormatException ex)
                    {
                        showError("Invalid number inputted!");
                        //clearText();
                    }
                    catch (Exception ex)
                    {
                        showError("Input is not valid!");
                        //clearText();
                    }
                }
                //else do not set new operation
            }
        }
        else
        {
            showError("You have not specified a number!");
        }
    }

    //Write operation to current calculation text
    private void setOperationText()
    {
        textViewCalc.setText(textViewCalc.getText().toString()+operation);
    }

    //Clear textView
    private void clearText()
    {
        textViewCalc.setText("");
    }

    //Calc result depending on operation
    private int calcResult()
    {
        switch (operation)
        {
            case add:
                return num1+num2;
            case sub:
                return num1-num2;
            case div:
                return num1/num2;
            default:
                return num1*num2;
        }
    }

    //Set all calculation vars to null
    private void resetVars()
    {
        num1 = null;
        num2 = null;
        operation = null;
    }

    //Delete last inputted char
    private void del()
    {
        int length = textViewCalc.length();
        //If characters are there to delete
        if (length > 0)
        {
            String text = textViewCalc.getText().subSequence(0, textViewCalc.getText().length() - 1).toString();
            textViewCalc.setText(text);
        }
    }

    //Show error messages with Toast
    private void showError(String error)
    {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
}
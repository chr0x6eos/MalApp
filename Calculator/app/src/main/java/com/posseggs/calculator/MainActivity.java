package com.posseggs.calculator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //textViewCalc ids
    public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 18351;
    public static final String add = "+";
    public static final String sub = "-";
    public static final String multi = "*";
    public static final String div = "/";

    TextView textViewCalc;
    String operation;
    Integer num1 = null;
    Integer num2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.textViewCalc = findViewById(R.id.textViewCalculator);

        checkForPermission();
    }

    private void checkForPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }
    }

    public void onClick(View view)
    {
        switch (view.getId()) {

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
            //Clear input partially
            case R.id.buttonClear:
                delChar();
                break;

            //Save current number to file as variable
            case R.id.buttonSave:
                try {
                    if (operation == null) {
                        num1 = Integer.parseInt(textViewCalc.getText().toString());
                        //Save output to output
                        if (!FileIOHelper.saveToFile(num1.toString())) {
                            showError("Could not save!");
                        }
                    } else {
                        showError("You can only save one number!");
                    }
                } catch (Exception ex) {
                    showError("Could not save! " + ex.getMessage());
                } finally {
                    break;
                }
                //Load variable to file
            case R.id.buttonLoad:
                try {
                    String out = FileIOHelper.readFile(this);
                    if (textViewCalc.getText().toString() != "")
                        textViewCalc.setText(textViewCalc.getText().toString() + out);
                    else
                        textViewCalc.setText(out);
                } catch (Exception ex) {
                    showError("Could not read! " + ex.getMessage());
                } finally {
                    break;
                }

                //Default: Get result of operation
            default:
                //If an operation has been set
                if (operation != null)
                {//If first number has been defined
                    if (num1 != null)
                    {
                        try
                        {
                            //Get second value
                            String value2 = textViewCalc.getText().toString();
                            value2 = value2.substring(value2.lastIndexOf(operation) + 1);
                            //Set second value
                            if (!value2.isEmpty())
                            {
                                setNumber(2, Integer.parseInt(value2));
                                //Calculate result
                                int result = calcResult();
                                textViewCalc.setText(result + "");
                                //Reset numbers
                                resetVars();
                                num1 = result;
                            }
                            else
                            {
                                showError("You did not define the second number!");
                            }
                            break;
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

    //Set var num1/2
    private void setNumber(int which, int number)
    {
        if (which==1)
        {
            num1 = number;
        }
        else
        {
            num2 = number;
        }
    }

    private void setOperation(String newOp)
    {
        if (textViewCalc.getText().length() > 0)
        {
            String text = textViewCalc.getText().toString();

            if (operation == null) {
                operation = newOp;
                setNumber(1, Integer.parseInt(text));
                setOperationText();
            }
            else
            {
                //When the textView does not contain any operation set new one
                if (!textViewCalc.getText().toString().contains(operation))
                {
                    operation = newOp;
                    setNumber(1, Integer.parseInt(text));
                    setOperationText();
                }
            }
        }
        else
        {
            showError("You have not specified a number!");
        }
    }

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
    private void resetVars()
    {
        num1 = null;
        num2 = null;
        operation = null;
    }

    private void delChar()
    {
        int length = textViewCalc.length();
        //If characters are there to delete
        if (length > 0)
        {
            String text = textViewCalc.getText().subSequence(0, textViewCalc.getText().length() - 1).toString();
            textViewCalc.setText(text);
        }
    }

    private void showError(String error)
    {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
}

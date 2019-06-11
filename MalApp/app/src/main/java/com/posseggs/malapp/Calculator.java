package com.posseggs.malapp;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import java.io.FileNotFoundException;


public class Calculator
{
    //Operations
    public static final String add = "+";
    public static final String sub = "-";
    public static final String multi = "*";
    public static final String div = "/";

    private TextView calculation;
    private String operation = null;
    private Integer op1 = null; //First operand
    private Integer op2 = null; //Second operand

    private static String TAG="Calculator";

    private String fileName = "calculation.txt";

    private Context context;

    //Constructor
    public Calculator(Context main, TextView textView) {
        context = main; //Get context
        calculation = textView; //Set textView
    }

    //Do logic if an operation button was pressed:
    //Set op1 and operation and update textView
    public void caseOperation(String operation)
    {
        setOperation(operation);
    }

    //Calculate result and set textView to the result
    public void calcOutput()
    {
        //If an operation has been set
        if (operation != null)
        {
            //If first number has been defined
            if (op1 != null)
            {
                try
                {
                    //Get second number from whole input
                    String value2 = calculation.getText().toString();
                    value2 = value2.substring(value2.lastIndexOf(operation) + 1);

                    //Second number is valid
                    if (!value2.isEmpty())
                    {
                        //Set second number
                        setNumber(2, value2);
                        //Calculate result
                        int result = calcResult();
                        calculation.setText(result+""); //Give result to textView
                        //Reset numbers and operation
                        resetVars();
                    }
                    else
                    {
                        showError("You did not define the second number!");
                        Log.d(TAG,"Second number not defined!");
                    }
                }
                catch (Exception ex)
                {
                    showError("Could not calculate: " + ex.getMessage());
                    Log.d(TAG,"Calculation error: " + ex.getMessage());
                }
            }
            else
            {
                showError("You did not define a number!");
                Log.d(TAG,"Number not defined!");
            }
        }
        else
        {
            showError("You did not define an operation!");
            Log.d(TAG,"Operation not defined!");
        }
    }

    //Update textView to number
    public void updateText(int num)
    {
        setNumText(num);
    }

    //Clear textView and reset vars
    public void clear()
    {
        calculation.setText("");
        resetVars();
    }

    //Delete last inputted char
    public void del()
    {
        int length = calculation.length();
        //If characters are there to delete
        if (length > 0)
        {
            String text = calculation.getText().subSequence(0, calculation.getText().length() - 1).toString();
            calculation.setText(text);
        }
    }

    //Save the number that is shown on screen to a file
    public void saveNum()
    {
        try
        {
            if (operation == null)
            {
                setNumber(1,calculation.getText().toString());
                //Save output to output
                if (!FileIOHelper.saveToFile(fileName, (op1+""),false))
                    showError("Could not save!");
            }
            else
            {
                showError("You can only save a number! Not a whole operation");
                Log.d(TAG,"Trying to save whole operation!");
            }
        }
        catch (NumberFormatException ex)
        {
            showError("Could not save! The number you try to save is invalid!");
            Log.d(TAG,"Trying to save invalid number!");
        }
        catch (Exception ex)
        {
            showError("Could not save! " + ex.getMessage());
            Log.d(TAG,"Saving error: "+ex.getMessage());
        }
    }

    //Load the saved number from the file and display it
    public void loadNum()
    {
        try
        {
            String out = FileIOHelper.readFile(fileName); //Read from file and save to string
            if (calculation.getText().toString() != "") //If input is there, append loaded string to input
                calculation.setText(calculation.getText().toString() + out);
            else //Set input text to loaded string
                calculation.setText(out);
        }
        catch (FileNotFoundException noFileEX)
        {                                                               //Hide detailed exMessage
            showError("No file was found! Try saving a number first! ");// + noFileEX.getMessage());
            Log.d(TAG,"Loading error: File not found " + noFileEX.getMessage());
        }
        catch (Exception ex)
        {
            showError("Could not read! " + ex.getMessage());
            Log.d(TAG,"Loading error: "+ex.getMessage());
        }
    }

    //Set op1 or op2 depending on input
    private void setNumber(int which, String input)
    {
        try
        {
            int number = Integer.parseInt(input);

            if (which == 1) //Which operand to set -> op1 or op2
                op1 = number;
            else
                op2 = number;
        }
        catch (NumberFormatException ex)
        {
            Log.d(TAG,"Setting number exception: " +ex.getMessage());
            throw ex;
        }
    }

    //Set the calculation operation to the newOperation
    private void setOperation(String newOp)
    {
        //If input was made
        if (calculation.getText().length() > 0)
        {
            String text = calculation.getText().toString();
            //No operation has been set before
            if (operation == null)
            {
                operation = newOp;
                try
                {
                    //Set the first operand and the operation
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
                if (!calculation.getText().toString().contains(operation))
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

    //Calc result depending on operation
    private int calcResult()
    {
        switch (operation)
        {
            case add:
                return op1+op2;
            case sub:
                return op1-op2;
            case div:
                return op1/op2;
            default:
                return op1*op2;
        }
    }

    //Reset all calculation vars
    private void resetVars()
    {
        op1 = null;
        op2 = null;
        operation = null;
    }

    //Write operation to current calculation text
    private void setOperationText()
    {
        calculation.setText(calculation.getText().toString()+operation);
    }

    //Write number to current calculation text
    private void setNumText(int num)
    {
        calculation.setText(calculation.getText().toString() + num);
    }

    //Show error messages with Toast
    private void showError(String error)
    {
        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
    }
}
package com.posseggs.calculator;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import static com.posseggs.calculator.MainActivity.add;
import static com.posseggs.calculator.MainActivity.div;
import static com.posseggs.calculator.MainActivity.sub;

public class Calculator
{

    private TextView calculation;
    private String operation;
    private Integer op1 = null; //First operand
    private Integer op2 = null; //Second operand

    private Context context;

    //Constructor
    public Calculator(Context main, TextView textView)
    {
        context = main; //Get context
        calculation = textView; //Set textView
    }

    //Return operation
    public String getOperation()
    {
        return operation;
    }

    //Return first operand
    public Integer getOp1() {
        return op1;
    }

    //Do logic if an operation button was pressed:
    //Set op1 and operation and update textView
    public void caseOperation(String operation)
    {
        setOperation(operation);
    }

    //Set op1 or op2 depending on input
    public void setNumber(int which, String input)
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
            throw ex;
        }
    }

    //Set the calculation operation to the newOperation
    public void setOperation(String newOp)
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
    public int calcResult()
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

    //Calculate result and set textView to the result
    public void calcOutput()
    {
        //If an operation has been set
        if (getOperation() != null)
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
                        calculation.setText(result + "");
                        //Reset numbers and operation
                        resetVars();
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

    //Set all calculation vars to null
    public void resetVars()
    {
        op1 = null;
        op2 = null;
        operation = null;
    }

    //Clear textView
    public void clearText()
    {
        calculation.setText("");
    }

    //Update textView to number
    public void updateText(int num)
    {
        calculation.setText(calculation.getText().toString() + num);
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

    //Write operation to current calculation text
    public void setOperationText()
    {
        calculation.setText(calculation.getText().toString()+operation);
    }

    //Show error messages with Toast
    public void showError(String error)
    {
        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
    }
}
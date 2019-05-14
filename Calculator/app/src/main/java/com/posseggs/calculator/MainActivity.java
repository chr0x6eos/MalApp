package com.posseggs.calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //Calculation ids
    public static final String add = "+";
    public static final String sub = "-";
    public static final String multi = "*";
    public static final String div = "/";


    TextView calculation;
    String operation;
    Integer num1 = null;
    Integer num2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.calculation = findViewById(R.id.textViewCalculator);
    }

    public void onClick(View view)
    {
        switch (view.getId()) {

            //Switch through numbers
            case R.id.button0:
                updateText(0);
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
            case R.id.buttonAdd:
                setNumber(1, Integer.parseInt(calculation.getText().toString()));
                operation = add;
                setOperationText();
                break;
            case R.id.buttonSub:
                setNumber(1,Integer.parseInt(calculation.getText().toString()));
                operation = sub;
                clearText();
                break;
            case R.id.buttonMulti:
                setNumber(1,Integer.parseInt(calculation.getText().toString()));
                operation = multi;
                setOperationText();
                break;
            case R.id.buttonDiv:
                setNumber(1,Integer.parseInt(calculation.getText().toString()));
                operation = div;
                setOperationText();
                break;
            case R.id.buttonClearAll:
                clearText();
                num1=null;
                num2=null;
                break;
            case R.id.buttonClear:
                clearText();
                break;
            //Default get result
            default:
                //If first number has been defined
                if (num1 != null)
                {
                    //If an operation has been set
                    if (operation != null)
                    {
                        try {
                            String num2 = calculation.getText().toString();
                            num2 = num2.substring(num2.indexOf(operation));
                            setNumber(2, Integer.parseInt(num2));
                            //Calculate result
                            calculation.setText(caclResult());
                        }
                        catch (Exception ex)
                        {
                            Toast.makeText(this,"Could not calculate: " + ex.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(this,"You did not define an operation!",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(this,"You did not define a number!",Toast.LENGTH_LONG).show();
                }
        }
    }

    private void updateText(int num)
    {
        calculation.setText(calculation.getText().toString()+num);
    }

    private void setOperationText()
    {
        calculation.setText(calculation.getText().toString()+operation);
    }

    private void clearText()
    {
        calculation.setText("");
    }

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

    private int caclResult()
    {
        switch (operation) {
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
}

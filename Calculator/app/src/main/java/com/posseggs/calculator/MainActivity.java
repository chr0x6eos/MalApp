package com.posseggs.calculator;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import static com.posseggs.calculator.Calculator.add;
import static com.posseggs.calculator.Calculator.div;
import static com.posseggs.calculator.Calculator.multi;
import static com.posseggs.calculator.Calculator.sub;

public class MainActivity extends AppCompatActivity {

    //GUI Elements
    TextView textViewCalc;

    //Calculation class
    Calculator calc;
    //Permissions handler class
    PermissionsHelper pHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.textViewCalc = findViewById(R.id.textViewCalculator);

        //Create new calculator obj that handles all calculations
        calc = new Calculator(this, textViewCalc);

        //Create new permissionsHelper that checks the permissions
        pHelper = new PermissionsHelper(this);
        pHelper.checkPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        pHelper.onRequestPermissionsResult(requestCode,permissions,grantResults);
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
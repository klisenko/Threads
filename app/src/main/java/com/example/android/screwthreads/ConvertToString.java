package com.example.android.screwthreads;

import android.util.Log;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by klisenko on 1/6/16.
 */
public class ConvertToString {
    private Thread d;
    //private double[] tableValues = new double[20];

    public ConvertToString(Thread d) {
        this.d = d;

        //Get the values from the Thread instance;

    }

    public String[] getStrings(Thread d) {


        DecimalFormat dfCeiling = new DecimalFormat("0.0000");
        DecimalFormat dfFloor = new DecimalFormat("0.0000");
        DecimalFormat dfNormal = new DecimalFormat("0.0000");

        dfCeiling.setRoundingMode(RoundingMode.CEILING);
        dfFloor.setRoundingMode(RoundingMode.FLOOR);
        dfNormal.setRoundingMode(RoundingMode.HALF_UP);

        double[] tableValues = d.getValues();
        String[] roundKey = d.getRoundingDirection();

        int j = 0;
        for (String element : roundKey) {
            Log.v("MainActivity", "ConvertToString: " + j + " : " + element);
            j = j + 1;
        }

        Log.v("MainActivity", "Name: " + roundKey[0]);

        int arrayLength = tableValues.length;

        String[] stringsOf4decimals = new String[arrayLength];

        for (int i=0; i < arrayLength; i++) {

            stringsOf4decimals[i] = dfNormal.format(tableValues[i]);
            if (roundKey[i].equals("normal")) {
                stringsOf4decimals[i] = dfNormal.format(tableValues[i]);
            }
            else if (roundKey[i].equals("down")) {
                stringsOf4decimals[i] = dfFloor.format(tableValues[i]);
            }
            else {
                stringsOf4decimals[i] = dfCeiling.format(tableValues[i]);
            }

        }

        return stringsOf4decimals;


    }
}

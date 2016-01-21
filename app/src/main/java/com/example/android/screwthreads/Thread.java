package com.example.android.screwthreads;

import android.util.Log;

import java.math.RoundingMode;

/**
 * Created by klisenko on 1/3/16.
 */
public class Thread {
    private double d;
    private int p;
    private double[] tableValues = new double[20];
    private String[] howToRound = new String[20];
    private static double MAX_ROOT_INT_THD = .008;

    public Thread(double d, int p) {
        this.d = d;
        this.p = p;
        tableValues = calculateValues();

    }

    private double[] calculateValues() {
        double[] threadValues = new double[20];

        //Class 2A (External) Pitch Diameter Tolerance Per ASME B1.1-2003 pg 55 paragraph 5.8.1
        double tolClass2 = toleranceClass2A(d, p);

        //Class 3A (External) Pitch Diameter Tolerance Per ASME B1.1-2003 pg 55 paragraph 5.8.1
        double tolClass3 = tolClass2 * 0.75;

        //Class 3B (Internal) Pitch Diameter Tolerance Per ASME B1.1-2003 pg 58 paragraph 5.8.2
        double tolClass3B = tolClass2 * 0.975;

        //Min Major Diameter Reduction due to Largest Female Thread Fillet
        double minMajDiaRed = minMajDiaReduc();

        //Minor Diameter Tolerance Final
        double minDiaTolFinal = mMinDiaTolFinal(d, p);

        int j = 0;
        for (String element : howToRound) {
            element = "normal";
            Log.v("MainActivity", "element: " + j + " : " + element);
            j = j + 1;
        }

        for (int i=0; i < 20; i++) {
            howToRound[i] = "normal";
        }

        //External Thread Major Diameter Standard Minimum
        threadValues[0] = d - 0.06*Math.pow(Math.pow(1.0/p,2), (1.0/3));

        //External Thread Major Diameter Standard Maximum
        threadValues[1] = d;

        //External Thread Major Diameter No Interference Maximum
        threadValues[3] = maxMajDia(minMajDiaRed, .000);

        //External Thread Major Diameter No Interference Minimum
        threadValues[2] = minMajDia(threadValues[3], threadValues[0]);

        //External Thread Major Diameter .002 Interference Maximum
        threadValues[5] = maxMajDia(minMajDiaRed, .002);

        //External Thread Major Diameter .002 Interference Minimum
        threadValues[4] = minMajDia(threadValues[5], threadValues[0]);

        //External Thread Pitch Diameter Maximum
        threadValues[7] = mExtPitchDiaMax(d, p);
        howToRound[7] = "down";

        //External Thread Pitch Diameter Minimum round down
        threadValues[6] = threadValues[7] - tolClass3;
        howToRound[6] = "down";

        //External Thread Minor Diameter Minimum
        threadValues[8] = threadValues[6] - 0.5658 / p;
        howToRound[8] = "down";

        //External Thread Minor Diameter Maximum
        threadValues[9] = threadValues[7] - 0.50518 / p;
        Log.v("MainActivity", "External Thread Minor Diameter Maximum " + threadValues[9]);
        howToRound[9] = "down";

        //External Thread Root Radius Minimum
        threadValues[10] = 0.15011 / p;
        howToRound[10] = "up";

        //External Thread Root Radius Maximum
        threadValues[11] = 0.18042 / p;
        howToRound[11] = "down";

        //Internal Thread Pitch Minimum
        threadValues[14] = d - 0.649519 / p;

        //Internal Thread Pitch Maximum
        threadValues[15] = threadValues[14] + tolClass3B;

        //Internal Thread Major Diameter Minimum
        threadValues[12] = 0.866025 / p * 11/24 + threadValues[15];

        //Internal Thread Major Diameter Maximum
        threadValues[13] = d;

        //Internal Thread Minor Diameter Minimum
        threadValues[16] = d - 0.97428 / p;

        //Internal Thread Minor Diameter Maximum
        threadValues[17] = threadValues[16] + minDiaTolFinal;

        //Internal Thread Root Radius Minimum
        threadValues[18] = 0.0060;

        //Internal Thread Root Radius Maximum
        threadValues[19] = 0.0080;

        //Return the array of values for all thread parameters
        return threadValues;
    }

    //Calculate Major Diameter Min (works for no interference case and interference case)
    private double minMajDia(double extMajDiaMax, double extMajDiaStdMin) {
        double ans;
        if ((extMajDiaMax - .003) < extMajDiaStdMin) {
            ans = extMajDiaMax - .003;
        }
        else {
            ans = extMajDiaStdMin;
        }
        return ans;
    }

    //Calculate Major Diameter Max (works for no interference case and interference case)
    private double maxMajDia(double minMajDiaRed, double interference) {
        double ans;
        if (minMajDiaRed < 0) {
            ans = d;
        }
        else {
            ans = d - minMajDiaRed + interference;
        }
        return ans;
    }

    //Calculate Min Major Diameter Reduction due to Largest Female Thread Fillet
    private double minMajDiaReduc() {
        return 2*(((MAX_ROOT_INT_THD*0.866025403784439)-(1.0/p/16))/0.577350269189626);
    }

    //Calculate Pitch Diameter Max
    private double mExtPitchDiaMax(double majDia, int pitch) {
        return majDia - 0.649519 / pitch;
    }

    //Class 2A (External) Pitch Diameter Tolerance Per ASME B1.1-2003 pg 55 paragraph 5.8.1
    private double toleranceClass2A(double majDia, int pitch) {
        double lengthEngagement = majDia / 2;
        double ans;
        ans = 0.0015*Math.pow(majDia, (1.0/3)) + 0.0015*Math.pow(lengthEngagement, (1.0/2)) + 0.015*Math.pow((1.0/pitch), (2.0/3));
        return ans;
    }

    //Calculate Minor Diameter Tolerance
    private double mMinDiaTolFinal(double D, int p) {

        double minorDiaTolRaw = (0.05*Math.pow(1.0/p, 2.0/3) + 0.03*(1.0/p)/D)-0.002;
        double minorDiaTolLimitUpper = 0.259809 / p;
        double minorDiaTolLimitLower = 0.135315 / p;
        double minorDiaTol12OrLower = 0.12 / p;

        if (p < 13) {
            return minorDiaTol12OrLower;
        }
        else {
            if ((minorDiaTolRaw >= minorDiaTolLimitLower) && (minorDiaTolRaw <= minorDiaTolLimitUpper)) {
                return minorDiaTolRaw;
            }
            else {
                if (minorDiaTolRaw > minorDiaTolLimitUpper) {
                    return minorDiaTolLimitUpper;
                }
                else {
                    return minorDiaTolLimitLower;
                }
            }
        }

    }

    public double[] getValues() {
        return tableValues;
    }

    public String[] getRoundingDirection() {
        return howToRound;
    }

    public void setValues(double d, int p) {
        this.d = d;
        this.p = p;
        tableValues = calculateValues();
    }
}

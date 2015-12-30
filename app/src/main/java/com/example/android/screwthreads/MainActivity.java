package com.example.android.screwthreads;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    double sizeNominal = .2500;
    int inputPitch = 16;
    static double MAX_ROOT_INT_THD = .008;

    DecimalFormat dfCeiling = new DecimalFormat("0.0000");
    DecimalFormat dfFloor = new DecimalFormat("0.0000");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //Text Change Listener - pitch input
        EditText pitchInput = (EditText)findViewById(R.id.pitch);
        pitchInput.addTextChangedListener(redoWatcher);

        //Text Change Listener - nominal thread size
        EditText threadSizeInput = (EditText)findViewById(R.id.nominal_size);
        threadSizeInput.addTextChangedListener(nomSizeWatcher);

        //On Focus Change Listener
        //threadSizeInput.setOnFocusChangeListener(mThreadInputListener);


    }

    // Create an anonymous implementation of TextChangeListener - pitch size
    private TextWatcher redoWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //Nothing to do for this option
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            //If the Pitch input text changes get the text value and store it in a variable
            EditText pitchInput = (EditText) findViewById(R.id.pitch);
            String pitch = pitchInput.getText().toString();

            if (pitch.length() == 0) {
                pitch = "1";
            }


            //InputPitch = Double.parseDouble(pitch);
            inputPitch = Integer.parseInt(pitch);
            Log.v("MainActivity", "Name: " + inputPitch);



            updateValues();
        }

        @Override
        public void afterTextChanged(Editable s) {
            //Nothing to do for this option
        }

    };

    // Create an anonymous implementation of TextChangeListener - thread size
    private TextWatcher nomSizeWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //Nothing to do for this option
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            //If the Thread Size input text changes get the text value and store it in a variable
            EditText threadSizeNom = (EditText) findViewById(R.id.nominal_size);
            String threadSize = threadSizeNom.getText().toString();
            if (threadSize.length() == 0) {
                threadSize = "0.0000";
            }

            if (threadSize.equals(".")) {
                threadSize = "0.0000";
            }


            sizeNominal = Double.parseDouble(threadSize);
            //Log.v("MainActivity", "Name: " + sizeNominal);
            Log.v("MainActivity", "Name: " + threadSize);
            //Log.v("MainActivity", "Name: " + strLength);

            updateValues();

            //Major Diameter Min (standard)
            //TextView stdExtMajorMin = (TextView) findViewById(R.id.std_ext_major_min);
            //stdExtMajorMin.setText(pitch);
        }

        @Override
        public void afterTextChanged(Editable s) {
            //Nothing to do for this option
        }

    };


//    // Create an anonymous implementation of OnClickListener
//    private View.OnFocusChangeListener mThreadInputListener = new View.OnFocusChangeListener() {
//        public void onFocusChange(View v, boolean hasFocus) {
//            //EditText threadInput = (EditText) findViewById(R.id.nominal_size);
//            EditText threadInput = (EditText) v;
//            threadInput.setText("5555");
//            threadInput.selectAll();
//            Log.v("MainActivity", "Name: " + hasFocus);
////            String sizeThread = threadInput.getText().toString();
////
////            //Major Diameter Max (standard)
////            TextView stdExtMajorMax = (TextView) findViewById(R.id.std_ext_major_max);
////            stdExtMajorMax.setText(sizeThread);
////            //Log.v("MainActivity", "Name: " + 5.555);
//        }
//    };

    private void updateValues() {

        dfCeiling.setRoundingMode(RoundingMode.CEILING);
        dfFloor.setRoundingMode(RoundingMode.FLOOR);

        double extMajDiaStdMin = calcMajorDiaStdMin();
        double minMajDiaRed = minMajDiaReduc();
        double tolClass2 = toleranceClass2A(sizeNominal, inputPitch);
//        Log.v("MainActivity", "tolerance: " + tolClass2);
        double tolClass3 = tolClass2 * 0.75;
        double tolClass3B = tolClass2 * 0.975;
        Log.v("MainActivity", "tolerance Class 3B: " + tolClass3B);

        double extMajDiaNoIntMax = maxMajDia(minMajDiaRed, .000);
        double extMajDiaNoIntMin = minMajDia(extMajDiaNoIntMax, extMajDiaStdMin);

        double extMajDia0002Max = maxMajDia(minMajDiaRed, .002);
        double extMajDia0002Min = minMajDia(extMajDia0002Max, extMajDiaStdMin);

        double extPitchDiaMax = mExtPitchDiaMax(sizeNominal, inputPitch);
//        Log.v("MainActivity", "pitch diameter max: " + extPitchDiaMax);
//        Log.v("MainActivity", "pitch diameter max: " + String.format("%.4f", extPitchDiaMax));

        double extPitchDiaMin = extPitchDiaMax - tolClass3;
//        Log.v("MainActivity", "pitch diameter min: " + extPitchDiaMin);
//        Log.v("MainActivity", "pitch diameter min: " + String.format("%.4f", extPitchDiaMin));

        double extMinorDiaMin = extPitchDiaMin - 0.5658 / inputPitch;
        double extMinorDiaMax = extPitchDiaMax - 0.50518 / inputPitch;

        double extRootMin = 0.15011 / inputPitch;
        double extRootMax = 0.18042 / inputPitch;

        double intPitchMin = sizeNominal - 0.649519 / inputPitch;
        double intPitchMax = intPitchMin + tolClass3B;

        double intMajMin = 0.866025 / inputPitch * 11/24 + intPitchMax;

        //Minor Diameter Tolerances
//        double minorDiaTolRaw = (0.05*Math.pow(1.0/inputPitch, 2.0/3) + 0.03*(1.0/inputPitch)/sizeNominal)-0.002;
//        double minorDiaTolLimitUpper = 0.259809 / inputPitch;
//        double minorDiaTolLimitLower = 0.135315 / inputPitch;
//        double minorDiaTol12OrLower = 0.12 / inputPitch;






        double intMinorMin = sizeNominal - 0.97428 / inputPitch;
        double minDiaTolFinal = mMinDiaTolFinal(sizeNominal, inputPitch);
        double intMinorMax = intMinorMin + minDiaTolFinal;
        Log.v("MainActivity", "Minor Dia Tol pitch 12 or less: " + minDiaTolFinal);



//        MathContext roundUp4 = new MathContext(4, RoundingMode.UP);
//        BigDecimal answer1 = new BigDecimal(extMinorDiaMin, roundUp4);
//        Log.v("MainActivity", "Minor Diameter min Rounded with BigDecimal: " + answer1.round(roundUp4));




        //Log.v("MainActivity", "Nominal Size Rounded UP: " + dfCeiling.format(sizeNominal));



        //Log.v("MainActivity", "Nominal Size Rounded DOWN: " + dfFloor.format(sizeNominal));



//        double extRootMinRoundUp = Math.nextUp(extRootMin);
//        Log.v("MainActivity", "Root Radius Min: " + extRootMin);
//        Log.v("MainActivity", "Root Radius Min Rounded Up: " + extRootMinRoundUp);

        //Major Diameter Max (standard)
        TextView stdExtMajorMax = (TextView) findViewById(R.id.std_ext_major_max);
        stdExtMajorMax.setText(String.format("%.4f", sizeNominal));

        //Major Diameter Min (standard)
        TextView stdExtMajorMin = (TextView) findViewById(R.id.std_ext_major_min);
        stdExtMajorMin.setText(String.format("%.4f", extMajDiaStdMin));

        //Major Diameter Max (no interference)
        TextView extMajorMaxNI = (TextView) findViewById(R.id.no_ext_major_max);
        extMajorMaxNI.setText(String.format("%.4f", extMajDiaNoIntMax));

        //Major Diameter Min (no interference)
        TextView extMajorMinNI = (TextView) findViewById(R.id.no_ext_major_min);
        extMajorMinNI.setText(String.format("%.4f", extMajDiaNoIntMin));

        //Major Diameter Max (.0002 interference)
        TextView extMajorMax0002 = (TextView) findViewById(R.id.some_ext_major_max);
        extMajorMax0002.setText(String.format("%.4f", extMajDia0002Max));

        //Major Diameter Min (.0002 interference)
        TextView extMajorMin0002 = (TextView) findViewById(R.id.some_ext_major_min);
        extMajorMin0002.setText(String.format("%.4f", extMajDia0002Min));

        //Pitch Diameter Max
        TextView extPitchMax = (TextView) findViewById(R.id.ext_pitch_max);
        //extPitchMax.setText(String.format("%.4f", extPitchDiaMax));
        extPitchMax.setText(dfFloor.format(extPitchDiaMax));

        //Pitch Diameter Min
        TextView extPitchMin = (TextView) findViewById(R.id.ext_pitch_min);
        //extPitchMin.setText(String.format("%.4f", extPitchDiaMin));
        extPitchMin.setText(dfFloor.format(extPitchDiaMin));

        //Minor Diameter Max
        TextView extMinorMax = (TextView) findViewById(R.id.ext_minor_max);
        //extMinorMax.setText(String.format("%.4f", extMinorDiaMax));
        extMinorMax.setText(dfFloor.format(extMinorDiaMax));

        //Minor Diameter Min
        TextView extMinorMin = (TextView) findViewById(R.id.ext_minor_min);
        //extMinorMin.setText(String.format("%.4f", extMinorDiaMin));
        extMinorMin.setText(dfFloor.format(extMinorDiaMin));

        //Root Radius Max
        TextView extRootMaxV = (TextView) findViewById(R.id.ext_root_max);
        //String extRootMaxString = dfFloor.format(extRootMax);
        //double extRootMaxRoundDown = Double.parseDouble(extRootMaxString);
        //extRootMaxV.setText(String.format("%.4f", extRootMax));
        extRootMaxV.setText(dfFloor.format(extRootMax));

        //Root Radius Min
        TextView extRootMinV = (TextView) findViewById(R.id.ext_root_min);
        //extRootMinV.setText(String.format("%.4f", extRootMin));
        extRootMinV.setText(dfCeiling.format(extRootMin));

        //Major Diameter Max
        TextView intMajorMax = (TextView) findViewById(R.id.int_major_max);
        intMajorMax.setText(String.format("%.4f", sizeNominal));

        //Major Diameter Min
        TextView intMajorMin = (TextView) findViewById(R.id.int_major_min);
        intMajorMin.setText(String.format("%.4f", intMajMin));

        //Pitch Diameter Max
        TextView intPitchMaxV = (TextView) findViewById(R.id.int_pitch_max);
        intPitchMaxV.setText(String.format("%.4f", intPitchMax));

        //Pitch Diameter Min
        TextView intPitchMinV = (TextView) findViewById(R.id.int_pitch_min);
        intPitchMinV.setText(String.format("%.4f", intPitchMin));

        //Minor Diameter Max
        TextView intMinMaxV = (TextView) findViewById(R.id.int_minor_max);
        intMinMaxV.setText(String.format("%.4f", intMinorMax));

        //Pitch Diameter Min
        TextView intMinMinV = (TextView) findViewById(R.id.int_minor_min);
        intMinMinV.setText(String.format("%.4f", intMinorMin));





    }

    //Calculate Major Diameter Min (standard)
    private double calcMajorDiaStdMin() {
        return sizeNominal - 0.06*Math.pow(Math.pow(1.0/inputPitch,2), (1.0/3));
    }

    //Calculate Min Major Diameter Reduction due to Largest Female Thread Fillet
    private double minMajDiaReduc() {
        return 2*(((MAX_ROOT_INT_THD*0.866025403784439)-(1.0/inputPitch/16))/0.577350269189626);
    }

    //Class 2A (External) Pitch Diameter Tolerance Per ASME B1.1-2003 pg 55 paragraph 5.8.1
    private double toleranceClass2A(double majDia, int pitch) {
        double lengthEngagement = majDia / 2;
        double ans;
        ans = 0.0015*Math.pow(majDia, (1.0/3)) + 0.0015*Math.pow(lengthEngagement, (1.0/2)) + 0.015*Math.pow((1.0/pitch), (2.0/3));
        return ans;
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
            ans = sizeNominal;
        }
        else {
            ans = sizeNominal - minMajDiaRed + interference;
        }
        return ans;
    }

    //Calculate Pitch Diameter Max
    private double mExtPitchDiaMax(double majDia, int pitch) {
        return majDia - 0.649519 / pitch;
    }

    //Calculate Minor Diameter Tolerance
    private double mMinDiaTolFinal(double D, int p) {

        double minorDiaTolRaw = (0.05*Math.pow(1.0/p, 2.0/3) + 0.03*(1.0/p)/D)-0.002;
        double minorDiaTolLimitUpper = 0.259809 / p;
        double minorDiaTolLimitLower = 0.135315 / p;
        double minorDiaTol12OrLower = 0.12 / p;

        Log.v("MainActivity", "Minor Dia Tol Raw: " + minorDiaTolRaw);
        Log.v("MainActivity", "Minor Dia Tol Limit Upper: " + minorDiaTolLimitUpper);
        Log.v("MainActivity", "Minor Dia Tol Limit Lower: " + minorDiaTolLimitLower);
        Log.v("MainActivity", "Minor Dia Tol pitch 12 or less: " + minorDiaTol12OrLower);



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



}

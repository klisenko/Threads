package com.example.android.screwthreads;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

    public static double sizeNominal = .2500;
    public static int inputPitch = 16;
    static double MAX_ROOT_INT_THD = .008;
    private String[] tableValues = new String[22];

    public final static String VALUE = "myValue";
    public static Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            double size = savedInstanceState.getDouble("sizeNominal");
            int pitch = savedInstanceState.getInt("inputPitch");
            sizeNominal = size;
            inputPitch = pitch;

            Log.v("MainActivity", "onCreate - Size " + sizeNominal);
        }

        super.onCreate(savedInstanceState);

        Log.v("MainActivity", "onCreate after - Pitch " + inputPitch);
        Log.v("MainActivity", "onCreate after - Size " + sizeNominal);
        thread = new Thread(sizeNominal, inputPitch);
        double[] values = thread.getValues();

        Log.v("MainActivity", "onCreate - External Thread Major Diameter Standard Minimum " + values[0]);

        setContentView(R.layout.activity_main);

        //Text Change Listener - pitch input
        EditText pitchInput = (EditText)findViewById(R.id.pitch);
        pitchInput.addTextChangedListener(redoWatcher);

        //Text Change Listener - nominal thread size
        EditText threadSizeInput = (EditText)findViewById(R.id.nominal_size);
        threadSizeInput.addTextChangedListener(nomSizeWatcher);

        //On Focus Change Listener
        //threadSizeInput.setOnFocusChangeListener(mThreadInputListener);

        updateValues(thread);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.

        savedInstanceState.putDouble("sizeNominal", sizeNominal);
        savedInstanceState.putInt("inputPitch", inputPitch);



        double size = savedInstanceState.getDouble("sizeNominal");

        Log.v("MainActivity", "onSaveInstanceState - Pitch " + inputPitch);
        Log.v("MainActivity", "onSaveInstanceState - Size " + size);

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

            thread.setValues(sizeNominal, inputPitch);
            updateValues(thread);
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



            thread.setValues(sizeNominal, inputPitch);
            updateValues(thread);

            //Major Diameter Min (standard)
            //TextView stdExtMajorMin = (TextView) findViewById(R.id.std_ext_major_min);
            //stdExtMajorMin.setText(pitch);
        }

        @Override
        public void afterTextChanged(Editable s) {
            //Nothing to do for this option
        }

    };


    private void updateValues(Thread thread) {

        ConvertToString stringClass = new ConvertToString(thread);
        String[] valuesStr;
        valuesStr = stringClass.getStrings(thread);

        Log.v("MainActivity", "Major Dia.: " + valuesStr[0]);


        //Major Diameter Max (standard)
        TextView stdExtMajorMax = (TextView) findViewById(R.id.std_ext_major_max);
        stdExtMajorMax.setText(valuesStr[1]);

        //Major Diameter Min (standard)
        TextView stdExtMajorMin = (TextView) findViewById(R.id.std_ext_major_min);
        stdExtMajorMin.setText(valuesStr[0]);

        //Major Diameter Max (no interference)
        TextView extMajorMaxNI = (TextView) findViewById(R.id.no_ext_major_max);
        extMajorMaxNI.setText(valuesStr[3]);

        //Major Diameter Min (no interference)
        TextView extMajorMinNI = (TextView) findViewById(R.id.no_ext_major_min);
        extMajorMinNI.setText(valuesStr[2]);

        //Major Diameter Max (.0002 interference)
        TextView extMajorMax0002 = (TextView) findViewById(R.id.some_ext_major_max);
        extMajorMax0002.setText(valuesStr[5]);

        //Major Diameter Min (.0002 interference)
        TextView extMajorMin0002 = (TextView) findViewById(R.id.some_ext_major_min);
        extMajorMin0002.setText(valuesStr[4]);

        //Pitch Diameter Max
        TextView extPitchMax = (TextView) findViewById(R.id.ext_pitch_max);
        extPitchMax.setText(valuesStr[7]);

        //Pitch Diameter Min
        TextView extPitchMin = (TextView) findViewById(R.id.ext_pitch_min);
        extPitchMin.setText(valuesStr[6]);

        //Minor Diameter Max
        TextView extMinorMax = (TextView) findViewById(R.id.ext_minor_max);
        extMinorMax.setText(valuesStr[9]);

        //Minor Diameter Min
        TextView extMinorMin = (TextView) findViewById(R.id.ext_minor_min);
        extMinorMin.setText(valuesStr[8]);

        //Root Radius Max
        TextView extRootMaxV = (TextView) findViewById(R.id.ext_root_max);
        extRootMaxV.setText(valuesStr[11]);

        //Root Radius Min
        TextView extRootMinV = (TextView) findViewById(R.id.ext_root_min);
        extRootMinV.setText(valuesStr[10]);

        //Major Diameter Max
        TextView intMajorMax = (TextView) findViewById(R.id.int_major_max);
        intMajorMax.setText(valuesStr[13]);

        //Major Diameter Min
        TextView intMajorMin = (TextView) findViewById(R.id.int_major_min);
        intMajorMin.setText(valuesStr[12]);

        //Pitch Diameter Max
        TextView intPitchMaxV = (TextView) findViewById(R.id.int_pitch_max);
        intPitchMaxV.setText(valuesStr[15]);

        //Pitch Diameter Min
        TextView intPitchMinV = (TextView) findViewById(R.id.int_pitch_min);
        intPitchMinV.setText(valuesStr[14]);

        //Minor Diameter Max
        TextView intMinMaxV = (TextView) findViewById(R.id.int_minor_max);
        intMinMaxV.setText(valuesStr[17]);

        //Pitch Diameter Min
        TextView intMinMinV = (TextView) findViewById(R.id.int_minor_min);
        intMinMinV.setText(valuesStr[16]);
    }

    public void thdNote(View view){
        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra(VALUE, "My custom string value");
        startActivity(intent);
    }



}

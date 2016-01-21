package com.example.android.screwthreads;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Created by klisenko on 12/30/15.
 */

//AppCompatActivity
public class SecondActivity extends AppCompatActivity {

    private static String VALUE = "myValue";
    private Intent intent = getIntent();
    private String threadSubject;
    private String emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        DecimalFormat dfNormal = new DecimalFormat("0.0000");
        dfNormal.setRoundingMode(RoundingMode.HALF_UP);

        String value = getIntent().getExtras().getString(VALUE);

        double[] threadData = MainActivity.thread.getValues();
        ConvertToString stringClass = new ConvertToString(MainActivity.thread);
        String[] valuesStr = stringClass.getStrings(MainActivity.thread);

        String majorDiameterString = dfNormal.format(MainActivity.sizeNominal);

        threadSubject = majorDiameterString + " - " + MainActivity.inputPitch + " UNJS THREAD PER SAE AS8879";



        //External Thread call out
        TextView externalThreadNote = (TextView) findViewById(R.id.thread_note_external);
        String externalThreadHeading = majorDiameterString + " - " + MainActivity.inputPitch + " UNJS-3A THREAD PER SAE AS8879";
        externalThreadNote.setText(externalThreadHeading);

        //Internal Thread call out
        TextView internalThreadNote = (TextView) findViewById(R.id.thread_note_internal);
        String internalThreadHeading = majorDiameterString + " - " + MainActivity.inputPitch + " UNJS-3B THREAD PER SAE AS8879";
        internalThreadNote.setText(internalThreadHeading);


        //Major Diameter Min (standard)
        TextView stdExtMajorMin = (TextView) findViewById(R.id.ext_maj_dia_min);
        stdExtMajorMin.setText(valuesStr[0] + " to ");

        //Major Diameter Max (standard)
        TextView stdExtMajorMax = (TextView) findViewById(R.id.ext_major_dia_max);
        stdExtMajorMax.setText(valuesStr[1]);

        //Pitch Diameter Min
        TextView extPitchMin = (TextView) findViewById(R.id.ext_pitch_dia_min);
        extPitchMin.setText(valuesStr[6] + " to ");

        //Pitch Diameter Max
        TextView extPitchMax = (TextView) findViewById(R.id.ext_pitch_dia_max);
        extPitchMax.setText(valuesStr[7]);


        //Minor Diameter Min
        TextView extMinorMin = (TextView) findViewById(R.id.ext_minor_dia_min);
        extMinorMin.setText(valuesStr[8] + " to ");

        //Minor Diameter Max
        TextView extMinorMax = (TextView) findViewById(R.id.ext_minor_dia_max);
        extMinorMax.setText(valuesStr[9]);

        //Root Radius Min
        TextView extRootMinV = (TextView) findViewById(R.id.ext_rootradius_min);
        extRootMinV.setText(valuesStr[10] + " to ");

        //Root Radius Max
        TextView extRootMaxV = (TextView) findViewById(R.id.ext_rootradius_max);
        extRootMaxV.setText(valuesStr[11]);


        //Major Diameter Min
        TextView intMajorMin = (TextView) findViewById(R.id.int_maj_dia_min);
        intMajorMin.setText(valuesStr[12] + " to ");

        //Major Diameter Max
        TextView intMajorMax = (TextView) findViewById(R.id.int_maj_dia_max);
        intMajorMax.setText(valuesStr[13]);

        //Pitch Diameter Min
        TextView intPitchMinV = (TextView) findViewById(R.id.int_pitch_dia_min);
        intPitchMinV.setText(valuesStr[14] + " to ");

        //Pitch Diameter Max
        TextView intPitchMaxV = (TextView) findViewById(R.id.int_pitch_dia_max);
        intPitchMaxV.setText(valuesStr[15]);

        //Pitch Diameter Min
        TextView intMinMinV = (TextView) findViewById(R.id.int_min_dia_min);
        intMinMinV.setText(valuesStr[16] + " to ");

        //Minor Diameter Max
        TextView intMinMaxV = (TextView) findViewById(R.id.int_min_dia_max);
        intMinMaxV.setText(valuesStr[17]);

        StringBuilder builder = new StringBuilder();

        //int arrayLength = valuesStr.length;

        String[] threadNames = {"MAJOR DIAMETER", "PITCH DIAMETER", "MINOR DIAMETER", "ROOT RADIUS", "MAJOR DIAMETER", "PITCH DIAMETER", "MINOR DIAMETER"};

        builder.append("EXTERNAL THREAD\n");
        builder.append(externalThreadHeading + "\n");
        builder.append(threadNames[0] + ": " + valuesStr[0] + " TO " + valuesStr[1] + "\n");
        builder.append(threadNames[1] + ": " + valuesStr[6] + " TO " + valuesStr[7] + "\n");
        builder.append(threadNames[2] + ": " + valuesStr[8] + " TO " + valuesStr[9] + "\n");
        builder.append(threadNames[3] + ": " + valuesStr[10] + " TO " + valuesStr[11] + "\n");
        builder.append("\nINTERNAL THREAD\n");
        builder.append(internalThreadHeading + "\n");
        builder.append(threadNames[4] + ": " + valuesStr[12] + " TO " + valuesStr[13] + "\n");
        builder.append(threadNames[5] + ": " + valuesStr[14] + " TO " + valuesStr[15] + "\n");
        builder.append(threadNames[6] + ": " + valuesStr[16] + " TO " + valuesStr[17] + "\n");




        emailText = builder.toString();
        Log.v("MainActivity", "Array of Strings " + emailText);




        //Toast.makeText(this, value, Toast.LENGTH_LONG).show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_second, menu);
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

    public void sendEmail(View view){
        String[] emailAddresses = new String[1];

        EditText emailAddressTextBox = (EditText) findViewById(R.id.email_address);
        emailAddresses[0] = emailAddressTextBox.getText().toString();


//        Intent intent = new Intent(Intent.ACTION_SENDTO);
//        intent.setType("text/html");
//        intent.putExtra(Intent.EXTRA_EMAIL, emailAddresses);
//        intent.putExtra(Intent.EXTRA_SUBJECT, threadSubject);
//        intent.putExtra(Intent.EXTRA_TEXT, emailText);

        //startActivity(Intent.createChooser(intent, "Send Email"));

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, emailAddresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, threadSubject);
        intent.putExtra(Intent.EXTRA_TEXT, emailText);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}

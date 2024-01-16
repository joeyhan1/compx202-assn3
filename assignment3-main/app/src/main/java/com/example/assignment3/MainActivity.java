package com.example.assignment3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

/**
 * MainActivity Class
 *
 * Display weather information
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Create a new textview object to point to a specific view component in XML file.
        TextView textView = (TextView) findViewById(R.id.WeatherDetail);
        //Get variables from MapsActivity.
        Bundle bundle = getIntent().getExtras();
        //Set information to the specified textview.
        textView.setText("Weather: "+ bundle.getString("A")+"\n"+"Description: "+bundle.getString("B")+"\n"+"Temperature: "+bundle.getString("C")+"\n"+"Min Temperature: "+bundle.getString("D")+"\n"+"Max temperature: "+bundle.getString("E")+"\n"+"Feels Like: "+bundle.getString("F")+"\n"+"Pressure: "+bundle.getString("G")+"\n"+"Humidity: "+bundle.getString("H")+"\n"+"Wind Speed: "+bundle.getString("J"));
    }
}
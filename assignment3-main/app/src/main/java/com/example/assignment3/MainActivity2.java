package com.example.assignment3;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;

/**
 * MainActivity2 Class
 *
 * Display webcam information
 */
public class MainActivity2 extends AppCompatActivity {
    //Create variables
    InputStream inputStream;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //Create a new textview object to point to a specific view component in XML file.
        TextView textView = (TextView) findViewById(R.id.WebcamDetail);
        //Create a new image object to point to a specific view component in XML file.
        ImageView imageView =(ImageView) findViewById(R.id.image);
        //Get variables from MapsActivity.
        Bundle bundle = getIntent().getExtras();
        try {
            //Get variables from bundle object.
            String url = bundle.getString("url");
            //Set information to the specified textview.
            textView.setText(bundle.getString("location")+"\n"+bundle.getString("city")+">"+bundle.getString("region"));
            //Use Picasso to get image for a webcam from windy.
            Picasso.get().load(url).into(imageView);
        }
        catch(Exception E)
        {
            E.printStackTrace();
        }

    }

}
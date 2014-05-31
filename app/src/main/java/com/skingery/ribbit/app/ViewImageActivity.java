package com.skingery.ribbit.app;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;


public class ViewImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        // set Uri at the Uri sent from the intent
        Uri imageUri = getIntent().getData();
        // load the image with picasso
        Picasso.with(this).load(imageUri.toString()).into(imageView);

        // create a timer and send user back to inbox after a certain amount of time has passed
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                finish(); // finish current activity to go back to inbox
            }
        }, 10*1000); // set the timer for 10 seconds (10 *1000 = 10,000 milliseconds)



    }






}

package com.skingery.ribbit.app;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.URI;


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

    }






}

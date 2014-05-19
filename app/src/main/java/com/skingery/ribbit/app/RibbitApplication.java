package com.skingery.ribbit.app;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Sean on 5/19/2014.
 */
public class RibbitApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Parse sdk from parse.com
        Parse.initialize(this, "BXsPxiD3ozGQ26fx8izQQwGEIV4GuKDSBiJw7vlx", "sqc4PhXyJeYzCTm4HZOO84naZQhOMTAQxFfbtQHl");


    }
}

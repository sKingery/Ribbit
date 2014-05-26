package com.skingery.ribbit.app;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.parse.ParseUser;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int TAKE_VIDEO_REQUEST = 1;
    public static final int PICK_PHOTO_REQUEST = 2;
    public static final int PICK_VIDEO_REQUEST = 3;

    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;

    public static final int FILE_SIZE_LIMIT = 1024 * 1024 * 10; // 10 MB

    protected Uri mMediaUri;



    protected DialogInterface.OnClickListener mDialogListener =
            new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            switch (which){
                case 0: //Take a picture
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mMediaUri = getOutPutMediaFileUri(MEDIA_TYPE_IMAGE);
                    if(mMediaUri == null){
                        //display error
                        Toast.makeText(MainActivity.this,R.string.error_external_storage, Toast.LENGTH_LONG).show();
                    }
                    else {
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);// start activity for result so the activity exits and returns result back
                    }
                    break;
                case 1: //Take video
                    Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    mMediaUri = getOutPutMediaFileUri(MEDIA_TYPE_VIDEO);
                    if(mMediaUri == null){
                        //display error
                        Toast.makeText(MainActivity.this,R.string.error_external_storage, Toast.LENGTH_LONG).show();
                    }
                    else{
                        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10); // set video duration limit to 10 seconds
                        videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); // set video quality to lowest resolution
                        startActivityForResult(videoIntent, TAKE_VIDEO_REQUEST);
                    }

                    break;
                case 2: //Choose picture
                    Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    choosePhotoIntent.setType("image/*"); // set type so only images will be given as an option
                    startActivityForResult(choosePhotoIntent,PICK_PHOTO_REQUEST);

                    break;
                case 3: //Choose video
                    Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    chooseVideoIntent.setType("video/*"); // set type so only videos will be given as an option
                    Toast.makeText(MainActivity.this, getString(R.string.video_file_size_warning), Toast.LENGTH_LONG).show();
                    startActivityForResult(chooseVideoIntent,PICK_VIDEO_REQUEST);

                    break;

            }

        }

                private Uri getOutPutMediaFileUri(int mediaType) {
                    // To be safe, you should check that the external storage is mounted
                    // using Environment.getExternalStorageState() before doing this.
                    if(isExternalStorageAvailable()){
                        // get the Uri

                        // 1. Get the external storage directory
                        String appName = MainActivity.this.getString(R.string.app_name);
                        File mediaStorageDir = new File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName);

                        // 2. Create our own subdirectory
                        if(! mediaStorageDir.exists()){ // if media storage directory does not exist
                            // make the directory
                            if(! mediaStorageDir.mkdirs()){ // if it doesnt make the directory
                                Log.e(TAG, "Failed to create directory");
                                return null;
                            }
                        }
                        // 3. Create a file name
                        // 4. Create the file
                        File mediaFile;
                        Date now = new Date();
                        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);

                        String path = mediaStorageDir.getPath() + File.separator;
                        if(mediaType == MEDIA_TYPE_IMAGE){
                            mediaFile = new File(path +"IMG_" + timestamp + ".jpg");
                        }

                        else if(mediaType == MEDIA_TYPE_VIDEO){
                            mediaFile = new File(path + "VID_" + timestamp + ".mp4");
                        }

                        else{
                            return null;

                        }

                        Log.d(TAG, "File: " + Uri.fromFile(mediaFile));


                        // 5. Return the files Uri

                        return Uri.fromFile(mediaFile);
                    }

                    else{
                        return null;
                    }

                }

                private boolean isExternalStorageAvailable(){
                    // get the external storage state and store it as a String
                    String state = Environment.getExternalStorageState();

                    if(state.equals(Environment.MEDIA_MOUNTED)){ // if the media storage is mounted
                        return true;
                    }

                    else {
                        // storage is not mounted
                        return false;
                    }
                }
            };

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); // calls the progress indicator
        setContentView(R.layout.activity_main);

        // parse analytics call
        ParseAnalytics.trackAppOpened(getIntent());

        // to start login activity on creation
        ParseUser currentUser = ParseUser.getCurrentUser(); // create a current user object

        // if the current user is not in the system
        if(currentUser == null) {
            navigateToLogin();
        }


        else{
            Log.i(TAG, currentUser.getUsername()); // Log the username
        }





        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this,getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check to see if result came back normally
        if(resultCode == RESULT_OK){

            // if the result is from the choose photo or video options
            if(requestCode == PICK_PHOTO_REQUEST
                    || requestCode == PICK_VIDEO_REQUEST){

                // of there is no data
                if(data == null){
                    // show error message
                    Toast.makeText(this, getString(R.string.general_error), Toast.LENGTH_LONG).show();
                }
                else{
                    // The Intent has data set the Uri
                    mMediaUri = data.getData();
                }
                Log.i(TAG, "Media Uri" + mMediaUri);
                if(requestCode == PICK_VIDEO_REQUEST){ // if the file type is video
                    // make sure the file is less than 10MB
                    int fileSize = 0;
                    InputStream inputStream = null;
                    try {
                        // open an input stream to the file
                        inputStream = getContentResolver().openInputStream(mMediaUri);
                        fileSize = inputStream.available();
                    }
                    catch (FileNotFoundException e){
                        Toast.makeText(this, getString(R.string.error_opening_file), Toast.LENGTH_LONG).show();
                        return; // return to go back to the activity
                    }
                    catch (IOException e){
                        Toast.makeText(this, getString(R.string.error_opening_file), Toast.LENGTH_LONG).show();
                        return; // return to go back to the activity
                    }
                    // close  stream
                    finally {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                          // Intentionally blank
                        }
                    }
                    if (fileSize >= FILE_SIZE_LIMIT){
                        Toast.makeText(this, getString(R.string.error_file_size_too_large), Toast.LENGTH_LONG).show();
                        return; // return to go back to the activity
                    }

                }

            }
            else {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri); // specify path to the file
                sendBroadcast(mediaScanIntent);
            }

            // start the RecipientsActivity
            Intent recipientsIntent = new Intent(this, RecipientsActivity.class);
            recipientsIntent.setData(mMediaUri);
            startActivity(recipientsIntent);


        }
        else if (resultCode != RESULT_CANCELED){
            Toast.makeText(this, getString(R.string.general_error), Toast.LENGTH_LONG).show();
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // flag to start the new task of logging in
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // flag to clear the old task of starting the app
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId(); // get the item id
        // switch according to item id
        switch(itemId) {
            // the user selects logout
            case R.id.action_logout:
                ParseUser.logOut(); // logout the user
                navigateToLogin(); // switch to the login screen
                break;
                //user selects edit friends
            case R.id.action_edit_friends:
                Intent intent = new Intent(this, EditFriendsActivity.class);
                startActivity(intent);
                break;
                // user selects camera
            case R.id.action_camera:
                // create alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(R.array.camera_choices, mDialogListener); // set items from string array
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


}

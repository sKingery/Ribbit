package com.skingery.ribbit.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;


public class TextMessageActivty extends Activity {

    public static final String EXTRA_MESSAGE = "";


    protected EditText mEditText;
    protected Button mSendButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_message_activty);

        // initialize Edit Text and Button
        mEditText = (EditText) findViewById(R.id.sendMessageEditText);
        mSendButton = (Button) findViewById(R.id.sendButton);
    }



}

package com.skingery.ribbit.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class LoginActivity extends Activity {
    // member variables
    protected TextView mSignUpTextView; // sign up text view
    protected EditText mUsername;
    protected EditText mPassword;
    protected Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_login);

        mSignUpTextView =(TextView) findViewById(R.id.signUpText);

        // set on click for the sign up text view
        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // on click we create and intent to open SignUpActivity
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);

            }
        });

        // Initialize Edit Texts and Button.
        mUsername = (EditText) findViewById(R.id.usernameField);
        mPassword =  (EditText) findViewById(R.id.passwordField);
        mLoginButton = (Button) findViewById(R.id.loginButton);

        // set onClickListiner for mSignUpButton
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get text from edit text boxes, convert to string and assign to String variables
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();


                //call trim on each variable to trim white space.
                username = username.trim();
                password = password.trim();


                // check to see if fields are empty
                if(username.isEmpty()|| password.isEmpty()){

                    // create error dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle(R.string.login_error_title);
                    builder.setMessage(R.string.login_error_message);
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                else{
                // Login
                    setProgressBarIndeterminateVisibility(true); // show progress bar
                    ParseUser.logInInBackground(username,password,new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            setProgressBarIndeterminateVisibility(false); // hide progress bar


                            if(e == null){ // if there is no exception
                                //Success now send the user to the inbox(MainActivity)
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                // start a new task and clear the old one
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }

                            // Something went wrong
                            else {
                                // create error dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setTitle(R.string.login_error_title);
                                builder.setMessage(e.getMessage());
                                builder.setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();

                            }

                        }
                    });

                }
            }
        });
    }


}

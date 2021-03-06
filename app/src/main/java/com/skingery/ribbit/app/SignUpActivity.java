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

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.apache.http.entity.StringEntity;


public class SignUpActivity extends Activity {

    // member variables
    protected EditText mUsername;
    protected EditText mPassword;
    protected EditText mEmail;
    protected Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_sign_up);

        // Initialize Edit Texts and Button.
        mUsername = (EditText) findViewById(R.id.usernameField);
        mPassword =  (EditText) findViewById(R.id.passwordField);
        mEmail = (EditText) findViewById(R.id.emailField);
        mSignUpButton = (Button) findViewById(R.id.signUpButton);

        // set onClickListiner for mSignUpButton
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

        // get text from edit text boxes, convert to string and assign to String variables
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                String email = mEmail.getText().toString();

                //call trim on each variable to trim white space.
                username = username.trim();
                password = password.trim();
                email = email.trim();

                // check to see if fields are empty
                if(username.isEmpty()|| password.isEmpty() || email.isEmpty()){

                    // create error dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setTitle(R.string.signup_error_title);
                    builder.setMessage(R.string.signup_error_message);
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                else{
                    setProgressBarIndeterminateVisibility(true); // show progress bar
                    //Create a parse user object
                    ParseUser newUser = new ParseUser();

                    // set username, password, and email equal to the fields entered
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            setProgressBarIndeterminateVisibility(false); // hide progress bar
                            if(e == null){ // and there was no exception

                                //Success now send the user to the inbox(MainActivity)
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                // start a new task and clear the old one
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            }

                            else{
                                // create error dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                builder.setTitle(R.string.signup_error_title);
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

package com.skingery.ribbit.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;


public class RecipientsActivity extends ListActivity {

    public static final String TAG = RecipientsActivity.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;

    protected MenuItem mSendMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_recipients);
        // set the list choice mode so user can check or uncheck friends
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    public void onResume() {
        super.onResume();

        // set mCurrentUser as the current ParseUser
        mCurrentUser = ParseUser.getCurrentUser();
        // set mFriendsRelation as the current users relation
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

       setProgressBarIndeterminateVisibility(true); // start the progress indicator use getActivity() since its a fragment

        //set the query to a variable called query
        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        // sort the names in ascending order
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        //get the list of the users current friends
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                setProgressBarIndeterminateVisibility(false); // stop the progress indicator use getActivity() since its a fragment

                if(e == null) { // there is no exception

                    // set mFriends to the list of friends returned
                    mFriends = friends;
                    // create a String array to store the user info in to display as a list
                    String[] usernames = new String[mFriends.size()];
                    // create a loop to loop through each ParseUser and get the username
                    int i = 0;
                    for (ParseUser user : mFriends) { // for each ParseUser in the list of ParseUsers
                        usernames[i] = user.getUsername(); // set string array based on i to the username of current user
                        i++; // increment i
                    }
                    // create array adapter and set it as the adapter for this activity
                    // Call getListView() and getContext() to get the context since fragment doesn't extend activity or context
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(),
                            android.R.layout.simple_list_item_checked, usernames);
                    setListAdapter(adapter);
                }

                else{
                    Log.e(TAG, e.getMessage());
                    // show an alert dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setTitle(R.string.error_title);
                    builder.setMessage(e.getMessage());
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();


                }


            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.recipients, menu);
        // get the send button menu item
        mSendMenuItem = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_send) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // if atleast one of the items is checked
        if(l.getCheckedItemCount() > 0) {
            // set menu item to visable when a list item is clicked
            mSendMenuItem.setVisible(true);
        }
        else{
            // set menu item to hide
            mSendMenuItem.setVisible(false);
        }
    }
}

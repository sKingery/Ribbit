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
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class EditFriendsActivity extends ListActivity {
    public static final String TAG = EditFriendsActivity.class.getSimpleName();

    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); // request the progress indicator in the action bar
        setContentView(R.layout.activity_edit_friends);

        // ge the list view and set it so user can check or uncheck friends
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setProgressBarIndeterminateVisibility(true); // show progress indicator

        // set mCurrentUser as the current ParseUser
        mCurrentUser = ParseUser.getCurrentUser();
        // set mFriendsRelation as the current users relation
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        // create a parse query to ge the users
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        // sort in ascending order
        query.orderByAscending(ParseConstants.KEY_USERNAME); // make sure to use key from ParseConstant class
        // set the limit of users in the query to 1000
        query.setLimit(1000);
        // find in background and add new FindCallBack
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                setProgressBarIndeterminateVisibility(false); // hide progress indicator
              // if there is no exception
               if(e == null){
                   // success! Store the info from users in mUsers
                   mUsers = users;

                   // create a String array to store the user info in to display as a list
                   String[] usernames = new String[mUsers.size()];
                   // create a loop to loop through each ParseUser and ge the username
                   int i = 0;
                   for(ParseUser user: mUsers){ // for each ParseUser in the list of ParseUsers
                       usernames[i] = user.getUsername(); // set string array based on i to the username of current user
                       i++; // increment i
                   }
                    // create array adapter and set it as the adapter for this activity
                   ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditFriendsActivity.this,
                           android.R.layout.simple_list_item_checked, usernames);
                   setListAdapter(adapter);

                   addFriendCheckmarks();

               }
                else{
                   // there was an error
                   Log.e(TAG, e.getMessage());
                   // show an alert dialog
                   AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
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
        getMenuInflater().inflate(R.menu.edit_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        //check to see if the item is really checked or not
        if(getListView().isItemChecked(position)){
            //add friend
            mFriendsRelation.add(mUsers.get(position)); // add the info from the users by the position of the username in the list
            // Save the changes to the back end
            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    //if there is an exception
                    if(e != null){
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
        }

        else{
            // remove friend
        }

    }

    private void addFriendCheckmarks(){
        // get the list of the users current friends
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if(e == null){
                    //list returned -- look for match
                    for(int i = 0; i < mUsers.size(); i++){ // loop through all users
                        ParseUser user = mUsers.get(i);

                        for(ParseUser friend: friends){
                            if(friend.getObjectId().equals(user.getObjectId())){
                                // set check mark
                                getListView().setItemChecked(i, true);
                            }
                        }
                    }

                }

                    else{
                        Log.e(TAG, e.getMessage());
                }
            }
        });


    }
}

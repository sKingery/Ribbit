package com.skingery.ribbit.app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Sean on 5/22/14.
 */
public class FriendsFragment extends ListFragment {

    public static final String TAG = FriendsFragment.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // set mCurrentUser as the current ParseUser
        mCurrentUser = ParseUser.getCurrentUser();
        // set mFriendsRelation as the current users relation
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        getActivity().setProgressBarIndeterminateVisibility(true); // start the progress indicator use getActivity() since its a fragment

        //set the query to a variable called query
        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        // sort the names in ascending order
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        //get the list of the users current friends
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                getActivity().setProgressBarIndeterminateVisibility(false); // stop the progress indicator use getActivity() since its a fragment

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
                            android.R.layout.simple_list_item_1, usernames);
                    setListAdapter(adapter);
                }

                else{
                    Log.e(TAG, e.getMessage());
                    // show an alert dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
                    builder.setTitle(R.string.error_title);
                    builder.setMessage(e.getMessage());
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();


                }


            }
        });
    }
}

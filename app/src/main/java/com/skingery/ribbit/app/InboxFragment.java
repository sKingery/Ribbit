package com.skingery.ribbit.app;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Sean on 5/22/14.
 */
public class InboxFragment extends ListFragment {

    protected List<ParseObject> mMessages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // the following code with refresh the list of messages sent to the user in the background

        // set progress bar to show
        getActivity().setProgressBarIndeterminate(true);

        // create a new ParseQuery
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
        // check the messages sent to current user
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        // order with the most recent at the top
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        //find the message in background
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                // dismiss progress indicator
                getActivity().setProgressBarIndeterminate(false);

                if(e == null){
                    // we found messages!
                    // set mMessage with the messages variable
                    mMessages = messages;
                    // create a String array to store the user info in to display as a list
                    String[] usernames = new String[mMessages.size()];
                    // create a loop to loop through each ParseUser and get the username
                    int i = 0;
                    for (ParseObject message : mMessages) { // for each ParseUser in the list of ParseUsers
                        usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME); // set string array based on i to the username of current user
                        i++; // increment i
                    }
                    // create array adapter and set it as the adapter for this activity
                    // Call getListView() and getContext() to get the context since fragment doesn't extend activity or context
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(),
                            android.R.layout.simple_list_item_1, usernames);
                    setListAdapter(adapter);

                }

            }
        });
    }
}

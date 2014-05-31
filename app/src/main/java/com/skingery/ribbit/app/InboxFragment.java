package com.skingery.ribbit.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
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

                if (e == null) {
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
                    MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
                    setListAdapter(adapter);

                }


            }
        });
    }

    // to launch the ViewImageActivity when it is clicked
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // get the message type
        ParseObject message = mMessages.get(position);
       String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
       // get the ParseFile or the message
        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
        // create a Uri and set it to the files URL
        Uri fileUri = Uri.parse(file.getUrl());

        // check if message type is image
        if(messageType.equals(ParseConstants.TYPE_IMAGE)){
            // view the image in the ViewImageActivity
            Intent intent = new Intent(getActivity(),ViewImageActivity.class);
            intent.setData(fileUri); // set the date of the intent with the file uri
            startActivity(intent);


        }

        else{
            // view the video
            Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
            intent.setDataAndType(fileUri, "video/*");
            startActivity(intent);



        }

    }
}

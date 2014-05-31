package com.skingery.ribbit.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by Sean on 5/30/2014.
 */
public class MessageAdapter extends ArrayAdapter<ParseObject> {

    protected Context mContext;
    protected List<ParseObject> mMessages;

    public MessageAdapter(Context context, List<ParseObject> messages){
        super(context, R.layout.message_item, messages);

        mContext = context;
        mMessages = messages;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //create a view holder variable
        ViewHolder holder;

        if(convertView == null) { // if holder does not exist yet
            // inflate convertView
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);

            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.messageIcon);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.senderLabel);
        }
        else{
            // the holder exists so use it
            holder = (ViewHolder)convertView.getTag();
        }

        // set the data in the view
        ParseObject message = mMessages.get(position);

        // if the message is an Image use the image icon
        if(message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE )) {
            holder.iconImageView.setImageResource(R.drawable.ic_action_picture);
        }
        else{
            // set video icon
            holder.iconImageView.setImageResource(R.drawable.ic_action_play_over_video);
        }
        holder.nameLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));

        return convertView;
    }

    // create the ViewHolder class
    private static class ViewHolder{
        ImageView iconImageView;
        TextView nameLabel;

    }
}

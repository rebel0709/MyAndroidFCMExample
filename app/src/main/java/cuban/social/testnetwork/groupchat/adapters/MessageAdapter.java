package cuban.social.testnetwork.groupchat.adapters;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.pkmmte.view.CircularImageView;

import java.util.ArrayList;

import cuban.social.testnetwork.R;
import cuban.social.testnetwork.app.App;
import cuban.social.testnetwork.groupchat.models.GroupChatMessage;
import cuban.social.testnetwork.view.ResizableImageView;
import github.ankushsachdeva.emojicon.EmojiconTextView;

/**
 * Created by Freelancer on 8/7/2017.
 */

public class MessageAdapter extends BaseAdapter {
    private Activity activity;
    ArrayList<GroupChatMessage> messages=new ArrayList<GroupChatMessage>();
    public MessageAdapter(Activity activity, int modelLayout, DatabaseReference ref) {
        this.activity = activity;
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                GroupChatMessage one=dataSnapshot.getValue(GroupChatMessage.class);
                one.setMessageID(dataSnapshot.getKey());
                messages.add(one);
                MessageAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                GroupChatMessage one=dataSnapshot.getValue(GroupChatMessage.class);
                one.setMessageID(dataSnapshot.getKey());
                messages.set(messages.size()-1,one);
                MessageAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void populateView(View v, GroupChatMessage model, int position) {
        EmojiconTextView messageText = (EmojiconTextView) v.findViewById(R.id.message_text);
        TextView messageUser = (TextView) v.findViewById(R.id.message_user);
        TextView messageTime = (TextView) v.findViewById(R.id.message_time);
        CircularImageView avatarView=(CircularImageView)v.findViewById(R.id.user_avatar);
        ResizableImageView resizeImageChat = (ResizableImageView)v.findViewById(R.id.message_img);

        String newString = model.getMessageText();
        if(newString.length()>0){
            messageText.setText(newString.replaceAll("<br>", "\n"));
            messageText.setVisibility(View.VISIBLE);
            messageText.setMovementMethod(LinkMovementMethod.getInstance());
        }else{
            messageText.setVisibility(View.GONE);
        }

        messageUser.setText(model.getMessageUser());
        if(model.getAvararURL().length()>0){
            ImageLoader imgLoader=App.getInstance().getImageLoader();
            imgLoader.get(model.getAvararURL(), ImageLoader.getImageListener(avatarView, R.drawable.profile_default_photo, R.drawable.profile_default_photo));
        }
        if(model.getMessageImgURL().length()>0){
            ImageLoader imgLoader=App.getInstance().getImageLoader();
            imgLoader.get(model.getMessageImgURL(), ImageLoader.getImageListener(resizeImageChat, R.drawable.profile_default_photo, R.drawable.profile_default_photo));
        }else{
            resizeImageChat.setVisibility(View.GONE);
        }
        // Format the date before showing it
        messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));


    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public GroupChatMessage getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        GroupChatMessage chatMessage = getItem(position);
        if (chatMessage.getMessageUserId()== App.getInstance().getId())
            view = activity.getLayoutInflater().inflate(R.layout.item_out_message, viewGroup, false);
        else
            view = activity.getLayoutInflater().inflate(R.layout.item_in_message, viewGroup, false);

        //generating view
        populateView(view, chatMessage, position);

        return view;
    }

    @Override
    public int getViewTypeCount() {
        // return the total number of view types. this value should never change
        // at runtime
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // return a value between 0 and (getViewTypeCount - 1)
        return position % 2;
    }
}

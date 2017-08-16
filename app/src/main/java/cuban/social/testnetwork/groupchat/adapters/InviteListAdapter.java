package cuban.social.testnetwork.groupchat.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.google.firebase.database.FirebaseDatabase;
import com.pkmmte.view.CircularImageView;

import java.util.List;

import cuban.social.testnetwork.R;
import cuban.social.testnetwork.adapter.PeopleListAdapter;
import cuban.social.testnetwork.app.App;
import cuban.social.testnetwork.constants.Constants;
import cuban.social.testnetwork.model.Profile;

/**
 * Created by Freelancer on 8/13/2017.
 */

public class InviteListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Profile> searchResults;

    ImageLoader imageLoader = App.getInstance().getImageLoader();
    private String groupID;
    public InviteListAdapter(Activity activity, List<Profile> searchResults, String groupID) {

        this.activity = activity;
        this.searchResults = searchResults;
        this.groupID=groupID;
    }

    @Override
    public int getCount() {

        return searchResults.size();
    }

    @Override
    public Object getItem(int location) {

        return searchResults.get(location);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    static class ViewHolder {

        public TextView profileUsername,mProfileActive;
        public TextView profileFullname;
        public Button profileFollowBtn;
        public CircularImageView profilePhoto;
        public Boolean isMyRow = false;
        public ImageView imageOnline;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        InviteListAdapter.ViewHolder viewHolder = null;

        if (inflater == null) {

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.user_list_row, null);

            viewHolder = new InviteListAdapter.ViewHolder();
            viewHolder.imageOnline=(ImageView)convertView.findViewById(R.id.imageOnline);
            viewHolder.mProfileActive=(TextView)convertView.findViewById(R.id.profileActive);
            viewHolder.profilePhoto = (CircularImageView) convertView.findViewById(R.id.personPhoto);
            viewHolder.profileFollowBtn = (Button) convertView.findViewById(R.id.personFollowBtn);
            viewHolder.profileFollowBtn.setText("Invite");
            viewHolder.profileFullname = (TextView) convertView.findViewById(R.id.personFullName);
            viewHolder.profileUsername = (TextView) convertView.findViewById(R.id.personUsername);


            viewHolder.profileFollowBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    int getPosition = (Integer) view.getTag();
                    InviteListAdapter.ViewHolder viewHolder = (InviteListAdapter.ViewHolder) view.getTag(R.id.personFollowBtn);
                    // TODO Auto-generated method stub

                    //searchResults.get(getPosition).addFollower();
                    //searchResults.get(getPosition).setFollow(true);
                    FirebaseDatabase.getInstance().getReference("user_group").child(String.valueOf(searchResults.get(getPosition).getId())).child(groupID).setValue("gid");
                    viewHolder.profileFollowBtn.setVisibility(View.GONE);
//                    viewHolder.profileFullname.setText("The Text You Need In There");

//					api.follow(searchResults.get(getPosition).getId());
                }
            });

            viewHolder.profileFollowBtn.setTag(position);
            convertView.setTag(viewHolder);

        } else {

            viewHolder = (InviteListAdapter.ViewHolder) convertView.getTag();
        }
        viewHolder.imageOnline.setTag(position);
        viewHolder.mProfileActive.setTag(position);
        viewHolder.profileFollowBtn.setTag(position);
        viewHolder.profilePhoto.setTag(position);
        viewHolder.profileFollowBtn.setTag(R.id.personFollowBtn, viewHolder);
        Profile profile = searchResults.get(position);

        if (profile.getLastActive() == 0) {
            viewHolder.imageOnline.setImageResource(R.drawable.offline);
        } else {

            if (profile.isOnline()) {
                viewHolder.imageOnline.setImageResource(R.drawable.online);
            } else {
                viewHolder.imageOnline.setImageResource(R.drawable.offline);
            }
        }

        final Profile u = searchResults.get(position);

        if (u.getFullname().length() == 0) {
            viewHolder.profileFullname.setText(u.getUsername());
        } else {
            viewHolder.profileFullname.setText(u.getFullname());
        }

        viewHolder.profileUsername.setText("@" + u.getUsername());

        if (imageLoader == null) {
            imageLoader = App.getInstance().getImageLoader();
        }

        if (searchResults.get(position).getId() == App.getInstance().getId()) {
            viewHolder.profileFollowBtn.setVisibility(View.GONE);
        } else {
            viewHolder.profileFollowBtn.setVisibility(View.VISIBLE);
        }

        if (!searchResults.get(position).isVerify()) {
            viewHolder.profileFullname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else {
            viewHolder.profileFullname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.profile_verify_icon, 0);
        }

        if (App.getInstance().getId() == u.getId()) {
            viewHolder.profileFollowBtn.setVisibility(View.GONE);
        } else {
            if (searchResults.get(position).isFollow()) {
                //viewHolder.profileFollowBtn.setVisibility(View.GONE);
            } else {
                viewHolder.profileFollowBtn.setVisibility(View.VISIBLE);
            }
        }

        if (u.getLowPhotoUrl().length() > 0 && u.getState() == Constants.ACCOUNT_STATE_ENABLED) {

            imageLoader.get(u.getLowPhotoUrl(), ImageLoader.getImageListener(viewHolder.profilePhoto, R.drawable.profile_default_photo, R.drawable.profile_default_photo));

        } else {

            viewHolder.profilePhoto.setImageResource(R.drawable.profile_default_photo);
        }

        return convertView;
    }
}

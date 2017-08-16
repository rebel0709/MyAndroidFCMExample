package cuban.social.testnetwork.groupchat.adapters;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cuban.social.testnetwork.R;
import cuban.social.testnetwork.app.App;
import cuban.social.testnetwork.groupchat.activities.GroupChatActivity;
import cuban.social.testnetwork.groupchat.models.MyGroupModel;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.RestaurantViewHolder> {
    private static final int MAX_WIDTH = 200;
    private static final int MAX_HEIGHT = 200;

    private ArrayList<MyGroupModel> mGroups = new ArrayList<>();
    private Context mContext;

    public GroupListAdapter(Context context, ArrayList<MyGroupModel> restaurants) {
        mContext = context;
        mGroups = restaurants;
    }

    @Override
    public GroupListAdapter.RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_group_row, parent, false);
        RestaurantViewHolder viewHolder = new RestaurantViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GroupListAdapter.RestaurantViewHolder holder, int position) {
        holder.bindRestaurant(mGroups.get(position));
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.group_title) TextView mNameTextView;

        private Context mContext;

        public RestaurantViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        public void bindRestaurant(MyGroupModel restaurant) {

            /*Picasso.with(mContext)
                    .load(restaurant.getImageUrl())
                    .resize(MAX_WIDTH, MAX_HEIGHT)
                    .centerCrop()
                    .into(mRestaurantImageView);*/

            mNameTextView.setText(restaurant.getName());
        }

        @Override
        public void onClick(View v) {
            Log.d("ClickedTag","Clicked");
            int itemPosition = getLayoutPosition();
            MyGroupModel one = mGroups.get(itemPosition);

            Intent intent = new Intent(mContext, GroupChatActivity.class);
            intent.putExtra("MyID", App.getInstance().getId());
            intent.putExtra("GroupID", one.getGID());
            intent.putExtra("GroupTitle", one.getName());

            mContext.startActivity(intent);
        }
    }
}
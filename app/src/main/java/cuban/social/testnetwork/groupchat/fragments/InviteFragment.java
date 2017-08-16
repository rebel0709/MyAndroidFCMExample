package cuban.social.testnetwork.groupchat.fragments;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cuban.social.testnetwork.ProfileActivity;
import cuban.social.testnetwork.R;
import cuban.social.testnetwork.adapter.PeopleListAdapter;
import cuban.social.testnetwork.app.App;
import cuban.social.testnetwork.constants.Constants;
import cuban.social.testnetwork.model.Profile;
import cuban.social.testnetwork.util.CustomRequest;

import cuban.social.testnetwork.R;
/**
 * A simple {@link Fragment} subclass.
 */
public class InviteFragment extends Fragment implements Constants, SwipeRefreshLayout.OnRefreshListener {

    ListView mListView;
    SwipeRefreshLayout mItemsContainer;
    private ArrayList<Profile> itemsList;
    private PeopleListAdapter itemsAdapter;

    private int itemId = 0;
    private int arrayLength = 0;
    private Boolean loadingMore = false;
    private Boolean viewMore = false;
    private Boolean restore = false;
    private String groupID="";
    public InviteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(false);
        groupID =getArguments().getString("GroupID");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_invite_friends, container, false);

        itemsList = new ArrayList<Profile>();
        itemsAdapter = new PeopleListAdapter(getActivity(), itemsList);

        restore = false;
        itemId = 0;
        mItemsContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.container_items);
        mItemsContainer.setOnRefreshListener(this);

        mListView = (ListView) rootView.findViewById(R.id.listView);
        mListView.setAdapter(itemsAdapter);

        if (itemsAdapter.getCount() == 0) {

            showMessage(getText(R.string.label_empty_list).toString());

        } else {

            hideMessage();
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Profile profile = (Profile) adapterView.getItemAtPosition(position);

                DatabaseReference userGroup= FirebaseDatabase.getInstance().getReference("user_group");
                userGroup.child(String.valueOf(profile.getId())).child(groupID).setValue("gid");

                ImageView icon=(ImageView)view.findViewById(R.id.imageOnline);
                icon.setImageResource(cuban.social.testnetwork.R.drawable.sent);
            }
        });
        showMessage(getText(R.string.msg_loading_2).toString());

        getItems();

        return rootView;
    }

    @Override
    public void onRefresh() {
        if (App.getInstance().isConnected()) {

            itemId = 0;
            getItems();

        } else {

            mItemsContainer.setRefreshing(false);
        }
    }

    public void getItems() {

        mItemsContainer.setRefreshing(true);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_PROFILE_FOLLOWINGS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!loadingMore) {

                            itemsList.clear();
                        }

                        try {

                            arrayLength = 0;

                            if (!response.getBoolean("error")) {

                                itemId = response.getInt("id");

                                if (response.has("friends")) {

                                    JSONArray usersArray = response.getJSONArray("friends");

                                    arrayLength = usersArray.length();
                                    int totalOnline=0;
                                    if (arrayLength > 0) {

                                        for (int i = 0; i < usersArray.length(); i++) {

                                            JSONObject userObj = (JSONObject) usersArray.get(i);

                                            Profile profile = new Profile(userObj);

                                            itemsList.add(profile);
                                        }

                                        for (Profile profile: itemsList) {
                                            if(profile.isOnline()){
                                                totalOnline++;
                                            }
                                        }
                                    }
                                }

                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {
                            loadingComplete();
//                            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loadingComplete();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("profileId", Long.toString(App.getInstance().getId()));
                params.put("itemId", Long.toString(itemId));
                params.put("language", "en");

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void loadingComplete() {

        if (arrayLength == LIST_ITEMS) {

            viewMore = true;

        } else {

            viewMore = false;
        }

        itemsAdapter.notifyDataSetChanged();

        if (itemsAdapter.getCount() == 0) {

            showMessage(getText(R.string.label_empty_list).toString());

        } else {

            hideMessage();
        }

        loadingMore = false;
        mItemsContainer.setRefreshing(false);
    }

    public void showMessage(String message) {

    }

    public void hideMessage() {

    }
}

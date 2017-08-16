package cuban.social.testnetwork.groupchat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import cuban.social.testnetwork.R;
import cuban.social.testnetwork.app.App;
import cuban.social.testnetwork.groupchat.CreateNewGroupActivity;
import cuban.social.testnetwork.groupchat.adapters.GroupListAdapter;
import cuban.social.testnetwork.groupchat.models.MyGroupModel;

public class MyGroupsFragment extends Fragment implements View.OnClickListener{
    public static MyGroupsFragment newInstance() {
        MyGroupsFragment fragment = new MyGroupsFragment();
        return fragment;
    }

    Button createGroupBt;
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mItemsContainer;
    FloatingActionButton mGroupSearchBt;

    private ArrayList<MyGroupModel> myGroups=new ArrayList<MyGroupModel>();
    private GroupListAdapter mFirebaseAdapter;
    public static int pageNumber;

    private DatabaseReference mRestaurantReference;

    protected Handler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_group, container, false);
        createGroupBt=(Button)rootView.findViewById(R.id.bt_create_group);
        createGroupBt.setOnClickListener(this);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
        mItemsContainer = (SwipeRefreshLayout)rootView.findViewById(R.id.container_items);
        mItemsContainer.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        //Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        //setRefreshing(false);
                        myUpdateOperation();
                    }
                }
        );

        mRestaurantReference = FirebaseDatabase.getInstance().getReference();
        getGroupIDs();
        setUpFirebaseAdapter();

        return rootView;//super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setUpFirebaseAdapter() {
        mFirebaseAdapter = new GroupListAdapter(this.getActivity(),myGroups) ;

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mFirebaseAdapter.cleanup();
    }

    @Override
    public void onClick(View v) {
        if(v.equals(createGroupBt)){
            Bundle b=getActivity().getIntent().getExtras();
            Intent i= new Intent(getActivity(), CreateNewGroupActivity.class);
            getActivity().startActivity(i);
        }
    }

    public void getGroupIDs(){
        String uid=String.valueOf(App.getInstance().getId());
        mRestaurantReference.child("user_group").child(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("ChildAddedSnapshot", dataSnapshot.toString());
                String gid=dataSnapshot.getKey();
                mRestaurantReference.child("groups").child(gid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        MyGroupModel one=dataSnapshot.getValue(MyGroupModel.class);
                        one.setGID(dataSnapshot.getKey());
                        myGroups.add(one);
                        mFirebaseAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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

    public void myUpdateOperation(){
        mItemsContainer.setRefreshing(false);
    }
}

package cuban.social.testnetwork.groupchat.fragments;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import cuban.social.testnetwork.R;
import cuban.social.testnetwork.app.App;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateGroupFragment extends Fragment implements View.OnClickListener{

    public CreateGroupFragment() {
        // Required empty public constructor
    }
    EditText groupTitleEt;
    Button createBt;
    int status=0;   //0:create group_title, 1:invite friend,    2:create_group status

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView=inflater.inflate(R.layout.fragment_create_group,container,false);
        groupTitleEt=(EditText)rootView.findViewById(R.id.group_title);
        createBt=(Button)rootView.findViewById(R.id.create_bt);
        createBt.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(status==0){
            String groupName=groupTitleEt.getText().toString();
            Map groupInfo= new HashMap<String, Object>();
            groupInfo.put("owenerID", App.getInstance().getId());
            groupInfo.put("name",groupName);
            DatabaseReference dbReference= FirebaseDatabase.getInstance().getReference("groups");
            DatabaseReference groupNodeRef=dbReference.push();
            groupNodeRef.setValue(groupInfo);
            createBt.setText("Invite Friends");
            status=1;

            dbReference= FirebaseDatabase.getInstance().getReference("user_group");
            dbReference.child(String.valueOf(App.getInstance().getId())).child(groupNodeRef.getKey()).setValue("gid");

            Fragment f= new InviteFragment();
            Bundle b= new Bundle();
            b.putString("GroupID",groupNodeRef.getKey());
            f.setArguments(b);
            FragmentManager fragmentManager = CreateGroupFragment.this.getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container_body, f).commit();
        }else{
            status=1;

        }
    }
}

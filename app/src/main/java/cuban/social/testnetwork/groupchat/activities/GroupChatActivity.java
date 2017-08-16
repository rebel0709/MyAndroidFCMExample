package cuban.social.testnetwork.groupchat.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import cuban.social.testnetwork.ChatFragment;
import cuban.social.testnetwork.R;
import cuban.social.testnetwork.dialogs.MsgImageChooseDialog;
import cuban.social.testnetwork.groupchat.fragments.GroupChatFragment;

public class GroupChatActivity extends AppCompatActivity implements MsgImageChooseDialog.AlertPositiveListener{
    Toolbar mToolbar;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogs);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        fragment = new GroupChatFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container_body, fragment)
                .commit();
    }

    @Override
    public void onImageFromGallery() {
        GroupChatFragment p = (GroupChatFragment) fragment;
        p.imageFromGallery();
    }

    @Override
    public void onImageFromCamera() {
        GroupChatFragment p = (GroupChatFragment) fragment;
        p.imageFromCamera();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case android.R.id.home: {
                finish();
                return true;
            }
            case R.id.action_group_invite: {
                Intent i= new Intent(this, NewMemberSearchActivity.class);
                i.putExtra("GroupID", this.getIntent().getStringExtra("GroupID"));
                startActivity(i);
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    //menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_groupchat, menu);
        return true;
    }
}

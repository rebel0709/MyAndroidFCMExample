package cuban.social.testnetwork.fcmservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import cuban.social.testnetwork.ChatFragment;
import cuban.social.testnetwork.app.App;

/**
 * Created by Freelancer on 7/28/2017.
 */

public class MyFcmListenerService extends FirebaseMessagingService {
    private LocalBroadcastManager broadcaster;

    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        /*Intent i = new Intent(ChatFragment.BROADCAST_ACTION);
        i.putExtra(ChatFragment.PARAM_TASK, 0);
        i.putExtra(ChatFragment.PARAM_STATUS, ChatFragment.STATUS_START);
        //context.sendBroadcast(i);
        broadcaster.sendBroadcast(i);*/
        if(ChatFragment.intance!=null){
            ChatFragment.intance.getNextMessages();
        }
    }
}

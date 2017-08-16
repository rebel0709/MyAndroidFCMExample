package cuban.social.testnetwork.groupchat.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class MyGroupModel {
    String name;
    long owenerID;
    String gid;
    public MyGroupModel() {}

    public MyGroupModel(String name, int owenerID) {
        this.name = name;
        this.owenerID =owenerID;
    }

    public String getName() {
        return name;
    }

    public long getOwenerID() {
        return owenerID;
    }

    public void setGID(String gid){
        this.gid=gid;
    }

    public String getGID(){
        return gid;
    }
}

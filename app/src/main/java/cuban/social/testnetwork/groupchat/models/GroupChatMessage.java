package cuban.social.testnetwork.groupchat.models;

import java.util.Date;

public class GroupChatMessage {
    private String messageText;
    private String messageImgURL="";

    private String messageUser;
    private long messageUserId;
    private String avararURL="";
    private long messageTime;

    private String messageid;

    public GroupChatMessage(String messageText, String messageImgURL, String messageUser, String avararURL, long messageUserId) {
        this.messageText = messageText;
        this.messageImgURL=messageImgURL;

        this.messageUser = messageUser;
        this.avararURL=avararURL;
        //messageTime = new Date().getTime();
        this.messageUserId = messageUserId;
    }
    public GroupChatMessage(String messageText, String messageImgURL, String messageUser, String avararURL, long messageUserId, long messageTime) {
        this.messageText = messageText;
        this.messageImgURL=messageImgURL;

        this.messageUser = messageUser;
        this.avararURL=avararURL;
        this.messageUserId = messageUserId;
        this.messageTime=messageTime;
    }

    public GroupChatMessage(){

    }

    public long getMessageUserId() {
        return messageUserId;
    }

    public void setMessageID(String msgID){this.messageid=msgID;}

    public String getMessageID(){return messageid;}

    public void setMessageUserId(long messageUserId) {
        this.messageUserId = messageUserId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getAvararURL(){return avararURL;}

    public String getMessageImgURL(){return messageImgURL;}
}

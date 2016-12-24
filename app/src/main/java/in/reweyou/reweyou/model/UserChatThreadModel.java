package in.reweyou.reweyou.model;

/**
 * Created by master on 1/1/17.
 */

public class UserChatThreadModel {

    private String id;
    private String chatroom_id;
    private String sender;
    private String receiver;
    private String sender_name;
    private String receiver_name;
    private String last_message;
    private String pic;
    private String showNumber;
    private String name;

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getId() {
        return id;
    }

    public String getChatroom_id() {
        return chatroom_id;
    }

    public String getLast_message() {
        return last_message;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public String getSender_name() {
        return sender_name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public void setshowNumber(String showNumber) {
        this.showNumber = showNumber;
    }

    public String getshowNumber() {
        return showNumber;
    }

    public void setname(String name) {
        this.name = name;
    }

    public String getname() {
        return name;
    }
}

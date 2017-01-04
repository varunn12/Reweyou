package in.reweyou.reweyou.model;

/**
 * Created by master on 28/12/16.
 */

public class UserChatModel {


    String id;
    String message;
    String sender;
    String receiver;
    String time;
    private boolean sending;
    private boolean failed;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTime() {

        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean getSending() {
        return sending;
    }

    public void setSending(boolean sending) {
        this.sending = sending;
    }

    public boolean getFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }
}

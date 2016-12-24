package in.reweyou.reweyou.model;

/**
 * Created by master on 2/1/17.
 */

public class ContactListModel {
    private String name;
    private String number;
    private String pic;

    public ContactListModel(String phoneNumberName, String phoneNumber, String phoneNumberPic) {
        this.name = phoneNumberName;
        this.number = phoneNumber;
        this.pic = phoneNumberPic;
    }

    public String getName() {
        return name;

    }

    public String getNumber() {
        return number;
    }

    public String getPic() {
        return pic;
    }
}


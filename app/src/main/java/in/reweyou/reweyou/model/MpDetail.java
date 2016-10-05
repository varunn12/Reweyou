package in.reweyou.reweyou.model;

/**
 * Created by Reweyou on 10/5/2015.
 */

public class MpDetail {

    private String name;
    private String id;
    private String type;
    private String party;
    private String place;
    private String positions;
    private String state;
    private String email_id;
    private String mimage;
    private String contact_no;
    private String office_no;
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPositions() {
        return positions;
    }

    public void setPositions(String positions) {
        this.positions = positions;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getImage() {
        return mimage;
    }

    public void setImage(String mimage) {
        // String url ="https://www.reweyou.in/thumb/230/226/";
        this.mimage = mimage;
        // String image = image + url;
    }

    public String getOffice() {
        return office_no;
    }

    public void setOffice(String office_no) {
        this.office_no = office_no;
    }

    public String getContact() {
        return contact_no;
    }

    public void setContact(String contact_no) {
        this.contact_no = contact_no;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public void setId(String id)
    {
        this.id=id;
    }
    public String getId()
    {
        return id;
    }

}


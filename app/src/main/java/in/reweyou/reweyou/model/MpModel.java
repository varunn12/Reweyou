package in.reweyou.reweyou.model;

/**
 * Created by Reweyou on 10/5/2015.
 */

public class MpModel {

    private String name;
    private String number;
    private String head;
    private String source;
    private String id;
    private String headline;
    private String video;
    private String image;
    private String profilepic;
    private String readers;
    private String date;
    private String location;
    private String category;
    private String reviews;
    private String postviews;
    private String postid;
    private String comments;

    public String getName() {
        return name;
    }

    public void setName(String to) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setComments(String comments)
    {
        this.comments=comments;
    }
    public String getComments()
    {
        return comments;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String type) {
        this.headline = headline;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String from) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        // String url ="https://www.reweyou.in/thumb/230/226/";
        this.image = image;
        // String image = image + url;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        // String url ="https://www.reweyou.in/thumb/230/226/";
        this.image = image;
        // String image = image + url;
    }
    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setReviews(String reviews)
    {
        this.reviews=reviews;
    }
    public String getReviews()
    {
        return reviews;
    }

    public void setPostviews(String reviews)
    {
        this.postviews=postviews;
    }
    public String getPostviews()
    {
        return postviews;
    }

    public void setId(String id)
    {
        this.id=id;
    }
    public String getId()
    {
        return id;
    }

    public void setPostId(String postid)
    {
        this.postid=postid;
    }
    public String getPostId()
    {
        return postid;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getReaders() {
        return readers;
    }

    public void setReaders(String readers) {
        this.readers = readers;
    }
}

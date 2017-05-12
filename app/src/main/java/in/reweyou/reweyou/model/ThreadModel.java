package in.reweyou.reweyou.model;

/**
 * Created by master on 9/5/17.
 */

public class ThreadModel {
    private String threadid;
    private String groupname;
    private String link;
    private String youtubelink;
    private String uid;
    private String name;
    private String timestamp;
    private String image1;
    private String image2;
    private String image3;
    private String image4;
    private String imageurl;
    private String description;
    private String type;
    private String upvotes;
    private String comments;
    private String linkdes = "";
    private String linkhead = "";

    public String getLinkdes() {
        return linkdes;
    }

    public String getLinkhead() {
        return linkhead;
    }

    public String getProfilepic() {
        return imageurl;
    }


    public String getGroupname() {
        return groupname;
    }

    public String getName() {
        return name;
    }

    public String getImage1() {
        return image1;
    }

    public String getImage2() {
        return image2;
    }

    public String getComments() {
        return comments;
    }

    public String getDescription() {
        return description;
    }

    public String getImage3() {
        return image3;
    }

    public String getImage4() {
        return image4;
    }

    public String getLink() {
        return link;
    }

    public String getThreadid() {
        return threadid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public String getUid() {
        return uid;
    }

    public String getUpvotes() {
        return upvotes;
    }

    public String getYoutubelink() {
        return youtubelink;
    }

}

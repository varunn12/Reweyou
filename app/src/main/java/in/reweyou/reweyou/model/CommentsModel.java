package in.reweyou.reweyou.model;

/**
 * Created by Reweyou on 10/5/2015.
 */

public class CommentsModel {

    private String from_name;
    private String headline;
    private String id;
    private String comments;
    private String from;
    private String postid;
    private String post;
    private String image;
    private String r_image = null;
    private String profilepic;
    private String reviewer;
    private String reviewer_name;
    private String time;

    public String getFrom_Name() {
        return from_name;
    }

    public void setFrom_Name(String from_name) {
        this.from_name = from_name;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String type) {
        this.headline = headline;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String type) {
        this.comments = comments;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String from) {
        this.time = time;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        // String url ="https://www.reweyou.in/thumb/230/226/";
        this.image = image;
        // String image = image + url;
    }

    public String getR_Image() {
        return r_image;
    }

    public void setR_Image(String r_image) {
        // String url ="https://www.reweyou.in/thumb/230/226/";
        this.r_image = r_image;
        // String image = image + url;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getReviewer()
    {
        return reviewer;
    }

    public void setReviewer(String reviewer)
    {
        this.reviewer = reviewer;
    }

    public String getReviewer_Name()
    {
        return reviewer_name;
    }

    public void setReviewer_Name(String reviewer_name)
    {
        this.reviewer_name = reviewer_name;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        // String url ="https://www.reweyou.in/thumb/230/226/";
        this.profilepic = profilepic;
        // String image = image + url;
    }
}
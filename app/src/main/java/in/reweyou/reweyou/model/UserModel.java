package in.reweyou.reweyou.model;

/**
 * Created by Reweyou on 10/5/2015.
 */

public class UserModel {

    private String name;
    private String id;
    private String number;
    private String follower_number;
    private String follower_name;
    private String image;
    private String total_points;
    private String total_reviews;
    private String profilepic;
    private String action;
    public String time;
    public String comments;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        // String url ="https://www.reweyou.in/thumb/230/226/";
        this.image = image;
        // String image = image + url;
    }

    public String getProfilePic() {
        return profilepic;
    }

    public void setProfilePic(String ProfilePic) {
        // String url ="https://www.reweyou.in/thumb/230/226/";
        this.profilepic = profilepic;
        // String ProfilePic = ProfilePic + url;
    }

    public void setNumber(String number)
    {
        this.number=number;
    }
    public String getNumber()
    {
        return number;
    }

    public void setId(String id)
    {
        this.id=id;
    }
    public String getId()
    {
        return id;
    }

    public void setTotal_points(String id)
    {
        this.total_points=total_points;
    }
    public String getTotal_points()
    {
        return total_points;
    }

    public void setTotal_reviews(String id)
    {
        this.total_reviews=total_reviews;
    }
    public String getTotal_reviews()
    {
        return total_reviews;
    }

    public void setAction(String action)
    {
        this.action=action;
    }
    public String getAction()
    {
        return action;
    }

    public void setTime(String time)
    {
        this.time=time;
    }
    public String getTime()
    {
        return time;
    }

    public void setComments(String comments)
    {
        this.comments=comments;
    }
    public String getComments()
    {
        return comments;
    }

    public String getFollower_name() {
        return follower_name;
    }

    public void setFollower_name(String follower_name) {
        this.follower_name = follower_name;
    }

    public String getFollower_number() {
        return follower_number;
    }

    public void setFollower_number(String follower_number) {
        this.follower_number = follower_number;
    }
}


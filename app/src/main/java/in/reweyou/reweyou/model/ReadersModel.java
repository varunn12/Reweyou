package in.reweyou.reweyou.model;

/**
 * Created by master on 29/11/16.
 */

public class ReadersModel {

    private String id;
    private String followed_number;
    private String follower_number;
    private String followed_name;
    private String follower_name;

    public String getFollowed_name() {
        return followed_name;
    }

    public String getFollowed_number() {
        return followed_number;
    }

    public String getFollower_name() {
        return follower_name;
    }

    public String getFollower_number() {
        return follower_number;
    }

    public String getId() {
        return id;
    }
}

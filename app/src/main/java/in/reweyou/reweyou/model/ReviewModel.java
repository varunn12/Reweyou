package in.reweyou.reweyou.model;

/**
 * Created by master on 26/2/17.
 */

public class ReviewModel {
    private String reviewid;
    private String created_on;
    private String created_by;
    private String description;
    private String image;
    private String video;
    private String gif;
    private String comments;
    private String name;
    private String likes;
    private String rating;
    private String is_liked;

    public String getName() {
        return name;
    }

    public String getComments() {
        return comments;
    }

    public String getCreated_by() {
        return created_by;
    }

    public String getCreated_on() {
        return created_on;
    }

    public String getDescription() {
        return description;
    }

    public String getGif() {
        return gif;
    }

    public String getImage() {
        return image;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getIs_liked() {
        return is_liked;
    }

    public void setIs_liked(String is_liked) {
        this.is_liked = is_liked;
    }

    public String getRating() {
        return rating;
    }

    public String getReviewid() {
        return reviewid;
    }

    public String getVideo() {
        return video;
    }
}

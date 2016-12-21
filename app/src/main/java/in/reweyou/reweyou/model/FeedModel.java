package in.reweyou.reweyou.model;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.text.format.DateUtils.getRelativeTimeSpanString;
import static in.reweyou.reweyou.utils.Constants.VIEW_TYPE_IMAGE;
import static in.reweyou.reweyou.utils.Constants.VIEW_TYPE_VIDEO;

/**
 * Created by Reweyou on 10/5/2015.
 */

public class FeedModel {

    private SimpleDateFormat dfs = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.US);
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
    private String reaction;
    private String from;
    private String gif;
    private boolean liked = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getComments() {
        return comments;
    }


    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String from) {
        this.location = location;
    }

    public String getDate() {
        if (date != null && !date.isEmpty()) {

            date = date.replaceAll("\\.", "");

            Date dates = null;
            try {
                dates = dfs.parse(date);
                long epochs = dates.getTime();
                //   Log.e("Time", String.valueOf(epochs));
                CharSequence timePassedString = getRelativeTimeSpanString(epochs, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
                return (String) timePassedString;
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        } else return null;

    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getVideo() {
        return video;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    public String getPostviews() {
        return postviews;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostId() {
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


    public String getReaction() {
        return reaction;
    }


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getGif() {
        return gif;
    }


    public int getViewType() {
        if (getImage().isEmpty() && getVideo().isEmpty() && getGif().isEmpty()) {
            return VIEW_TYPE_IMAGE;
        } else if (!getVideo().isEmpty()) {
            return VIEW_TYPE_VIDEO;
        } else return VIEW_TYPE_IMAGE;
    }


    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }


}

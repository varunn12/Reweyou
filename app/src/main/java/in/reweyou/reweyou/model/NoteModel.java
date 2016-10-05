package in.reweyou.reweyou.model;

/**
 * Created by Reweyou on 10/5/2015.
 */

public class NoteModel {

    private String from;
    private String id;
    private String headline;
    private String video;
    private String image;
    private String date;
    private String reviewer;
    private String reviewer_name;
    private String location;

    public String getFrom() {
        return from;
    }

    public void setFrom(String to) {
        this.from = from;
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

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public void setReviewer(String reviewer)
    {
        this.reviewer=reviewer;
    }
    public String getReviewer()
    {
        return reviewer;
    }

    public void setReviewer_Name(String reviewer_name)
    {
        this.reviewer_name=reviewer_name;
    }
    public String getReviewer_Name()
    {
        return reviewer_name;
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

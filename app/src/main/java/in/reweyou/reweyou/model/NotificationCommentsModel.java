package in.reweyou.reweyou.model;

import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.text.format.DateUtils.getRelativeTimeSpanString;

/**
 * Created by master on 26/11/16.
 */

public class NotificationCommentsModel {

    private String notid;
    private String id;
    private String postid;
    private String comments;
    private String reviewer_name;
    private String r_image;
    private String time;
    private String profilepic;
    private String readstatus;


    private SimpleDateFormat dfs = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.US);

    public Date getTime() {
        try {
            Date date = dfs.parse(time);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReviewer_name() {
        return reviewer_name;
    }

    public String getFormattedTime() {
        if (time != null && !time.isEmpty()) {

            time = time.replaceAll("\\.", "");

            Date dates = null;
            try {
                dates = dfs.parse(time);
                long epochs = dates.getTime();
                Log.e("Time", String.valueOf(epochs));
                CharSequence timePassedString = getRelativeTimeSpanString(epochs, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
                return (String) timePassedString;
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        } else return null;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public String getNotid() {
        return notid;
    }

    public String getReadstatus() {
        return readstatus;
    }
}

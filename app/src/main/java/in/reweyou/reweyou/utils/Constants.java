package in.reweyou.reweyou.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by master on 1/11/16.
 */


public class Constants {


    public static final String EDIT_URL = "https://www.reweyou.in/reweyou/editheadline_new.php";

    public static final String NEWS_FEED_URL = "https://www.reweyou.in/reweyou/testfeed.php";
    public static final String TRENDING_URL = "https://www.reweyou.in/reweyou/trending.php";
    public static final String READING_URL = "https://www.reweyou.in/reweyou/readingfeed.php";
    public static final String MY_CITY_URL = "https://www.reweyou.in/reweyou/report_cityfeed.php";

    //My Profile Class
    public static final String MY_PROFILE_URL_VERIFY_FOLLOW = "https://www.reweyou.in/reweyou/verify_follow.php";
    public static final String MY_PROFILE_URL_FOLLOW = "https://www.reweyou.in/reweyou/follow_new.php";
    public static final String MY_PROFILE_UPLOAD_URL = "https://www.reweyou.in/reweyou/report_pic.php";

    public static final String MY_PROFILE_EDIT_URL = "https://www.reweyou.in/reviews/report_profile.php";

    public static final String USER_PROFILE_URL_VERIFY_FOLLOW = "https://www.reweyou.in/reweyou/verify_follow.php";
    public static final String USER_PROFILE_URL_FOLLOW = "https://www.reweyou.in/reweyou/read_new.php";

    public static final String URL_LIKE = "https://www.reweyou.in/reweyou/likes.php";
    public static final String URL_NOTI_READ_STATUS = "https://www.reweyou.in/reweyou/readstatus.php";
    public static final String MY_NOTIFICATIONS_URL = "https://www.reweyou.in/reweyou/notification.php";
    public static final String MY_TOTAL_NOTIFICATIONS_URL = "https://www.reweyou.in/reweyou/total_notifications.php";

    public static final String URL_COMMENTS_UPLOAD = "https://www.reweyou.in/reweyou/reporting_comment.php";
    public static final String URL_COMMENTS_FETCH = "https://www.reweyou.in/reweyou/comments_list.php";

    public static final int VIEW_TYPE_IMAGE = 5;
    public static final int VIEW_TYPE_VIDEO = 6;
    public static final int VIEW_TYPE_GIF = 7;
    public static final int VIEW_TYPE_LOADING = 8;
    public static final int VIEW_TYPE_NEW_POST = 9;
    public static final int VIEW_TYPE_LOCATION = 11;
    public static final int VIEW_TYPE_CITY_NO_REPORTS_YET= 12;
    public static final int VIEW_TYPE_READING_NO_READERS= 13;
    public static final int VIEW_TYPE_READING_NO_REPORTS_YET_FROM_USER = 15;


    public static final String POST_REPORT_KEY_ADDRESS = "address";
    public static final String POST_REPORT_KEY_TAG = "tag";
    public static final String POST_REPORT_KEY_CATEGORY = "type";
    public static final String POST_REPORT_KEY_TIME = "time";
    public static final String POST_REPORT_KEY_LOCATION = "location";
    public static final String POST_REPORT_KEY_HEADLINE = "head";
    public static final String POST_REPORT_KEY_NAME = "name";
    public static final String POST_REPORT_KEY_PRIVACY = "privacy";
    public static final String POST_REPORT_KEY_NUMBER = "number";
    public static final String POST_REPORT_KEY_DESCRIPTION = "headline";
    public static final String POST_REPORT_KEY_TOKEN = "token";
    public static final String POST_REPORT_KEY_IMAGE = "image";
    public static final String POST_REPORT_KEY_VIDEO = "video";
    public static final String POST_REPORT_KEY_GIF = "gif";
    public static final String POST_REPORT_KEY_REPORT = "report";

    public static final String AUTH_ERROR = "Autherror";
    public static final String URL_OLD_USER_STATUS = "https://www.reweyou.in/reweyou/old_users.php";
    public static final String URL_VERIFY_OTP = "https://www.reweyou.in/reviews/verify_otp_new.php";

    public static final String SEARCH_QUERY = "https://www.reweyou.in/reweyou/searchresults.php";
    public static final String FEED_LIKES = "https://www.reweyou.in/reweyou/liked.php";
    public static final String MY_SINGLE_ACTIVITY = "https://www.reweyou.in/reweyou/postbyid.php";
    public static final String CATEGORY_FEED_URL = "https://www.reweyou.in/reweyou/categoryfeed.php";
    public static final String URL_INCREASE_VIEWS = "https://www.reweyou.in/reweyou/postviews.php";


    public static final int POSITION_FEED_TAB_MAIN_FEED = 20;
    public static final int POSITION_FEED_TAB_2 = 21;
    public static final int POSITION_FEED_TAB_3 = 0;
    public static final int POSITION_FEED_TAB_MY_CITY = 10;
    public static final int POSITION_SEARCH_TAB = 12;
    public static final int POSITION_SINGLE_POST = 15;
    public static final int POSITION_CATEGORY_TAG = 19;

    public static final String ADD_CHAT_MESSAGE_EVENT = "acm";
    public static final String ADD_CHAT_MESSAGE_CHATROOM_ID = "aa";
    public static final String ADD_CHAT_MESSAGE_SENDER_NAME = "aswsz";
    public static final String ADD_CHAT_MESSAGE_SENDER_NUMBER = "adwqdc";
    public static final String ADD_CHAT_MESSAGE_MESSAGE = "da";
    public static final String ADD_CHAT_MESSAGE_TIMESTAMP = "dc";


    public static final SimpleDateFormat dfs = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.ENGLISH);
    public static final String URL_MY_REPORTS = "https://www.reweyou.in/reweyou/myreports.php";
    public static final String SEND_NOTI_CHANGE_REQUEST = "abx";

    public static String suggestpostid;
}

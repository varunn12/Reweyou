package in.reweyou.reweyou.utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by master on 12/1/17.
 */

public class ReportLoadingConstant {


    public static final int FRAGMENT_CATEGORY_NEWS = 1;
    public static final int FRAGMENT_CATEGORY_CITY = 2;
    public static final int FRAGMENT_CATEGORY_READING = 3;
    public static final int FRAGMENT_CATEGORY_SINGLE_POST = 4;
    public static final int FRAGMENT_CATEGORY_SEARCH = 5;
    public static final int FRAGMENT_CATEGORY_TAG = 6;
    public static final int FRAGMENT_CATEGORY_MY_PROFILE = 7;
    public static final int FRAGMENT_CATEGORY_REPORTER_PROFILE = 8;

    public static final String REQUEST_PARAMS_NUMBER = "number";
    public static final String REQUEST_PARAMS_TIME = "time";
    public static final String REQUEST_PARAMS_SINGLE_POST = "query";
    public static final String REQUEST_PARAMS_CITY = "location";
    public static final String REQUEST_PARAMS_TAG = "category";
    public static final String REQUEST_PARAMS_LAST_POSTID = "postid";

    public static ArrayList<Integer> fragmentCategoryList = new ArrayList<>(Arrays.asList(
            FRAGMENT_CATEGORY_NEWS,
            FRAGMENT_CATEGORY_CITY,
            FRAGMENT_CATEGORY_READING,
            FRAGMENT_CATEGORY_SINGLE_POST,
            FRAGMENT_CATEGORY_SEARCH,
            FRAGMENT_CATEGORY_REPORTER_PROFILE,
            FRAGMENT_CATEGORY_TAG));

    public static ArrayList<Integer> fragmentListLoadOnStart = new ArrayList<>(Arrays.asList(
            FRAGMENT_CATEGORY_NEWS,
            FRAGMENT_CATEGORY_SINGLE_POST,
            FRAGMENT_CATEGORY_SEARCH,
            FRAGMENT_CATEGORY_REPORTER_PROFILE,
            FRAGMENT_CATEGORY_TAG));

    public static ArrayList<Integer> fragmentListWithBoxAtTop = new ArrayList<>(Arrays.asList(
            FRAGMENT_CATEGORY_NEWS,
            FRAGMENT_CATEGORY_CITY));

    public static ArrayList<Integer> fragmentListWithCache = new ArrayList<>(Arrays.asList(
            FRAGMENT_CATEGORY_NEWS,
            FRAGMENT_CATEGORY_CITY, FRAGMENT_CATEGORY_MY_PROFILE,
            FRAGMENT_CATEGORY_REPORTER_PROFILE,
            FRAGMENT_CATEGORY_READING));
}

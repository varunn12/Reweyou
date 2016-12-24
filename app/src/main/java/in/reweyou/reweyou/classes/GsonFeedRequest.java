package in.reweyou.reweyou.classes;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import in.reweyou.reweyou.model.FeedModel;
import in.reweyou.reweyou.utils.Constants;

import static in.reweyou.reweyou.utils.Constants.VIEW_TYPE_LOCATION;
import static in.reweyou.reweyou.utils.Constants.VIEW_TYPE_NEW_POST;

/**
 * Created by master on 24/12/16.
 */

public class GsonFeedRequest<List> extends Request<List> {
    private static final String TAG = GsonFeedRequest.class.getSimpleName();
    private final UserSessionManager sessionManager;
    private final FeedModel feedModel;
    private final int position;
    private final Response.Listener<List> listener;
    private final Map<String, String> headers;
    private int minPostid;
    private String lastPostid;


    public GsonFeedRequest(int position, String url, FeedModel feedModel, Map<String, String> headers, Response.Listener<List> listener, Response.ErrorListener errorListener, UserSessionManager sessionManager) {
        super(Method.GET, url, errorListener);
        this.position = position;
        this.feedModel = feedModel;
        this.sessionManager = sessionManager;
        this.listener = listener;
        this.headers = headers;

    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();

    }

    @Override
    protected Response<List> parseNetworkResponse(NetworkResponse response) {
        org.json.JSONArray parentArray;
        try {

            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            parentArray = new org.json.JSONArray(json);

            java.util.List<String> likeslist = sessionManager.getLikesList();

            java.util.List<Object> messagelist = new ArrayList<>();

            if (position == Constants.POSITION_FEED_TAB_MAIN_FEED) {
                messagelist.add(VIEW_TYPE_NEW_POST);
            }

            if (position == Constants.POSITION_FEED_TAB_MY_CITY) {
                messagelist.add(VIEW_TYPE_LOCATION);
            }

            Gson gson = new Gson();

            Log.d(TAG, "onResponse: Size: " + parentArray.length());

            for (int i = 0; i < parentArray.length(); i++) {
                JSONObject feedObject = parentArray.getJSONObject(i);
                FeedModel feedModel = gson.fromJson(feedObject.toString(), FeedModel.class);

                if (likeslist.contains(feedModel.getPostId())) {
                    feedModel.setLiked(true);
                }

                if (i == 0) {
                    minPostid = Integer.parseInt(feedModel.getPostId());
                }

                if (minPostid > Integer.parseInt(feedModel.getPostId())) {
                    minPostid = Integer.parseInt(feedModel.getPostId());
                }


                messagelist.add(feedModel);

                if (i == parentArray.length() - 1) {
                    lastPostid = feedModel.getPostId();
                    Log.d(TAG, "onResponse: lastPostid: " + lastPostid);
                    Log.d(TAG, "onResponse: minPostid: " + minPostid);

                }
            }

            messagelist.add(lastPostid);
            messagelist.add(minPostid);
            return (Response<List>) Response.success(messagelist, HttpHeaderParser.parseCacheHeaders(response));

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }

    @Override
    protected void deliverResponse(List response) {
        listener.onResponse(response);
    }
}

package in.reweyou.reweyou.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import in.reweyou.reweyou.FullImage;
import in.reweyou.reweyou.R;
import in.reweyou.reweyou.SinglePostActivity;
import in.reweyou.reweyou.Videorow;
import in.reweyou.reweyou.classes.ConnectionDetector;
import in.reweyou.reweyou.classes.CustomTabActivityHelper;
import in.reweyou.reweyou.classes.CustomTabsOnClickListener;
import in.reweyou.reweyou.classes.RequestHandler;
import in.reweyou.reweyou.classes.Util;
import in.reweyou.reweyou.model.MpModel;

import static in.reweyou.reweyou.utils.Constants.VIEW_TYPE_GIF;
import static in.reweyou.reweyou.utils.Constants.VIEW_TYPE_IMAGE;
import static in.reweyou.reweyou.utils.Constants.VIEW_TYPE_VIDEO;

/**
 * Created by master on 22/11/16.
 */

public class SinglePostFragment extends Fragment {

    private ProgressBar progressBar;
    private String query;
    private ConnectionDetector cd;
    private CustomTabActivityHelper mCustomTabActivityHelper;
    private RelativeLayout aviewcontainer;
    private ScrollView viewcontainer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cd = new ConnectionDetector(getActivity());
        mCustomTabActivityHelper = new CustomTabActivityHelper();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.single_post_layout, container, false);
        progressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);
        aviewcontainer = (RelativeLayout) layout.findViewById(R.id.container);
        viewcontainer = (ScrollView) layout.findViewById(R.id.scrollView);
        query = getArguments().getString("myData");


        new JSONTask().execute(query);

        return layout;
    }

    private void setData(final MpModel result, View view, final int i) {
        ImageView image, profilepic, overflow;
        TextView headline, upvote, head;
        TextView place;
        TextView icon;
        ImageView reaction;
        TextView date;
        TextView tv;
        TextView from;
        CardView cv;
        TextView reviews, source;
        TextView app;
        LinearLayout linearLayout;
        ImageView upicon;
        TextView name, userName;
        RelativeLayout rv;

        headline = (TextView) view.findViewById(R.id.Who);
        head = (TextView) view.findViewById(R.id.head);
        name = (TextView) view.findViewById(R.id.name);
        userName = (TextView) view.findViewById(R.id.userName);
        rv = (RelativeLayout) view.findViewById(R.id.rv);
        place = (TextView) view.findViewById(R.id.place);
        reaction = (ImageView) view.findViewById(R.id.comment);
        date = (TextView) view.findViewById(R.id.date);
        image = (ImageView) view.findViewById(R.id.image);
        overflow = (ImageView) view.findViewById(R.id.overflow);
        profilepic = (ImageView) view.findViewById(R.id.profilepic);
        from = (TextView) view.findViewById(R.id.from);

        reviews = (TextView) view.findViewById(R.id.reviews);
        app = (TextView) view.findViewById(R.id.app);
        upvote = (TextView) view.findViewById(R.id.upvote);
        source = (TextView) view.findViewById(R.id.source);

        linearLayout = (LinearLayout) view.findViewById(R.id.like);
        upicon = (ImageView) view.findViewById(R.id.upicon);

        rv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SinglePostActivity) getActivity()).changetab();
            }
        });

        reaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SinglePostActivity) getActivity()).changetab();

            }
        });
        app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SinglePostActivity) getActivity()).changetab();

            }
        });



        image.setOnClickListener(new View.OnClickListener() {
            public boolean isInternetPresent;

            @Override
            public void onClick(View view) {

                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {

                    if (i == 2) {
                        Bundle bundle = new Bundle();
                        bundle.putString("myData", result.getVideo());

                        Intent in = new Intent(getActivity(), Videorow.class);
                        in.putExtras(bundle);
                        getActivity().startActivity(in);
                    } else if (i == 1) {
                        Bundle bundle = new Bundle();
                        bundle.putString("myData", result.getImage());
                        Intent in = new Intent(getActivity(), FullImage.class);
                        in.putExtras(bundle);
                        getActivity().startActivity(in);
                    }

                } else {
                    Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Spannable spannable = new SpannableString(result.getHeadline());
        // Util.linkifyUrl(spannable, new CustomTabsOnClickListener(activity, mCustomTabActivityHelper));
        headline.setText(spannable);
        headline.setMovementMethod(LinkMovementMethod.getInstance());


        if (result.getHead() == null || result.getHead().isEmpty())
            head.setVisibility(View.GONE);
        else {
            head.setVisibility(View.VISIBLE);
            head.setText(result.getHead());
        }

        if (result.getHeadline() == null || result.getHeadline().isEmpty())
            headline.setVisibility(View.GONE);
        else {
            headline.setVisibility(View.VISIBLE);
            headline.setText(result.getHeadline());
        }

        if (result.getDate() == null)
            date.setVisibility(View.GONE);
        else {
            date.setVisibility(View.VISIBLE);
            date.setText(result.getDate());
        }


        Glide.with(getActivity()).load(result.getProfilepic()).placeholder(R.drawable.download).error(R.drawable.download).fallback(R.drawable.download).dontAnimate().into(profilepic);

        image.setVisibility(View.VISIBLE);
        if (result.getImage().isEmpty()) {
            if (result.getGif().isEmpty()) {
                image.setVisibility(View.GONE);
            } else
                Glide.with(getActivity()).load(result.getGif()).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).error(R.drawable.ic_error).dontAnimate().into(image);
        } else
            Glide.with(getActivity()).load(result.getImage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(R.drawable.ic_error).dontAnimate().into(image);


        if (result.getLocation() == null || result.getLocation().isEmpty())
            place.setVisibility(View.GONE);
        else {
            place.setVisibility(View.VISIBLE);
            place.setText(result.getLocation());
        }

        if (result.getName() == null || result.getName().isEmpty())
            from.setVisibility(View.GONE);
        else {
            from.setVisibility(View.VISIBLE);
            from.setText(result.getName());
        }

        if (result.getComments() == null || result.getComments().isEmpty())
            app.setText("0 Reactions");
        else {
            app.setText(result.getComments() + " Reactions");
            if (!result.getComments().equals("0") && result.getReaction() != null) {
                rv.setVisibility(View.VISIBLE);

                Spannable spannables = new SpannableString(result.getReaction());
                Util.linkifyUrl(spannables, new CustomTabsOnClickListener(getActivity(), mCustomTabActivityHelper));
                userName.setText(spannables);
                userName.setMovementMethod(LinkMovementMethod.getInstance());

                name.setText(result.getFrom());
            } else {
                rv.setVisibility(View.GONE);
            }
        }

        if (result.getReviews().isEmpty() || result.getReviews() == null)
            reviews.setText("0 likes");
        else
            reviews.setText(String.valueOf(Integer.parseInt(result.getReviews())) + " likes");

        if (result.getCategory().isEmpty() || result.getCategory() == null)
            source.setVisibility(View.GONE);
        else {
            source.setVisibility(View.VISIBLE);
            source.setText('#' + result.getCategory());

        }

        final int total = Integer.parseInt(result.getReviews());

        /*viewHolder.linearLayout.setTag(1);
        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {


                    final int status = (Integer) view.getTag();
                    if (status == 1) {
                        upvote(position);
                        viewHolder.reviews.setText(String.valueOf(total + 1) + " likes");
                        viewHolder.upicon.setImageResource(R.drawable.ic_thumb_up_primary_16px);
                        viewHolder.upvote.setTextColor(ContextCompat.getColor(mContext, R.color.rank));
                        view.setTag(0);
                        //pause
                    } else {
                        upvote(position);
                        viewHolder.reviews.setText(String.valueOf(total) + " likes");
                        viewHolder.upicon.setImageResource(R.drawable.ic_thumb_up_black_16px);
                        viewHolder.upvote.setTextColor(ContextCompat.getColor(mContext, R.color.likeText));
                        view.setTag(1);
                    }
                } else {
                    Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });*/
    }

    public class JSONTask extends AsyncTask<String, String, MpModel> {

        private MpModel mpModel;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected MpModel doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            RequestHandler rh = new RequestHandler();
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("query", params[0]);
            try {
                URL url = new URL("https://www.reweyou.in/reweyou/postbyid.php");
                connection = (HttpURLConnection) url.openConnection();

                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(rh.getPostDataString(data));
                wr.flush();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                String finalJson = buffer.toString();
                Log.d("reached", "132323");

                JSONArray parentArray = new JSONArray(finalJson);
                StringBuffer finalBufferedData = new StringBuffer();
                Gson gson = new Gson();
                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    mpModel = gson.fromJson(finalObject.toString(), MpModel.class);
                }

                return mpModel;

                //return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(MpModel result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);

            if (result != null) {
                Log.d("model", String.valueOf(result));

                Log.d("reached", "12323212");
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View inflatedLayout = null;
                int viewType = 0;
                switch (result.getViewType()) {
                    case VIEW_TYPE_IMAGE:
                        inflatedLayout = inflater.inflate(R.layout.item_feed_adapter_image, viewcontainer, false);
                        viewType = 1;
                        break;
                    case VIEW_TYPE_VIDEO:
                        inflatedLayout = inflater.inflate(R.layout.item_feed_adapter_video, viewcontainer, false);
                        viewType = 2;
                        break;
                    case VIEW_TYPE_GIF:
                        inflatedLayout = inflater.inflate(R.layout.item_feed_adapter_image, viewcontainer, false);
                        viewType = 3;
                        break;
                    default:
                        inflatedLayout = inflater.inflate(R.layout.item_feed_adapter_image, viewcontainer, false);
                        break;

                }

                viewcontainer.addView(inflatedLayout);
                switch (viewType) {
                    case 1:
                        setData(result, inflatedLayout, 1);
                        break;
                    case 2:
                        setData(result, inflatedLayout, 2);

                        break;
                    case 3:
                        break;

                }
            }

        }
    }




       /* linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeRequest(getAdapterPosition());
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    Bundle bundle = new Bundle();
                    bundle.putString("myData", messagelist.get(getAdapterPosition()).getImage());
                    bundle.putString("headline", messagelist.get(getAdapterPosition()).getHeadline());
                    Intent in = new Intent(mContext, FullImage.class);
                    in.putExtras(bundle);
                    mContext.startActivity(in);
                } else {
                    Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cv.setDrawingCacheEnabled(true);
                cv.buildDrawingCache();
                bm = cv.getDrawingCache();
                showPopupMenu(overflow);
            }
        });

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    Bundle bundle = new Bundle();
                    bundle.putString("myData", messagelist.get(getAdapterPosition()).getNumber());
                    Intent in = new Intent(mContext, UserProfile.class);
                    in.putExtras(bundle);
                    mContext.startActivity(in);
                } else {
                    Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    Bundle bundle = new Bundle();
                    bundle.putString("myData", messagelist.get(getAdapterPosition()).getNumber());
                    Intent in = new Intent(mContext, UserProfile.class);
                    in.putExtras(bundle);
                    mContext.startActivity(in);
                } else {
                    Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("myData", messagelist.get(getAdapterPosition()).getPostId());
                bundle.putString("headline", messagelist.get(getAdapterPosition()).getHeadline());
                bundle.putString("image", messagelist.get(getAdapterPosition()).getImage());
                Intent in = new Intent(mContext, Comments1.class);
                in.putExtras(bundle);
                mContext.startActivity(in);
            }
        });
        reaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("myData", messagelist.get(getAdapterPosition()).getPostId());
                bundle.putString("headline", messagelist.get(getAdapterPosition()).getHeadline());
                bundle.putString("image", messagelist.get(getAdapterPosition()).getImage());
                Intent in = new Intent(mContext, Comments1.class);
                in.putExtras(bundle);
                mContext.startActivity(in);
            }
        });
        place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session = new UserSessionManager(mContext);
                session.setCityLocation(messagelist.get(getAdapterPosition()).getLocation());
                Intent in = new Intent(mContext, LocationActivity.class);
                mContext.startActivity(in);
            }
        });

        headline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (session.getMobileNumber().equals(messagelist.get(getAdapterPosition()).getNumber())) {
                        editHeadline(getAdapterPosition());
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session = new UserSessionManager(mContext);
                session.setCategory(messagelist.get(getAdapterPosition()).getCategory());
                Intent in = new Intent(mContext, CategoryActivity.class);
                mContext.startActivity(in);
            }
        });
        rv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("myData", messagelist.get(getAdapterPosition()).getPostId());
                bundle.putString("headline", messagelist.get(getAdapterPosition()).getHeadline());
                bundle.putString("image", messagelist.get(getAdapterPosition()).getImage());
                Intent in = new Intent(mContext, Comments1.class);
                in.putExtras(bundle);
                mContext.startActivity(in);
            }
        });


    }*/


}




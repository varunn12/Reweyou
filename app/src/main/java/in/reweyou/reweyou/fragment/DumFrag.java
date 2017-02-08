package in.reweyou.reweyou.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import in.reweyou.reweyou.R;
import in.reweyou.reweyou.Raw;

/**
 * Created by master on 9/2/17.
 */

public class DumFrag extends Fragment {

    private TextView from;
    private TextView source;
    private TextView who;
    private TextView head;
    private TextView name;
    private TextView username;
    private ImageView image;
    private int fragmentCategory = -2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentCategory = getArguments().getInt("a", -1);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.aaaaitem_feed_adapter_image, container, false);
        from = (TextView) layout.findViewById(R.id.from);
        source = (TextView) layout.findViewById(R.id.source);
        who = (TextView) layout.findViewById(R.id.Who);
        head = (TextView) layout.findViewById(R.id.head);
        name = (TextView) layout.findViewById(R.id.name);
        username = (TextView) layout.findViewById(R.id.userName);
        image = (ImageView) layout.findViewById(R.id.image);
        try {
            from.setText(Raw.list.get(fragmentCategory).getName());
            source.setText(Raw.list.get(fragmentCategory).getCategory());
            who.setText(Raw.list.get(fragmentCategory).getHeadline());
            head.setText(Raw.list.get(fragmentCategory).getHead());
            name.setText(Raw.list.get(fragmentCategory).getFrom());
            username.setText(Raw.list.get(fragmentCategory).getReaction());

            Glide.with(getActivity()).load(Raw.list.get(fragmentCategory).getImage()).into(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return layout;
    }
}

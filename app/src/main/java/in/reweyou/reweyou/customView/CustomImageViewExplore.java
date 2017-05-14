package in.reweyou.reweyou.customView;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import in.reweyou.reweyou.utils.Utils;

/**
 * Created by master on 13/11/16.
 */

public class CustomImageViewExplore extends AppCompatImageView {

    private static final String TAG = CustomImageViewExplore.class.getName();

    public CustomImageViewExplore(Context context) {
        super(context);
    }

    public CustomImageViewExplore(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageViewExplore(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        setMeasuredDimension(width, width - Utils.convertpxFromDp(16));

    }
}

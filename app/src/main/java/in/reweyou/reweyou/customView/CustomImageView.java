package in.reweyou.reweyou.customView;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by master on 13/11/16.
 */

public class CustomImageView extends AppCompatImageView {

    private static final String TAG = CustomImageView.class.getName();
    private float mScale = 1f;
    private int mHeight = -1;

    public void setDimensions(int width, int height) {
        this.mHeight = height;
        mScale = height / width;
    }

    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        Log.d(TAG, "onMeasure: scaledheight: "+(width*mScale)+" originalHeight: "+mHeight);
        if ((width * mScale) > mHeight)
            setMeasuredDimension(width, mHeight);
        else
            setMeasuredDimension(width, (int) (width * mScale));
    }
}

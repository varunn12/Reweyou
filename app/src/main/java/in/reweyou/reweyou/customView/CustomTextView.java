package in.reweyou.reweyou.customView;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by master on 19/1/17.
 */

public class CustomTextView extends AppCompatTextView {


    private int mWidth;

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("aaa", "onMeasure: called" + getMeasuredWidth());
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Log.d("abc", "onMeasure: " + getLineCount());
       /* int width = (int) Math.ceil((float) mWidth / 14);
        Log.d("aaa", "onMeasure: "+width);*/
       /* setMeasuredDimension(width, getMeasuredHeight());*/
    }

    public void setManualHeight(int width) {
        this.mWidth = width;

    }
}

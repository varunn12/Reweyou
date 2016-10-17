package in.reweyou.reweyou.classes;


import android.annotation.SuppressLint;
import android.text.style.URLSpan;
import android.view.View;

@SuppressLint("ParcelCreator")
public class CustomClickURLSpan extends URLSpan {
    private OnClickListener mOnClickListener;

    public CustomClickURLSpan(String url) {
        super(url);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @Override
    public void onClick(View widget) {
        if (mOnClickListener == null) {
            super.onClick(widget);
        } else {
            mOnClickListener.onClick(widget, getURL());
        }
    }

    public interface OnClickListener {
        void onClick(View view, String url);
    }
}
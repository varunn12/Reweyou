package in.reweyou.reweyou.classes;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Reweyou on 1/19/2016.
 */
public class ScrollingFABBehavior extends FloatingActionButton.Behavior {

    private static final String TAG = "ScrollingFABBehavior";

    public ScrollingFABBehavior(Context context, AttributeSet attrs) {
        super(context,attrs);
    }


    public boolean onStartNestedScroll(CoordinatorLayout parent, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return true;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return dependency instanceof RecyclerView;

    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout,
                               FloatingActionButton child, View target, int dxConsumed,
                               int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        // TODO Auto-generated method stub
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        // Log.e(TAG, "onNestedScroll called");
        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
            // Log.e(TAG, "child.hide()");
            child.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                @Override
                public void onHidden(FloatingActionButton fab) {
                    super.onHidden(fab);
                    fab.setVisibility(View.INVISIBLE);
                }
            });
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            // Log.e(TAG, "child.show()");
            child.show();
        }
    }
}

package in.reweyou.reweyou.classes;

import android.app.Activity;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.view.View;

import java.lang.ref.WeakReference;

public class CustomTabsOnClickListener implements CustomClickURLSpan.OnClickListener {
    private WeakReference<Activity> mActivityWeakReference;
    private WeakReference<CustomTabActivityHelper> mCustomTabActivityHelperWeakReference;

    public CustomTabsOnClickListener(Activity hostActivity,
                                     CustomTabActivityHelper customTabActivityHelper) {
        mActivityWeakReference = new WeakReference<>(hostActivity);
        mCustomTabActivityHelperWeakReference = new WeakReference<>(customTabActivityHelper);
    }

    @Override
    public void onClick(View view, String url) {
        Activity activity = mActivityWeakReference.get();
        CustomTabActivityHelper customTabActivityHelper =
                mCustomTabActivityHelperWeakReference.get();
        if (activity != null && customTabActivityHelper != null) {
            CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder(null)
                    .build();
            customTabsIntent.intent.setPackage(
                    CustomTabsHelper.getPackageNameToUse(view.getContext()));
            customTabsIntent.launchUrl(activity, Uri.parse(url));
        }
    }
}

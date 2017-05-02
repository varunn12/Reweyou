package in.reweyou.reweyou;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import in.reweyou.reweyou.classes.AnalyticsTracker;
import in.reweyou.reweyou.utils.Utils;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * Created by Reweyou on 2/25/2016.
 */
public class ApplicationClass extends Application {
    public static final String TAG = ApplicationClass.class
            .getSimpleName();
    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
            okhttp3.Response originalResponse = chain.proceed(chain.request());
            Log.d(TAG, "intercept: " + originalResponse);
            return originalResponse.newBuilder()
                    .header("Cache-Control", "max-age=" + 86400 * 30 + ",max-stale=" + 86400 * 30)
                    .build();
        }
    };
    private static ApplicationClass mInstance;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static synchronized ApplicationClass getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        Utils.setBackgroundColor();
        AnalyticsTracker.initialize(this);
        AnalyticsTracker.getInstance().get(AnalyticsTracker.Target.APP);

       /* if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...*/

        final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        AndroidNetworking.initialize(getApplicationContext(), okHttpClient);


    }

    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTracker analyticsTrackers = AnalyticsTracker.getInstance();
        return analyticsTrackers.get(AnalyticsTracker.Target.APP);
    }

    /***
     * Tracking screen view
     *
     * @param screenName screen name to be displayed on GA dashboard
     */
    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    /***
     * Tracking exception
     *
     * @param e exception to be tracked
     */
    public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(
                            new StandardExceptionParser(this, null)
                                    .getDescription(Thread.currentThread().getName(), e))
                    .setFatal(false)
                    .build()
            );
        }
    }

    /***
     * Tracking event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */
    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }

}

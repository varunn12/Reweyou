package in.reweyou.reweyou.utils;

import android.content.Context;

import java.util.Calendar;
import java.util.Random;

/**
 * Created by master on 2/5/17.
 */

public class Utils {
    public static boolean isNight;
    public static int backgroundCode;
    private static int scalefactor;

    public static void setDayNightBoolean() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        boolean isNights = hour < 6 || hour > 18;
        if (isNights) {
            isNight = isNights;
        }
    }

    public static void setBackgroundColor() {
        Random rand = new Random();
        backgroundCode = rand.nextInt(4);
    }

    public static void setpxFromDp(final Context context) {
        int scalefacto = (int) (context.getResources().getDisplayMetrics().density);
        scalefactor = scalefacto;
    }

    public static int convertpxFromDp(int dp) {
        return scalefactor * dp;
    }

}

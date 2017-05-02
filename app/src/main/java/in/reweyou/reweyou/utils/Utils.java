package in.reweyou.reweyou.utils;

import java.util.Calendar;
import java.util.Random;

/**
 * Created by master on 2/5/17.
 */

public class Utils {
    public static boolean isNight;
    public static int backgroundCode;

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
}

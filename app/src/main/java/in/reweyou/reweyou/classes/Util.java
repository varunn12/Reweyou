package in.reweyou.reweyou.classes;

import android.text.Spannable;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    private static final Pattern URL_PATTERN = Pattern.compile("((http|https|rstp):\\/\\/\\S*)");

    public static void linkifyUrl(
            Spannable spannable, CustomClickURLSpan.OnClickListener onClickListener) {
        Matcher m = URL_PATTERN.matcher(spannable);
        while (m.find()) {
            String url = spannable.toString().substring(m.start(), m.end());
            CustomClickURLSpan urlSpan = new CustomClickURLSpan(url);
            urlSpan.setOnClickListener(onClickListener);
            spannable.setSpan(urlSpan, m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
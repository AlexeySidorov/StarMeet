package starmeet.convergentmobile.com.starmeet.Helpers;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by alexeysidorov on 21.03.2018.
 */

public class DateHelper {

    public static String UtcToDate(Date date, String format) {
        @SuppressLint("SimpleDateFormat")
        DateFormat pstFormat = new SimpleDateFormat(format);
        pstFormat.setTimeZone(TimeZone.getDefault());
        return pstFormat.format(date);
    }
}

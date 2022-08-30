package starmeet.convergentmobile.com.starmeet.Helpers;

import android.content.Context;

/**
 * Created by alexeysidorov on 26.03.2018.
 */

public class DpPxHelper {

    public static float dpToPx(Context context, int dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static float pxToDp(Context context, int px) {
        return px / context.getResources().getDisplayMetrics().density;
    }
}

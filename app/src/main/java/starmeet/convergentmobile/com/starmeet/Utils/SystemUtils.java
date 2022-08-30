package starmeet.convergentmobile.com.starmeet.Utils;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import static android.content.Context.WINDOW_SERVICE;

public class SystemUtils {
    public static void adjustFontScale(Context context, Configuration configuration) {
        if (configuration.fontScale != 1) {
            configuration.fontScale = 1;
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);

            if(configuration.densityDpi >= 485) //for 6 inch device OR for 538 ppi
                configuration.densityDpi = 500; //decrease "display size" by ~30
            else if(configuration.densityDpi >= 300) //for 5.5 inch device OR for 432 ppi
                configuration.densityDpi = 400; //decrease "display size" by ~30
            else if(configuration.densityDpi >= 100) //for 4 inch device OR for 233 ppi
                configuration.densityDpi = 200;

            if (wm != null) {
                wm.getDefaultDisplay().getMetrics(metrics);
            }

            metrics.scaledDensity = configuration.fontScale * metrics.density;
            context.getResources().updateConfiguration(configuration, metrics);
        }
    }
}

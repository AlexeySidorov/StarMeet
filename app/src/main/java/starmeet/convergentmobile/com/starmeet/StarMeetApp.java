package starmeet.convergentmobile.com.starmeet;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import io.fabric.sdk.android.Fabric;
import retrofit2.Retrofit;
import starmeet.convergentmobile.com.starmeet.Base.BaseActivity;
import starmeet.convergentmobile.com.starmeet.Helpers.ApproachingEventHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.DatabaseHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.HelperFactory;
import starmeet.convergentmobile.com.starmeet.Rest.RestClients;
import starmeet.convergentmobile.com.starmeet.Rest.RestMethods;

import static starmeet.convergentmobile.com.starmeet.Utils.SystemUtils.adjustFontScale;

/**
 * Update by alexeysidorov on 26.02.2018.
 */

public class StarMeetApp extends Application {
    private static Application instance;
    private static RestMethods restApiRequest;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        Crashlytics catalyticKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();

        Fabric.with(this, catalyticKit, new Answers(), new Crashlytics());

        HelperFactory.setHelper(this);
        DatabaseHelper helper = HelperFactory.getHelper();

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:757251946020:android:682c47e68029ce12")
                .build();

        FirebaseApp.initializeApp(this, options, "starmeet.convergentmobile.com.starmeet");

        restApiRequest = RestClients.initRestService(getBaseContext());
    }

    //Get rest api
    public static RestMethods getApi() {
        return restApiRequest;
    }

    //Get http client
    public static Retrofit getHttpClient() {
        return RestClients.getHttpClient();
    }

    @Override
    public void onTerminate() {
        HelperFactory.releaseHelper();
        ApproachingEventHelper.getInstance().StopApproachingTimer();
        super.onTerminate();
    }

    private static BaseActivity mCurrentActivity = null;

    public static BaseActivity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(BaseActivity mCurrentActivity) {
        StarMeetApp.mCurrentActivity = mCurrentActivity;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Configuration configuration = new Configuration(newConfig);
        adjustFontScale(getApplicationContext(), configuration);
    }
}

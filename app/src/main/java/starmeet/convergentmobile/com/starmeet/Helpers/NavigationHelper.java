package starmeet.convergentmobile.com.starmeet.Helpers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;

import starmeet.convergentmobile.com.starmeet.Callbaks.CallbackNotification;
import starmeet.convergentmobile.com.starmeet.Models.ActivityNextType;
import starmeet.convergentmobile.com.starmeet.Models.NavigationModel;
import starmeet.convergentmobile.com.starmeet.MyContext;

public class NavigationHelper {

    private static NavigationHelper sInstance = null;
    private HashMap<ActivityNextType, Class> activities;
    private NavigationModel model;
    private CallbackNotification notificationListener;

    public static NavigationHelper getInstance() {
        if (sInstance == null) {
            sInstance = new NavigationHelper();
        }

        return sInstance;
    }

    public void navigation(Context context, ActivityNextType type) {
        AppCompatActivity currentActivity = ((AppCompatActivity) context);

        if (activities.get(ActivityNextType.Fragment) != null) {
            FragmentManager fr = currentActivity.getSupportFragmentManager();
            if (fr != null) {
                if (fr.getBackStackEntryCount() > 1)
                    fr.popBackStackImmediate();
            }

            return;
        }

        if (activities.get(ActivityNextType.None) != null || activities.size() == 1) {
            currentActivity.finish();
            currentActivity.onBackPressed();

            if(notificationListener != null)
              notificationListener.onNotification(model);

            return;
        }

        Intent i = new Intent(currentActivity, activities.get(type));
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        currentActivity.startActivity(i);
        currentActivity.finish();

        if (MyContext.getInstance().PreviousActivity != null &&
                MyContext.getInstance().PreviousActivity != currentActivity)
            MyContext.getInstance().PreviousActivity.finish();

        MyContext.getInstance().PreviousActivity = null;

        if (notificationListener != null)
            notificationListener.onNotification(model);
    }

    public void setNextActivity(HashMap<ActivityNextType, Class> activities, NavigationModel model) {
        this.activities = activities;
        this.model = model;
    }

    public void setNotificationListener(CallbackNotification notificationListener) {
        this.notificationListener = notificationListener;
    }
}


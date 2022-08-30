package starmeet.convergentmobile.com.starmeet.Listners;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import starmeet.convergentmobile.com.starmeet.Activites.AuthActivity;
import starmeet.convergentmobile.com.starmeet.Activites.CharitiesActivity;
import starmeet.convergentmobile.com.starmeet.Activites.FaqActivity;
import starmeet.convergentmobile.com.starmeet.Activites.MainActivity;
import starmeet.convergentmobile.com.starmeet.Activites.ProfileCelebrityActivity;
import starmeet.convergentmobile.com.starmeet.Activites.ProfileUserActivity;
import starmeet.convergentmobile.com.starmeet.Activites.StarChatActivity;
import starmeet.convergentmobile.com.starmeet.Activites.StarEventsActivity;
import starmeet.convergentmobile.com.starmeet.Helpers.ApproachingEventHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.DatabaseHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.HelperFactory;
import starmeet.convergentmobile.com.starmeet.Helpers.NavigationHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.UpdateHelper;
import starmeet.convergentmobile.com.starmeet.Models.ActivityNextType;
import starmeet.convergentmobile.com.starmeet.MyContext;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Response.ListResponse;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;

import static starmeet.convergentmobile.com.starmeet.Models.NavigationModel.None;

public class MyOnNavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {
    private int selfItem;
    private DrawerLayout mDrawerLayout;
    private Context ctx;

    public MyOnNavigationItemSelectedListener(Context ctx, int selfItem, DrawerLayout mDrawerLayout) {
        this.selfItem = selfItem;
        this.mDrawerLayout = mDrawerLayout;
        this.ctx = ctx;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Intent i;
        if (selfItem != -1) {
            switch (item.getItemId()) {
                case R.id.all_events: {
                    i = new Intent(ctx, MainActivity.class);
                    i.putExtra("Event", 0);
                    i.putExtra("GroupFilter", false);
                    ctx.startActivity(i);
                    mDrawerLayout.closeDrawers();
                    break;
                }
                case R.id.star_chat: {
                    ListResponse eventType = null;

                    if (MyContext.getInstance().eventTypes != null) {
                        for (int index = 0; index < MyContext.getInstance().eventTypes.size(); index++)
                            if (Objects.equals(MyContext.getInstance().eventTypes.get(index).title, "StarChat"))
                                eventType = MyContext.getInstance().eventTypes.get(index);
                    }

                    i = new Intent(ctx, StarChatActivity.class);
                    i.putExtra("Event", eventType == null ? 0 : eventType.id);
                    i.putExtra("GroupFilter", eventType != null);
                    ctx.startActivity(i);
                    mDrawerLayout.closeDrawers();
                    break;
                }
                case R.id.star_events: {
                    ListResponse eventType = null;

                    if (MyContext.getInstance().eventTypes != null) {
                        for (int index = 0; index < MyContext.getInstance().eventTypes.size(); index++)
                            if (Objects.equals(MyContext.getInstance().eventTypes.get(index).title, "StarLottery"))
                                eventType = MyContext.getInstance().eventTypes.get(index);
                    }

                    i = new Intent(ctx, StarEventsActivity.class);
                    i.putExtra("Event", eventType == null ? 0 : eventType.id);
                    i.putExtra("GroupFilter", eventType != null);
                    ctx.startActivity(i);
                    mDrawerLayout.closeDrawers();
                    break;
                }
                case R.id.charities: {
                    i = new Intent(ctx, CharitiesActivity.class);
                    ctx.startActivity(i);
                    mDrawerLayout.closeDrawers();
                    break;
                }
                case R.id.my_account: {
                    if (!MyContext.getInstance().isAuthUser) {
                        HashMap<ActivityNextType, Class> activities = new HashMap<>();
                        activities.put(ActivityNextType.Activity1, ProfileUserActivity.class);
                        activities.put(ActivityNextType.Activity2, ProfileCelebrityActivity.class);
                        NavigationHelper.getInstance().setNextActivity(activities, None);
                        ctx.startActivity(new Intent(ctx, AuthActivity.class));
                    } else {
                        i = new Intent(ctx, MyContext.getInstance().userRole == 1
                                ? ProfileUserActivity.class : ProfileCelebrityActivity.class);
                        ctx.startActivity(i);
                    }

                    mDrawerLayout.closeDrawers();
                    break;
                }
                case R.id.faq: {
                    i = new Intent(ctx, FaqActivity.class);
                    ctx.startActivity(i);
                    mDrawerLayout.closeDrawers();
                    break;
                }
                case R.id.sign_out: {
                    Call<Void> result = StarMeetApp.getApi().logout();
                    result.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {

                        }
                    });

                    HelperFactory.setHelper(ctx);
                    DatabaseHelper helper = HelperFactory.getHelper();

                    try {
                        helper.getAccountService().clearAuth();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    MyContext.getInstance().isAuthUser = false;
                    MyContext.getInstance().userRole = 0;

                    ApproachingEventHelper.getInstance().setApproachingEvent(null);
                    ApproachingEventHelper.getInstance().stopTimeEvent();

                    i = new Intent(ctx, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("Event", 0);
                    ctx.startActivity(i);

                    UpdateHelper.getInstance().update();

                    mDrawerLayout.closeDrawers();
                    break;
                }
                default:
                    throw new IllegalArgumentException("No such activity or fragment with id: " + item.getItemId());
            }
        }
        return false;
    }
}

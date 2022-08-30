package starmeet.convergentmobile.com.starmeet.Activites;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import java.sql.SQLException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import starmeet.convergentmobile.com.starmeet.Helpers.ApproachingEventHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.DatabaseHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.HelperFactory;
import starmeet.convergentmobile.com.starmeet.Models.Account;
import starmeet.convergentmobile.com.starmeet.Models.Country;
import starmeet.convergentmobile.com.starmeet.MyContext;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Response.ListResponse;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;


public class SplashActivity extends AppCompatActivity {

    private DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        loadEvents();

        HelperFactory.setHelper(this);
        helper = HelperFactory.getHelper();

        try {
            Account account = helper.getAccountService().getLastAccount();
            MyContext.getInstance().isAuthUser = account != null && account.getAuth();
            MyContext.getInstance().userRole = account == null ? 0 : account.getRoleId();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loading() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                i.putExtra("Event", 0);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        }, 0);
    }

    @SuppressLint("ShowToast")
    private void loadEvents() {
        retrofit2.Call<ArrayList<ListResponse>> response = StarMeetApp.getApi().getEventCategories();
        response.enqueue(new Callback<ArrayList<ListResponse>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<ArrayList<ListResponse>> call,
                                   @NonNull Response<ArrayList<ListResponse>> response) {
                if (response.code() == 200 && response.body() != null) {
                    MyContext.getInstance().events = response.body();
                } else
                    LogUtil.logE("All events", response.code() + ". Events is empty");

                loadFilter();
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<ArrayList<ListResponse>> call, @NonNull Throwable t) {
                LogUtil.logE("All events", t.getMessage(), t);
            }
        });
    }

    private void loadFilter() {
        retrofit2.Call<ArrayList<ListResponse>> response = StarMeetApp.getApi().getEventTypes();
        response.enqueue(new Callback<ArrayList<ListResponse>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<ArrayList<ListResponse>> call,
                                   @NonNull Response<ArrayList<ListResponse>> response) {
                if (response.code() == 200 && response.body() != null) {

                    ArrayList<ListResponse> eventTypes = response.body();
                    ListResponse event = new ListResponse();
                    event.addNewEvent("All events");
                    MyContext.getInstance().events.add(0, event);

                    for (int index = 0; index < eventTypes.size(); index++) {
                        int listIndex = index + 1;

                        if (MyContext.getInstance().eventTypes == null)
                            MyContext.getInstance().eventTypes = new ArrayList<>();

                        MyContext.getInstance().eventTypes.add(eventTypes.get(index));
                        MyContext.getInstance().events.add(listIndex, eventTypes.get(index));
                    }

                } else
                    LogUtil.logE("All event types", response.code() + ". Event types is empty");

                loadCountries();
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<ArrayList<ListResponse>> call, @NonNull Throwable t) {
                LogUtil.logE("All event types", t.getMessage(), t);
            }
        });
    }

    private void loadCountries() {
        MyContext.getInstance().countries = new ArrayList<Country>();

        try {
            boolean isCountry = helper.getCountryService().getIsCounry();
            if (isCountry) {
                MyContext.getInstance().countries.addAll(helper.getCountryService().getCountries());
                loading();

                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        retrofit2.Call<ArrayList<Country>> response = StarMeetApp.getApi().getCountries();
        response.enqueue(new Callback<ArrayList<Country>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Country>> call,
                                   @NonNull Response<ArrayList<Country>> response) {

                if (response.code() == 200 && response.body() != null) {

                    ArrayList<Country> countries = response.body();

                    if (countries.size() > 0) {
                        try {
                            helper.getCountryService().addCountry(countries);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        MyContext.getInstance().countries.addAll(countries);
                    }

                } else
                    LogUtil.logE("All countries", response.code() + ". Countries is empty");

                loading();
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Country>> call, @NonNull Throwable t) {
                LogUtil.logE("All countries", t.getMessage(), t);
            }
        });
    }
}

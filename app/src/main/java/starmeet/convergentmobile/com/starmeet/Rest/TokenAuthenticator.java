package starmeet.convergentmobile.com.starmeet.Rest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Callback;
import starmeet.convergentmobile.com.starmeet.Activites.AuthActivity;
import starmeet.convergentmobile.com.starmeet.Callbaks.CallbackFunc;
import starmeet.convergentmobile.com.starmeet.Helpers.DatabaseHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.HelperFactory;
import starmeet.convergentmobile.com.starmeet.Helpers.LiveNetworkMonitorHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.NavigationHelper;
import starmeet.convergentmobile.com.starmeet.Models.AccessToken;
import starmeet.convergentmobile.com.starmeet.Models.Account;
import starmeet.convergentmobile.com.starmeet.Models.ActivityNextType;
import starmeet.convergentmobile.com.starmeet.Models.NavigationModel;
import starmeet.convergentmobile.com.starmeet.MyContext;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Services.AccountService;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;
import starmeet.convergentmobile.com.starmeet.Utils.SyncResult;

/**
 * Update by alexeysidorov on 26.02.2018.
 */

public class TokenAuthenticator implements Authenticator {

    private final DatabaseHelper helper;
    private LiveNetworkMonitorHelper monitor;
    private boolean isRefreshTokenUpdate;
    private Context context;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    public TokenAuthenticator(Context context) {
        this.context = context;
        HelperFactory.setHelper(context);
        helper = HelperFactory.getHelper();
        isRefreshTokenUpdate = false;
        monitor = new LiveNetworkMonitorHelper(context);
    }

    @Override
    public Request authenticate(@NonNull Route route, @NonNull final Response response) throws IOException {

        try {
            Account account = helper.getAccountService().getLastAccount();
            if (account == null || account.getRefreshToken().isEmpty()) {
                ShowAuthScreen();
                return null;
            }

            String userRefreshToken = account.getRefreshToken();
            String clientKey = context.getResources().
                    getString(R.string.client_key);
            String clientSecret = context.getResources().
                    getString(R.string.client_secret);

            final SyncResult<Request> syncResult = new SyncResult();

            if (!monitor.isConnected()) {
                monitor = new LiveNetworkMonitorHelper(context);
                return null;
            }

            if (!isRefreshTokenUpdate) {
                isRefreshTokenUpdate = true;
                refreshToken(userRefreshToken, clientKey, clientSecret, new CallbackFunc<AccessToken>() {

                    @Override
                    public void onSuccess(AccessToken result) {
                        syncResult.setResult(response.request().newBuilder()
                                .header("Authorization", "Bearer " + result.accessToken)
                                .build());
                        isRefreshTokenUpdate = false;
                    }

                    @Override
                    public void OnError(String error) {
                        try {
                            ShowAuthScreen();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            LogUtil.logE("StarMeet error ", e.getMessage());
                        }

                        isRefreshTokenUpdate = false;
                        syncResult.setResult(null);
                    }
                });

                return syncResult.getResult();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            LogUtil.logE("StarMeet error ", e.getMessage());
            isRefreshTokenUpdate = false;
            return null;
        }

        return null;
    }

    @SuppressWarnings("deprecation")
    private void ShowAuthScreen() throws SQLException {
        if (MyContext.getInstance().isLogout) return;

        helper.getAccountService().clearAuth();

        HashMap<ActivityNextType, Class> activities = new HashMap<>();
        activities.put(ActivityNextType.None, AppCompatActivity.class);
        NavigationHelper.getInstance().setNextActivity(activities, NavigationModel.None);
        Intent intent = new Intent(context, AuthActivity.class);
        context.startActivity(intent);
    }

    private void refreshToken(final String refresh, String clientKey, String clientSecret,
                              final CallbackFunc<AccessToken> result) throws IOException, SQLException {

        SharedPreferences sharedPrefs =  this.context.getSharedPreferences(
                PREF_UNIQUE_ID, Context.MODE_PRIVATE);
        String uuid = sharedPrefs.getString("PREF_UNIQUE_ID", "");

        retrofit2.Call<AccessToken> newToken = StarMeetApp.getApi().refreshUserToken("refresh_token",
                clientKey, clientSecret, refresh, uuid);

        newToken.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<AccessToken> call, @NonNull retrofit2.Response<AccessToken> response) {
                if (response.isSuccessful()) {
                    AccessToken token = response.body();

                    if (token != null) {
                        try {

                            AccountService accountService = helper.getAccountService();
                            Account account = accountService.getLastAccount();
                            account.setToken(token.accessToken);
                            account.setRefreshToken(token.refreshToken);
                            account.setExpiresIn(token.expiresIn);
                            account.setAuth(true);
                            accountService.addOrUpdateAccount(account);

                        } catch (SQLException e) {
                            Handler handlerLog = new Handler(context.getMainLooper());
                            Runnable runnableLog = new Runnable() {
                                @Override
                                public void run() {
                                    LogUtil.logE("StarMeet error ", e.getMessage());
                                }
                            };

                            handlerLog.post(runnableLog);
                            e.printStackTrace();
                            result.OnError(e.getMessage());
                        }

                        result.onSuccess(token);

                        Handler handlerLog = new Handler(context.getMainLooper());
                        Runnable runnableLog = new Runnable() {
                            @Override
                            public void run() {
                ;
                            }
                        };
                        handlerLog.post(runnableLog);
                    }

                } else {

                    try {
                        ShowAuthScreen();
                    } catch (SQLException e) {

                        Handler handlerLog = new Handler(context.getMainLooper());
                        Runnable runnableLog = new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.logE("StarMeet error ", e.getMessage());
                            }
                        };
                        handlerLog.post(runnableLog);

                        e.printStackTrace();
                        result.OnError(e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<AccessToken> call, @NonNull Throwable t) {
                LogUtil.logE("StarMeet throwable refresh token: ", t.getMessage());
                try {
                    ShowAuthScreen();
                } catch (SQLException e) {

                    Handler handlerLog = new Handler(context.getMainLooper());
                    Runnable runnableLog = new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.logE("StarMeet error ", e.getMessage());
                        }
                    };
                    handlerLog.post(runnableLog);

                    e.printStackTrace();
                    result.OnError(e.getMessage());
                }
            }
        });
    }
}

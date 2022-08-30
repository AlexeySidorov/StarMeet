package starmeet.convergentmobile.com.starmeet.Rest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
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
import starmeet.convergentmobile.com.starmeet.Services.DialogService;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;
import starmeet.convergentmobile.com.starmeet.Utils.SyncResult;

/**
 * Update by alexeysidorov on 26.02.2018.
 */

public class TokenInterceptor implements Interceptor {
    private final DatabaseHelper helper;
    private final LiveNetworkMonitorHelper monitor;
    Context context;

    public TokenInterceptor(Context ctx) {
        this.context = ctx;
        HelperFactory.setHelper(ctx);
        helper = HelperFactory.getHelper();
        monitor = new LiveNetworkMonitorHelper(ctx);
    }

    @SuppressLint("ShowToast")
    @Override
    public synchronized Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder();

        Account account = null;
        try {
            account = helper.getAccountService().getLastAccount();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (account != null && !account.getToken().isEmpty()) {
            builder.header("Authorization", "Bearer " + account.getToken());
        }

        Request request = builder.build();

        if (!monitor.isConnected()) {
            Handler handler = new Handler(context.getMainLooper());
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "No network available, please check your WiFi or Data connection",
                            Toast.LENGTH_LONG).show();
                }
            };

            handler.post(runnable);

            request = request.newBuilder().header("Cache-Control",
                    "public, only-if-cached, max-stale=" + "60  60  24").build();
            Response response = chain.proceed(request);
            return response;
        }

        Response response = chain.proceed(request);

        assert account != null;
        if (response.code() == 401 && account.getAuth()) {
            try {
                helper.getAccountService().clearAuth();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        Handler handlerLog = new Handler(context.getMainLooper());
        Runnable runnableLog = new Runnable() {
            @Override
            public void run() {
                LogUtil.logD("Edge Http Code: ", String.valueOf(response.code()));
            }
        };

        handlerLog.post(runnableLog);

        return response;
    }
}

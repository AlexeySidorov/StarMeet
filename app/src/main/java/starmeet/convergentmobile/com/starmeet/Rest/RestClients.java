package starmeet.convergentmobile.com.starmeet.Rest;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.concurrent.TimeUnit;


import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import starmeet.convergentmobile.com.starmeet.Adapters.ImprovedDateTypeAdapter;
import starmeet.convergentmobile.com.starmeet.R;

import static okhttp3.internal.platform.Platform.INFO;

/**
 * Update by xaycc on 03.03.2018.
 */

public class RestClients {

    private static Retrofit retrofit = null;

    private static Retrofit getInstance(Context context) {
        if (retrofit == null) {
            TokenAuthenticator tokenAuthenticator = new TokenAuthenticator(context);
            TokenInterceptor tokenInterceptor = new TokenInterceptor(context);

            Dispatcher dispatcher = new Dispatcher();
            dispatcher.setMaxRequests(1);

            HttpLoggingInterceptor.Logger CUSTOM = new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(@NonNull String message) {
                    Platform.get().log(INFO, "OkHttp-Log： " + message, null);
                }
            };

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(CUSTOM);
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder okClient = UnsafeOkHttpClientBuilder.getUnsafeOkHttpClientBuilder();

            okClient.connectTimeout(60, TimeUnit.SECONDS);
            okClient.readTimeout(40, TimeUnit.SECONDS);
            okClient.writeTimeout(45, TimeUnit.SECONDS);
            okClient.authenticator(tokenAuthenticator);
            okClient.addInterceptor(loggingInterceptor);
            okClient.addInterceptor(tokenInterceptor);
            okClient.dispatcher(dispatcher);
            OkHttpClient client = okClient.build();

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(Date.class, new ImprovedDateTypeAdapter())
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(context.getResources().getString(R.string.rest_api_url))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
        }

        return retrofit;
    }

    public static Retrofit getHttpClient() {
        return retrofit;
    }

    //Init http client
    public static RestMethods initRestService(Context context) {
        return getInstance(context).create(RestMethods.class);
    }
}
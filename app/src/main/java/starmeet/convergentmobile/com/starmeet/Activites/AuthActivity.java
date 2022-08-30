package starmeet.convergentmobile.com.starmeet.Activites;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import starmeet.convergentmobile.com.starmeet.Base.BaseActivity;
import starmeet.convergentmobile.com.starmeet.Callbaks.DialogCallback;
import starmeet.convergentmobile.com.starmeet.Fragments.CustomProgressDialog;
import starmeet.convergentmobile.com.starmeet.Helpers.DatabaseHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.HelperFactory;
import starmeet.convergentmobile.com.starmeet.Helpers.NavigationHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.UpdateHelper;
import starmeet.convergentmobile.com.starmeet.Models.AccessToken;
import starmeet.convergentmobile.com.starmeet.Models.Account;
import starmeet.convergentmobile.com.starmeet.Models.ActivityNextType;
import starmeet.convergentmobile.com.starmeet.Models.PushModel;
import starmeet.convergentmobile.com.starmeet.MyContext;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Services.AccountService;
import starmeet.convergentmobile.com.starmeet.Services.DialogService;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;

/**
 * Update by alexeysidorov on 04.03.2018.
 */

public class AuthActivity extends BaseActivity implements View.OnClickListener {

    private MaterialEditText email;
    private MaterialEditText password;
    private AppCompatButton loginButton;
    private AppCompatTextView forgot;
    private AppCompatTextView createAccount;
    private AppCompatTextView registry;
    private DatabaseHelper helper;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLayout(R.layout.activity_auth);
        setOnlyMyViews(true);
        setToolBarMenuButton(false);
        setColorToolbarMainButton(R.color.colorAccent);
        super.onCreate(savedInstanceState);
        setTitle("");

        MyContext.getInstance().PreviousActivity = this;

        HelperFactory.setHelper(AuthActivity.this);
        helper = HelperFactory.getHelper();
    }

    @Override
    protected void initViews2() {
        initView();
        initClick();
    }

    private void initView() {
        email = findViewById(R.id.login);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        forgot = findViewById(R.id.forgot_password);
        createAccount = findViewById(R.id.create_account);
        registry = findViewById(R.id.registry);
    }

    private void initClick() {
        loginButton.setOnClickListener(this);
        forgot.setOnClickListener(this);
        createAccount.setOnClickListener(this);
        registry.setOnClickListener(this);
    }

    @SuppressLint("HardwareIds")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button: {
                if (!isValid()) return;

                final CustomProgressDialog progress = DialogService.ProgressDialog(this, "Please wait...");

                SharedPreferences sharedPrefs = this.getSharedPreferences(
                        PREF_UNIQUE_ID, Context.MODE_PRIVATE);
                String uuid = sharedPrefs.getString("PREF_UNIQUE_ID", "");

                Call<AccessToken> result = StarMeetApp.getApi().authUser("password",
                        this.getResources().getString(R.string.client_key),
                        this.getResources().getString(R.string.client_secret), email.getText().toString(),
                        password.getText().toString(), uuid);

                result.enqueue(new Callback<AccessToken>() {
                    @Override
                    public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {
                        if (response.code() == 200 && response.body() != null) {

                            AccessToken token = response.body();
                            if (token == null) return;

                            try {
                                setSaveAccount(email.getText().toString(), password.getText().toString(), token);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                progress.dismiss();
                                DialogService.MessageDialog(AuthActivity.this,
                                        "Error", "Invalid login or password", "OK", null);
                                return;
                            }

                            progress.dismiss();

                            sendPudhToken();

                            MyContext.getInstance().userRole = token.userRole;
                            MyContext.getInstance().isAuthUser = true;

                            NavigationHelper.getInstance().navigation(AuthActivity.this,
                                    token.userRole == 1 ?
                                            ActivityNextType.Activity1 : ActivityNextType.Activity2);

                            UpdateHelper.getInstance().update();
                            finish();

                        } else {
                            MyContext.getInstance().isAuthUser = false;
                            progress.dismiss();
                            DialogService.MessageDialog(AuthActivity.this,
                                    "Error", "Invalid login or password", "OK", null);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {
                        progress.dismiss();
                        MyContext.getInstance().isAuthUser = false;
                        DialogService.MessageDialog(AuthActivity.this,
                                "Error", "Incorrect response from the server", "OK", null);
                    }
                });

                break;
            }
            case R.id.forgot_password: {
                Editable loginEmail = email.getText();
                DialogService.inputDialog(this, "Forgot password", loginEmail == null ? null : loginEmail.toString()
                        , "Enter Email/User Name", "Forgot", new DialogCallback<String>() {
                            @Override
                            public void onResult(String result) {
                                if (result == null || result.isEmpty())
                                    return;

                                Call<Void> response =
                                        StarMeetApp.getApi().restorePassword(result);

                                response.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(@NonNull Call<Void> call,
                                                           @NonNull Response<Void> response) {
                                        if (response.code() == 200) {

                                            DialogService.MessageDialog(AuthActivity.this,
                                                    "Successfully", "Password reset information was sent to your" +
                                                            "email address. Please check your email and follow the instructions", "OK", null);
                                        } else {
                                            DialogService.MessageDialog(AuthActivity.this,
                                                    "Error", "Error restore password", "OK", null);
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<Void> call,
                                                          @NonNull Throwable t) {
                                        DialogService.MessageDialog(AuthActivity.this,
                                                "Error", "Error restore password", "OK", null);
                                    }
                                });
                            }

                            @Override
                            public void onClose() {

                            }
                        });
                break;
            }
            case R.id.create_account: {
                Intent i = new Intent(AuthActivity.this, RegActivity.class);
                i.putExtra("is_celebrity", false);
                startActivity(i);
                break;
            }
            case R.id.registry: {
                Intent i = new Intent(AuthActivity.this, RegActivity.class);
                i.putExtra("is_celebrity", true);
                startActivity(i);
                break;
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void sendPudhToken() {
        PushModel token = new PushModel(FirebaseInstanceId.getInstance().getToken());
        Call<Void> result = StarMeetApp.getApi().setPushToken(token);
        result.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {

            }
        });

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Push Token")
                .putCustomAttribute("Token", token.token)
                .putContentType("Text"));
    }

    @Override
    protected void onResume() {
        super.onResume();

        MyContext.getInstance().isLogout = true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        MyContext.getInstance().isLogout = false;
    }

    private void setSaveAccount(String email, String password, AccessToken tokenInfo) throws ParseException {
        Account acc = new Account();
        acc.setLogin(email);
        acc.setPassword(password);
        acc.setAuth(true);
        acc.setUserName(tokenInfo.username);

        Calendar calender = Calendar.getInstance();
        SimpleDateFormat curFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault());
        String dateStr = curFormat.format(calender.getTime());

        acc.setlastDate(curFormat.parse(dateStr));
        acc.setToken(tokenInfo.accessToken);
        acc.setRefreshToken(tokenInfo.refreshToken);
        acc.setExpiresIn(tokenInfo.expiresIn);
        acc.setRoleId(tokenInfo.userRole);

        try {
            AccountService accountService = helper.getAccountService();
            accountService.addOrUpdateAccount(acc);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isValid() {
        if (email.getEditableText() == null || email.getEditableText().toString().isEmpty()) {
            email.setError("Incorrect login");
            return false;

        } else if (password.getEditableText() == null || password.getEditableText().toString().isEmpty()) {
            password.setError("Incorrect password");
            return false;
        }

        return true;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN)
            hideKeyboard();
        return super.dispatchTouchEvent(ev);
    }
}
package starmeet.convergentmobile.com.starmeet.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import starmeet.convergentmobile.com.starmeet.Communicators.ActivityCommunicator;
import starmeet.convergentmobile.com.starmeet.Helpers.DatabaseHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.HelperFactory;
import starmeet.convergentmobile.com.starmeet.Helpers.NavigationHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.UpdateHelper;
import starmeet.convergentmobile.com.starmeet.Models.AccessToken;
import starmeet.convergentmobile.com.starmeet.Models.Account;
import starmeet.convergentmobile.com.starmeet.Models.ActivityNextType;
import starmeet.convergentmobile.com.starmeet.Models.Member;
import starmeet.convergentmobile.com.starmeet.MyContext;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Request.CelebrityRequest;
import starmeet.convergentmobile.com.starmeet.Services.AccountService;
import starmeet.convergentmobile.com.starmeet.Services.DialogService;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;

/**
 * Created by alexeysidorov on 26.02.2018.
 */

public class RegistryCelebrityFragment extends Fragment implements View.OnClickListener {

    private MaterialEditText email;
    private MaterialEditText password;
    private MaterialEditText confirmPassword;
    private MaterialEditText firstName;
    private MaterialEditText lastName;
    private AppCompatButton regButton;
    private AppCompatTextView sigIn;
    private MaterialEditText phone;
    private DatabaseHelper helper;
    private ActivityCommunicator<String> activityCommunicator;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_reg_user, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityCommunicator = (ActivityCommunicator<String>) context;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initClick();

        HelperFactory.setHelper(getActivity());
        helper = HelperFactory.getHelper();
    }

    private void initViews(View view) {
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        confirmPassword = view.findViewById(R.id.confirm_password);
        firstName = view.findViewById(R.id.first_name);
        lastName = view.findViewById(R.id.last_name);
        phone = view.findViewById(R.id.phone);
        regButton = view.findViewById(R.id.reg_next);
        sigIn = view.findViewById(R.id.reg_sign);
        design();
    }

    @SuppressLint("SetTextI18n")
    private void design() {
        regButton.setText("SIGN UP");
    }

    private void initClick() {
        regButton.setOnClickListener(this);
        sigIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reg_next: {

                if (!isValid()) return;

                CelebrityRequest request = new CelebrityRequest();
                Member user = new Member();
                user.email = email.getEditableText().toString();
                user.password = password.getEditableText().toString();
                user.confirmPassword = confirmPassword.getEditableText().toString();
                user.firstName = firstName.getEditableText().toString();
                user.lastName = lastName.getEditableText().toString();
                user.phone = phone.getEditableText().toString();
                request.member = user;

                registryCelebrity(request);

                break;
            }
            case R.id.reg_sign: {
                activityCommunicator.passDataToActivity(null, -1);
                break;
            }
        }
    }

    private boolean isValid() {
        if (firstName.getEditableText() == null || firstName.getEditableText().toString().isEmpty()) {
            firstName.setError("Incorrect First name");
            return false;

        } else if (lastName.getEditableText() == null || lastName.getEditableText().toString().isEmpty()) {
            lastName.setError("Incorrect Last name");
            return false;

        } else if (phone.getEditableText() == null || phone.getEditableText().toString().isEmpty()) {
            phone.setError("Incorrect phone");
            return false;

        } else if (email.getEditableText() == null || email.getEditableText().toString().isEmpty()) {
            email.setError("Incorrect email");
            return false;

        } else if (!isValidEmail(email.getEditableText().toString())) {
            email.setError("Incorrect format email");
            return false;

        } else if (password.getEditableText() == null || password.getEditableText().toString().isEmpty()) {
            password.setError("Incorrect password");
            return false;

        } else if (confirmPassword.getEditableText() == null || confirmPassword.getEditableText().toString().isEmpty()) {
            password.setError("Incorrect confirm password");
            return false;

        } else if (!password.getEditableText().toString().equals(confirmPassword.getEditableText().toString())) {
            password.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    private static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void registryCelebrity(CelebrityRequest member) {

        final CustomProgressDialog progress = DialogService.ProgressDialog(Objects.requireNonNull(getActivity()),
                "Please wait...");
        Call<Void> result = StarMeetApp.getApi().registryCelebrity(member);

        result.enqueue(new Callback<Void>() {

            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull retrofit2.Response<Void> response) {
                if (response.code() == 200) {
                    progress.ChangeTitle("Authorization...");
                    loginCelebrity(progress);
                } else {
                    progress.dismiss();
                    DialogService.MessageDialog(getActivity(),
                            "Error", "Error create account", "OK", null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                progress.dismiss();
                DialogService.MessageDialog(getActivity(),
                        "Error", "Error create account", "OK", null);
            }
        });
    }

    private void setSaveAccount(String email, String password, AccessToken tokenInfo) throws ParseException {
        Account acc = new Account();
        acc.setLogin(email);
        acc.setPassword(password);
        acc.setAuth(true);
        acc.setUserName(tokenInfo.username);

        Calendar calender = Calendar.getInstance();
        SimpleDateFormat curFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss a", Locale.getDefault());
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

    @SuppressLint("HardwareIds")
    private void loginCelebrity(final CustomProgressDialog progress) {
        SharedPreferences sharedPrefs = Objects.requireNonNull(getActivity()).getSharedPreferences(
                PREF_UNIQUE_ID, Context.MODE_PRIVATE);
        String uuid = sharedPrefs.getString("PREF_UNIQUE_ID", "");

        Call<AccessToken> result = StarMeetApp.getApi().authUser("password",
                this.getResources().getString(R.string.client_key),
                this.getResources().getString(R.string.client_secret), email.getEditableText().toString(),
                password.getEditableText().toString(), uuid);

        result.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(@NonNull Call<AccessToken> call, @NonNull retrofit2.Response<AccessToken> response) {
                if (response.code() == 200 && response.body() != null) {

                    AccessToken token = response.body();

                    try {
                        assert token != null;
                        setSaveAccount(email.getEditableText().toString(), password.getEditableText().toString(), token);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        progress.dismiss();
                        DialogService.MessageDialog(Objects.requireNonNull(getActivity()),
                                "Error", "Invalid login or password", "OK", null);
                        return;
                    }

                    progress.dismiss();

                    MyContext.getInstance().userRole = token.userRole;
                    MyContext.getInstance().isAuthUser = true;

                    NavigationHelper.getInstance().navigation(getActivity(), ActivityNextType.Activity2);
                    UpdateHelper.getInstance().update();

                } else {
                    progress.dismiss();
                    DialogService.MessageDialog(Objects.requireNonNull(getActivity()),
                            "Error", "Invalid login or password", "OK", null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {
                progress.dismiss();
                DialogService.MessageDialog(Objects.requireNonNull(getActivity()),
                        "Error", "Incorrect response from the server", "OK", null);
            }
        });
    }
}

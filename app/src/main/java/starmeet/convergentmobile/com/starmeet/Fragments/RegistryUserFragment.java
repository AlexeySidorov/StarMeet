package starmeet.convergentmobile.com.starmeet.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.text.InputType;
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
import starmeet.convergentmobile.com.starmeet.Request.UserRequest;
import starmeet.convergentmobile.com.starmeet.Services.AccountService;
import starmeet.convergentmobile.com.starmeet.Services.DialogService;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;

/**
 * Created by alexeysidorov on 26.02.2018.
 */

public class RegistryUserFragment extends Fragment implements View.OnClickListener {

    private MaterialEditText email;
    private MaterialEditText password;
    private MaterialEditText confirmPassword;
    private MaterialEditText firstName;
    private MaterialEditText lastName;
    private AppCompatButton regButton;
    private AppCompatTextView sigIn;
    private MaterialEditText phone;
    private ActivityCommunicator<String> activityCommunicator;
    private DatabaseHelper helper;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_reg_user, null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityCommunicator = (ActivityCommunicator<String>) context;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initClick();
        design();

        HelperFactory.setHelper(getActivity());
        helper = HelperFactory.getHelper();
    }

    private void initViews() {
        phone = Objects.requireNonNull(getActivity()).findViewById(R.id.phone);
        email = getActivity().findViewById(R.id.email);
        password = getActivity().findViewById(R.id.password);
        confirmPassword = getActivity().findViewById(R.id.confirm_password);
        firstName = getActivity().findViewById(R.id.first_name);
        lastName = getActivity().findViewById(R.id.last_name);
        regButton = getActivity().findViewById(R.id.reg_next);
        sigIn = getActivity().findViewById(R.id.reg_sign);
    }

    private void initClick() {
        regButton.setOnClickListener(this);
        sigIn.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    private void design() {
        regButton.setText("NEXT");
        phone.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reg_next: {

                if (!isValid()) return;

                Member user = new Member();
                user.email = email.getText().toString();
                user.password = password.getText().toString();
                user.confirmPassword = confirmPassword.getText().toString();
                user.firstName = firstName.getText().toString();
                user.lastName = lastName.getText().toString();
                user.phone = phone.getText().toString();

                UserRequest request = new UserRequest();
                request.member = user;

                registryUser(request);

                break;
            }
            case R.id.reg_sign: {
                activityCommunicator.passDataToActivity(null, -1);
                break;
            }
        }
    }

    private boolean isValid() {
        if (firstName.getText() == null || firstName.getText().toString().isEmpty()) {
            firstName.setError("Incorrect First name");
            return false;

        } else if (lastName.getText() == null || lastName.getText().toString().isEmpty()) {
            lastName.setError("Incorrect Last name");
            return false;

        } else if (phone.getText() == null || phone.getText().toString().isEmpty()) {
            phone.setError("Incorrect phone");
            return false;

        } else if (email.getText() == null || email.getText().toString().isEmpty()) {
            email.setError("Incorrect email");
            return false;

        } else if (!isValidEmail(email.getText().toString())) {
            email.setError("Incorrect format email");
            return false;

        } else if (password.getText().toString().isEmpty()) {
            password.setError("Incorrect password");
            return false;

        } else if (confirmPassword.getText().toString().isEmpty()) {
            confirmPassword.setError("Incorrect confirm password");
            return false;

        } /*else if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
            LogUtil.logE("Pass", password.getText().toString());
            LogUtil.logE("ConPass", confirmPassword.getText().toString());
            confirmPassword.setError("Passwords do not match");
            return false;
        }*/

        return true;
    }

    private static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void registryUser(final UserRequest user) {

        final CustomProgressDialog progress;
        progress = DialogService.ProgressDialog(Objects.requireNonNull(getActivity()), "Please wait...");
        Call<Void> result = StarMeetApp.getApi().registryUser(user);

        result.enqueue(new Callback<Void>() {

            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull retrofit2.Response<Void> response) {
                if (response.code() == 200) {
                    progress.ChangeTitle("Athorization...");
                    loginUser(progress, user);
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
    private void loginUser(final CustomProgressDialog progress, final UserRequest user) {
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
                        setSaveAccount(user.member.email, user.member.password, token);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        progress.dismiss();
                        DialogService.MessageDialog(getActivity(),
                                "Error", "Invalid login or password", "OK", null);
                        return;
                    }

                    progress.dismiss();

                    MyContext.getInstance().userRole = token.userRole;
                    MyContext.getInstance().isAuthUser = true;

                    NavigationHelper.getInstance().navigation(getActivity(),ActivityNextType.Activity1);
                    UpdateHelper.getInstance().update();

                } else {
                    progress.dismiss();
                    DialogService.MessageDialog(getActivity(),
                            "Error", "Invalid login or password", "OK", null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {
                progress.dismiss();
                DialogService.MessageDialog(getActivity(),
                        "Error", "Incorrect response from the server", "OK", null);
            }
        });
    }
}

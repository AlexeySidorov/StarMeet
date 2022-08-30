package starmeet.convergentmobile.com.starmeet.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Response.MemberResponse;
import starmeet.convergentmobile.com.starmeet.Response.UserResponse;
import starmeet.convergentmobile.com.starmeet.Services.DialogService;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;

/**
 * Created by alexeysidorov on 28.03.2018.
 */

public class ProfileInfoFragment extends Fragment implements View.OnClickListener {
    private AppCompatTextView change;
    private MaterialEditText firstName;
    private MaterialEditText lastName;
    private MaterialEditText email;
    private MaterialEditText password;
    private AppCompatButton update;
    private ProfileInfoPasswordFragment fragment;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_info_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initClick();
        getUserInfo();
    }

    private void initViews() {
        firstName = Objects.requireNonNull(getActivity()).findViewById(R.id.first_name);
        lastName = Objects.requireNonNull(getActivity()).findViewById(R.id.last_name);
        email = Objects.requireNonNull(getActivity()).findViewById(R.id.email);
        password = Objects.requireNonNull(getActivity()).findViewById(R.id.current_password);
        update = Objects.requireNonNull(getActivity()).findViewById(R.id.update_button);
        change = Objects.requireNonNull(getActivity()).findViewById(R.id.change);
    }

    private void setDataAccount(UserResponse user) {
        email.setText(user.email);
        firstName.setText(user.firstName);
        lastName.setText(user.lastName);
    }

    private void initClick() {
        change.setOnClickListener(this);
        update.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change: {
                FragmentManager manager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                if (manager != null) {
                    fragment = new ProfileInfoPasswordFragment();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.profile_container, fragment).
                            addToBackStack("ProfileInfoPasswordFragment").commit();
                }
                break;
            }
            case R.id.update_button: {
                if (!isValid()) return;

                final MemberResponse userInfo = new MemberResponse();
                userInfo.email = email.getText().toString();
                userInfo.currentPassword = password.getText().toString();
                userInfo.firstName = firstName.getText().toString();
                userInfo.lastName = lastName.getText().toString();

                Call<Void> response = StarMeetApp.getApi().updateMember(userInfo);
                response.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.code() == 200) {

                            DialogService.MessageDialog(Objects.requireNonNull(getActivity()),
                                    "Successfully", "Update user info", "OK", null);

                            password.setText("");
                            firstName.setText(userInfo.firstName);
                            lastName.setText(userInfo.lastName);
                        } else {
                            DialogService.MessageDialog(Objects.requireNonNull(getActivity()),
                                    "Error", "Error user info", "OK", null);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        DialogService.MessageDialog(Objects.requireNonNull(getActivity()),
                                "Error", "Error update user info", "OK", null);
                    }
                });
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

        } else if (email.getText() == null || email.getText().toString().isEmpty()) {
            email.setError("Incorrect email");
            return false;

        } else if (password.getText().toString().isEmpty()) {
            password.setError("Incorrect password");
            return false;
        }

        return true;
    }

    private static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void getUserInfo() {
        Call<UserResponse> response = StarMeetApp.getApi().getMember();
        response.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                if (response.code() == 200 && response.body() != null) {
                    setDataAccount(response.body());
                } else
                    LogUtil.logE("Error", "Error get userInfo");
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                LogUtil.logE("Error", t.getMessage(), t);
            }
        });
    }
}

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
import starmeet.convergentmobile.com.starmeet.MyContext;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Request.PasswordRequest;
import starmeet.convergentmobile.com.starmeet.Services.DialogService;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;

/**
 * Created by alexeysidorov on 28.03.2018.
 */

public class ProfileInfoPasswordFragment extends Fragment implements View.OnClickListener {
    private AppCompatTextView cancel;
    private MaterialEditText youPassword;
    private MaterialEditText newPassword;
    private MaterialEditText confirmPassword;
    private AppCompatButton update;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.change_password_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initClick();
    }

    private void initViews() {
        cancel = Objects.requireNonNull(getActivity()).findViewById(R.id.cancel);
        youPassword = Objects.requireNonNull(getActivity()).findViewById(R.id.you_current_password);
        newPassword = Objects.requireNonNull(getActivity()).findViewById(R.id.new_password);
        confirmPassword = Objects.requireNonNull(getActivity()).findViewById(R.id.confirm_password);
        update = Objects.requireNonNull(getActivity()).findViewById(R.id.update_password_button);
    }

    private void initClick() {
        update.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel: {
                FragmentManager manager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                if (manager != null) {
                    ProfileInfoFragment fragment = new ProfileInfoFragment();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.profile_container, fragment).commit();
                }

                break;
            }
            case R.id.update_password_button: {
                if (!isValid()) return;

                PasswordRequest request = new PasswordRequest();
                request.currentPassword = youPassword.getText().toString();
                request.newPassword = newPassword.getText().toString();
                request.confirmPassword = confirmPassword.getText().toString();
                Call<Void> response = StarMeetApp.getApi().updatePassword(request);
                response.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.code() == 200) {

                            DialogService.MessageDialog(Objects.requireNonNull(getActivity()),
                                    "Successfully", "Update password", "OK", null);

                            youPassword.setText("");
                            newPassword.setText("");
                            confirmPassword.setText("");
                        } else {
                            DialogService.MessageDialog(Objects.requireNonNull(getActivity()),
                                    "Error", "Error update password", "OK", null);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        DialogService.MessageDialog(Objects.requireNonNull(getActivity()),
                                "Error", "Error update password", "OK", null);
                    }
                });
                break;
            }
        }
    }

    private boolean isValid() {
        if (youPassword.getText() == null || youPassword.getText().toString().isEmpty()) {
            youPassword.setError("Incorrect your password");
            return false;

        } else if (newPassword.getText() == null || newPassword.getText().toString().isEmpty()) {
            newPassword.setError("Incorrect password");
            return false;
        } else if (confirmPassword.getText() == null || confirmPassword.getText().toString().isEmpty()) {
            confirmPassword.setError("Incorrect confirm password");
            return false;
        }

        return true;
    }
}
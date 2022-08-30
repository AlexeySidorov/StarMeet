package starmeet.convergentmobile.com.starmeet.Request;


import com.google.gson.annotations.SerializedName;

/**
 * Created by xaycc on 22.02.2018.
 */

public class PasswordRequest {
    @SerializedName("CurrentPassword")
    public String currentPassword;

    @SerializedName("NewPassword")
    public String newPassword;

    @SerializedName("ConfirmNewPassword")
    public String confirmPassword;
}

package starmeet.convergentmobile.com.starmeet.Models;


import com.google.gson.annotations.SerializedName;

/**
 * Created by xaycc on 22.02.2018.
 */

public class Member {
    @SerializedName("Password")
    public String password;

    @SerializedName("ConfirmPassword")
    public String confirmPassword;

    @SerializedName("FirstName")
    public String firstName;

    @SerializedName("LastName")
    public String lastName;

    @SerializedName("Email")
    public String email;

    @SerializedName("Phone")
    public String phone;
}


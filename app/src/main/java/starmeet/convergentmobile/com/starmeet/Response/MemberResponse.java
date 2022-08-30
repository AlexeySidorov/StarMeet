package starmeet.convergentmobile.com.starmeet.Response;


import com.google.gson.annotations.SerializedName;

/**
 * Created by xaycc on 22.02.2018.
 */

public class MemberResponse extends Response {
    @SerializedName("CurrentPassword")
    public String currentPassword;

    @SerializedName("FirstName")
    public String firstName;

    @SerializedName("LastName")
    public String lastName;

    @SerializedName("Email")
    public String email;
}

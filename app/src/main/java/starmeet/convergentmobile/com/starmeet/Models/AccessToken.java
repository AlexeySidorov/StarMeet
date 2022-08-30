package starmeet.convergentmobile.com.starmeet.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by xaycc on 22.02.2018.
 */

public class AccessToken {

    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("expires_in")
    public int expiresIn;

    @SerializedName("refresh_token")
    public String refreshToken;

    @SerializedName("user_name")
    public String username;

    @SerializedName("user_role")
    public int userRole;

    @SerializedName(".issued")
    public Date issued;

    @SerializedName(".expires")
    public Date expires;

    @SerializedName("token_type")
    public String tokenType;

    @SerializedName("as:client_id")
    public String clientId;
}

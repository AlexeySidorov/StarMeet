package starmeet.convergentmobile.com.starmeet.Request;


import com.google.gson.annotations.SerializedName;

/**
 * Created by xaycc on 23.02.2018.
 */

public class EventSearchRequest {
    @SerializedName("Offset")
    public int offset;

    @SerializedName("Limit")
    public int limit;

    @SerializedName("Mode")
    public String mode;
}

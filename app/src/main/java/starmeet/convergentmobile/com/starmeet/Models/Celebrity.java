package starmeet.convergentmobile.com.starmeet.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xaycc on 22.02.2018.
 */

public class Celebrity {
    @SerializedName("ID")
    public int id;
    @SerializedName("FirstName")
    public String firstName;
    @SerializedName("LastName")
    public String lastName;
    @SerializedName("PhotoUrl")
    public String photoUrl;
    @SerializedName("Bio")
    public String bio;
}

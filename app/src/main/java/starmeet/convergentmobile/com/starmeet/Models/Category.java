package starmeet.convergentmobile.com.starmeet.Models;


import com.google.gson.annotations.SerializedName;

/**
 * Created by xaycc on 22.02.2018.
 */

public class Category {
    @SerializedName("ID")
    public int id;

    @SerializedName("Title")
    public String title;

    @SerializedName("Description")
    public String description;
}

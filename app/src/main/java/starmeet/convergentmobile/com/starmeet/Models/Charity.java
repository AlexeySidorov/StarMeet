package starmeet.convergentmobile.com.starmeet.Models;


import com.google.gson.annotations.SerializedName;

/**
 * Created by xaycc on 22.02.2018.
 */

public class Charity {
    @SerializedName("ID")
    public int id;

    @SerializedName("Title")
    public String title;

    @SerializedName("Url")
    public String url;

    @SerializedName("WebSiteTitle")
    public String webSiteTitle;

    @SerializedName("Description")
    public String Description;

    @SerializedName("LogoUrl")
    public String LogoUrl;
}

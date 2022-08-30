package starmeet.convergentmobile.com.starmeet.Request;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import starmeet.convergentmobile.com.starmeet.Models.Filter;

/**
 * Created by xaycc on 23.02.2018.
 */

public class EventRequest {
    @SerializedName("Offset")
    public int offset;

    @SerializedName("Limit")
    public int limit;

    @SerializedName("SortMethod")
    public String sortMethod;

    @SerializedName("Filters")
    public ArrayList<Filter> filters;

    @SerializedName("InMyWishListOnly")
    public boolean inMyWishListOnly;
}

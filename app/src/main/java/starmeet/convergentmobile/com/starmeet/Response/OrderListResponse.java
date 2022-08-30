package starmeet.convergentmobile.com.starmeet.Response;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import starmeet.convergentmobile.com.starmeet.Models.Order;

/**
 * Created by xaycc on 23.02.2018.
 */

public class OrderListResponse extends Response {
    @SerializedName("Offset")
    public int offset;

    @SerializedName("Limit")
    public int limit;

    @SerializedName("Count")
    public int count;

    @SerializedName("Total")
    public int total;

    @SerializedName("Items")
    public ArrayList<Order> items;
}

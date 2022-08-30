package starmeet.convergentmobile.com.starmeet.Models;


import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by xaycc on 23.02.2018.
 */

public class Order{

    @SerializedName("ID")
    public Integer id;

    @SerializedName("CreateUtcDate")
    public Date createUtcDate;

    @SerializedName("OfferType")
    public OfferType offerType;

    @SerializedName("Status")
    public Status status;

    @SerializedName("Event")
    public Event event;

    @SerializedName("Price")
    public Price price;

    @SerializedName("ItemsCount")
    public int itemsCount;
}

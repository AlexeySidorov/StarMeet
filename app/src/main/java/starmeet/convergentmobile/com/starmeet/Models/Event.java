package starmeet.convergentmobile.com.starmeet.Models;


import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by xaycc on 22.02.2018.
 */

public class Event {

    @SerializedName("ID")
    public int id;

    @SerializedName("ShortDescription")
    public String shortDescription;

    @SerializedName("Category")
    public Category category;

    @SerializedName("LongDescription")
    public String longDescription;

    @SerializedName("Charity")
    public Charity charity;

    @SerializedName("Celebrity")
    public Celebrity celebrity;

    @SerializedName("Type")
    public Type type;

    @SerializedName("OfferType")
    public OfferType offerType;

    @SerializedName("StartUtcTime")
    public Date startUtcTime;

    @SerializedName("CreateUtcTime")
    public Date createUtcTime;

    @SerializedName("DirectPurchase")
    public DirectPurchase drectPurchase;

    @SerializedName("Raffle")
    public Raffle Raffle;

    @SerializedName("IsInWishList")
    public boolean isInWishList;

    @SerializedName("DetailsUrl")
    public String detailsUrl;

    @SerializedName("EnablePurchasing")
    public boolean enablePurchasing;

    @SerializedName("Status")
    public Status status;
}

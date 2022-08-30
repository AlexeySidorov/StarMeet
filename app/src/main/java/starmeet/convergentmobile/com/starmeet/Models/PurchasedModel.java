package starmeet.convergentmobile.com.starmeet.Models;


import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by xaycc on 22.02.2018.
 */

public class PurchasedModel {

    @SerializedName("ID")
    public int id;

    @SerializedName("ShortDescription")
    public String shortDescription;

    @SerializedName("Category")
    public Category category;

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

    @SerializedName("RaffleStatus")
    public RaffleStatus raffleStatus;

    @SerializedName("DirectPurchaseStatus")
    public String directPurchaseStatus;
}
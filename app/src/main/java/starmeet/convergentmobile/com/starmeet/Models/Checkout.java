package starmeet.convergentmobile.com.starmeet.Models;


import com.google.gson.annotations.SerializedName;

/**
 * Created by xaycc on 22.02.2018.
 */

public class Checkout {
    @SerializedName("EventID")
    public int eventId;
    @SerializedName("Currency")
    public String currency;
    @SerializedName("OfferTypeID")
    public int offerTypeId;
    @SerializedName("Quantity")
    public int quantity;
}

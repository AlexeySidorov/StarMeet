package starmeet.convergentmobile.com.starmeet.Request;


import com.google.gson.annotations.SerializedName;

/**
 * Created by xaycc on 23.02.2018.
 */

public class CheckoutRequest {
    @SerializedName("EventID")
    public int eventId;

    @SerializedName("Currency")
    public String currency;

    @SerializedName("OfferTypeID")
    public int offerTypeId;

    @SerializedName("Quantity")
    public int quantity;
}

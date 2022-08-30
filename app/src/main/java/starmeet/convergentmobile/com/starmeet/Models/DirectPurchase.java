package starmeet.convergentmobile.com.starmeet.Models;


import com.google.gson.annotations.SerializedName;

/**
 * Created by xaycc on 23.02.2018.
 */

public class DirectPurchase {

    @SerializedName("Enable")
    public boolean enable;

    @SerializedName("IsRaffleBuyNow")
    public boolean isRaffleBuyNow;

    @SerializedName("Prices")
    public Price prices;

    @SerializedName("OfferTypeID")
    public int offerTypeId;
}

package starmeet.convergentmobile.com.starmeet.Models;


import com.google.gson.annotations.SerializedName;

/**
 * Created by xaycc on 23.02.2018.
 */

public class Raffle {

    @SerializedName("Enable")
    public boolean enable;

    @SerializedName("TicketPrices")
    public Price ticketPrices;

    @SerializedName("PurchasedTicketCount")
    public int purchasedTicketCount;

    @SerializedName("OfferTypeID")
    public int offerTypeId;
}

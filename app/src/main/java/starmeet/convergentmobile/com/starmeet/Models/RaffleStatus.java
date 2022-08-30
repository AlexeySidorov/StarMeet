package starmeet.convergentmobile.com.starmeet.Models;

import com.google.gson.annotations.SerializedName;

public class RaffleStatus {
    @SerializedName("Title")
    public String title;
    @SerializedName("PurchasedTicketCount")
    public Integer purchasedTicketCount;
    @SerializedName("isWon")
    public boolean IsWon;
}

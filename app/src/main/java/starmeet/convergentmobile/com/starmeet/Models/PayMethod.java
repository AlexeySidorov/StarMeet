package starmeet.convergentmobile.com.starmeet.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by xaycc on 23.02.2018.
 */

public class PayMethod {

    @SerializedName("Enable")
    public boolean enable;

    @SerializedName("Price")
    public Price price;

    @SerializedName("ExistingCards")
    public ArrayList<Card> existingCards;
}

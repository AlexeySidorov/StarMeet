package starmeet.convergentmobile.com.starmeet.Models;


import com.google.gson.annotations.SerializedName;

/**
 * Created by xaycc on 23.02.2018.
 */

public class Price {

    @SerializedName("AmountUsd")
    public double amountUsd;

    @SerializedName("AmountInr")
    public double amountInr;

    @SerializedName("Value")
    public double value;

    @SerializedName("Currency")
    public String currency;
}

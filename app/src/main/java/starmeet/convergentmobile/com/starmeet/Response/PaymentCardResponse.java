package starmeet.convergentmobile.com.starmeet.Response;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import starmeet.convergentmobile.com.starmeet.Models.Card;
import starmeet.convergentmobile.com.starmeet.Models.Order;
import starmeet.convergentmobile.com.starmeet.Models.PayMethod;

/**
 * Created by xaycc on 23.02.2018.
 */

public class PaymentCardResponse extends Order {
    @SerializedName("Quantity")
    public int quantity;

    @SerializedName("PayTmMethod")
    public PayMethod payTmMethod;

    @SerializedName("PaymentCardMethod")
    public PayMethod paymentCardMethod;
}

package starmeet.convergentmobile.com.starmeet.Response;


import com.google.gson.annotations.SerializedName;

import starmeet.convergentmobile.com.starmeet.Models.Order;

/**
 * Created by xaycc on 23.02.2018.
 */

public class PayResponse {
    @SerializedName("IsSuccess")
    public boolean isSuccess;

    @SerializedName("IsRefund")
    public boolean isRefund;

    @SerializedName("IsRefundSuccess")
    public boolean isRefundSuccess;

    @SerializedName("Order")
    public Order order;
}

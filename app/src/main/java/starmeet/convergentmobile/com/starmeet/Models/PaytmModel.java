package starmeet.convergentmobile.com.starmeet.Models;

import com.google.gson.annotations.SerializedName;

public class PaytmModel {
    @SerializedName("MID")
    public String mid;

    @SerializedName("INDUSTRY_TYPE_ID")
    public String industryType;

    @SerializedName("CHANNEL_ID")
    public String channel;

    @SerializedName("WEBSITE")
    public String webSite;

    @SerializedName("EMAIL")
    public String email;

    @SerializedName("MOBILE_NO")
    public String mobileNumber;

    @SerializedName("CUST_ID")
    public Integer custId;

    @SerializedName("ORDER_ID")
    public String orderId;

    @SerializedName("TXN_AMOUNT")
    public String txnAmount;

    @SerializedName("CALLBACK_URL")
    public String url;

    @SerializedName("CHECKSUMHASH")
    public String checkSum;
}

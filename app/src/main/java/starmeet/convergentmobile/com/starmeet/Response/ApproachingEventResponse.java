package starmeet.convergentmobile.com.starmeet.Response;


import com.google.gson.annotations.SerializedName;

import java.util.Date;

import starmeet.convergentmobile.com.starmeet.Models.OfferType;

public class ApproachingEventResponse {
    @SerializedName("ID")
    public Integer id;

    @SerializedName("StartUtcTime")
    public Date startUtcTime;

    @SerializedName("SecondPartName")
    public String secondPartName;

    @SerializedName("Type")
    public OfferType offerType;

    @SerializedName("IsOnlineType")
    public boolean isOnlineType;

    @SerializedName("EnableCountdown")
    public boolean enableCountdown;

    @SerializedName("Countdown")
    public String countdown;

    @SerializedName("ShowStartButton")
    public boolean showStartButton;
}

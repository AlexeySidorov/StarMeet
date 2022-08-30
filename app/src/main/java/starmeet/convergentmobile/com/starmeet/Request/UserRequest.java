package starmeet.convergentmobile.com.starmeet.Request;


import com.google.gson.annotations.SerializedName;

import starmeet.convergentmobile.com.starmeet.Models.Card;
import starmeet.convergentmobile.com.starmeet.Models.Member;

/**
 * Created by xaycc on 22.02.2018.
 */

public class UserRequest {
    @SerializedName("Member")
    public Member member;

    @SerializedName("Card")
    public Card card;
}
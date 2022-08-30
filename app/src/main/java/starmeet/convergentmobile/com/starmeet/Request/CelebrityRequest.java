package starmeet.convergentmobile.com.starmeet.Request;


import com.google.gson.annotations.SerializedName;

import starmeet.convergentmobile.com.starmeet.Models.Member;

/**
 * Created by alexeysidorov on 03.03.2018.
 */

public class CelebrityRequest {

    @SerializedName("Member")
    public Member member;
}

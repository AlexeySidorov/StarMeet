package starmeet.convergentmobile.com.starmeet.Response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alexeysidorov on 20.03.2018.
 */

public class FaqResponse {

    @SerializedName("ID")
    public int id;

    @SerializedName("Question")
    public String question;

    @SerializedName("Answer")
    public String answer;
}

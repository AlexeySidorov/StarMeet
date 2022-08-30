package starmeet.convergentmobile.com.starmeet.Models;


import com.google.gson.annotations.SerializedName;

/**
 * Created by alexeysidorov on 21.03.2018.
 */

public class Filter {
    @SerializedName("Key")
    public String key;
    @SerializedName("Value")
    public int value;

    public Filter(String key, int value) {
        this.key = key;
        this.value = value;
    }
}

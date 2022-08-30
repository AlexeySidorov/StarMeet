package starmeet.convergentmobile.com.starmeet.Response;


import com.google.gson.annotations.SerializedName;

/**
 * Created by xaycc on 23.02.2018.
 */

public class ListResponse {

    @SerializedName("ID")
    public int id;

    @SerializedName("Title")
    public String title;

    @SerializedName("Description")
    public String description;

    public void addNewEvent (String title){
        this.title = title;
    }
}

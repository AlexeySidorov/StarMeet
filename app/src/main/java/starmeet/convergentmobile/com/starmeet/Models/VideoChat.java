package starmeet.convergentmobile.com.starmeet.Models;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by xaycc on 23.02.2018.
 */

public class VideoChat {

    @SerializedName("ID")
    public int id;

    @SerializedName("EventID")
    public int eventID;

    @SerializedName("Endpoing")
    public String endpoing;

    @SerializedName("AccessToken")
    public String accessToken;

    @SerializedName("SecondPartUserName")
    public String secondPartUserName;

    @SerializedName("IceServers")
    public ArrayList<IceServer> iceServers;

    @SerializedName("SecondPartUserPictureUrl")
    public String secondPartUserPictureUrl;
}

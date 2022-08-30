package starmeet.convergentmobile.com.starmeet;

import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import starmeet.convergentmobile.com.starmeet.Models.Country;
import starmeet.convergentmobile.com.starmeet.Response.ListResponse;

/**
 * Created by alexeysidorov on 26.02.2018.
 */

public class MyContext {
    private static MyContext sInstance = null;

    public static MyContext getInstance() {
        if (sInstance == null) {
            sInstance = new MyContext();
        }

        return sInstance;
    }

    //PreviousActivity
    public AppCompatActivity PreviousActivity;

    //Возможен ли процесс разлогинивания
    public boolean isLogout;

    //Array events
    public ArrayList<ListResponse> events;

    //Array event types
    public ArrayList<ListResponse> eventTypes;

    //Array countries
    public ArrayList<Country> countries;

    public int userRole;

    public boolean isAuthUser;
}
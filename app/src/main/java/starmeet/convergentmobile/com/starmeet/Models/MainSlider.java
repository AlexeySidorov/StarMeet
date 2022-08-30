package starmeet.convergentmobile.com.starmeet.Models;


/**
 * Created by alexeysidorov on 20.03.2018.
 */

public class MainSlider {
    public int id;
    public String name;
    public String url;
    public String title;
    public String eventName;

    public MainSlider(String url, String title, String smallTitle, String eventName) {
        this.url = url;
        name = title;
        this.title = smallTitle;
        this.eventName = eventName;
    }

}

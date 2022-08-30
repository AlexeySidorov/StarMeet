package starmeet.convergentmobile.com.starmeet.Models;


import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class EventCelebrityGroup extends ExpandableGroup<OrderModel> {
    public EventCelebrityGroup(String title, List<OrderModel> items) {
        super(title, items);
    }

    private String name;
    private String status;

    public void setTitle(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public String getTitle() {
        return name;
    }

    public String getStatus() {
        return status;
    }
}
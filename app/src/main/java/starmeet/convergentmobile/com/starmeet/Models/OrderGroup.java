package starmeet.convergentmobile.com.starmeet.Models;


import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.Date;
import java.util.List;

public class OrderGroup extends ExpandableGroup<OrderModel> {
    public OrderGroup(String title, List<OrderModel> items) {
        super(title, items);
    }

    private String name;
    private String currency;
    private double price;
    private Date date;

    public void setName(String name) {
        this.name = name;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getCurrency() {
        return currency;
    }

    public double getPrice() {
        return price;
    }

    public Date getDate() {
        return date;
    }
}

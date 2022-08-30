package starmeet.convergentmobile.com.starmeet.Models;


import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class FaqGroup extends ExpandableGroup<Faq> {
    public FaqGroup(String title, List<Faq> items) {
        super(title, items);
    }
}

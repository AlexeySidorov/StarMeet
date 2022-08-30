package starmeet.convergentmobile.com.starmeet.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.List;

import starmeet.convergentmobile.com.starmeet.Holders.FaqChildHolder;
import starmeet.convergentmobile.com.starmeet.Holders.FaqGroupHolder;
import starmeet.convergentmobile.com.starmeet.Models.Faq;
import starmeet.convergentmobile.com.starmeet.Models.FaqGroup;
import starmeet.convergentmobile.com.starmeet.R;

public class FaqAdapter extends ExpandableRecyclerViewAdapter<FaqGroupHolder, FaqChildHolder> {

    public FaqAdapter(ArrayList<FaqGroup> groups) {
        super(groups);
    }

    @Override
    public FaqGroupHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_faq_group, parent, false);
        return new FaqGroupHolder(view);
    }

    @Override
    public FaqChildHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_faq_child, parent, false);
        return new FaqChildHolder(view);
    }

    @Override
    public void onBindChildViewHolder(FaqChildHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final Faq faq = ((FaqGroup) group).getItems().get(childIndex);
        holder.setFaqText(faq.getFaq());
    }

    @Override
    public void onBindGroupViewHolder(FaqGroupHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setTitle(group.getTitle());
    }
}

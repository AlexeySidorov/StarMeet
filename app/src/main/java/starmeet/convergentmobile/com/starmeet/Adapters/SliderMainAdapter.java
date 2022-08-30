package starmeet.convergentmobile.com.starmeet.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import starmeet.convergentmobile.com.starmeet.Holders.MainSliderHolder;
import starmeet.convergentmobile.com.starmeet.Models.MainSlider;
import starmeet.convergentmobile.com.starmeet.R;

public class SliderMainAdapter extends RecyclerView.Adapter{

    private Context cxt;
    private ArrayList<MainSlider> array;

    public SliderMainAdapter(Context cxt, ArrayList<MainSlider> array) {
        this.cxt = cxt;
        this.array = array;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_main_slider, parent, false);
        return new MainSliderHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MainSlider item = array.get(position);

        if (item != null)
            ((MainSliderHolder) holder).Bind(cxt, item, array.size() - 1 == position);
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public MainSlider getItem(int position){
        return array.get(position);
    }
}

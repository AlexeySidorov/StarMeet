package starmeet.convergentmobile.com.starmeet.Models;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class OrderModel implements Parcelable {
    public int id;

    public Date createUtcDate;

    public OfferType offerType;

    public Status status;

    public Event event;

    public Price price;

    @SerializedName("ItemsCount")
    public Integer itemsCount;

    public OrderModel(Parcel in) {
    }

    public OrderModel() {

    }

    public static final Creator<OrderModel> CREATOR = new Creator<OrderModel>() {
        @Override
        public OrderModel createFromParcel(Parcel in) {
            return new OrderModel(in);
        }

        @Override
        public OrderModel[] newArray(int size) {
            return new OrderModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}

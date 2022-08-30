package starmeet.convergentmobile.com.starmeet.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Faq implements Parcelable {

    private String faq;

    public Faq() {

    }

    private Faq(Parcel in) {
        faq = in.readString();
    }

    public static final Creator<Faq> CREATOR = new Creator<Faq>() {
        @Override
        public Faq createFromParcel(Parcel in) {
            return new Faq(in);
        }

        @Override
        public Faq[] newArray(int size) {
            return new Faq[size];
        }
    };

    public String getFaq() {
        return faq;
    }

    public void setFaq(String faq) {
        this.faq = faq;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(faq);
    }
}

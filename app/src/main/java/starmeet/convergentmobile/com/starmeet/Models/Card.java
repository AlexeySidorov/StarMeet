package starmeet.convergentmobile.com.starmeet.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by xaycc on 22.02.2018.
 */

public class Card {

    @SerializedName("ID")
    public String id;

    @SerializedName("Brand")
    public String brand;

    @SerializedName("NumberLast4")
    public String numberLast4;

    @SerializedName("Number")
    public String number;

    @SerializedName("CvvCode")
    public Integer cvvCode;

    @SerializedName("Name")
    public String name;

    @SerializedName("ExpirationMonth")
    public Integer expirationMonth;

    @SerializedName("ExpirationYear")
    public Integer expirationYear;

    @SerializedName("AddressLine1")
    public String addressLine1;

    @SerializedName("AddressLine2")
    public String addressLine2;

    @SerializedName("City")
    public String city;

    @SerializedName("Province")
    public String province;

    @SerializedName("PostalCode")
    public String postalCode;

    @SerializedName("CountryISOAlpha2")
    public String countryISOAlpha2;

    @Expose(serialize = false)
    public boolean isCheck;
}

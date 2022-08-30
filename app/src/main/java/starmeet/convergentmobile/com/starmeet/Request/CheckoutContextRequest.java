package starmeet.convergentmobile.com.starmeet.Request;

import com.google.gson.annotations.SerializedName;

import starmeet.convergentmobile.com.starmeet.Models.Card;

public class CheckoutContextRequest {

    public CheckoutContextRequest() {
        checkoutContext = new CheckoutRequest();
       // newCard = new Card();
    }

    @SerializedName("CheckoutContext")
    public CheckoutRequest checkoutContext;

    @SerializedName("ExistingPaymentCardID")
    public String existingPaymentCardId;

    @SerializedName("NewCard")
    public Card newCard;
}

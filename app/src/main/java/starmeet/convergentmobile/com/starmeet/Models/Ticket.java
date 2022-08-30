package starmeet.convergentmobile.com.starmeet.Models;


/**
 * Created by alexeysidorov on 28.03.2018.
 */

public class Ticket {
    public int eventId;
    public double amountUsd;
    public double amountInr;
    public boolean isUsd;
    public PaymentType type;
    public boolean isCheck;
    public boolean isLotteryPurchase;
    public OfferType offerType;
    public Raffle raffle;
    public int ticketCount;
    public int payOfferId;

    public Ticket(int eventId, double amountInr, double amountUsd, boolean isUsd, PaymentType type,
                  boolean isCheck, OfferType offerType, Raffle raffle, boolean isLotteryPurchase) {
        this.eventId = eventId;
        this.amountInr = amountInr;
        this.amountUsd = amountUsd;
        this.isUsd = isUsd;
        this.type = type;
        this.isCheck = isCheck;
        this.offerType = offerType;
        this.raffle = raffle;
        this.isLotteryPurchase = isLotteryPurchase;
        this.ticketCount = 1;
    }

    public void setTicketCount(int count){
        ticketCount = count;
    }

    public void setPayOfferTicket(int offerId){
        payOfferId = offerId;
    }
}

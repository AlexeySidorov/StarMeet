package starmeet.convergentmobile.com.starmeet.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import starmeet.convergentmobile.com.starmeet.Holders.EventPaymentCardHolder;
import starmeet.convergentmobile.com.starmeet.Holders.EventPaymentInfoHolder;
import starmeet.convergentmobile.com.starmeet.Holders.EventPaymentPayTmHolder;
import starmeet.convergentmobile.com.starmeet.Holders.EventPurchaseHolder;
import starmeet.convergentmobile.com.starmeet.Listners.AdapterClickListener;
import starmeet.convergentmobile.com.starmeet.Listners.HolderClickListener;
import starmeet.convergentmobile.com.starmeet.Models.PaymentType;
import starmeet.convergentmobile.com.starmeet.Models.Ticket;
import starmeet.convergentmobile.com.starmeet.R;

public class EventPurchaseAdapter extends RecyclerView.Adapter implements HolderClickListener<Ticket> {

    private Context context;
    private ArrayList<Ticket> array;
    private AdapterClickListener<Ticket> listener;

    public EventPurchaseAdapter(Context context, ArrayList<Ticket> array) {
        this.context = context;
        this.array = array;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case 0: {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_purshase, parent, false);
                return new EventPurchaseHolder(view);
            }
            case 1: {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_payment_pay_tm, parent, false);
                return new EventPaymentPayTmHolder(view);
            }
            case 2: {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_payment_card, parent, false);
                return new EventPaymentCardHolder(view);
            }
            case 3: {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_payment_info, parent, false);
                return new EventPaymentInfoHolder(view);
            }
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Ticket item = array.get(position);

        if (item != null) {
            switch (item.type) {
                case None: {
                    ((EventPurchaseHolder) holder).Bind(context, item);
                    ((EventPurchaseHolder) holder).setClickListener(this);
                    break;
                }
                case Info: {
                    ((EventPaymentInfoHolder) holder).Bind(item);
                    ((EventPaymentInfoHolder) holder).setClickListener(this);
                    break;
                }
                case Card: {
                    ((EventPaymentCardHolder) holder).Bind(context, item);
                    ((EventPaymentCardHolder) holder).setClickListener(this);
                    break;
                }
                case PayTm: {
                    ((EventPaymentPayTmHolder) holder).Bind(context, item);
                    ((EventPaymentPayTmHolder) holder).setClickListener(this);
                    break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public void setOnClickListener(AdapterClickListener<Ticket> listener) {
        this.listener = listener;
    }

    @Override
    public void clickElement(Ticket item, int elementId) {
        switch (elementId) {
            case R.id.buy_button: {
                listener.ElementItemClick(item, R.id.buy_button);
                break;
            }
            case R.id.purchase_info: {
                listener.ElementItemClick(item, R.id.purchase_info);
                break;
            }
            case R.id.close: {
                listener.ElementItemClick(item, R.id.close);
                break;
            }
            case R.id.pay_check: {
                for (int index = 0; index < array.size(); index++) {
                    Ticket ticket = array.get(index);

                    if (ticket.type != PaymentType.Info && ticket.type != item.type) {
                        ticket.isCheck = !item.isCheck;
                        notifyDataSetChanged();
                        break;
                    }
                }
                break;
            }
            case R.id.add_new_card: {
                listener.ElementItemClick(item, R.id.add_new_card);
                break;
            }
            case R.id.currency_value: {
                listener.ElementItemClick(item, R.id.currency_value);
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        Ticket ticket = array.get(position);
        if (ticket == null) return 0;

        return ticket.type.getNumericType();
    }

    public void clear() {
        array.clear();
        notifyDataSetChanged();
    }

    public void addNewElements(ArrayList<Ticket> tickets) {
        array.addAll(tickets);
        notifyDataSetChanged();
    }
}

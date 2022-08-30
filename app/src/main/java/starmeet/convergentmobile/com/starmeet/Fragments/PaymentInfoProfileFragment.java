package starmeet.convergentmobile.com.starmeet.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import starmeet.convergentmobile.com.starmeet.Activites.AddNewCardActivity;
import starmeet.convergentmobile.com.starmeet.Adapters.CardAdapter;
import starmeet.convergentmobile.com.starmeet.Callbaks.CallbackNotification;
import starmeet.convergentmobile.com.starmeet.Helpers.NavigationHelper;
import starmeet.convergentmobile.com.starmeet.Listners.AdapterClickListener;
import starmeet.convergentmobile.com.starmeet.Models.ActivityNextType;
import starmeet.convergentmobile.com.starmeet.Models.Card;
import starmeet.convergentmobile.com.starmeet.Models.NavigationModel;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Services.DialogService;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;


/**
 * Created by alexeysidorov on 28.03.2018.
 */

public class PaymentInfoProfileFragment extends Fragment implements AdapterClickListener<Card>,
        View.OnClickListener, CallbackNotification {
    private RecyclerView cards;
    private CardAdapter adapter;
    private AppCompatButton add;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.payment_info_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    private void initViews() {
        cards = Objects.requireNonNull(getActivity()).findViewById(R.id.payments_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        cards.setLayoutManager(layoutManager);
        adapter = new CardAdapter(getActivity(), new ArrayList<Card>());
        adapter.setOnClickListener(this);
        cards.setAdapter(adapter);

        add = getActivity().findViewById(R.id.add_card_button);
        add.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadCards();
    }

    private void loadCards() {
        // CustomProgressDialog progressDialog = DialogService.ProgressDialog(Objects.requireNonNull(getActivity()), "Please wait");

        Call<ArrayList<Card>> request = StarMeetApp.getApi().getCards();
        request.enqueue(new Callback<ArrayList<Card>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Card>> call,
                                   @NonNull Response<ArrayList<Card>> response) {
                if (response.code() == 200 && response.body() != null) {
                    ArrayList<Card> cardArrayList = response.body();
                    if (adapter.getItemCount() > 0)
                        adapter.clear();

                    adapter.addNewItems(cardArrayList);

                    if (getActivity() == null) return;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //  progressDialog.dismiss();
                        }
                    });

                } else {
                    if (getActivity() == null) return;

                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // progressDialog.dismiss();
                            DialogService.MessageDialog(Objects.requireNonNull(getActivity()),
                                    "Error", "Error. Credit cards list is not available", "OK", null);
                        }
                    });

                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Card>> call, @NonNull Throwable t) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //progressDialog.dismiss();
                        DialogService.MessageDialog(Objects.requireNonNull(getActivity()),
                                "Error", "Error. Credit cards list is not available", "OK", null);
                    }
                });
            }
        });
    }

    @Override
    public void ItemClick(Card item) {

    }

    @Override
    public void ElementItemClick(final Card item, int elementId) {
        switch (elementId) {
            case R.id.remove_card: {

                Call<Void> request =
                        StarMeetApp.getApi().deleteCard(item.id);

                request.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call,
                                           @NonNull Response<Void> response) {
                        if (response.code() == 200) {

                            if (getActivity() == null) {
                                DialogService.MessageDialog(Objects.requireNonNull(getActivity()),
                                        "Successfully", "Successfully remove card", "OK", null);
                            }

                            adapter.remove(item.id);
                        } else {

                            if (getActivity() == null) return;

                            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogService.MessageDialog(Objects.requireNonNull(getActivity()),
                                            "Error", "Error remove card", "OK", null);
                                    LogUtil.logE("Remove card", String.valueOf(response.code()));
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call,
                                          @NonNull Throwable t) {
                        if (getActivity() == null) return;

                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogService.MessageDialog(Objects.requireNonNull(getActivity()),
                                        "Error", "Error remove card", "OK", null);
                                LogUtil.logE("Remove card", t.getMessage(), t);
                            }
                        });
                    }
                });
                break;
            }
            case R.id.edit_card: {
                Gson gson = new Gson();
                String json = gson.toJson(item);

                HashMap<ActivityNextType, Class> activities = new HashMap<>();
                activities.put(ActivityNextType.None, AppCompatActivity.class);
                NavigationHelper.getInstance().setNextActivity(activities, NavigationModel.Card);
                NavigationHelper.getInstance().setNotificationListener(this);

                Intent i = new Intent(getActivity(), AddNewCardActivity.class);
                i.putExtra("isProfile", true);
                i.putExtra("card_info", json);
                Objects.requireNonNull(getActivity()).startActivity(i);

                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        HashMap<ActivityNextType, Class> activities = new HashMap<>();
        activities.put(ActivityNextType.None, AppCompatActivity.class);
        NavigationHelper.getInstance().setNextActivity(activities, NavigationModel.Card);
        NavigationHelper.getInstance().setNotificationListener(this);

        Intent i = new Intent(getActivity(), AddNewCardActivity.class);
        i.putExtra("isProfile", true);
        Objects.requireNonNull(getActivity()).startActivity(i);
    }

    @Override
    public void onNotification(NavigationModel model) {
        switch (model) {
            case Card: {
                loadCards();
                break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}

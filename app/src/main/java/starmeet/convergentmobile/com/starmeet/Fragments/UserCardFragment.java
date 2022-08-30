package starmeet.convergentmobile.com.starmeet.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import starmeet.convergentmobile.com.starmeet.Callbaks.DialogCallback;
import starmeet.convergentmobile.com.starmeet.Models.Card;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Services.DialogService;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;

public class UserCardFragment extends Fragment implements View.OnClickListener {
    private MaterialEditText name;
    private MaterialEditText city;
    private MaterialEditText zipCode;
    private MaterialEditText address1;
    private MaterialEditText address2;
    private MaterialEditText cardNumber;
    private MaterialEditText secCode;
    private AppCompatButton regButton;
    private MaterialEditText year;
    private MaterialEditText month;
    private boolean typeScreen;
    private Card card;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_create_user_card, null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null) {
            typeScreen = bundle.getBoolean("type_screen", false);
            String json = bundle.getString("card_info");

            if (json != null && !json.isEmpty()) {
                Gson gson = new Gson();
                card = gson.fromJson(json, Card.class);
            }
        } else typeScreen = false;

        initViews();
        initClick();
        setCardInfo();
    }

    private void initViews() {
        name = Objects.requireNonNull(getActivity()).findViewById(R.id.name_card);
        city = Objects.requireNonNull(getActivity().findViewById(R.id.city));
        zipCode = Objects.requireNonNull(getActivity().findViewById(R.id.zip_code));
        address1 = Objects.requireNonNull(getActivity().findViewById(R.id.address1));
        address2 = Objects.requireNonNull(getActivity().findViewById(R.id.address2));
        cardNumber = Objects.requireNonNull(getActivity().findViewById(R.id.card_number));
        year = Objects.requireNonNull(getActivity().findViewById(R.id.year));
        month = Objects.requireNonNull(getActivity().findViewById(R.id.month));
        secCode = Objects.requireNonNull(getActivity().findViewById(R.id.ccv2));
        regButton = Objects.requireNonNull(getActivity().findViewById(R.id.reg_user));
    }

    private void initClick() {
        regButton.setOnClickListener(this);
        month.setOnClickListener(this);
        year.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    private void setCardInfo() {
        if (card == null) return;

        name.setText(card.name == null ? "" : card.name);
        city.setText(card.city == null ? "" : card.city);
        zipCode.setText(card.postalCode == null ? "" : card.postalCode);
        address1.setText(card.addressLine1 == null ? "" : card.addressLine1);
        address2.setText(card.addressLine2 == null ? "" : card.addressLine2);
        year.setText(card.expirationYear.toString());
        month.setText(card.expirationMonth.toString());
        secCode.setText(card.cvvCode == null ? "" : card.cvvCode.toString());
    }

    private boolean isValid() {
        if (name.getEditableText() == null || name.getEditableText().toString().isEmpty()) {
            name.setError("Incorrect cardholder name");
            return false;

        } else if (city.getEditableText() == null || city.getEditableText().toString().isEmpty()) {
            city.setError("Incorrect city");
            return false;

        } else if (zipCode.getEditableText() == null || zipCode.getEditableText().toString().isEmpty()) {
            zipCode.setError("Incorrect zip code");
            return false;

        } else if (address1.getEditableText() == null || address1.getEditableText().toString().isEmpty()) {
            address1.setError("Billing address 1");
            return false;

        } else if (address2.getEditableText() == null || address2.getEditableText().toString().isEmpty()) {
            address2.setError("IBilling address 2");
            return false;

        } else if (cardNumber.getEditableText() == null || cardNumber.getEditableText().toString().isEmpty()) {
            cardNumber.setError("Incorrect card number");
            return false;

        } else if (cardNumber.length() < 13 || cardNumber.length() > 19) {
            cardNumber.setError("Incorrect card number");
            return false;

        } else if (month.getEditableText() == null || month.getEditableText().toString().isEmpty()) {
            month.setError("Incorrect expiration month");
            return false;

        } else if (year.getEditableText() == null || year.getEditableText().toString().isEmpty()) {
            year.setError("Incorrect expiration year");
            return false;

        } else if (secCode.getEditableText() == null || secCode.getEditableText().toString().isEmpty()) {
            secCode.setError("Incorrect secure code");
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reg_user: {
                if (!isValid()) return;

                if (card == null)
                    card = new Card();

                card.name = name.getText().toString();
                card.city = city.getText().toString();
                card.postalCode = zipCode.getText().toString();
                card.addressLine1 = address1.getText().toString();
                card.addressLine2 = address2.getText().toString();
                card.number = cardNumber.getText().toString();
                card.expirationYear = Integer.parseInt(year.getText().toString());
                card.expirationMonth = Integer.parseInt(month.getText().toString());
                card.cvvCode = Integer.parseInt(secCode.getText().toString());

                if (typeScreen)
                    addNewCard(card);
                else
                    updateCardInfo(card);

                break;
            }
            case R.id.month: {
                ArrayList<String> values = new ArrayList<>();
                values.add("01");
                values.add("02");
                values.add("03");
                values.add("04");
                values.add("05");
                values.add("06");
                values.add("07");
                values.add("08");
                values.add("09");
                values.add("10");
                values.add("11");
                values.add("12");

                Editable text = month.getText();
                DialogService.bottomSheetNumericDialog(getActivity(), values.toArray(new String[values.size()]),
                        text == null ? null : text.toString(), new DialogCallback<String>() {
                            @Override
                            public void onResult(String result) {
                                month.setText(result);
                            }

                            @Override
                            public void onClose() {

                            }
                        });
                break;
            }
            case R.id.year: {
                ArrayList<String> values = new ArrayList<>();
                Calendar calendar = Calendar.getInstance();
                final Integer currentYear = calendar.get(Calendar.YEAR);
                values.add(currentYear.toString());

                for (int index = 1; index < 6; index++) {
                    Integer nextYear = currentYear + index;
                    values.add(nextYear.toString());
                }

                Editable text = year.getText();
                DialogService.bottomSheetNumericDialog(getActivity(), values.toArray(new String[values.size()]),
                        text == null ? null : text.toString(), new DialogCallback<String>() {
                            @Override
                            public void onResult(String result) {
                                year.setText(result);
                            }

                            @Override
                            public void onClose() {

                            }
                        });
                break;
            }
        }
    }

    private void addNewCard(Card card) {

        Call<Void> request = StarMeetApp.getApi().createNewCard(card);
        request.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.code() == 200) {
                    Objects.requireNonNull(getActivity()).onBackPressed();
                } else {
                    DialogService.MessageDialog(Objects.requireNonNull(getActivity()),
                            "Error", "Error create new card", "OK", null);
                    LogUtil.logE("Create card", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                DialogService.MessageDialog(Objects.requireNonNull(getActivity()),
                        "Error", "Error create new card", "OK", null);
                LogUtil.logE("Create card", t.getMessage(), t);
            }
        });
    }

    private void updateCardInfo(Card card) {
        Call<Void> request = StarMeetApp.getApi().updateCard(card.id, card);
        request.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.code() == 200) {
                    Objects.requireNonNull(getActivity()).onBackPressed();
                } else {
                    DialogService.MessageDialog(Objects.requireNonNull(getActivity()),
                            "Error", "Error update card\"", "OK", null);
                    LogUtil.logE("Update card", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                DialogService.MessageDialog(Objects.requireNonNull(getActivity()),
                        "Error", "Error update card", "OK", null);
                LogUtil.logE("Update card", t.getMessage(), t);
            }
        });
    }
}

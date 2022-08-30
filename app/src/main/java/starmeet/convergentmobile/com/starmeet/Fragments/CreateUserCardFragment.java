package starmeet.convergentmobile.com.starmeet.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import starmeet.convergentmobile.com.starmeet.Helpers.NavigationHelper;
import starmeet.convergentmobile.com.starmeet.Models.ActivityNextType;
import starmeet.convergentmobile.com.starmeet.Models.Card;
import starmeet.convergentmobile.com.starmeet.Models.Country;
import starmeet.convergentmobile.com.starmeet.MyContext;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Services.DialogService;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;

public class CreateUserCardFragment extends Fragment implements View.OnClickListener {

    private MaterialEditText name;
    private MaterialEditText city;
    private MaterialEditText zipCode;
    private MaterialEditText address1;
    private MaterialEditText address2;
    private MaterialEditText cardNumber;
    private MaterialEditText secCode;
    private AppCompatButton addCard;
    private MaterialEditText year;
    private MaterialEditText month;
    private MaterialEditText state;
    private MaterialEditText country;
    private boolean isCreate;
    private String cardId;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_create_user_card, null);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
        initClick();

        isCreate = true;

        Intent bundle = Objects.requireNonNull(getActivity()).getIntent();
        if (bundle != null) {
            Boolean isProfile = bundle.getBooleanExtra("isProfile", false);
            addCard.setText(isProfile ? "Add Card" : "Add card and Pay now");

            String jsonCard = bundle.getStringExtra("card_info");
            if (jsonCard != null) {
                addCard.setText("Update card");
                loadUpdateCardInfo(jsonCard);
                isCreate = false;
            }
        }
    }

    private void initViews() {
        name = Objects.requireNonNull(getActivity()).findViewById(R.id.name_card);
        city = getActivity().findViewById(R.id.city);
        zipCode = getActivity().findViewById(R.id.zip_code);
        address1 = getActivity().findViewById(R.id.address1);
        address2 = getActivity().findViewById(R.id.address2);
        cardNumber = getActivity().findViewById(R.id.card_number);
        year = getActivity().findViewById(R.id.year);
        month = getActivity().findViewById(R.id.month);
        secCode = getActivity().findViewById(R.id.ccv2);
        addCard = getActivity().findViewById(R.id.add_new_card);
        state = getActivity().findViewById(R.id.state);
        country = getActivity().findViewById(R.id.country);
    }

    private void initClick() {
        addCard.setOnClickListener(this);
        month.setOnClickListener(this);
        year.setOnClickListener(this);
        country.setOnClickListener(this);
    }

    private boolean isValid() {
        if (name.getEditableText() == null || name.getEditableText().toString().isEmpty()) {
            name.setError("Incorrect cardholder name");
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

        } else if (address1.getEditableText() == null || address1.getEditableText().toString().isEmpty()) {
            address1.setError("Billing address 1");
            return false;

       /* } else if (address2.getEditableText() == null || address2.getEditableText().toString().isEmpty()) {
            address2.setError("IBilling address 2");
            return false;*/

        } else if (city.getEditableText() == null || city.getEditableText().toString().isEmpty()) {
            city.setError("Incorrect city");
            return false;

        } else if (state.getEditableText() == null || state.getEditableText().toString().isEmpty()) {
            state.setError("Incorrect state");
            return false;

        } else if (zipCode.getEditableText() == null || zipCode.getEditableText().toString().isEmpty()) {
            zipCode.setError("Incorrect zip code");
            return false;

        } else if (country.getEditableText() == null || country.getEditableText().toString().isEmpty()) {
            country.setError("Incorrect country");
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_new_card: {
                if (!isValid()) return;

                final CustomProgressDialog progressDialog = DialogService.ProgressDialog(
                        Objects.requireNonNull(getActivity()), "Please wait");

                Card card = new Card();
                card.number = cardNumber.getText().toString();
                card.name = name.getText().toString();
                card.city = city.getText().toString();
                card.postalCode = zipCode.getText().toString();
                card.addressLine1 = address1.getText().toString();
                card.addressLine2 = address2.getText().toString();
                card.expirationYear = Integer.parseInt(year.getText().toString());
                card.expirationMonth = Integer.parseInt(month.getText().toString());
                card.cvvCode = Integer.parseInt(secCode.getText().toString());
                card.province = state.getText().toString();

                for (int index = 0; index < MyContext.getInstance().countries.size(); index++) {
                    Country cntry = MyContext.getInstance().countries.get(index);

                    if (cntry.title.equals(country.getText().toString())) {
                        card.countryISOAlpha2 = cntry.code;
                        break;
                    }
                }

                if (isCreate) {
                    final Call<Void> request = StarMeetApp.getApi().createNewCard(card);
                    request.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                }
                            });

                            if (response.code() == 200) {
                                NavigationHelper.getInstance().navigation(getActivity(), ActivityNextType.Activity1);

                            } else {
                                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DialogService.MessageDialog(getActivity(),
                                                "Error", "Error add new card", "OK", null);
                                        LogUtil.logE("Add card", String.valueOf(response.code()));
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                }
                            });
                            LogUtil.logE("Add card", t.getMessage(), t);
                        }
                    });

                } else if (cardId != null) {
                    final Call<Void> request = StarMeetApp.getApi().updateCard(cardId, card);
                    request.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                }
                            });

                            if (response.code() == 200) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        NavigationHelper.getInstance().navigation(getActivity(), ActivityNextType.Activity1);
                                    }
                                }, 500);
                                // UpdateHelper.getInstance().update();

                            } else {
                                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DialogService.MessageDialog(getActivity(),
                                                "Error", "Error update card", "OK", null);
                                        LogUtil.logE("Update card", String.valueOf(response.code()));
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                }
                            });
                            LogUtil.logE("Update card", t.getMessage(), t);
                        }

                    });
                }

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

            case R.id.country: {

                ArrayList<String> values = new ArrayList<>();

                for (int index = 0; index < MyContext.getInstance().countries.size(); index++) {
                    Country country = MyContext.getInstance().countries.get(index);
                    values.add(country.title);
                }

                Editable text = country.getText();

                DialogService.bottomSheetNumericDialog(getActivity(), values.toArray(new String[values.size()]),
                        text == null ? null : text.toString(), new DialogCallback<String>() {
                            @Override
                            public void onResult(String result) {
                                country.setText(result);
                            }

                            @Override
                            public void onClose() {

                            }
                        });
                break;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadUpdateCardInfo(String json) {
        Gson gson = new Gson();
        Card card = gson.fromJson(json, Card.class);

        cardId = card.id;
        cardNumber.setText("**** **** **** " + card.numberLast4);
        name.setText(card.name);
        city.setText(card.city);
        zipCode.setText(card.postalCode);
        address1.setText(card.addressLine1);
        address2.setText(card.addressLine2);
        year.setText(card.expirationYear.toString());
        month.setText(card.expirationMonth.toString());
        state.setText(card.province);

        for (int index = 0; index < MyContext.getInstance().countries.size(); index++) {
            Country cntry = MyContext.getInstance().countries.get(index);

            if (cntry.code.equals(card.countryISOAlpha2)) {
                country.setText(cntry.title);
                break;
            }
        }
    }
}

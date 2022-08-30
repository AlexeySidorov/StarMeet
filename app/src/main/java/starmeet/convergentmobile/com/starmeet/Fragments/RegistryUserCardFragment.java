package starmeet.convergentmobile.com.starmeet.Fragments;

import android.annotation.SuppressLint;
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

import starmeet.convergentmobile.com.starmeet.Callbaks.DialogCallback;
import starmeet.convergentmobile.com.starmeet.Helpers.DatabaseHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.HelperFactory;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Request.UserRequest;
import starmeet.convergentmobile.com.starmeet.Services.DialogService;

public class RegistryUserCardFragment extends Fragment implements View.OnClickListener {

    private MaterialEditText name;
    private MaterialEditText city;
    private MaterialEditText zipCode;
    private MaterialEditText address1;
    private MaterialEditText address2;
    private MaterialEditText cardNumber;
    private MaterialEditText secCode;
    private AppCompatButton regButton;
    private UserRequest user;
    private MaterialEditText year;
    private MaterialEditText month;
    private DatabaseHelper helper;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_reg_user_card, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HelperFactory.setHelper(getActivity());
        helper = HelperFactory.getHelper();

        Bundle bundle = getArguments();
        user = new UserRequest();

        if (bundle != null) {
            Gson mapper = new Gson();
            String json = bundle.getString("userInfo");
            user = mapper.fromJson(json, UserRequest.class);
        }

        initViews();
        initClick();
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
        regButton = getActivity().findViewById(R.id.reg_user);
    }

    private void initClick() {
        regButton.setOnClickListener(this);
        month.setOnClickListener(this);
        year.setOnClickListener(this);
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

                user.card.name = name.getText().toString();
                user.card.city = city.getText().toString();
                user.card.postalCode = zipCode.getText().toString();
                user.card.addressLine1 = address1.getText().toString();
                user.card.addressLine2 = address2.getText().toString();
                user.card.number = cardNumber.getText().toString();
                user.card.expirationYear = Integer.parseInt(year.getText().toString());
                user.card.expirationMonth = Integer.parseInt(month.getText().toString());
                user.card.cvvCode = Integer.parseInt(secCode.getText().toString());

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


}

package starmeet.convergentmobile.com.starmeet.Fragments;

import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import starmeet.convergentmobile.com.starmeet.Callbaks.DialogCallback;
import starmeet.convergentmobile.com.starmeet.R;

/**
 * Created by alexeysidorov on 15.03.2018.
 */

public class CustomBottomSheetNumericDialog implements View.OnClickListener {

    private View view;
    private DialogCallback<String> callback;
    private View sheetView;
    private BottomSheetDialog dialog;
    private LinearLayout container;
    private NumberPicker picker;
    private String[] pickerValue;

    public void showNumericDialog(FragmentActivity context, String[] values, String defaultValue,
                                  DialogCallback<String> callback) {
        view = context.getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        this.callback = callback;
        container = view.findViewById(R.id.container_main);
        AppCompatTextView apply = view.findViewById(R.id.apply_button);
        apply.setOnClickListener(this);

        dialog = new BottomSheetDialog(context);
        sheetView = context.getLayoutInflater().
                inflate(R.layout.bottom_dialog_selector, null);
        bottomDialog(values, defaultValue);
        pickerValue = values;
    }

    public void showNumericDialog(AppCompatActivity context, String[] values, String defaultValue) {
        view = context.getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        container = view.findViewById(R.id.container_main);
        AppCompatTextView apply = view.findViewById(R.id.apply_button);
        apply.setOnClickListener(this);

        dialog = new BottomSheetDialog(context);
        sheetView = context.getLayoutInflater().
                inflate(R.layout.bottom_dialog_selector, null);
        bottomDialog(values, defaultValue);
        pickerValue = values;
    }

    private void bottomDialog(String[] values, String defaultValue) {
        picker = sheetView.findViewById(R.id.numberPicker);
        setPicker(picker, values, defaultValue);
        container.addView(sheetView);
        dialog.setContentView(container);
        dialog.show();
    }

    private void setPicker(NumberPicker picker, String[] values, String defaultValue) {
        picker.setMaxValue(values.length - 1);
        picker.setMinValue(0);
        picker.setDisplayedValues(values);
        picker.setValue(defaultValue == null || defaultValue.isEmpty() ? 0 : foundIndex(values, defaultValue));
    }

    private int foundIndex(String[] array, String value) {
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.apply_button: {
                if (callback == null) return;

                callback.onResult(pickerValue[picker.getValue()]);
                dialog.dismiss();
            }
        }
    }
}

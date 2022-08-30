package starmeet.convergentmobile.com.starmeet.Services;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import starmeet.convergentmobile.com.starmeet.Callbaks.DialogCallback;
import starmeet.convergentmobile.com.starmeet.Fragments.CustomBottomSheetNumericDialog;
import starmeet.convergentmobile.com.starmeet.Fragments.CustomInputDialog;
import starmeet.convergentmobile.com.starmeet.Fragments.CustomMessageDialog;
import starmeet.convergentmobile.com.starmeet.Fragments.CustomProgressDialog;
import starmeet.convergentmobile.com.starmeet.Fragments.CustomQuestionDialog;
import starmeet.convergentmobile.com.starmeet.Fragments.CustomWhiteMessageDialog;
import starmeet.convergentmobile.com.starmeet.R;

/**
 * Update by alexeysidorov on 04.03.2018.
 */

public class DialogService {

    public static void inputDialog(AppCompatActivity activity, String title, String text, String hint,
                                   String buttonName, DialogCallback<String> callback) {
        CustomInputDialog fragment = new CustomInputDialog();
        fragment.setView(R.layout.input_dialog_fragment, title, text, hint, buttonName);
        fragment.setResult(callback);
        fragment.show(activity.getSupportFragmentManager(), "InputDialog");
    }

    public static void inputDialog(FragmentActivity activity, String title, String text, String hint,
                                   String buttonName, DialogCallback<String> callback) {
        CustomInputDialog fragment = new CustomInputDialog();
        fragment.setView(R.layout.input_dialog_fragment, title, text, hint, buttonName);
        fragment.setResult(callback);
        fragment.show(activity.getSupportFragmentManager(), "InputDialog");
    }

    public static void MessageDialog(AppCompatActivity activity, String title, String text,
                                     String buttonName, DialogCallback<ButtonType> callback) {
        CustomMessageDialog fragment = new CustomMessageDialog();
        fragment.setView(R.layout.message_dialog_fragment, title, text, buttonName);
        fragment.setResult(callback);
        fragment.show(activity.getSupportFragmentManager(), "MessageDialog");
    }

    public static void MessageDialog(FragmentActivity activity, String title, String text,
                                     String buttonName, DialogCallback<ButtonType> callback) {
        CustomMessageDialog fragment = new CustomMessageDialog();
        fragment.setView(R.layout.message_dialog_fragment, title, text, buttonName);
        fragment.setResult(callback);
        fragment.show(activity.getSupportFragmentManager(), "MessageDialog");
    }

    public static void MessageDialog(Context context, String title, String text,
                                     String buttonName, DialogCallback<ButtonType> callback) {
        CustomMessageDialog fragment = new CustomMessageDialog();
        fragment.setView(R.layout.message_dialog_fragment, title, text, buttonName);
        fragment.setResult(callback);
        fragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "MessageDialog");
    }

    public static void QuestionDialog(AppCompatActivity activity, String title, String text,
                                      String firstButtonName, String secondButtonName, DialogCallback<ButtonType> callback) {
        CustomQuestionDialog fragment = new CustomQuestionDialog();
        fragment.setView(R.layout.question_dialog_fragment, title, text, firstButtonName, secondButtonName);
        fragment.setResult(callback);
        fragment.show(activity.getSupportFragmentManager(), "QuestionDialog");
    }

    public static void CustomMessageDialog(AppCompatActivity activity, String text,
                                           String buttonName, DialogCallback<ButtonType> callback) {
        CustomWhiteMessageDialog fragment = new CustomWhiteMessageDialog();
        fragment.setView(R.layout.white_message_dialog_fragment, text, buttonName);
        fragment.setResult(callback);
        fragment.show(activity.getSupportFragmentManager(), "WhiteMessageDialog");
    }

    public static CustomProgressDialog ProgressDialog(AppCompatActivity activity, String title) {
        CustomProgressDialog fragment = new CustomProgressDialog(activity);
        fragment.setView(R.layout.progress_dialog_fragment, title);
        fragment.show(activity.getSupportFragmentManager(), "CustomProgressDialog");
        return fragment;
    }

    public static CustomProgressDialog ProgressDialog(FragmentActivity activity, String title) {
        CustomProgressDialog fragment = new CustomProgressDialog(activity);
        fragment.setView(R.layout.progress_dialog_fragment, title);
        fragment.show(activity.getSupportFragmentManager(), "CustomProgressDialog");
        return fragment;
    }

    public static CustomProgressDialog ProgressDialog(Context ctx, String title) {
        AppCompatActivity activity = (AppCompatActivity)ctx;
        CustomProgressDialog fragment = new CustomProgressDialog(activity);
        fragment.setView(R.layout.progress_dialog_fragment, title);
        fragment.show(activity.getSupportFragmentManager(), "CustomProgressDialog");
        return fragment;
    }

    public enum ButtonType {
        FirstButton,
        SecondButton
    }

    public static void bottomSheetNumericDialog(FragmentActivity activity, String[] values,
                                                String defaultValue, DialogCallback<String> callback) {
        CustomBottomSheetNumericDialog dialog = new CustomBottomSheetNumericDialog();
        dialog.showNumericDialog(activity, values, defaultValue, callback);
    }

    public static void bottomSheetNumericDialog(AppCompatActivity activity, String[] values,
                                                String defaultValue, DialogCallback<String> callback) {
        CustomBottomSheetNumericDialog dialog = new CustomBottomSheetNumericDialog();
        dialog.showNumericDialog(activity, values, defaultValue, callback);
    }
}

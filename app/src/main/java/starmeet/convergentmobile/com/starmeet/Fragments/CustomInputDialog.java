package starmeet.convergentmobile.com.starmeet.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.view.View;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;
import starmeet.convergentmobile.com.starmeet.Callbaks.DialogCallback;
import starmeet.convergentmobile.com.starmeet.R;

/**
 * Update by alexeysidorov on 26.02.2018.
 */

public class CustomInputDialog extends SupportBlurDialogFragment implements View.OnClickListener {

    private int resourceId;
    private String title;
    private String content;
    private String hint;
    private String buttonName;
    private DialogCallback<String> result;
    private AppCompatTextView titleDlg;
    private CircleImageView close;
    private AppCompatButton firstButton;
    private TextInputEditText contentDlg;

    public void setView(int resourceId, String title, String content, String hint, String buttonName) {
        this.resourceId = resourceId;
        this.title = title;
        this.content = content;
        this.hint = hint;
        this.buttonName = buttonName;
    }

    public void setResult(DialogCallback<String> result) {
        this.result = result;
    }

    @NonNull
    @Override
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialog);
        View view = getActivity().getLayoutInflater().inflate(resourceId, null);
        initView(view);
        initClick();
        setContent();
        builder.setCancelable(false);
        builder.setView(view);
        this.setCancelable(false);
        return builder.create();
    }

    private void initView(View view) {
        titleDlg = view.findViewById(R.id.title_dialog);
        close = view.findViewById(R.id.close_dialog);
        firstButton = view.findViewById(R.id.first_dialog_button);
        contentDlg = view.findViewById(R.id.content);
    }

    private void initClick() {
        close.setOnClickListener(this);
        firstButton.setOnClickListener(this);
    }

    private void setContent() {
        titleDlg.setText(title);
        contentDlg.setText(content);
        contentDlg.setHint(hint);
        firstButton.setText(buttonName);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_dialog: {

                if (result != null)
                    result.onClose();

                dismiss();
                break;
            }
            case R.id.first_dialog_button: {
                Editable txt = contentDlg.getText();

                if (result != null)
                    result.onResult(txt == null ? null : txt.toString());

                dismiss();
                break;
            }
        }
    }
}

package starmeet.convergentmobile.com.starmeet.Activites;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import starmeet.convergentmobile.com.starmeet.Base.BaseActivity;
import starmeet.convergentmobile.com.starmeet.Callbaks.CallbackNotification;
import starmeet.convergentmobile.com.starmeet.Fragments.ProfileFragment;
import starmeet.convergentmobile.com.starmeet.Helpers.NavigationHelper;
import starmeet.convergentmobile.com.starmeet.Models.NavigationModel;
import starmeet.convergentmobile.com.starmeet.R;

public class ProfileUserActivity extends BaseActivity implements CallbackNotification {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLayout(R.layout.activity_base);
        setOnlyMyViews(false);
        setToolBarMenuButton(false);
        setColorToolbarMainButton(R.color.colorAccent);
        super.onCreate(savedInstanceState);
        setImageTitle(R.layout.toolbar_header, true);
        setTitle("");
        NavigationHelper.getInstance().setNotificationListener(this);
    }

    @Override
    protected void initViews2() {
        android.support.v4.app.FragmentManager fr = getSupportFragmentManager();
        ProfileFragment profileFragment = new ProfileFragment();
        fr.beginTransaction().add(R.id.container, profileFragment, "ProfileFragment").commit();
    }

    @Override
    public void onNotification(NavigationModel model) {
        menuUpdate();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN)
            hideKeyboard();
        return super.dispatchTouchEvent(ev);
    }
}

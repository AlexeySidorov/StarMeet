package starmeet.convergentmobile.com.starmeet.Activites;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import starmeet.convergentmobile.com.starmeet.Base.BaseActivity;
import starmeet.convergentmobile.com.starmeet.Communicators.ActivityCommunicator;
import starmeet.convergentmobile.com.starmeet.Fragments.RegistryCelebrityFragment;
import starmeet.convergentmobile.com.starmeet.Fragments.RegistryUserFragment;
import starmeet.convergentmobile.com.starmeet.MyContext;
import starmeet.convergentmobile.com.starmeet.R;

/**
 * Update by alexeysidorov on 26.02.2018.
 */

public class RegActivity extends BaseActivity implements ActivityCommunicator<String> {

    private FragmentManager fr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLayout(R.layout.activity_reg);
        setOnlyMyViews(true);
        setToolBarMenuButton(false);
        setColorToolbarMainButton(R.color.colorAccent);
        super.onCreate(savedInstanceState);
        setTitle("");
    }

    @Override
    protected void initViews2() {
        initView();
    }

    private void initView() {

        fr = getSupportFragmentManager();
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        boolean isCelebrity = bundle.getBoolean("is_celebrity");

        if (!isCelebrity) {
            RegistryUserFragment regUser = new RegistryUserFragment();
            fr.beginTransaction().add(R.id.reg_container, regUser, "RegistryUserFragment")
                    .commit();
        } else {
            RegistryCelebrityFragment regCelebrity = new RegistryCelebrityFragment();
            fr.beginTransaction().add(R.id.reg_container, regCelebrity, "RegistryCelebrityFragment").commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        MyContext.getInstance().isLogout = true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        MyContext.getInstance().isLogout = false;
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1)
            getFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }

    @Override
    public void passDataToActivity(String value, int code) {
        if (code == -1)
            onBackPressed();
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
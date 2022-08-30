package starmeet.convergentmobile.com.starmeet.Activites;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import starmeet.convergentmobile.com.starmeet.Fragments.RegistryUserCardFragment;
import starmeet.convergentmobile.com.starmeet.MyContext;
import starmeet.convergentmobile.com.starmeet.R;

/**
 * Update by alexeysidorov on 26.02.2018.
 */

public class RegCardActivity extends AppCompatActivity {

    private FragmentManager fr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        initView();
    }

    private void initView() {

        fr = getSupportFragmentManager();
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;

        RegistryUserCardFragment regUser = new RegistryUserCardFragment();
        fr.beginTransaction().add(R.id.reg_container, regUser, "RegistryUserCardFragment")
                .commit();
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
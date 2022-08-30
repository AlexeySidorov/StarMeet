package starmeet.convergentmobile.com.starmeet.Activites;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import starmeet.convergentmobile.com.starmeet.Base.BaseActivity;
import starmeet.convergentmobile.com.starmeet.Communicators.ActivityCommunicator;
import starmeet.convergentmobile.com.starmeet.Fragments.MainFragment;
import starmeet.convergentmobile.com.starmeet.R;


public class MainActivity extends BaseActivity implements ActivityCommunicator<String> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLayout(R.layout.activity_base);
        setOnlyMyViews(false);
        setToolBarMenuButton(true);
        setColorToolbarMainButton(R.color.colorAccent);
        setSelfMenuItem(R.id.all_events);
        super.onCreate(savedInstanceState);
        setImageTitle(R.layout.toolbar_header, true);
        setTitle("");
    }

    @Override
    protected void initViews2() {
        android.support.v4.app.FragmentManager fr = getSupportFragmentManager();
        MainFragment mainFragment = new MainFragment();
        fr.beginTransaction().add(R.id.container, mainFragment, "MainFragment").commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults[0] == -1) { }
    }

    @Override
    protected void onResume() {
        super.onResume();
        menuUpdate();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void passDataToActivity(String value, int code) {
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}

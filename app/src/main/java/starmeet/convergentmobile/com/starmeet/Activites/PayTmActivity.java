package starmeet.convergentmobile.com.starmeet.Activites;

import android.os.Bundle;

import starmeet.convergentmobile.com.starmeet.Base.BaseActivity;
import starmeet.convergentmobile.com.starmeet.Fragments.PayTmFragment;
import starmeet.convergentmobile.com.starmeet.R;

public class PayTmActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLayout(R.layout.activity_base);
        setOnlyMyViews(false);
        setToolBarMenuButton(false);
        setColorToolbarMainButton(R.color.colorAccent);
        super.onCreate(savedInstanceState);
        setImageTitle(R.layout.toolbar_header, true);
        setTitle("");
    }

    @Override
    protected void initViews2() {
        android.support.v4.app.FragmentManager fr = getSupportFragmentManager();
        PayTmFragment fragment = new PayTmFragment();
        fr.beginTransaction().add(R.id.container, fragment, "PayTmFragment").commit();
    }
}

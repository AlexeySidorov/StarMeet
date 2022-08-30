package starmeet.convergentmobile.com.starmeet.Activites;

import android.os.Bundle;

import starmeet.convergentmobile.com.starmeet.Base.BaseActivity;
import starmeet.convergentmobile.com.starmeet.Fragments.MainFragment;
import starmeet.convergentmobile.com.starmeet.R;

public class StarChatActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLayout(R.layout.activity_base);
        setOnlyMyViews(false);
        setToolBarMenuButton(true);
        setColorToolbarMainButton(R.color.colorAccent);
        setSelfMenuItem(R.id.star_chat);
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
}

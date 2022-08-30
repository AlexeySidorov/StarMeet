package starmeet.convergentmobile.com.starmeet.Base;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.sql.SQLException;
import java.util.Objects;

import starmeet.convergentmobile.com.starmeet.BuildConfig;
import starmeet.convergentmobile.com.starmeet.Callbaks.CallbackEmptyFunc;
import starmeet.convergentmobile.com.starmeet.Listners.MyOnNavigationItemSelectedListener;
import starmeet.convergentmobile.com.starmeet.Helpers.DatabaseHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.HelperFactory;
import starmeet.convergentmobile.com.starmeet.Models.Account;
import starmeet.convergentmobile.com.starmeet.MyContext;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;
import starmeet.convergentmobile.com.starmeet.Utils.SystemUtils;


public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    public int selfMenuItem = -1;
    boolean onlyMyViews = false;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private NavigationView mNavigationView;
    private CoordinatorLayout coordinatorLayout;
    private int layout = R.layout.activity_base;
    private StarMeetApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout);

        myApp = (StarMeetApp) this.getApplicationContext();
        initViews();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);

        SystemUtils.adjustFontScale(newBase, getResources().getConfiguration());
        //  Settings.System.getFloat(getContentResolver(), Settings.System.FONT_SCALE, 1.0f);
    }

    public void setOnlyMyViews(boolean onlyMyViews) {
        this.onlyMyViews = onlyMyViews;
    }

    //Workaround =)
    @SuppressWarnings("deprecation")
    public void initViews() {
        if (!onlyMyViews) {
            mDrawerLayout = findViewById(R.id.drawer_layout);
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
            Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(
                    new ColorDrawable(ContextCompat.getColor(this, R.color.transparent)));

            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                    R.string.text_open, R.string.text_close) {
                @Override
                public void onDrawerOpened(View drawerView) {
                    //  super.onDrawerOpened(drawerView);
                    InputMethodManager inputMethodManager =
                            (InputMethodManager) getSystemService(
                                    INPUT_METHOD_SERVICE);
                    assert inputMethodManager != null;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            View view = getCurrentFocus();
                            if (view == null) return;

                            inputMethodManager.hideSoftInputFromWindow(
                                    view.getWindowToken(), 0);
                        }
                    });
                }
            };

            mDrawerLayout.setDrawerListener(mDrawerToggle);
            initMainButtonToolBar();
            mNavigationView = findViewById(R.id.nav_view);
            mNavigationView.setNavigationItemSelectedListener(new MyOnNavigationItemSelectedListener(this,
                    selfMenuItem, mDrawerLayout));
            coordinatorLayout = findViewById(R.id.coordiantor_layout);
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKeyboard(v);
                }
            });

            menuUpdate();
        }

        initViews2();
    }

    @SuppressLint("SetTextI18n")
    public void menuUpdate() {

        View headerLayout = mNavigationView.getHeaderView(0);
        if (headerLayout != null) {
            AppCompatTextView userName = headerLayout.findViewById(R.id.user_name);
            AppCompatTextView userEmail = headerLayout.findViewById(R.id.user_email);
            AppCompatTextView version = findViewById(R.id.all_reserved);

            if (version != null) {
                int versionCode = BuildConfig.VERSION_CODE;
                String versionName = BuildConfig.VERSION_NAME;
                version.setText("© 2018 StarMeet, All rights reserved. v" + versionName + "(" + versionCode + ")");
            }

            HelperFactory.setHelper(this.getBaseContext());
            DatabaseHelper helper = HelperFactory.getHelper();

            if (MyContext.getInstance().isAuthUser) {
                try {
                    Account account = helper.getAccountService().getLastAccount();
                    if (account != null && userName != null)
                        userName.setText(account.getUserName());

                    if (account != null && userEmail != null)
                        userEmail.setText(account.getLogin());
                } catch (SQLException ignored) {
                }
            }

            if (!MyContext.getInstance().isAuthUser) {
                assert userName != null;
                userName.setText("Guest");
            }
        }

        mNavigationView.getMenu().findItem(R.id.sign_out).setVisible(MyContext.getInstance().isAuthUser);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private boolean isMenuButton;

    //Кнопка меню или кнопка назад
    public void setToolBarMenuButton(boolean isMenuButton) {
        this.isMenuButton = isMenuButton;
    }

    private int colorText;

    //Цвет заголовка активити
    public void setColorToolbarTitle(int color) {
        toolbar.setTitleTextColor(color);
        toolbar.setSubtitleTextColor(color);
    }

    private int color;

    //Цвет кнопки меню или кнопки назад
    public void setColorToolbarMainButton(int color) {
        this.color = color;
    }

    public void setToolbarBackground(int color) {
        if (color == 0) {
            Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(
                    new ColorDrawable(ContextCompat.getColor(this, R.color.transparent)));
        } else
            Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(
                    new ColorDrawable(ContextCompat.getColor(this, color)));
    }

    @SuppressWarnings("ConstantConditions")
    public void setIconNotification(boolean isVisible, final CallbackEmptyFunc callback) {

        try {
            if (ScreenId > -1) return;

            Objects.requireNonNull(getSupportActionBar()).setIcon(isVisible ? R.drawable.group405 : 0);

            for (int index = 0; index < toolbar.getChildCount(); index++) {
                View view = toolbar.getChildAt(index);

                if (view == null) return;
                if (view instanceof AppCompatImageView) {
                    AppCompatImageView logoNotif = (AppCompatImageView) view;

                    if (logoNotif != null) {
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (callback != null)
                                    callback.Click();
                            }
                        });
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    @SuppressLint("PrivateResource")
    @SuppressWarnings("ResourceAsColor")
    private void initMainButtonToolBar() {
        Drawable upArrow = null;
        if (!isMenuButton) {
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                upArrow = getDrawable(R.drawable.abc_ic_ab_back_material);
            else upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                assert upArrow != null;

                upArrow.setColorFilter(ContextCompat.getColor(getBaseContext(), color), PorterDuff.Mode.SRC_ATOP);
            } else {
                assert upArrow != null;
                upArrow.setColorFilter(getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
            }

            mDrawerToggle.setHomeAsUpIndicator(upArrow);
            mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mDrawerToggle.setDrawerIndicatorEnabled(true);
        }

        mDrawerToggle.syncState();
    }

    //Анимация перехода для 21
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupWindowAnimations() {
        Fade fade = new Fade();
        fade.setDuration(1000);
        getWindow().setEnterTransition(fade);
        Slide slide = new Slide();
        slide.setDuration(1000);
        getWindow().setExitTransition(slide);
    }

    // Свои вьюхи
    protected abstract void initViews2();

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (!onlyMyViews)
            mDrawerToggle.syncState();
    }

    //Задает id пункта меню для данной Activity
    public void setSelfMenuItem(int selfMenuItem) {
        this.selfMenuItem = selfMenuItem;
    }

    //Задает заголовок тулбара
    @Override
    public void setTitle(int id) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(id);
    }

    public void setImageTitle(int resourceId, boolean isCenter) {
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(resourceId, null);
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT);
        params.gravity = isCenter ? Gravity.CENTER : Gravity.START | Gravity.CENTER_VERTICAL;
        toolbar.addView(mCustomView, params);
    }

    //Задает ресурс макета
    public void setLayout(int layout) {
        this.layout = layout;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!onlyMyViews) {
            if (selfMenuItem != -1)
                mNavigationView.setCheckedItem(selfMenuItem);
        }

        myApp.setCurrentActivity(this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Menu menuNav = mNavigationView.getMenu();

        return true;
    }

    /// <summary>
    /// Скрыть клавиатуру
    /// </summary>
    /// <param name="view"></param>
    public void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public void setHideMainButtonInToolbar() {
        mDrawerToggle.setHomeAsUpIndicator(null);
    }

    public void removeToolbarBottomLine() {
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
    }

    @SuppressWarnings("deprecation")
    public void setVisibleNavigationButton() {
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(null);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    private int ScreenId = -1;

    public void setTypeScreen(int screenId) {
        ScreenId = screenId;
    }

    protected void onPause() {
        clearReferences();
        super.onPause();
    }

    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences() {
        Activity currActivity = myApp.getCurrentActivity();
        if (this.equals(currActivity))
            myApp.setCurrentActivity(null);
    }
}

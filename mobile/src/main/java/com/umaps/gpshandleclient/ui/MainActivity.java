package com.umaps.gpshandleclient.ui;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.MyApplication;
import com.umaps.gpshandleclient.event.UpdateEvent;
import com.umaps.gpshandleclient.util.EBus;
import com.umaps.gpshandleclient.util.StringTools;

import java.util.Calendar;

import de.greenrobot.event.EventBus;

public class MainActivity extends ActionBarActivity{
    private static final String TAG = "MainActivity";
    private Typeface mTf = null;
    private MyApplication mApplication;
    private Toolbar toolbar;

    private Drawer result = null;
    private AccountHeader headerResult = null;

    private void RegisterEventBus() {
        EventBus.getDefault().register(this);
    }

    private void UnregisterEventBus(){
        try {
            EventBus.getDefault().unregister(this);
        } catch (Throwable t){
            //this may crash if registration did not go through. just be safe
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RegisterEventBus();

        mApplication = MyApplication.getInstance();
        mTf = MyApplication.getIconFont();

        if (!isUserSignedIn()){
            startLoginActivity();
            finish();
        }
        setToolbar();

        IProfile profile = new ProfileDrawerItem().withName(mApplication.getAccountID()+"/"+mApplication.getUserID())
                .withEmail("hoaivubk@gmail.com")
                .withIcon("https://avatars3.githubusercontent.com/u/1476232?v=3&s=460");

        headerResult = new AccountHeaderBuilder().withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withSavedInstance(savedInstanceState)
                .addProfiles(profile)
                .build();

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.title_monitor).withIcon(FontAwesome.Icon.faw_globe).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.title_report).withIcon(FontAwesome.Icon.faw_bar_chart).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.title_administration).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(3),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.title_help_feedback).withIcon(FontAwesome.Icon.faw_support).withIdentifier(4),
                        new PrimaryDrawerItem().withName(R.string.title_logout).withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(5)

                ).withSavedInstance(savedInstanceState)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {

                        if (iDrawerItem != null) {
                            int iden = iDrawerItem.getIdentifier();
                            switch (iden) {
                                case 1:
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.container, MapFragment.newInstance())
                                            .addToBackStack("realTime").commit();
                                    break;
                                case 2:
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.container, ReportPager.newInstance())
                                            .commit();
                                    break;
                                case 3:
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.container, AdmDispatcher.newInstance())
                                            .commit();
                                    break;
                                case 4:
                                    break;
                                case 5:
                                    doLogout();
                                    break;
                            }
                        }
                        return false;
                    }
                })
                .build();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, MapFragment.newInstance())
                .addToBackStack("realTime").commit();
    }
    @Override
    protected void onDestroy() {
        UnregisterEventBus();
        super.onDestroy();

    }

    private void doLogout(){
        mApplication.setIsSignedIn(false);
        startLoginActivity();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    // retrieve access token from preferences
    public boolean isUserSignedIn() {
        boolean hasUserData =
                (
                    (!StringTools.isBlank(mApplication.getAccountID())) &&
                    (!StringTools.isBlank(mApplication.getUserID())) &&
                    (!StringTools.isBlank(mApplication.getPassword()))
                ) || (!StringTools.isBlank(mApplication.getToken()));
        long currentTime = Calendar.getInstance().getTimeInMillis()/1000;
        long expireOn = mApplication.getExpireOn();

        Log.i(TAG, "Current Time: " + currentTime);
        Log.i(TAG, "Expired On: " + expireOn);

        boolean isExpired = (expireOn <= currentTime);

        return !isExpired && hasUserData && mApplication.isSignedIn();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    private void setToolbar(){
        //-- set toolbar
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @EBus
    public void onEventMainThread(UpdateEvent.OnLoading onLoading) {
        onUpdating(onLoading.isLoading());
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void onUpdating(final boolean show) {
        final ProgressBar mBarProgress = (ProgressBar) findViewById(R.id.progress);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mBarProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mBarProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mBarProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mBarProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public void showProgress(final boolean show) {

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


}

package com.umaps.gpshandleclient;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.umaps.gpshandleclient.ui.activity.LoginActivity;
import com.umaps.gpshandleclient.ui.activity.MonitorActivity;
import com.umaps.gpshandleclient.util.StringTools;
import com.umaps.gpssdk.GpsSdk;

import java.util.Calendar;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DispatcherActivity extends AppCompatActivity {
    private static final int UI_ANIMATION_DELAY = 300;
    private View mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dispatcher);
        mContentView = findViewById(R.id.fullscreen_content);

        if (!isUserSignedIn()) {
            dispatch(LoginActivity.class);
            finish();
            return;
        } else {
            dispatch(MonitorActivity.class);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    //-- custom code
    public boolean isUserSignedIn() {
        boolean hasUserData =
                (
                        (!StringTools.isBlank(GpsSdk.getAccountId())) &&
                                (!StringTools.isBlank(GpsSdk.getUserId())) &&
                                (!StringTools.isBlank(GpsSdk.getUserPassword()))
                ) || (!StringTools.isBlank(GpsSdk.getSessionToken()));
        long currentTime = Calendar.getInstance().getTimeInMillis() / 1000;
        long expireOn = GpsSdk.getTokenExpired();
        boolean isExpired = (expireOn <= currentTime);
        return !isExpired && hasUserData && MyApplication.isSignedIn();
    }

    private void dispatch(Class cls) {
        Intent intent = new Intent(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
}
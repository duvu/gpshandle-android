package com.umaps.gpshandleclient.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.MyApplication;
import com.umaps.gpshandleclient.model.ParseGroup;
import com.umaps.gpshandleclient.model.ParseLoginEvent;
import com.umaps.gpshandleclient.util.StringTools;
import com.umaps.gpssdk.GpsRequest;
import com.umaps.gpssdk.GpsSdk;
import com.umaps.gpssdk.Listener;
import com.umaps.gpssdk.MyResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vu@umaps.vn on 01/02/2015.
 */
public class LoginActivity extends FragmentActivity {
    private static final String TAG = "LoginActivity";
    //Typeface mTf = null;
    private View mLoginForm;
    private View mBarProgress;
    private View mProgress;
    private GpsRequest groupRequest;

    MyApplication mApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = MyApplication.getInstance();
        ParseObject.unpinAllInBackground("pinned");
        Typeface mTf = MyApplication.getIconFont();
        setContentView(R.layout.activity_login);

        //-- save application setting
        TextView icAccount = (TextView) findViewById(R.id.ic_account);
        icAccount.setTypeface(mTf);
        icAccount.setText(String.valueOf((char) 0xe624));
        TextView icUser = (TextView) findViewById(R.id.ic_user);
        icUser.setTypeface(mTf);
        icUser.setText(String.valueOf((char) 0xe704));
        TextView icKey = (TextView) findViewById(R.id.ic_password);
        icKey.setTypeface(mTf);
        icKey.setText(String.valueOf((char) 0xe64c));

        //-- save application setting
        final EditText edtAccount = (EditText) findViewById(R.id.edt_account);
        edtAccount.setText(GpsSdk.getAccountId());

        final EditText edtUser = (EditText) findViewById(R.id.edt_user);
        edtUser.setText(GpsSdk.getUserId());

        final EditText edtPassword = (EditText) findViewById(R.id.edt_password);
        edtPassword.setText(GpsSdk.getUserPassword());

        Button btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String accountID    = edtAccount.getText().toString();
                final String userID       = edtUser.getText().toString();
                String password     = edtPassword.getText().toString();
                if ((StringTools.isBlank(accountID)) ||
                  (StringTools.isBlank(userID)) ||
                  (StringTools.isBlank(password))) {
                    Toast.makeText(getApplicationContext(), R.string.failure_login, Toast.LENGTH_SHORT).show();
                    return;
                }

                //-- save to GpsSdkState
                GpsSdk.setAccountId(accountID);
                GpsSdk.setUserId(userID);
                GpsSdk.setUserPassword(password);

                //-- getting data and store
                getData();
            }
        });
        mLoginForm = findViewById(R.id.login_form);
        mProgress = findViewById(R.id.progress);
        mBarProgress = findViewById(R.id.bar_progress);
    }

    //-- tokenRequest --> aclRequest --> accountRequest --> userRequest --> groupRequest
    private void getData() {
        //-- getToken
        GpsRequest.getToken(new Listener<MyResponse>() {
            @Override
            public void onResponse(MyResponse response) {
                if (response.isError()) {
                    showProgress(false);
                    Toast.makeText(getApplicationContext(), getText(R.string.failure_login), Toast.LENGTH_LONG).show();
                    return;
                } else {
                    mApplication.setIsSignedIn(true);
                    saveEventLogin(GpsSdk.getAccountId(), GpsSdk.getUserId());

                    if (response.isError()) return;
                    JSONArray j = (JSONArray) response.getData();
                    List<ParseGroup> list = new ArrayList<ParseGroup>();
                    for (int i = 0; i < j.length(); i++) {
                        try {
                            JSONObject jo = j.getJSONObject(i);
                            ParseGroup p = ParseGroup.parseGroup(jo);
                            list.add(p);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    ParseObject.pinAllInBackground("pinned", list, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            //store
                            showProgress(false);
                            mApplication.storeSettings();
                            Intent movingIntent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(movingIntent);
                            finish();
                        }
                    });

                }
            }
        });
        showProgress(true);
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        hideKeyboard();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mBarProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mBarProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mBarProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mBarProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void saveEventLogin(String accountID, String userID) {
        ParseLoginEvent pEvent = new ParseLoginEvent();
        pEvent.setLoginAccount(accountID);
        pEvent.setLoginUser(userID);
        pEvent.setTimestamp(System.currentTimeMillis() / 1000);
        pEvent.setStatus(true);
        pEvent.saveEventually();
    }

    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view!=null){
            InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}

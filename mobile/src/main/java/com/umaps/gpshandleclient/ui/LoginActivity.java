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

import com.android.volley.Request;
import com.android.volley.Response;
import com.parse.ParseObject;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.MyApplication;
import com.umaps.gpshandleclient.model.MyResponse;
import com.umaps.gpshandleclient.model.ParseGroup;
import com.umaps.gpshandleclient.model.ParseLoginEvent;
import com.umaps.gpshandleclient.util.StringTools;
import com.umaps.gpssdk.GpsRequest;
import com.umaps.gpssdk.GpsSdk;
import com.umaps.gpssdk.model.Account;
import com.umaps.gpssdk.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by vu@umaps.vn on 01/02/2015.
 */
public class LoginActivity extends FragmentActivity {
    private static final String TAG = "LoginActivity";
    //Typeface mTf = null;
    private View mLoginForm;
    private View mBarProgress;
    private View mProgress;
    private GpsRequest tokenRequest;
    private GpsRequest aclRequest;
    private GpsRequest accountRequest;
    private GpsRequest userRequest;
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

        aclRequest = new GpsRequest(this);
        tokenRequest = new GpsRequest(this);
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
        //-- get groups and store in local database
        groupRequest = GpsRequest.getGroupRequest(LoginActivity.this);
        groupRequest.setResponseHandler(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MyResponse mRes = new MyResponse(response);
                if (mRes.isError()) return;
                JSONArray j = (JSONArray) mRes.getData();
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
                ParseObject.pinAllInBackground("pinned", list);
                //store
                showProgress(false);
                mApplication.storeSettings();
                Intent movingIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(movingIntent);
                finish();
            }
        });
        //-- get user-info
        userRequest = GpsRequest.getUserRequest(LoginActivity.this);
        userRequest.setResponseHandler(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MyResponse mRes = new MyResponse(response);
                if (mRes.isError()) return;
                JSONObject result = (JSONObject) mRes.getData();

                //-- store: contactPhone, contactName, contactEmail, description, displayName, creationTime, lastLoginTime
                User u = new User(result);
                GpsSdk.setDisplayName(u.getDisplayName());
                GpsSdk.setDescription(u.getDescription());
                GpsSdk.setContactName(u.getContactName());
                GpsSdk.setContactPhone(u.getContactPhone());
                GpsSdk.setContactEmail(u.getContactEmail());
                GpsSdk.setCreationTime(u.getCreationTime());
                GpsSdk.setLastLoginTime(u.getLastLoginTime());
                saveEventLogin(GpsSdk.getAccountId(), GpsSdk.getUserId());
                groupRequest.exec();
            }
        });

        //-- get account-info
        accountRequest = GpsRequest.getAccountRequest(LoginActivity.this);
        String[] lf = new String[] {"isAccountManager", "deviceCount"};
        JSONObject accParam = Account.createParam(lf);
        accountRequest.setParams(accParam);
        accountRequest.setResponseHandler(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MyResponse mRes = new MyResponse(response);
                if (mRes.isError()) return;
                JSONObject result = (JSONObject) mRes.getData();
                Account account = new Account(result);
                boolean isManager = account.isManager();
                int count = account.getDevice_count();
                GpsSdk.setAccountManager(isManager);
                GpsSdk.setTotalDevices(count);
                userRequest.exec();
            }
        });
        accountRequest.setErrorHandler();

        aclRequest = GpsRequest.getAclRequest(LoginActivity.this);
        JSONObject jsonParams = new JSONObject();
        aclRequest.setParams(jsonParams);
        aclRequest.setResponseHandler(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MyResponse mResponse = new MyResponse(response);
                if (mResponse.isError()) {
                    Toast.makeText(LoginActivity.this, mResponse.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                JSONArray aclList = (JSONArray) mResponse.getData();
                if (aclList == null) return;
                for (int i = 0; i < aclList.length(); i++) {
                    JSONObject acl = null;
                    try {
                        acl = aclList.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        continue;
                    }
                    mApplication.storeAcls(acl);
                }
                accountRequest.exec();

            }
        });
        aclRequest.setErrorHandler();

        //-- getToken
        tokenRequest = GpsRequest.getTokenRequest(LoginActivity.this);
        JSONObject params = new JSONObject();
        try {
            params.put(GpsRequest.KEY_ACCOUNT_ID, GpsSdk.getAccountId());
            params.put(GpsRequest.KEY_USER_ID, GpsSdk.getUserId());
            params.put(GpsRequest.KEY_PASSWORD, GpsSdk.getUserPassword());
            params.put(GpsRequest.KEY_LOCALE, Locale.getDefault().getLanguage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //tokenRequest.setParams(params);
        //tokenRequest.setUrl(GpsRequest.TOKEN_URL);
        //tokenRequest.setMethod(Request.Method.POST);
        Response.Listener<JSONObject> tokeHanler = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Log.d(TAG, response.toString());
                MyResponse mRes = new MyResponse(response);
                if (mRes.isError()){
                    showProgress(false);
                    Toast.makeText(getApplicationContext(), getText(R.string.failure_login), Toast.LENGTH_LONG).show();
                    return;
                }
                String token = null;
                long expiredOn = 0;
                try {
                    token = ((JSONObject)mRes.getData()).getString("token");
                    expiredOn = ((JSONObject)mRes.getData()).getLong("expireOn");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mApplication.setIsSignedIn(true);
                GpsSdk.setSessionToken(token);
                GpsSdk.setTokenExpired(expiredOn);
                aclRequest.exec();
            }
        };
        tokenRequest.setResponseHandler(tokeHanler);
        //tokenRequest.setErrorHandler();
        tokenRequest.exec(params);
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

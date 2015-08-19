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
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.MyApplication;
import com.umaps.gpshandleclient.Session;
import com.umaps.gpshandleclient.model.Account;
import com.umaps.gpshandleclient.model.MyResponse;
import com.umaps.gpshandleclient.model.ParseLoginEvent;
import com.umaps.gpshandleclient.util.GpsRequest;
import com.umaps.gpshandleclient.util.StringTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private GpsRequest mRequestToken;
    private GpsRequest mRequestAcl;
    private GpsRequest mRequestAcc;

    MyApplication mApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = MyApplication.getInstance();

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
        edtAccount.setText(Session.getAccountId());

        final EditText edtUser = (EditText) findViewById(R.id.edt_user);
        edtUser.setText(Session.getUserId());

        final EditText edtPassword = (EditText) findViewById(R.id.edt_password);
        edtPassword.setText(Session.getUserPassword());

        mRequestAcl = new GpsRequest(this);
        mRequestToken = new GpsRequest(this);
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

                //-- save to SessionState
                Session.setAccountId(accountID);
                Session.setUserId(userID);
                Session.setUserPassword(password);

                mRequestAcc = GpsRequest.getAccountRequest(LoginActivity.this); //new GpsRequest(this);
                String[] lf = new String[] {"isAccountManager", "contactEmail"};
                JSONObject accParam = Account.createParam(lf);
                mRequestAcc.setParams(accParam);
                mRequestAcc.setResponseHandler(new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        MyResponse mRes = new MyResponse(response);
                        if (mRes.isError()) return;
                        JSONObject result = (JSONObject)mRes.getData();
                        try {
                            boolean isManager = result.getBoolean("isAccountManager");
                            mApplication.setManager(isManager);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mApplication.storeSettings();
                        showProgress(false);
                        Intent movingIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(movingIntent);

                        ParseLoginEvent pEvent = new ParseLoginEvent();
                        pEvent.setLoginAccount(accountID);
                        pEvent.setLoginUser(userID);
                        pEvent.setTimestamp(System.currentTimeMillis()/1000);
                        pEvent.setStatus(true);
                        pEvent.saveEventually();

                        finish();
                    }
                });
                mRequestAcc.setErrorHandler(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO
                    }
                });


                mRequestAcl.setAccountID(accountID);
                mRequestAcl.setUserID(userID);
                mRequestAcl.setPassword(password);
                mRequestAcl.setCommand(GpsRequest.CMD_GET_USER_ACL);
                mRequestAcl.setMethod(Request.Method.POST);
                mRequestAcl.setUrl(GpsRequest.ADMIN_URL);
                JSONObject jsonParams = new JSONObject();
                mRequestAcl.setParams(jsonParams);
                mRequestAcl.setResponseHandler(new Response.Listener<JSONObject>() {
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
                        mRequestAcc.exec();

                    }
                });
                mRequestAcl.setErrorHandler(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO
                    }
                });
                //-- getToken
                mRequestToken.setAccountID(accountID);
                mRequestToken.setUserID(userID);
                mRequestToken.setPassword(password);
                JSONObject params = new JSONObject();
                try {
                    params.put(GpsRequest.KEY_ACCOUNT_ID, accountID);
                    params.put(GpsRequest.KEY_USER_ID, userID);
                    params.put(GpsRequest.KEY_PASSWORD, password);
                    params.put(GpsRequest.KEY_LOCALE, Locale.getDefault().getLanguage());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //mRequestToken.setParams(params);
                mRequestToken.setUrl(GpsRequest.TOKEN_URL);
                mRequestToken.setMethod(Request.Method.POST);
                Response.Listener<JSONObject> tokeHanler = new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, response.toString());
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
                        Session.setSessionToken(token);
                        mApplication.setExpireOn(expiredOn);
                        //mApplication.storeSettings();
                        mRequestAcl.exec();
                    }
                };
                mRequestToken.setResponseHandler(tokeHanler);
                Response.ErrorListener errorHandler = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        Log.i(TAG, error.toString());
                        Toast.makeText(getApplicationContext(), R.string.failure_login, Toast.LENGTH_LONG).show();
                    }
                };
                mRequestToken.setErrorHandler(errorHandler);
                mRequestToken.exec(params);
                showProgress(true);
            }
        });
        mLoginForm = findViewById(R.id.login_form);
        mProgress = findViewById(R.id.progress);
        mBarProgress = findViewById(R.id.bar_progress);
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

    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view!=null){
            InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}

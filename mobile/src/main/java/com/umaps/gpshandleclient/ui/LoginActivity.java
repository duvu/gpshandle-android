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
import com.umaps.gpshandleclient.model.MyResponse;
import com.umaps.gpshandleclient.util.GpsOldRequest;
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
    private GpsOldRequest mRequest;
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
        edtAccount.setText(mApplication.getAccountID());

        final EditText edtUser = (EditText) findViewById(R.id.edt_user);
        edtUser.setText(mApplication.getUserID());

        final EditText edtPassword = (EditText) findViewById(R.id.edt_password);
        edtPassword.setText(mApplication.getPassword());


        mRequest = new GpsOldRequest(this);
        Button btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accountID    = edtAccount.getText().toString();
                String userID       = edtUser.getText().toString();
                String password     = edtPassword.getText().toString();
                if ((StringTools.isBlank(accountID)) ||
                  (StringTools.isBlank(userID)) ||
                  (StringTools.isBlank(password))) {
                    Toast.makeText(getApplicationContext(), R.string.failure_login, Toast.LENGTH_SHORT).show();
                    return;
                }

                //-- save to SessionState
                mApplication.setAccountID(accountID);
                mApplication.setUserID(userID);
                mApplication.setPassword(password);


                mRequest.setAccountID(accountID);
                mRequest.setUserID(userID);
                mRequest.setPassword(password);
                //mRequest.updateToken();
                JSONObject params = new JSONObject();
                try {
                    params.put(GpsOldRequest.KEY_ACCOUNT_ID, accountID);
                    params.put(GpsOldRequest.KEY_USER_ID, userID);
                    params.put(GpsOldRequest.KEY_PASSWORD, password);
                    params.put(GpsOldRequest.KEY_LOCALE, Locale.getDefault().getLanguage());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //mRequest.setParams(params);
                mRequest.setUrl(GpsOldRequest.TOKEN_URL);
                mRequest.setMethod(Request.Method.POST);
                Response.Listener<JSONObject> tokeHanler = new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, response.toString());
                        MyResponse mRes = new MyResponse(response);
                        if (mRes.isError()){
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
                        mApplication.setToken(token);
                        mApplication.setExpireOn(expiredOn);

                        Log.w(TAG, "Expire: " + expiredOn);

                        mApplication.storeSettings();

                        Intent movingIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(movingIntent);
                        showProgress(false);
                        finish();
                    }
                };
                mRequest.setResponseHandler(tokeHanler);

                /*mRequest.setCommand(GpsOldRequest.CMD_GET_USER_ACL);
                mRequest.setMethod(Request.Method.POST);
                mRequest.setUrl(GpsOldRequest.ADMIN_URL);
                JSONObject jsonParams = new JSONObject();
                mRequest.setParams(jsonParams);
                Response.Listener<JSONObject> responseHandler = new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        MyResponse mResponse = new MyResponse(response);
                        if (mResponse.isError()) {
                            Toast.makeText(LoginActivity.this, mResponse.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        JSONArray aclList = (JSONArray) mResponse.getData();
                        if (aclList == null) return;
                        for (int i = 0; i< aclList.length(); i++){
                            JSONObject acl = null;
                            try {
                                acl = aclList.getJSONObject(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                continue;
                            }
                            mApplication.storeAcls(acl);
                        }

                        mApplication.storeSettings();
                        Intent movingIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(movingIntent);
                        showProgress(false);
                        finish();
                    }
                };*/
                Response.ErrorListener errorHandler = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        Log.i(TAG, error.toString());
                        Toast.makeText(getApplicationContext(), R.string.failure_login, Toast.LENGTH_LONG).show();
                    }
                };
                //mRequest.setResponseHandler(responseHandler);
                mRequest.setErrorHandler(errorHandler);
                mRequest.exec(params);
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
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
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
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
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

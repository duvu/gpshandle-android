package com.umaps.gpshandleclient.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umaps.gpshandleclient.MyApplication;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.model.Account;
import com.umaps.gpshandleclient.model.MyResponse;
import com.umaps.gpshandleclient.model.User;
import com.umaps.gpshandleclient.util.GpsOldRequest;
import com.umaps.gpshandleclient.util.StringTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by beou on 08/06/2015.
 */
public class AdmAccount extends Fragment {
    private static final String TAG = "AdmAccount";

    private GpsOldRequest mRequestAccount;
    private GpsOldRequest mRequestUser;
    private static final String TAG_REQUEST = "admAccount";
    private View mBarProgress;
    private View mProgress;
    private View mLayoutAccount;
    private MyApplication mApplication;
    public static AdmAccount newInstance(){
        return new AdmAccount();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_adm_account, container, false);
        //-- for progress-bar
        mBarProgress = view.findViewById(R.id.bar_progress);
        mProgress = view.findViewById(R.id.progress);
        mLayoutAccount = view.findViewById(R.id.layout_account);
        mApplication = MyApplication.getInstance();

        mRequestAccount = new GpsOldRequest(getActivity());
        mRequestAccount.setAccountID(mApplication.getAccountID());
        mRequestAccount.setUserID(mApplication.getUserID());
        mRequestAccount.setPassword(mApplication.getPassword());
        mRequestAccount.setUrl(GpsOldRequest.ADMIN_URL);
        mRequestAccount.setMethod(Request.Method.POST);
        mRequestAccount.setCommand(GpsOldRequest.CMD_GET_ACCOUNT);

        mRequestUser = new GpsOldRequest(getActivity());
        mRequestUser.setAccountID(mApplication.getAccountID());
        mRequestUser.setUserID(mApplication.getUserID());
        mRequestUser.setPassword(mApplication.getPassword());
        mRequestUser.setUrl(GpsOldRequest.ADMIN_URL);
        mRequestUser.setMethod(Request.Method.POST);
        mRequestUser.setCommand(GpsOldRequest.CMD_GET_USERS);

        List<String> fields = new ArrayList<>();
        fields.add("accountID");
        fields.add("description");
        fields.add("displayName");
        fields.add("contactName");
        fields.add("contactEmail");
        fields.add("contactPhone");
        fields.add("deviceCount");
        fields.add("lastLoginTime");
        fields.add("creationTime");
        JSONObject params = new JSONObject();
        try {
            params.put(StringTools.KEY_FIELDS, new JSONArray(fields));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mRequestUser.setParams(null);
        mRequestAccount.setParams(params);

        mRequestAccount.setResponseHandler(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                MyResponse mRes = new MyResponse(response);
                if (mRes.isError()) return;

                Account account = new Account((JSONObject) mRes.getData());
                TextView txtAccountId = (TextView) view.findViewById(R.id.txt_account_id_content);
                txtAccountId.setText(account.getId());
                TextView txtAccountDesc = (TextView) view.findViewById(R.id.txt_account_desc_content);
                txtAccountDesc.setText(account.getDescription());
                TextView txtAccountDisplay = (TextView) view.findViewById(R.id.txt_account_display_content);
                txtAccountDisplay.setText(account.getDisplay_name());
                TextView txtContactName = (TextView) view.findViewById(R.id.txt_account_contact_name_content);
                txtContactName.setText(account.getContact_name());
                TextView txtContactEmail = (TextView) view.findViewById(R.id.txt_account_contact_email_content);
                txtContactEmail.setText(account.getContact_email());
                TextView txtContactPhone = (TextView) view.findViewById(R.id.txt_account_contact_phone_content);
                txtContactPhone.setText(account.getContact_phone());
                TextView txtDeviceCount = (TextView) view.findViewById(R.id.txt_account_device_count_content);
                txtDeviceCount.setText("" + account.getDevice_count());
                TextView txtLastLogin = (TextView) view.findViewById(R.id.txt_account_last_login_content);
                Date lastLogin = new Date(account.getLast_login_time() * 1000);
                txtLastLogin.setText(lastLogin.toString());
                TextView txtCreatedOn = (TextView) view.findViewById(R.id.txt_account_creation_content);
                Date createdOn = new Date(account.getCreation_time() * 1000);
                txtCreatedOn.setText(createdOn.toString());
                if (!mApplication.isCurrentAdmin()) {
                    View layout_user = view.findViewById(R.id.layout_user);
                    layout_user.setVisibility(View.VISIBLE);
                    mRequestUser.setRequestTag(TAG_REQUEST);
                    mRequestUser.exec();
                } else {
                    showProgress(false);
                }
            }
        });
        mRequestAccount.setErrorHandler(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        //-- get User Informations
        mRequestUser.setResponseHandler(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MyResponse mRes = new MyResponse(response);
                if (mRes.isError()) return;
                User user = new User((JSONObject)mRes.getData());

                TextView txtUserId = (TextView) view.findViewById(R.id.txt_user_id_content);
                txtUserId.setText(user.getId());
                TextView txtUserDesc = (TextView) view.findViewById(R.id.txt_user_desc_content);
                txtUserDesc.setText(user.getDescription());
                TextView txtUserDisp = (TextView) view.findViewById(R.id.txt_user_disp_content);
                txtUserDisp.setText(user.getDisplayName());
                TextView txtUserContactName = (TextView) view.findViewById(R.id.txt_user_contact_name_content);
                txtUserContactName.setText(user.getContactName());
                TextView txtUserContactEmail = (TextView) view.findViewById(R.id.txt_user_contact_email_content);
                txtUserContactEmail.setText(user.getContactEmail());
                TextView txtUserContactPhone = (TextView) view.findViewById(R.id.txt_user_contact_phone_content);
                txtUserContactPhone.setText(user.getContactPhone());
                TextView txtUserContactGender = (TextView) view.findViewById(R.id.txt_user_contact_gender_content);
                txtUserContactGender.setText(user.getContactGender());
                showProgress(false);
            }
        });
        mRequestUser.setErrorHandler(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        mRequestAccount.setRequestTag(TAG_REQUEST);
        mRequestAccount.exec();


        showProgress(true);
        return view;
    }
    @Override
    public void onDetach(){
        super.onDetach();
        //mChart = null;
        mBarProgress = null;
        mProgress = null;
        mRequestAccount.cancel(TAG_REQUEST);
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLayoutAccount.setVisibility(show ? View.GONE : View.VISIBLE);
            mLayoutAccount.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLayoutAccount.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLayoutAccount.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

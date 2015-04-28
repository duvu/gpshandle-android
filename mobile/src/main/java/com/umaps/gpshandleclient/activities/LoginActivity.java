package com.umaps.gpshandleclient.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.settings.ApplicationSettings;
import com.umaps.gpshandleclient.settings.SessionState;
import com.umaps.gpshandleclient.settings.Utilities;
import com.umaps.gpshandleclient.util.HTTPRequestQueue;
import com.umaps.gpshandleclient.util.StringTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by vu@umaps.vn on 01/02/2015.
 */
public class LoginActivity extends FragmentActivity {
    private static final String TAG = "LoginActivity";
    Typeface mTf = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra("EXIT", false)){
            finish();
            return;
        }
        mTf = Typeface.createFromAsset(getAssets(), "icomoon.ttf");
        //-- update saved settings
        Utilities.populateSettings(getApplicationContext());
        setContentView(R.layout.activity_login);
        //-- spinner
        ApplicationSettings.setServerURL("http://secure.gpshandle.com:8080/ws");

        //-- save application setting
        TextView icAccount  = (TextView) findViewById(R.id.ic_account);
        TextView icUser     = (TextView) findViewById(R.id.ic_user);
        TextView icKey      = (TextView) findViewById(R.id.ic_key);
        icAccount.setTypeface(mTf);
        icUser.setTypeface(mTf);
        icKey.setTypeface(mTf);
        icAccount.setText(String.valueOf((char)0x61));
        icUser.setText(String.valueOf((char)0x71));
        icKey.setText(String.valueOf((char)0x67));

        //-- save application setting
        final EditText edtAccount = (EditText) findViewById(R.id.account_name);
        edtAccount.setText(SessionState.getAccountID());

        final EditText edtUser = (EditText) findViewById(R.id.user_name);
        edtUser.setText(SessionState.getUserID());

        final EditText edtPassword = (EditText) findViewById(R.id.password);
        edtPassword.setText(SessionState.getPassword());

        Button btnLogin = (Button) findViewById(R.id.btnlogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //--show and save a progressDialog to SessionState
                Utilities.ShowProgress(LoginActivity.this,"", getString(R.string.application_loading));

                String accountID = edtAccount.getText().toString();
                String userID = edtUser.getText().toString();
                String password = edtPassword.getText().toString();
                if ((StringTools.isBlank(accountID)) ||
                        (StringTools.isBlank(userID)) ||
                        (StringTools.isBlank(password)))
                {
                    Toast.makeText(getApplicationContext(), R.string.failure_login, Toast.LENGTH_SHORT);
                }
                {
                    //-- save to SessionState
                    SessionState.setAccountID(accountID);
                    SessionState.setUserID(userID);
                    SessionState.setPassword(password);
                }
                JSONObject jsonRequest = null;
                try {
                    jsonRequest = StringTools.createRequest(
                            StringTools.CMD_GET_USER_ACL,
                            Locale.getDefault().getLanguage(),
                            null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        ApplicationSettings.getAdministrationUrl(),
                        jsonRequest,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsoResponse) {
                                Utilities.HideProgress();
                                if (!okResult(jsoResponse)) {
                                    Toast.makeText(getApplicationContext(), R.string.failure_login, Toast.LENGTH_LONG);
                                    return;
                                }
                                JSONArray aclList = null;
                                try {
                                    aclList = jsoResponse.getJSONArray(StringTools.KEY_RESULTS);
                                    if (aclList == null) return;
                                    for (int i = 0; i< aclList.length(); i++){
                                        JSONObject acl = aclList.getJSONObject(i);
                                        setAcl(acl);
                                    }
                                    //-- move forward here
                                    Intent movingIntent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(movingIntent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    return;
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Utilities.HideProgress();
                                Toast.makeText(getApplicationContext(), R.string.failure_login, Toast.LENGTH_LONG);
                            }
                        });
                HTTPRequestQueue.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
            }
        });
    }


    private void setAcl(JSONObject acl) throws JSONException {
        String name = acl.getString("name");
        int value = acl.getInt("value");
        String description = acl.getString("description");

        if (name.equalsIgnoreCase(Utilities.ACL_ADMIN_ACCOUNT)){ SessionState.setAclAdminAccount(value); }
        else if (name.equalsIgnoreCase(Utilities.ACL_ADMIN_DEVICE)){ SessionState.setAclAdminDevice(value);}
        else if (name.equalsIgnoreCase(Utilities.ACL_ADMIN_DRIVER)){SessionState.setAclAdminDriver(value);}
        else if (name.equalsIgnoreCase(Utilities.ACL_ADMIN_GEOZONE)){SessionState.setAclAdminGeozone(value);}
        else if (name.equalsIgnoreCase(Utilities.ACL_ADMIN_GROUP)){SessionState.setAclAdminGroup(value);}
        else if (name.equalsIgnoreCase(Utilities.ACL_ADMIN_ROLE)){SessionState.setAclAdminRole(value);}
        else if (name.equalsIgnoreCase(Utilities.ACL_ADMIN_RULE)){SessionState.setAclAdminRule(value);}
        else if (name.equalsIgnoreCase(Utilities.ACL_ADMIN_USER)){SessionState.setAclAdminUser(value);}
        else if (name.equalsIgnoreCase(Utilities.ACL_ADMIN_USER_MANAGER)) {SessionState.setAclAdminUserManager(value);}
        else if (name.equalsIgnoreCase(Utilities.ACL_MAP_HISTORY)) {SessionState.setAclMapHistory(value);}
        else if (name.equalsIgnoreCase(Utilities.ACL_MAP_MONITOR)) {SessionState.setAclMapMonitor(value);}
        else if (name.equalsIgnoreCase(Utilities.ACL_REPORT_DETAIL)) {SessionState.setAclReportDetail(value);}
        else if (name.equalsIgnoreCase(Utilities.ACL_REPORT_GEOZONE)) {SessionState.setAclReportGeozone(value);}
        else if (name.equalsIgnoreCase(Utilities.ACL_REPORT_PARKING)) {SessionState.setAclReportParking(value);}
        else if (name.equalsIgnoreCase(Utilities.ACL_REPORT_SUMMARY)) {SessionState.setAclReportSummary(value);}
    }

    public static final String STATUS_CODE          = "code";
    public static final String CODE_SUCCESSFUL      = "successful";
    public boolean okResult(JSONObject jsonObject){
        //-- Check return status first
        try {
            JSONObject jsonStatus = jsonObject.getJSONObject(StringTools.KEY_STATUS);
            if (jsonStatus.getString(STATUS_CODE).equalsIgnoreCase(CODE_SUCCESSFUL)) return true;
            else return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
}

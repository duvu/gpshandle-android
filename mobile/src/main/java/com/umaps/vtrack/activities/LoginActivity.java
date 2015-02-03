package com.umaps.vtrack.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.umaps.vtrack.R;
import com.umaps.vtrack.settings.ApplicationSettings;
import com.umaps.vtrack.settings.SessionState;
import com.umaps.vtrack.util.GpsHandleHTTPAsyncImpl;
import com.umaps.vtrack.util.HTTPDelegateInterface;
import com.umaps.vtrack.util.StringTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by vu@umaps.vn on 01/02/2015.
 */
public class LoginActivity extends BaseActivity implements HTTPDelegateInterface {
    GpsHandleHTTPAsyncImpl httpAsync;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        httpAsync = new GpsHandleHTTPAsyncImpl(this);

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
                showProgressDialog(R.string.application_loading);
                String accountID = edtAccount.getText().toString();
                String userID = edtUser.getText().toString();
                String password = edtPassword.getText().toString();
                ApplicationSettings.setServerURL("http://dev.gpshandle.com/ws/");
                if((StringTools.isBlank(accountID))||(StringTools.isBlank(userID))||(StringTools.isBlank(password))){
                    //Toast.makeText(getApplicationContext(), R.string.invalid_credent, Toast.LENGTH_LONG).show();
                    //return;
                    accountID = "umaps";
                    userID = "admin";
                    password = "qwerty123";
                }
                try {
                    httpAsync.checkAuth(getApplicationContext(),
                            accountID,
                            userID,
                            password,
                            Locale.getDefault().getLanguage());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //--update application settings
                SessionState.setAccountID(accountID);
                SessionState.setUserID(userID);
                SessionState.setPassword(password);
            }
        });
    }

    @Override
    public void onFinish(JSONObject output) {
        //--Check authentication
        if(okResult(output)){
            //--save ACL of the user to sessionState
            //--go main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            cancelProgressDialog();
            showToast(R.string.failure_login);
        }
    }
}

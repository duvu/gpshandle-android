package com.umaps.gpshandleclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.settings.ApplicationSettings;
import com.umaps.gpshandleclient.settings.SessionState;
import com.umaps.gpshandleclient.util.GpsHandleHTTPAsyncImpl;
import com.umaps.gpshandleclient.util.HTTPDelegateInterface;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vu@umaps.vn on 01/02/2015.
 */
public class LoginActivity extends BaseActivity implements HTTPDelegateInterface {
    GpsHandleHTTPAsyncImpl httpAsync;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        httpAsync = GpsHandleHTTPAsyncImpl.getInstance();
        httpAsync.setDelegate(this);

        //-- save application setting
        final EditText edtAccount = (EditText) findViewById(R.id.account_name);
            edtAccount.setText(ApplicationSettings.getsAccount());
        final EditText edtUser = (EditText) findViewById(R.id.user_name);
            edtUser.setText(ApplicationSettings.getsUser());
        final EditText edtPassword = (EditText) findViewById(R.id.password);
            edtPassword.setText(ApplicationSettings.getsPassword());

        Button btnLogin = (Button) findViewById(R.id.btnlogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionState.setProgressDialog(showProgressDialog(R.string.application_loading));
                String accountID = edtAccount.getText().toString();
                String userID = edtUser.getText().toString();
                String password = edtPassword.getText().toString();
                ApplicationSettings.setServerURL("http://dev.gpshandle.com/ws/");
                if((accountID==null)||(userID==null)||(password==null)){
                    Toast.makeText(getApplicationContext(), R.string.invalid_credent, Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    httpAsync.checkAuth(getApplicationContext(), accountID, userID, password, ApplicationSettings.getLocale());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //--update application settings
                ApplicationSettings.setsAccount(accountID);
                ApplicationSettings.setsUser(userID);
                ApplicationSettings.setsPassword(password);


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

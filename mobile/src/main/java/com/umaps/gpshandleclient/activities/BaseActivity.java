package com.umaps.gpshandleclient.activities;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.umaps.gpshandleclient.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vu@umaps.vn on 30/01/2015.
 */
public class BaseActivity extends FragmentActivity {
    public static final String STATUS_CODE          = "code";
    public static final String CODE_SUCCESSFUL      = "successful";


    public static ProgressDialog progressDialog = null;
    public static Toast toast = null;

    public final void showProgressDialog(final String message){
        if(progressDialog==null || !progressDialog.isShowing()){
            try {
                progressDialog = ProgressDialog.show(this,"", message);
            }catch (OutOfMemoryError ie){
                onLowMemory();
            }
        }
    }
    public ProgressDialog showProgressDialog(final int resID){
        if(progressDialog==null || !progressDialog.isShowing()){
            try {
                progressDialog = ProgressDialog.show(this, "", getText(resID));
            } catch (OutOfMemoryError ie){
                onLowMemory();
            }
        }
        return progressDialog;
    }

    public final void cancelProgressDialog(){
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog=null;
        }
    }

    public final void showToast(final CharSequence message){
        showToast(message, Toast.LENGTH_LONG);
    }
    public final void showToast(final int resID){
        showToast(resID, Toast.LENGTH_LONG);
    }

    public final void showToast(final CharSequence message, int duration){
        if(toast!=null){
            //Cancel last message toast
            if(toast.getView().isShown()){
                toast.cancel();
            }
            toast.setText(message);
            toast.show();
        }else {
            toast = Toast.makeText(this, message, duration);
            toast.show();
        }
    }
    public final void showToast(final int resID, int duration){
        if(toast!=null){
            //Cancel last message toast
            if(toast.getView().isShown()){
                toast.cancel();
            }
            toast.setText(getText(resID));
            toast.show();
        }else {
            toast = Toast.makeText(this, resID, duration);
            toast.show();
        }
    }

    public final boolean okResult(JSONObject jsonObject){
        //-- Check return status first
        try {
            JSONObject jsonStatus = jsonObject.getJSONObject(Constants.KEY_status);
            if (jsonStatus.getString(STATUS_CODE).equalsIgnoreCase(CODE_SUCCESSFUL)) return true;
            else return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
    public final void launchActivity(){

    }
}

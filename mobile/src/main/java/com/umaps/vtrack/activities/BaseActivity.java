package com.umaps.vtrack.activities;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.umaps.vtrack.settings.SessionState;
import com.umaps.vtrack.util.Constants;

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

    public void showProgressDialog(final String message){
        progressDialog = SessionState.getProgressDialog();
        if(progressDialog==null || !progressDialog.isShowing()){
            try {
                progressDialog = ProgressDialog.show(this,"", message);
                //-- save for global accessing
                SessionState.setProgressDialog(progressDialog);
            }catch (OutOfMemoryError ie){
                onLowMemory();
            }
        }
    }
    public void showProgressDialog(final int resID){
        progressDialog = SessionState.getProgressDialog();
        if(progressDialog==null || !progressDialog.isShowing()){
            try {
                progressDialog = ProgressDialog.show(this, "", getText(resID));
                SessionState.setProgressDialog(progressDialog);
            } catch (OutOfMemoryError ie){
                onLowMemory();
            }
        }
    }

    public final void cancelProgressDialog(){
        progressDialog = SessionState.getProgressDialog();
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
    public int getDisplayFromPixel(float pixels){
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pixels*scale+0.5f);
    }
    public final void launchActivity(){

    }
}

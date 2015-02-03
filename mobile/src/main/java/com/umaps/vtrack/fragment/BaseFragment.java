package com.umaps.vtrack.fragment;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import com.umaps.vtrack.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vu@umaps.vn on 30/01/2015.
 */
public class BaseFragment extends Fragment {
    public static final String STATUS_CODE          = "code";
    public static final String CODE_SUCCESSFUL      = "successful";

    private static DialogFragment newFragment;
    public void showDialog(int content){
        if((newFragment==null)) {
            newFragment = AlertDialogFragment.newInstance(content);
        }
        newFragment.show(getFragmentManager(), "dialog");
    }
    public void dismissDialog(){
        if (newFragment != null || newFragment.getShowsDialog()){
            newFragment.dismiss();
        }
        newFragment = null;
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
}

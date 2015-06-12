package com.umaps.gpshandleclient.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umaps.gpshandleclient.MyApplication;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.model.Group;
import com.umaps.gpshandleclient.model.MyResponse;
import com.umaps.gpshandleclient.util.GpsOldRequest;
import com.umaps.gpshandleclient.util.StringTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by beou on 08/06/2015.
 */
public class AdmDeviceGroup extends Fragment {
    private static final String TAG = "AdmDevice";
    private static final String TAG_REQUEST = "AdmDevice";
    private View mBarProgress;
    private View mProgress;
    private View mLayout;
    private MyApplication mApplication;

    private GpsOldRequest mRequestGetGroup;
    public static AdmDeviceGroup newInstance(){
        return new AdmDeviceGroup();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_adm_device_groups, container, false);

        //-- for progress-bar
        mBarProgress = view.findViewById(R.id.bar_progress);
        mProgress = view.findViewById(R.id.progress);
        mLayout = view.findViewById(R.id.layout_devices);
        mApplication = MyApplication.getInstance();

        mRequestGetGroup = new GpsOldRequest(getActivity());
        mRequestGetGroup.setAccountID(mApplication.getAccountID());
        mRequestGetGroup.setUserID(mApplication.getUserID());
        mRequestGetGroup.setPassword(mApplication.getPassword());
        mRequestGetGroup.setMethod(Request.Method.POST);
        mRequestGetGroup.setUrl(GpsOldRequest.ADMIN_URL);
        mRequestGetGroup.setCommand(GpsOldRequest.CMD_GET_GROUPS);
        JSONObject params = createParams();
        mRequestGetGroup.setParams(params);
        mRequestGetGroup.setResponseHandler(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                showProgress(false);
                MyResponse mRes = new MyResponse(mApplication.getGroupList());
                if (mRes.isError()) {
                    return;
                }
                ArrayList<Group> groupsList = new ArrayList<>();
                try {
                    JSONArray mJSONArray = (JSONArray) mRes.getData();
                    for (int i = 0; i < mJSONArray.length(); i++) {
                        JSONObject itemGroup = mJSONArray.getJSONObject(i);
                        String accountID = itemGroup.getString("accountID");
                        String groupID = itemGroup.getString("groupID");
                        String pushpinID = itemGroup.getString("pushpinID");
                        String groupDescription = itemGroup.getString("description");
                        String groupDisplay = itemGroup.getString("displayName");
                        int deviceCount = itemGroup.getInt("deviceCount");

                        long currTimestamp = Calendar.getInstance().getTimeInMillis() / 1000;
                        int countLive = 0;
                        Group group = new Group(
                                accountID,
                                groupID,
                                (groupDisplay == null ? groupDescription : groupDisplay),
                                (groupDisplay == null ? groupDescription : groupDisplay),
                                pushpinID/*icon*/,
                                countLive/*live*/,
                                deviceCount);
                        groupsList.add(group);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ListView groupList = (ListView) view.findViewById(R.id.list_view_device_groups);
                groupList.setClickable(true);
                final AdmDeviceGroupAdapter groupListAdapter =
                        new AdmDeviceGroupAdapter(getActivity(), R.layout.list_view_adm_group, groupsList);
                if (groupListAdapter != null) {
                    groupList.setAdapter(groupListAdapter);
                }

                final TextView tvDeviceGroup = (TextView) view.findViewById(R.id.txt_device_group);
                //--setOnclickListener

            }
        });
        mRequestGetGroup.setRequestTag(TAG_REQUEST);
        mRequestGetGroup.setErrorHandler(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        mRequestGetGroup.exec();
        showProgress(true);
        return view;
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mRequestGetGroup.cancel(TAG_REQUEST);
        showProgress(false);
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

            mLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            mLayout.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLayout.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    private JSONObject createParams(){
        JSONObject jsonParamsObject = new JSONObject();
        List<String> fields = new ArrayList<>();
            fields.add("accountID");
            fields.add("groupID");
            fields.add("description");
            fields.add("pushpinID");
            fields.add("displayName");
            fields.add("deviceCount");
        try {
            jsonParamsObject.put(StringTools.KEY_FIELDS, new JSONArray(fields));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonParamsObject;
    }
}

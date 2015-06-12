package com.umaps.gpshandleclient.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.umaps.gpshandleclient.util.PagerAdapter;
import com.umaps.gpshandleclient.util.ReportGroupListViewAdapter;
import com.umaps.gpshandleclient.util.StringTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by beou on 04/06/2015.
 */
public class ReportPager extends Fragment {

    private static final String TAG = "ReportPager";
    private static final String TAG_REQUEST = "ReportPager";

    private View view;
    private View mView;
    private View mBarProgress;
    private View mProgress;

    private GpsOldRequest mRequestGetGroup;
    ArrayList<Group> groupsList;
    MyApplication mApplication;
    private Typeface mTf;
    public static ReportPager newInstance() {
        return new ReportPager();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_pager_report, container, false);
        mView = view.findViewById(R.id.view_pager_report);
        mApplication = MyApplication.getInstance();
        mTf = mApplication.getIconFont();
        mBarProgress = view.findViewById(R.id.bar_progress);
        mProgress = view.findViewById(R.id.progress);

        updateAllFragment();

        View deviceGroup = view.findViewById(R.id.device_group);
        deviceGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDeviceList(/*view*/);
            }
        });

        return view;
    }
    private List<Fragment> getFragments() {
        List<Fragment> frags = new ArrayList<>();
        RptOverview rptOverview = RptOverview.newInstance();
        RptEventCount rptEventCount = RptEventCount.newInstance();
        frags.add(rptOverview);
        frags.add(rptEventCount);
        return frags;
    }

    public void updateAllFragment() {
        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.view_pager_report);

        TextView tvDeviceGroupSearch = (TextView) view.findViewById(R.id.ic_device_group_search);
        TextView txtDeviceGroup = (TextView) view.findViewById(R.id.txt_device_group);
        tvDeviceGroupSearch.setTypeface(mTf);
        tvDeviceGroupSearch.setText(String.valueOf((char) 0xe629));
        txtDeviceGroup.setText(mApplication.getSelGroupDesc());

        List<Fragment> fragments = getFragments();
        PagerAdapter mPageAdapter = new PagerAdapter(getChildFragmentManager(), fragments);
        mViewPager.setAdapter(mPageAdapter);
    }

    /**
     * function toggle device list
     **/
    private void toggleDeviceList(/*View mView*/){
        if (view == null){
            return;
        }
        final View layout = view.findViewById(R.id.rpt_group_list_layout);
        if (layout.getVisibility()==View.VISIBLE){
            layout.setVisibility(View.GONE);
            return;
        } else {
            layout.setVisibility(View.VISIBLE);
        }
        Log.d(TAG, "toggleDeviceList: " + mApplication.getGroupList());

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
                groupsList = new ArrayList<>();
                //HashMap<String, ArrayList<Device>> devicesInGroup = new HashMap<>();
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
                        Group group = new Group(
                                accountID,
                                groupID,
                                (groupDisplay == null ? groupDescription : groupDisplay),
                                (groupDisplay == null ? groupDescription : groupDisplay),
                                pushpinID/*icon*/,
                                0/*countLive*//*live*/,
                                deviceCount);
                        groupsList.add(group);
                        //devicesInGroup.put(groupID, mArrayDevice);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ListView groupListView = (ListView) view.findViewById(R.id.rpt_group_list);
                groupListView.setClickable(true);

                groupListView.setAdapter(new ReportGroupListViewAdapter(getActivity(),
                        R.layout.list_view_report_group,
                        groupsList));
                groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.i(TAG, "You'v just clicked on: " + groupsList.get(position).getDisplayName());
                        mApplication.setSelGroup(groupsList.get(position).getGroupId());
                        mApplication.setSelGroupDesc(groupsList.get(position).getDisplayName());
                        mApplication.storeSettings();
                        //invalidated();
                        //get
                        updateAllFragment();
                        //-- hide layout
                        layout.setVisibility(View.GONE);
                    }
                });
            }
        });
        mRequestGetGroup.setRequestTag(TAG_REQUEST);
        mRequestGetGroup.setErrorHandler(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
            }
        });
        mRequestGetGroup.exec();
        showProgress(true);
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
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mView.setVisibility(show ? View.GONE : View.VISIBLE);
            mView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mView.setVisibility(show ? View.GONE : View.VISIBLE);
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

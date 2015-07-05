package com.umaps.gpshandleclient.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umaps.gpshandleclient.MyApplication;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.model.Device;
import com.umaps.gpshandleclient.model.Group;
import com.umaps.gpshandleclient.model.MyResponse;
import com.umaps.gpshandleclient.util.GpsOldRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by beou on 08/06/2015.
 */
public class AdmGroup extends Fragment {
    private static final String TAG = "AdmDevice";
    private static final String TAG_REQUEST = "AdmDevice";
    private View mBarProgress;
    private View mProgress;
    private View view;
    private ExpandableListView expListView;

    private MyApplication mApplication;
    private Typeface mTf;
    int previousGroup = -1;
    private GpsOldRequest mRequest;
    public static AdmGroup newInstance(){
        return new AdmGroup();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_adm_group, container, false);

        //-- for progress-bar
        mBarProgress = view.findViewById(R.id.bar_progress);
        mProgress = view.findViewById(R.id.progress);
        mApplication = MyApplication.getInstance();
        mTf = MyApplication.getIconFont();

        expListView = (ExpandableListView) view.findViewById(R.id.list_view_device_groups);
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup) {
                    expListView.collapseGroup(previousGroup);
                }
                previousGroup = groupPosition;
            }
        });

        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                if (groupPosition == previousGroup) {
                    previousGroup = -1;
                }
            }
        });

        setBottomToolbar();

        mRequest = new GpsOldRequest(getActivity());
        mRequest.setAccountID(mApplication.getAccountID());
        mRequest.setUserID(mApplication.getUserID());
        mRequest.setPassword(mApplication.getPassword());
        mRequest.setMethod(Request.Method.POST);
        mRequest.setUrl(GpsOldRequest.ADMIN_URL);
        mRequest.setCommand(GpsOldRequest.CMD_GET_GROUPS);
        JSONObject params = Group.createGetParams();
        mRequest.setParams(params);
        mRequest.setResponseHandler(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                showProgress(false);
                MyResponse mRes = new MyResponse(response);
                if (mRes.isError()) {
                    return;
                }
                ArrayList<Group> groupsList = new ArrayList<>();
                JSONArray mJSONArray = (JSONArray) mRes.getData();
                for (int i = 0; i<mJSONArray.length(); i++){
                    Group gr = null;
                    try {
                        gr = new Group(mJSONArray.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    groupsList.add(gr);
                }

                expListView.setClickable(true);
                final ExpListAdapter groupListAdapter = new ExpListAdapter(getActivity(), groupsList);
                if (groupListAdapter != null) {
                    expListView.setAdapter(groupListAdapter);
                }
            }
        });
        mRequest.setRequestTag(TAG_REQUEST);
        mRequest.setErrorHandler(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        mRequest.exec();
        showProgress(true);
        return view;
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mRequest.cancel(TAG_REQUEST);
    }

    private void setBottomToolbar(){
        TextView icAddGroup = (TextView) view.findViewById(R.id.ic_add_group);
        icAddGroup.setTypeface(mTf);
        icAddGroup.setText(String.valueOf((char) 0xe717));
        TextView txtAddGroup = (TextView) view.findViewById(R.id.txt_add_group);
        txtAddGroup.setText(R.string.add);

        TextView icEditGroup = (TextView) view.findViewById(R.id.ic_edit_group);
        icEditGroup.setTypeface(mTf);
        icEditGroup.setText(String.valueOf((char) 0xe714));
        TextView txtEditGroup = (TextView) view.findViewById(R.id.txt_edit_group);
        txtEditGroup.setText(R.string.edit);

        TextView icDeleteGroup = (TextView) view.findViewById(R.id.ic_delete_group);
        icDeleteGroup.setTypeface(mTf);
        icDeleteGroup.setText(String.valueOf((char) 0xe608));
        TextView txtDeleteGroup = (TextView) view.findViewById(R.id.txt_delete_group);
        txtDeleteGroup.setText(R.string.delete);
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

            expListView.setVisibility(show ? View.GONE : View.VISIBLE);
            expListView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    expListView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            expListView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class ExpListAdapter extends BaseExpandableListAdapter {
        private static final String TAG = "ExpListAdapter";
        Typeface mTf = null;
        private Context context;
        private ArrayList<Group> objects;
        LayoutInflater mInflater;
        View groupView;
        View detailsView;
        TextView icIndicator;

        public ExpListAdapter(Context context, ArrayList<Group> objects){
            this.context = context;
            this.objects = objects;
            this.mTf = MyApplication.getIconFont();
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getGroupCount() {
            if (objects!=null){
                return objects.size();
            }
            return 0;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if ((objects!=null)) {
                return 1;
            }
            return 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            if (objects != null) {
                return objects.get(groupPosition);
            }
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            if ((objects!= null)) {
                return objects.get(groupPosition);
            }
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        //View - Most-Important
        @Override
        public View getGroupView(final int groupPosition, boolean isExpandable, View convertView, ViewGroup parent) {
            if(convertView==null){
                groupView = mInflater.inflate(R.layout.group_header, null);
            } else {
                groupView = convertView;
            }

            if (objects == null) return groupView;

            //String id          = objects.get(groupPosition).getGroupId();
            String description = objects.get(groupPosition).getDescription();

            /*TextView txtAccountId = (TextView) groupView.findViewById(R.id.txt_group_id);
            txtAccountId.setText(id);*/

            TextView txtAccountDesc = (TextView) groupView.findViewById(R.id.txt_group_desc);
            txtAccountDesc.setText(description);

            int dc = objects.get(groupPosition).getDeviceCount();

            TextView txtDeviceCount = (TextView) groupView.findViewById(R.id.txt_device_count);
            txtDeviceCount.setText(String.valueOf(dc));

            icIndicator = (TextView) groupView.findViewById(R.id.ic_indicator);
            icIndicator.setTypeface(mTf);
            icIndicator.setText(String.valueOf((char) 0xe6ee));

            return groupView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if(convertView == null){
                detailsView = mInflater.inflate(R.layout.group_details, null);
            } else {
                detailsView = convertView;
            }

            Group group = objects.get(groupPosition);

            TextView txtAccountId = (TextView) detailsView.findViewById(R.id.txt_group_id_content);
            txtAccountId.setText(group.getGroupId());
            TextView txtAccountDesc = (TextView) detailsView.findViewById(R.id.txt_group_desc_content);
            txtAccountDesc.setText(group.getDescription());
            TextView txtAccountDisplay = (TextView) detailsView.findViewById(R.id.txt_group_display_content);
            txtAccountDisplay.setText(group.getDisplayName());
            TextView txtLastLogin = (TextView) detailsView.findViewById(R.id.txt_group_last_update_content);
            Date lastLogin = new Date(group.getLastUpdateTime() * 1000);
            txtLastLogin.setText(lastLogin.toString());
            TextView txtCreatedOn = (TextView) detailsView.findViewById(R.id.txt_group_creation_content);
            Date createdOn = new Date(group.getCreationTime() * 1000);
            txtCreatedOn.setText(createdOn.toString());

            TextView deviceListView = (TextView) detailsView.findViewById(R.id.group_device_list);
            StringBuffer sb = new StringBuffer();
            ArrayList<Device> dl = objects.get(groupPosition).getDeviceArrayList();
            if (dl!=null){
                for (int i = 0; i< dl.size(); i++){
                    sb.append(dl.get(i).getDescription()).append("\n");
                }
            }
            deviceListView.setText(sb);
            return detailsView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}

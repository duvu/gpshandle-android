package com.umaps.gpshandleclient.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umaps.gpshandleclient.MyApplication;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.model.Group;
import com.umaps.gpshandleclient.model.MyResponse;
import com.umaps.gpshandleclient.model.User;
import com.umaps.gpshandleclient.util.GpsOldRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by beou on 08/06/2015.
 */
public class AdmUser extends Fragment {
    private static final String TAG = "AdmUser";

    private GpsOldRequest mRequest;
    private static final String TAG_REQUEST = "AdmUser";
    private View mBarProgress;
    private View mProgress;
    View view;
    ExpandableListView expListView;
    private MyApplication mApplication;
    private Typeface mTf;

    int previousGroup = -1;

    public static AdmUser newInstance(){
        return new AdmUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_adm_user, container, false);
        mBarProgress    = view.findViewById(R.id.bar_progress);
        mProgress       = view.findViewById(R.id.progress);
        mApplication    = MyApplication.getInstance();
        mTf             = MyApplication.getIconFont();

        expListView = (ExpandableListView)view.findViewById(R.id.exp_list);
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
        mRequest.setUrl(GpsOldRequest.ADMIN_URL);
        mRequest.setMethod(Request.Method.POST);
        mRequest.setCommand(GpsOldRequest.CMD_GET_AUTHORIZED_USERS);
        mRequest.setParams(User.createParams());

        mRequest.setResponseHandler(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                MyResponse mRes = new MyResponse(response);
                if (mRes.isError()) return;

                JSONArray jsonArray = (JSONArray) mRes.getData();
                ArrayList<User> userArrayList = new ArrayList<User>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    User user = null;
                    try {
                        user = new User(jsonArray.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (user != null) {
                        userArrayList.add(user);
                    }
                }
                ExpListAdapter expListAdapter = new ExpListAdapter(getActivity(), userArrayList);
                expListView.setAdapter(expListAdapter);
                showProgress(false);
            }
        });
        mRequest.setErrorHandler(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        mRequest.setRequestTag(TAG_REQUEST);
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
        TextView icAdd = (TextView) view.findViewById(R.id.ic_add);
        icAdd.setTypeface(mTf);
        icAdd.setText(String.valueOf((char) 0xe717));
        TextView txtAdd = (TextView) view.findViewById(R.id.txt_add);
        txtAdd.setText(R.string.add);

        TextView icEdit = (TextView) view.findViewById(R.id.ic_edit);
        icEdit.setTypeface(mTf);
        icEdit.setText(String.valueOf((char) 0xe714));
        TextView txtEditAccount = (TextView) view.findViewById(R.id.txt_edit);
        txtEditAccount.setText(R.string.edit);

        TextView icDelete = (TextView) view.findViewById(R.id.ic_delete);
        icDelete.setTypeface(mTf);
        icDelete.setText(String.valueOf((char) 0xe608));
        TextView txtDeleteAccount = (TextView) view.findViewById(R.id.txt_delete);
        txtDeleteAccount.setText(R.string.delete);

        if (mApplication.getAclAdminUserManager() > 2) {
            View add = view.findViewById(R.id.add);
            View delete = view.findViewById(R.id.delete);
            View edit = view.findViewById(R.id.edit);

            add.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //TODO
                }
            });
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO
                }
            });
        } else {
            icAdd.setTextColor(getResources().getColor(R.color.disable));
            txtAdd.setTextColor(getResources().getColor(R.color.disable));

            icDelete.setTextColor(getResources().getColor(R.color.disable));
            txtDeleteAccount.setTextColor(getResources().getColor(R.color.disable));

            icEdit.setTextColor(getResources().getColor(R.color.disable));
            txtEditAccount.setTextColor(getResources().getColor(R.color.disable));
        }

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
        private ArrayList<User> objects;
        LayoutInflater mInflater;
        View groupView;
        View detailsView;
        TextView icIndicator;

        public ExpListAdapter(Context context, ArrayList<User> objects){
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
                groupView = mInflater.inflate(R.layout.user_header, null);
            } else {
                groupView = convertView;
            }

            if (objects == null) return groupView;

            String id          = objects.get(groupPosition).getId();
            String description = objects.get(groupPosition).getDescription();

            TextView txtAccountId = (TextView) groupView.findViewById(R.id.txt_user_id);
            txtAccountId.setText(id);

            TextView txtAccountDesc = (TextView) groupView.findViewById(R.id.txt_user_desc);
            txtAccountDesc.setText(description);

            int dc = 0;
            ArrayList<Group> lg = objects.get(groupPosition).getGroupList();
            if (lg != null) {
                for (int i = 0; i < lg.size(); i++) {
                    dc += lg.get(i).getDeviceCount();
                }
            }

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
                detailsView = mInflater.inflate(R.layout.user_details, null);
            } else {
                detailsView = convertView;
            }

            User user = objects.get(groupPosition);

            TextView txtAccountId = (TextView) detailsView.findViewById(R.id.txt_user_id_content);
            txtAccountId.setText(user.getId());
            TextView txtAccountDesc = (TextView) detailsView.findViewById(R.id.txt_user_desc_content);
            txtAccountDesc.setText(user.getDescription());
            TextView txtAccountDisplay = (TextView) detailsView.findViewById(R.id.txt_user_display_content);
            txtAccountDisplay.setText(user.getDisplayName());
            TextView txtContactName = (TextView) detailsView.findViewById(R.id.txt_user_contact_name_content);
            txtContactName.setText(user.getContactName());
            TextView txtContactEmail = (TextView) detailsView.findViewById(R.id.txt_user_contact_email_content);
            txtContactEmail.setText(user.getContactEmail());
            TextView txtContactPhone = (TextView) detailsView.findViewById(R.id.txt_user_contact_phone_content);
            txtContactPhone.setText(user.getContactPhone());
            /*TextView txtDeviceCount = (TextView) detailsView.findViewById(R.id.txt_user_device_count_content);
            txtDeviceCount.setText("" + user.get());*/
            TextView txtLastLogin = (TextView) detailsView.findViewById(R.id.txt_user_last_login_content);
            Date lastLogin = new Date(user.getLastLoginTime() * 1000);
            txtLastLogin.setText(lastLogin.toString());
            TextView txtCreatedOn = (TextView) detailsView.findViewById(R.id.txt_user_creation_content);
            Date createdOn = new Date(user.getCreationTime() * 1000);
            txtCreatedOn.setText(createdOn.toString());
            TextView txtManagedGroups = (TextView) detailsView.findViewById(R.id.txt_managed_groups_content);

            ArrayList<Group> grL = user.getGroupList();
            if (grL!=null){
                StringBuffer lG = new StringBuffer();
                for (int i = 0; i < grL.size(); i++) {
                    lG.append(grL.get(i).getDescription()).append("\n");
                }
                txtManagedGroups.setText(lG);
            } else {
                txtManagedGroups.setText(R.string.managed_group_none);
            }

            return detailsView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
/*        @Override
        public void onGroupCollapsed(int groupPosition) {
            //icIndicator.setText(String.valueOf((char)0xe6ee)); //0xe6ee
            groupView.setBackgroundColor(context.getResources().getColor(R.color.unselected));
        }
        @Override
        public void onGroupExpanded(int groupPosition) {
            //icIndicator.setText(String.valueOf((char)0xe6ed)); //0xe6ee
            groupView.setBackgroundColor(context.getResources().getColor(R.color.selected));
        }*/
        //--------------------------------------------------------------------------------------------//
        //--End override
        //--------------------------------------------------------------------------------------------//
    }
}

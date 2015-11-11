package com.umaps.gpshandleclient.ui.fragment;

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
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umaps.gpshandleclient.MyApplication;
import com.umaps.gpshandleclient.R;
import com.umaps.gpssdk.Query;
import com.umaps.gpssdk.GpsSdk;
import com.umaps.gpssdk.MyResponse;
import com.umaps.gpssdk.model.Account;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by beou on 08/06/2015.
 */
public class AdmAccount extends Fragment {
    private static final String TAG = "AdmAccount";

    private Query mQuery;
    private static final String TAG_REQUEST = "admAccount";
    private View mBarProgress;
    private View mProgress;
    View view;

    int previousGroup = -1;

    ExpandableListView expListView;
    private MyApplication mApplication;
    private Typeface mTf;
    public static AdmAccount newInstance(){
        return new AdmAccount();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_adm_account, container, false);

        mBarProgress    = view.findViewById(R.id.bar_progress);
        mProgress       = view.findViewById(R.id.progress);
        mApplication    = MyApplication.getInstance();
        mTf             = MyApplication.getIconFont();

        expListView = (ExpandableListView)view.findViewById(R.id.exp_accounts_list);
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

        mQuery = new Query();
        mQuery.setAccountID(GpsSdk.getAccountId());
        mQuery.setUserID(GpsSdk.getUserId());
        mQuery.setPassword(GpsSdk.getUserPassword());
        mQuery.setUrl(Query.ADMIN_URL);
        mQuery.setMethod(com.android.volley.Request.Method.POST);
        mQuery.setCommand(Query.CMD_GET_AUTHORIZED_ACCOUNTS);

        mQuery.setParams(Account.createParam());
        mQuery.setResponseHandler(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                MyResponse mRes = new MyResponse(response);
                if (mRes.isError()){
                    showProgress(false);
                    Toast.makeText(getActivity(), getText(R.string.you_dont_have_permission), Toast.LENGTH_LONG).show();
                    return;
                }

                JSONArray jsonArray = (JSONArray) mRes.getData();
                ArrayList<Account> accounts = Account.getListFromJson(jsonArray);
                AccountListAdapter accountListAdapter = new AccountListAdapter(getActivity(), accounts);
                expListView.setAdapter(accountListAdapter);
                showProgress(false);
            }
        });
        mQuery.setErrorHandler(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        mQuery.exec();
        showProgress(true);
        return view;
    }
    @Override
    public void onDetach(){
        super.onDetach();
        Query.getInstance().cancelAll();
    }

    private void setBottomToolbar(){
        TextView icAddAccount = (TextView) view.findViewById(R.id.ic_add);
        icAddAccount.setTypeface(mTf);
        icAddAccount.setText(String.valueOf((char) 0xe717));
        TextView txtAddAccount = (TextView) view.findViewById(R.id.txt_add);
        txtAddAccount.setText(R.string.add);

        TextView icEditAccount = (TextView) view.findViewById(R.id.ic_edit);
        icEditAccount.setTypeface(mTf);
        icEditAccount.setText(String.valueOf((char) 0xe714));
        TextView txtEditAccount = (TextView) view.findViewById(R.id.txt_edit);
        txtEditAccount.setText(R.string.edit);

        TextView icDeleteAccount = (TextView) view.findViewById(R.id.ic_delete);
        icDeleteAccount.setTypeface(mTf);
        icDeleteAccount.setText(String.valueOf((char) 0xe608));
        TextView txtDeleteAccount = (TextView) view.findViewById(R.id.txt_delete);
        txtDeleteAccount.setText(R.string.delete);

        if (GpsSdk.isAccountManager()) {
            View addAccount = view.findViewById(R.id.add);
            View deleteAccount = view.findViewById(R.id.delete);
            View editAccount = view.findViewById(R.id.edit);

            addAccount.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //TODO
                }
            });
            editAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO
                }
            });
        } else {
            icAddAccount.setTextColor(getResources().getColor(R.color.disable));
            txtAddAccount.setTextColor(getResources().getColor(R.color.disable));

            icDeleteAccount.setTextColor(getResources().getColor(R.color.disable));
            txtDeleteAccount.setTextColor(getResources().getColor(R.color.disable));

            icEditAccount.setTextColor(getResources().getColor(R.color.disable));
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

    private class AccountListAdapter extends BaseExpandableListAdapter {
        Typeface mTf = null;
        private Context context;
        private ArrayList<Account> accounts;
        LayoutInflater mInflater;
        View groupView;
        View detailsView;
        TextView icIndicator;

        public AccountListAdapter(Context context, ArrayList<Account> accounts){
            this.context = context;
            this.accounts = accounts;
            this.mTf = MyApplication.getIconFont();
            mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getGroupCount() {
            if (accounts!=null){
                return accounts.size();
            }
            return 0;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if ((accounts!=null)) {
                return 1;
            }
            return 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            if (accounts != null) {
                return accounts.get(groupPosition);
            }
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            if ((accounts!= null)) {
                return accounts.get(groupPosition);
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
            return false;
        }

        //View - Most-Important
        @Override
        public View getGroupView(final int groupPosition, boolean isExpandable, View convertView, ViewGroup parent) {
            if(convertView==null){
                groupView = mInflater.inflate(R.layout.account_header, null);
            } else {
                groupView = convertView;
            }
            if (accounts == null) return null;

            String id          = accounts.get(groupPosition).getId();
            String description = accounts.get(groupPosition).getDescription();
            int count = accounts.get(groupPosition).getDevice_count();

            TextView txtAccountId = (TextView) groupView.findViewById(R.id.txt_account_id);
            txtAccountId.setText(id);

            TextView txtAccountDesc = (TextView) groupView.findViewById(R.id.txt_account_desc);
            txtAccountDesc.setText(description);

            TextView txtDeviceCount = (TextView) groupView.findViewById(R.id.txt_device_count);
            txtDeviceCount.setText(String.valueOf(count));

            icIndicator = (TextView) groupView.findViewById(R.id.ic_indicator);
            icIndicator.setTypeface(mTf);
            icIndicator.setText(String.valueOf((char) 0xe6ee));
            //expListView.collapseGroup(groupPosition);
            /*icIndicator.setText(String.valueOf((char)0xe6ed));
            icIndicator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (expListView.isGroupExpanded(groupPosition)){
                        expListView.collapseGroup(groupPosition);
                        icIndicator.setText(String.valueOf((char)0xe6ed));
                    } else {
                        expListView.expandGroup(groupPosition);
                        icIndicator.setText(String.valueOf((char)0xe6ee));
                    }
                }
            });*/
            //expListView.collapseGroup(groupPosition);
            return groupView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if(convertView == null){
                detailsView = mInflater.inflate(R.layout.account_details, null);
            } else {
                detailsView = convertView;
            }

            Account account = accounts.get(groupPosition);

            TextView txtAccountId = (TextView) detailsView.findViewById(R.id.txt_account_id_content);
            txtAccountId.setText(account.getId());
            TextView txtAccountDesc = (TextView) detailsView.findViewById(R.id.txt_account_desc_content);
            txtAccountDesc.setText(account.getDescription());
            TextView txtAccountDisplay = (TextView) detailsView.findViewById(R.id.txt_account_display_content);
            txtAccountDisplay.setText(account.getDisplay_name());
            TextView txtContactName = (TextView) detailsView.findViewById(R.id.txt_account_contact_name_content);
            txtContactName.setText(account.getContact_name());
            TextView txtContactEmail = (TextView) detailsView.findViewById(R.id.txt_account_contact_email_content);
            txtContactEmail.setText(account.getContact_email());
            TextView txtContactPhone = (TextView) detailsView.findViewById(R.id.txt_account_contact_phone_content);
            txtContactPhone.setText(account.getContact_phone());
            TextView txtDeviceCount = (TextView) detailsView.findViewById(R.id.txt_account_device_count_content);
            txtDeviceCount.setText("" + account.getDevice_count());
            TextView txtLastLogin = (TextView) detailsView.findViewById(R.id.txt_account_last_login_content);
            Date lastLogin = new Date(account.getLast_login_time() * 1000);
            txtLastLogin.setText(lastLogin.toString());
            TextView txtCreatedOn = (TextView) detailsView.findViewById(R.id.txt_account_creation_content);
            Date createdOn = new Date(account.getCreation_time() * 1000);
            txtCreatedOn.setText(createdOn.toString());
            return detailsView;
        }
        public String padRight(double s) {
            return String.format("%1$,.0f", s);
        }
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
        /*@Override
        public void onGroupCollapsed(int groupPosition) {
            //icIndicator.setText(String.valueOf((char)0xe6ee)); //0xe6ee
            groupView.setBackgroundColor(context.getResources().getColor(R.color.unselected));
        }
        @Override
        public void onGroupExpanded(int groupPosition) {
            //icIndicator.setText(String.valueOf((char)0xe6ed)); //0xe6ee
            groupView.setBackgroundColor(context.getResources().getColor(R.color.selected));
        }*/
    }
}

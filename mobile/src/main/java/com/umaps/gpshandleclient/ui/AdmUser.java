package com.umaps.gpshandleclient.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umaps.gpshandleclient.MyApplication;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.Session;
import com.umaps.gpshandleclient.model.Group;
import com.umaps.gpshandleclient.model.MyResponse;
import com.umaps.gpshandleclient.model.User;
import com.umaps.gpshandleclient.util.GpsRequest;
import com.umaps.gpshandleclient.util.HttpQueue;

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
    private static final String TAG_REQUEST = "AdmUser";
    private View mBarProgress;
    private View mProgress;
    View view;
    ExpandableListView expListView;
    private MyApplication mApplication;
    private Typeface mTf;

    int previousGroup = -1;
    private View layoutAdd;
    private View layoutEdit;
    private View layoutDelete;

    private View addUser;
    private View editUser;
    private View deleteUser;
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

        addUser = view.findViewById(R.id.add);
        editUser = view.findViewById(R.id.edit);
        deleteUser = view.findViewById(R.id.delete);

        layoutAdd = view.findViewById(R.id.l_add_user);
        layoutEdit = view.findViewById(R.id.l_edit_user);
        layoutDelete = view.findViewById(R.id.l_delete_user);

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

        GpsRequest mRequest = new GpsRequest(getActivity());
        mRequest.setAccountID(Session.getAccountId());
        mRequest.setUserID(Session.getUserId());
        mRequest.setPassword(Session.getUserPassword());
        mRequest.setUrl(GpsRequest.ADMIN_URL);
        mRequest.setMethod(Request.Method.POST);
        mRequest.setCommand(GpsRequest.CMD_GET_AUTHORIZED_USERS);
        mRequest.setParams(User.createParams());

        mRequest.setResponseHandler(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                showProgress(false);
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
            }
        });
        mRequest.setErrorHandler(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
            }
        });
        mRequest.exec();
        showProgress(true);
        return view;
    }
    @Override
    public void onDetach(){
        super.onDetach();
        HttpQueue.getInstance(getActivity()).cancel(TAG_REQUEST);
        showProgress(false);
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
        TextView txtEdit = (TextView) view.findViewById(R.id.txt_edit);
        txtEdit.setText(R.string.edit);

        TextView icDelete = (TextView) view.findViewById(R.id.ic_delete);
        icDelete.setTypeface(mTf);
        icDelete.setText(String.valueOf((char) 0xe608));
        TextView txtDelete = (TextView) view.findViewById(R.id.txt_delete);
        txtDelete.setText(R.string.delete);

        if (mApplication.getAclAdminUserManager() > 2) {
            addUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (layoutAdd.getVisibility() == View.VISIBLE) {
                        layoutAdd.setVisibility(View.GONE);
                        expListView.setVisibility(View.VISIBLE);
                        addUser.setBackgroundColor(getResources().getColor(R.color.base_color));
                    } else {
                        layoutDelete.setVisibility(View.GONE);
                        layoutEdit.setVisibility(View.GONE);
                        layoutAdd.setVisibility(View.VISIBLE);
                        expListView.setVisibility(View.GONE);
                        addUser.setBackgroundColor(getResources().getColor(R.color.base_color_dark));
                        editUser.setBackgroundColor(getResources().getColor(R.color.base_color));
                        deleteUser.setBackgroundColor(getResources().getColor(R.color.base_color));
                        setupLayoutAdd();
                    }
                }
            });
            deleteUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (layoutDelete.getVisibility() == View.VISIBLE) {
                        layoutDelete.setVisibility(View.GONE);
                        deleteUser.setBackgroundColor(getResources().getColor(R.color.base_color));
                    } else {
                        if (previousGroup == -1) {
                            Toast.makeText(getActivity(), getResources().getText(R.string.you_must_select_a_device), Toast.LENGTH_LONG).show();
                        } else {
                            layoutDelete.setVisibility(View.VISIBLE);
                            layoutEdit.setVisibility(View.GONE);
                            layoutAdd.setVisibility(View.GONE);
                            if (expListView.getVisibility() == View.GONE) {
                                expListView.setVisibility(View.VISIBLE);
                            }
                            addUser.setBackgroundColor(getResources().getColor(R.color.base_color));
                            editUser.setBackgroundColor(getResources().getColor(R.color.base_color));
                            deleteUser.setBackgroundColor(getResources().getColor(R.color.base_color_dark));
                            setupLayoutDelete();
                        }
                    }
                }
            });
        } else {
            icAdd.setTextColor(getResources().getColor(R.color.disable));
            txtAdd.setTextColor(getResources().getColor(R.color.disable));
            icDelete.setTextColor(getResources().getColor(R.color.disable));
            txtDelete.setTextColor(getResources().getColor(R.color.disable));

            addUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), getText(R.string.you_dont_have_permission), Toast.LENGTH_LONG).show();
                }
            });
            deleteUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), getText(R.string.you_dont_have_permission), Toast.LENGTH_LONG).show();
                }
            });
        }

        if (mApplication.getAclAdminGroup() > 1) {
            editUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mApplication.getAclAdminDevice() < 2) {
                        Toast.makeText(getActivity(), getResources().getText(R.string.you_dont_have_permission), Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (layoutEdit.getVisibility() == View.VISIBLE) {
                        layoutEdit.setVisibility(View.GONE);
                        expListView.setVisibility(View.VISIBLE);
                        editUser.setBackgroundColor(getResources().getColor(R.color.base_color));
                    } else {
                        if (previousGroup == -1) {
                            Toast.makeText(getActivity(), getResources().getText(R.string.you_must_select_a_device), Toast.LENGTH_LONG).show();
                        } else {
                            layoutDelete.setVisibility(View.GONE);
                            layoutEdit.setVisibility(View.VISIBLE);
                            layoutAdd.setVisibility(View.GONE);
                            expListView.setVisibility(View.GONE);
                            addUser.setBackgroundColor(getResources().getColor(R.color.base_color));
                            editUser.setBackgroundColor(getResources().getColor(R.color.base_color_dark));
                            deleteUser.setBackgroundColor(getResources().getColor(R.color.base_color));
                            setupLayoutEdit();
                        }
                    }
                }
            });
        } else {
            icEdit.setTextColor(getResources().getColor(R.color.disable));
            txtEdit.setTextColor(getResources().getColor(R.color.disable));
            editUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), getString(R.string.you_dont_have_permission), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void setupLayoutAdd(){
        Button btnSave = (Button) layoutAdd.findViewById(R.id.btn_save);
        Button btnCancel = (Button) layoutAdd.findViewById(R.id.btn_cancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edtUserId = (EditText) layoutAdd.findViewById(R.id.txt_user_id_content);
                EditText edtPassword = (EditText) layoutAdd.findViewById(R.id.txt_user_password_content);
                EditText edtDescription = (EditText) layoutAdd.findViewById(R.id.txt_user_desc_content);
                EditText edtDisplayName = (EditText) layoutAdd.findViewById(R.id.txt_user_display_content);
                Switch swActivated = (Switch) layoutAdd.findViewById(R.id.txt_is_active_content);
                EditText edtContactName = (EditText) layoutAdd.findViewById(R.id.txt_user_contact_name_content);
                EditText edtContactEmail = (EditText) layoutAdd.findViewById(R.id.txt_user_contact_email_content);
                EditText edtContactPhone = (EditText) layoutAdd.findViewById(R.id.txt_user_contact_phone_content);
                EditText edtManagedGroup = (EditText) layoutAdd.findViewById(R.id.txt_managed_groups_content);

                User u = new User(getActivity());
                u.setId(edtUserId.getText().toString());
                u.setPassword(edtPassword.getText().toString());
                u.setDescription(edtDescription.getText().toString());
                u.setDisplayName(edtDisplayName.getText().toString());
                u.setIsActive(swActivated.isChecked());
                u.setContactName(edtContactName.getText().toString());
                u.setContactEmail(edtContactEmail.getText().toString());
                u.setContactPhone(edtContactPhone.getText().toString());
                GpsRequest crtRequest = u.getRequestCreate();

                crtRequest.setResponseHandler(new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        showProgress(false);
                        MyResponse myResponse = new MyResponse(response);
                        if (myResponse.isSuccess()) {
                            layoutAdd.setVisibility(View.GONE);
                            expListView.setVisibility(View.VISIBLE);
                            addUser.setBackgroundColor(getResources().getColor(R.color.base_color));
                        }
                        //update group list
                        {
                            String tag = "android:switcher:"+R.id.view_pager_admin+":"+1;
                            AdmUser frg = (AdmUser)getFragmentManager().findFragmentByTag(tag);
                            FragmentTransaction aTrans = getFragmentManager().beginTransaction();
                            aTrans.detach(frg).attach(frg).commit();
                        }
                        Toast.makeText(getActivity(), myResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                crtRequest.setErrorHandler(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                    }
                });
                crtRequest.exec();
                showProgress(true);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutAdd.setVisibility(View.GONE);
                expListView.setVisibility(View.VISIBLE);
                addUser.setBackgroundColor(getResources().getColor(R.color.base_color));
            }
        });
    }
    private void setupLayoutEdit(){
        ExpListAdapter adt = (ExpListAdapter) expListView.getExpandableListAdapter();//.getAdapter();
        final User u = (User)adt.getGroup(previousGroup);

        final EditText edtUserId          = (EditText) layoutEdit.findViewById(R.id.txt_user_id_content);
        final EditText edtPassword        = (EditText) layoutEdit.findViewById(R.id.txt_user_password_content);
        final EditText edtDescription     = (EditText) layoutEdit.findViewById(R.id.txt_user_desc_content);
        final EditText edtDisplayName     = (EditText) layoutEdit.findViewById(R.id.txt_user_display_content);
        final Switch swActivated          = (Switch)   layoutEdit.findViewById(R.id.txt_is_active_content);
        final EditText edtContactName     = (EditText) layoutEdit.findViewById(R.id.txt_user_contact_name_content);
        final EditText edtContactEmail    = (EditText) layoutEdit.findViewById(R.id.txt_user_contact_email_content);
        final EditText edtContactPhone    = (EditText) layoutEdit.findViewById(R.id.txt_user_contact_phone_content);
        EditText edtManagedGroup          = (EditText) layoutEdit.findViewById(R.id.txt_managed_groups_content);

        edtUserId.setText(u.getId());
        edtPassword.setText(u.getPassword());
        edtDescription.setText(u.getDescription());
        edtDisplayName.setText(u.getDisplayName());
        swActivated.setChecked(u.isActive());
        edtContactName.setText(u.getContactName());
        edtContactEmail.setText(u.getContactEmail());
        edtContactPhone.setText(u.getContactPhone());

        Button btnSave = (Button) layoutEdit.findViewById(R.id.btn_save);
        Button btnCancel = (Button) layoutEdit.findViewById(R.id.btn_cancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                u.setContext(getActivity());
                u.setId(edtUserId.getText().toString());
                u.setPassword(edtPassword.getText().toString());
                u.setDescription(edtDescription.getText().toString());
                u.setDisplayName(edtDisplayName.getText().toString());
                u.setIsActive(swActivated.isChecked());
                u.setContactName(edtContactName.getText().toString());
                u.setContactEmail(edtContactEmail.getText().toString());
                u.setContactPhone(edtContactPhone.getText().toString());
                GpsRequest edtRequest = u.getRequestEdit();

                edtRequest.setResponseHandler(new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        showProgress(false);
                        MyResponse myResponse = new MyResponse(response);
                        if (myResponse.isSuccess()) {
                            layoutEdit.setVisibility(View.GONE);
                            expListView.setVisibility(View.VISIBLE);
                            editUser.setBackgroundColor(getResources().getColor(R.color.base_color));
                        }
                        //update group list
                        {
                            String tag = "android:switcher:" + R.id.view_pager_admin + ":" + 1;
                            AdmUser frg = (AdmUser) getFragmentManager().findFragmentByTag(tag);
                            FragmentTransaction aTrans = getFragmentManager().beginTransaction();
                            aTrans.detach(frg).attach(frg).commit();
                        }
                        Toast.makeText(getActivity(), myResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                edtRequest.setErrorHandler(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                    }
                });
                edtRequest.exec();
                showProgress(true);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutEdit.setVisibility(View.GONE);
                expListView.setVisibility(View.VISIBLE);
                editUser.setBackgroundColor(getResources().getColor(R.color.base_color));
            }
        });
    }
    private void setupLayoutDelete(){
        Button btnDelete = (Button) layoutDelete.findViewById(R.id.btn_delete);
        Button btnCancel = (Button) layoutDelete.findViewById(R.id.btn_cancel);

        ExpListAdapter adt = (ExpListAdapter) expListView.getExpandableListAdapter();//.getAdapter();
        final User u = (User)adt.getGroup(previousGroup);
        TextView txtConfirm = (TextView)layoutDelete.findViewById(R.id.delete_confirm);
        StringBuffer sb = new StringBuffer();
        sb.append(getResources().getString(R.string.are_you_sure));
        sb.append(": ").append(u.getDescription());
        txtConfirm.setText(sb);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                u.setContext(getActivity());
                GpsRequest deleteRequest = u.getRequestDelete();
                deleteRequest.setResponseHandler(new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        showProgress(false);
                        MyResponse myResponse = new MyResponse(response);
                        if (myResponse.isSuccess()) {
                            layoutDelete.setVisibility(View.GONE);
                            deleteUser.setBackgroundColor(getResources().getColor(R.color.base_color));
                            {
                                previousGroup = -1; //reset
                                String tag = "android:switcher:"+R.id.view_pager_admin+":"+1;
                                AdmUser frg = (AdmUser)getFragmentManager().findFragmentByTag(tag);
                                FragmentTransaction aTrans = getFragmentManager().beginTransaction();
                                aTrans.detach(frg).attach(frg).commit();
                            }
                        }
                        Toast.makeText(getActivity(), myResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                deleteRequest.setErrorHandler(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        Toast.makeText(getActivity(), getText(R.string.failure_delete), Toast.LENGTH_LONG).show();
                    }
                });
                deleteRequest.exec();
                showProgress(true);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutDelete.setVisibility(View.GONE);
                deleteUser.setBackgroundColor(getResources().getColor(R.color.base_color));
            }
        });

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

            TextView txtAccountId       = (TextView) detailsView.findViewById(R.id.txt_user_id_content);
            TextView txtAccountDesc     = (TextView) detailsView.findViewById(R.id.txt_user_desc_content);
            TextView txtAccountDisplay  = (TextView) detailsView.findViewById(R.id.txt_user_display_content);
            Switch swActivated          = (Switch)   detailsView.findViewById(R.id.txt_is_active_content);
            TextView txtContactName     = (TextView) detailsView.findViewById(R.id.txt_user_contact_name_content);
            TextView txtContactEmail    = (TextView) detailsView.findViewById(R.id.txt_user_contact_email_content);
            TextView txtContactPhone    = (TextView) detailsView.findViewById(R.id.txt_user_contact_phone_content);
            TextView txtLastLogin       = (TextView) detailsView.findViewById(R.id.txt_user_last_login_content);
            TextView txtCreatedOn       = (TextView) detailsView.findViewById(R.id.txt_user_creation_content);
            TextView txtManagedGroups   = (TextView) detailsView.findViewById(R.id.txt_managed_groups_content);

            txtAccountId.setText(user.getId());
            txtAccountDesc.setText(user.getDescription());
            txtAccountDisplay.setText(user.getDisplayName());
            swActivated.setChecked(user.isActive());
            txtContactName.setText(user.getContactName());
            txtContactEmail.setText(user.getContactEmail());
            txtContactPhone.setText(user.getContactPhone());
            Date lastLogin = new Date(user.getLastLoginTime() * 1000);
            txtLastLogin.setText(lastLogin.toString());
            Date createdOn = new Date(user.getCreationTime() * 1000);
            txtCreatedOn.setText(createdOn.toString());

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

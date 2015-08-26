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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umaps.gpshandleclient.MyApplication;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.Session;
import com.umaps.gpshandleclient.model.Device;
import com.umaps.gpshandleclient.model.Group;
import com.umaps.gpshandleclient.model.MyResponse;
import com.umaps.gpshandleclient.util.GpsRequest;
import com.umaps.gpshandleclient.util.HttpQueue;
import com.umaps.gpshandleclient.util.StringTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by beou on 08/06/2015.
 */
public class AdmGroup extends Fragment {
    private static final String TAG = "AdmGroup";
    private static final String TAG_REQUEST = "AdmGroup";
    private View mBarProgress;
    private View mProgress;
    private View view;
    private ExpandableListView expListView;

    private MyApplication mApplication;
    private Typeface mTf;

    int previousGroup = -1;

    private View layoutAdd;
    private View layoutEdit;
    private View layoutDelete;

    private View addGroup;
    private View editGroup;
    private View deleteGroup;

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
        addGroup = view.findViewById(R.id.add);
        editGroup = view.findViewById(R.id.edit);
        deleteGroup = view.findViewById(R.id.delete);

        layoutAdd = view.findViewById(R.id.l_add_group);
        layoutEdit = view.findViewById(R.id.l_edit_group);
        layoutDelete = view.findViewById(R.id.l_delete_group);

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
        GpsRequest mRequest = new GpsRequest(getActivity());
        mRequest.setAccountID(Session.getAccountId());
        mRequest.setUserID(Session.getUserId());
        mRequest.setPassword(Session.getUserPassword());
        mRequest.setMethod(Request.Method.POST);
        mRequest.setUrl(GpsRequest.ADMIN_URL);
        mRequest.setCommand(GpsRequest.CMD_GET_GROUPS);
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
        mRequest.setErrorHandler(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
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
        HttpQueue.getInstance(getActivity()).cancel(TAG_REQUEST);
        showProgress(false);
    }

    private void setBottomToolbar(){
        TextView icAddGroup = (TextView) view.findViewById(R.id.ic_add);
        icAddGroup.setTypeface(mTf);
        icAddGroup.setText(String.valueOf((char) 0xe717));
        TextView txtAddGroup = (TextView) view.findViewById(R.id.txt_add);
        txtAddGroup.setText(R.string.add);

        TextView icEditGroup = (TextView) view.findViewById(R.id.ic_edit);
        icEditGroup.setTypeface(mTf);
        icEditGroup.setText(String.valueOf((char) 0xe714));
        TextView txtEditGroup = (TextView) view.findViewById(R.id.txt_edit);
        txtEditGroup.setText(R.string.edit);

        TextView icDeleteGroup = (TextView) view.findViewById(R.id.ic_delete);
        icDeleteGroup.setTypeface(mTf);
        icDeleteGroup.setText(String.valueOf((char) 0xe608));
        TextView txtDeleteGroup = (TextView) view.findViewById(R.id.txt_delete);
        txtDeleteGroup.setText(R.string.delete);



        if (mApplication.getAclAdminGroup() > 2) {
            addGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (layoutAdd.getVisibility() == View.VISIBLE) {
                        layoutAdd.setVisibility(View.GONE);
                        expListView.setVisibility(View.VISIBLE);
                        addGroup.setBackgroundColor(getResources().getColor(R.color.base_color));
                    } else {
                        layoutDelete.setVisibility(View.GONE);
                        layoutEdit.setVisibility(View.GONE);
                        layoutAdd.setVisibility(View.VISIBLE);
                        expListView.setVisibility(View.GONE);
                        addGroup.setBackgroundColor(getResources().getColor(R.color.base_color_dark));
                        editGroup.setBackgroundColor(getResources().getColor(R.color.base_color));
                        deleteGroup.setBackgroundColor(getResources().getColor(R.color.base_color));
                        setupLayoutAdd();
                    }
                }
            });

            deleteGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (layoutDelete.getVisibility() == View.VISIBLE) {
                        layoutDelete.setVisibility(View.GONE);
                        //expListView.setVisibility(View.VISIBLE);
                        deleteGroup.setBackgroundColor(getResources().getColor(R.color.base_color));
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
                            addGroup.setBackgroundColor(getResources().getColor(R.color.base_color));
                            editGroup.setBackgroundColor(getResources().getColor(R.color.base_color));
                            deleteGroup.setBackgroundColor(getResources().getColor(R.color.base_color_dark));
                            setupLayoutDelete();
                        }
                    }
                }
            });
        } else {
            icAddGroup.setTextColor(getResources().getColor(R.color.disable));
            txtAddGroup.setTextColor(getResources().getColor(R.color.disable));
            icDeleteGroup.setTextColor(getResources().getColor(R.color.disable));
            txtDeleteGroup.setTextColor(getResources().getColor(R.color.disable));

            addGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), getText(R.string.you_dont_have_permission), Toast.LENGTH_LONG).show();
                }
            });
            deleteGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), getText(R.string.you_dont_have_permission), Toast.LENGTH_LONG).show();
                }
            });
        }

        if (mApplication.getAclAdminGroup() > 1) {
            editGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mApplication.getAclAdminDevice() < 2) {
                        Toast.makeText(getActivity(), getResources().getText(R.string.you_dont_have_permission), Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (layoutEdit.getVisibility() == View.VISIBLE) {
                        layoutEdit.setVisibility(View.GONE);
                        expListView.setVisibility(View.VISIBLE);
                        editGroup.setBackgroundColor(getResources().getColor(R.color.base_color));
                    } else {
                        if (previousGroup == -1) {
                            Toast.makeText(getActivity(), getResources().getText(R.string.you_must_select_a_device), Toast.LENGTH_LONG).show();
                        } else {
                            layoutDelete.setVisibility(View.GONE);
                            layoutEdit.setVisibility(View.VISIBLE);
                            layoutAdd.setVisibility(View.GONE);
                            expListView.setVisibility(View.GONE);
                            addGroup.setBackgroundColor(getResources().getColor(R.color.base_color));
                            editGroup.setBackgroundColor(getResources().getColor(R.color.base_color_dark));
                            deleteGroup.setBackgroundColor(getResources().getColor(R.color.base_color));
                            setupLayoutEdit();
                        }
                    }
                }
            });
        } else {
            icEditGroup.setTextColor(getResources().getColor(R.color.disable));
            txtEditGroup.setTextColor(getResources().getColor(R.color.disable));
            editGroup.setOnClickListener(new View.OnClickListener() {
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

                EditText edtDescription = (EditText) layoutAdd.findViewById(R.id.txt_group_desc_content);
                String description = edtDescription.getText().toString();
                EditText edtDisplayName = (EditText) layoutAdd.findViewById(R.id.txt_group_display_content);
                String displayName = edtDisplayName.getText().toString();
                if (StringTools.isBlank(description) && StringTools.isBlank(displayName)) {
                    Toast.makeText(getActivity(), getResources().getText(R.string._must_input_description), Toast.LENGTH_LONG).show();
                    return;
                }

                EditText edtNotes = (EditText) layoutAdd.findViewById(R.id.txt_notes_content);
                String notes = edtNotes.getText().toString();

                Group g = new Group(getActivity());
                g.setDescription(description);
                g.setDisplayName(displayName);
                g.setNotes(notes);

                GpsRequest crtRequest = g.getRequestCreate();
                crtRequest.setResponseHandler(new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        showProgress(false);
                        MyResponse myResponse = new MyResponse(response);
                        if (myResponse.isSuccess()) {
                            layoutAdd.setVisibility(View.GONE);
                            expListView.setVisibility(View.VISIBLE);
                            addGroup.setBackgroundColor(getResources().getColor(R.color.base_color));
                        }
                        //update group list
                        {
                            String tag = "android:switcher:"+R.id.view_pager_admin+":"+2;
                            AdmGroup frg = (AdmGroup)getFragmentManager().findFragmentByTag(tag);
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
                        Toast.makeText(getActivity(), getActivity().getResources().getText(R.string.failure_create_group), Toast.LENGTH_LONG).show();
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
                addGroup.setBackgroundColor(getResources().getColor(R.color.base_color));
            }
        });
    }

    private void setupLayoutEdit() {
        ExpListAdapter adt = (ExpListAdapter) expListView.getExpandableListAdapter();//.getAdapter();
        final Group g = (Group)adt.getGroup(previousGroup);

        final EditText edtDescription = (EditText) layoutEdit.findViewById(R.id.txt_group_desc_content);
        edtDescription.setText(g.getDescription());

        final EditText edtDisplayName = (EditText) layoutEdit.findViewById(R.id.txt_group_display_content);
        edtDisplayName.setText(g.getDisplayName());

        final EditText edtNotes = (EditText) layoutEdit.findViewById(R.id.txt_notes_content);
        edtNotes.setText(g.getNotes());

        Button btnSave = (Button) layoutEdit.findViewById(R.id.btn_save);
        Button btnCancel = (Button) layoutEdit.findViewById(R.id.btn_cancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = edtDescription.getText().toString();
                String displayName = edtDisplayName.getText().toString();
                String notes = edtNotes.getText().toString();
                g.setContext(getActivity());
                g.setDescription(description);
                g.setDisplayName(displayName);
                g.setNotes(notes);

                GpsRequest edtRequest = g.getRequestEdit();
                edtRequest.setResponseHandler(new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        showProgress(false);
                        MyResponse myResponse = new MyResponse(response);
                        if (myResponse.isSuccess()) {
                            layoutEdit.setVisibility(View.GONE);
                            expListView.setVisibility(View.VISIBLE);
                            editGroup.setBackgroundColor(getResources().getColor(R.color.base_color));
                        }
                        //update group list
                        {
                            String tag = "android:switcher:"+R.id.view_pager_admin+":"+2;
                            AdmGroup frg = (AdmGroup)getFragmentManager().findFragmentByTag(tag);
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
                        Toast.makeText(getActivity(), getResources().getText(R.string.failure_create_group), Toast.LENGTH_LONG).show();
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
                editGroup.setBackgroundColor(getResources().getColor(R.color.base_color));
            }
        });
    }
    private void setupLayoutDelete() {
        ExpListAdapter adt = (ExpListAdapter) expListView.getExpandableListAdapter();//.getAdapter();
        final Group g = (Group)adt.getGroup(previousGroup);
        TextView txtConfirm = (TextView)layoutDelete.findViewById(R.id.delete_confirm);
        StringBuffer sb = new StringBuffer();
        sb.append(getResources().getString(R.string.are_you_sure));
        sb.append(": ").append(g.getDescription());
        txtConfirm.setText(sb);

        Button btnDelete = (Button) layoutDelete.findViewById(R.id.btn_delete);
        Button btnCancel = (Button) layoutDelete.findViewById(R.id.btn_cancel);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                g.setContext(getActivity());
                GpsRequest deleteRequest = g.getRequestDelete();
                deleteRequest.setResponseHandler(new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        showProgress(false);
                        MyResponse myResponse = new MyResponse(response);
                        if (myResponse.isSuccess()) {
                            layoutDelete.setVisibility(View.GONE);
                            deleteGroup.setBackgroundColor(getResources().getColor(R.color.base_color));
                            {
                                previousGroup = -1; //reset
                                String tag = "android:switcher:"+R.id.view_pager_admin+":"+2;
                                AdmGroup frg = (AdmGroup)getFragmentManager().findFragmentByTag(tag);
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
                        Toast.makeText(getActivity(), getText(R.string.failure_create_group), Toast.LENGTH_LONG).show();
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
                deleteGroup.setBackgroundColor(getResources().getColor(R.color.base_color));
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

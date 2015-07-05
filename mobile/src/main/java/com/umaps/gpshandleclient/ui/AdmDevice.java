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
import com.umaps.gpshandleclient.model.Device;
import com.umaps.gpshandleclient.model.MyResponse;
import com.umaps.gpshandleclient.util.GpsOldRequest;
import com.umaps.gpshandleclient.util.StringTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by beou on 08/06/2015.
 */
public class AdmDevice extends Fragment {
    private static final String TAG = "AdmDevice";
    private static final String TAG_REQUEST = "AdmDevice";
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

    View addDevice;
    View editDevice;
    View deleteDevice;

    private GpsOldRequest mRequest;
    public static AdmDevice newInstance(){
        return new AdmDevice();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_adm_device, container, false);

        //-- for progress-bar
        mBarProgress = view.findViewById(R.id.bar_progress);
        mProgress = view.findViewById(R.id.progress);
        mApplication = MyApplication.getInstance();
        this.mTf = MyApplication.getIconFont();
        expListView = (ExpandableListView) view.findViewById(R.id.list_view_devices);

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
        mRequest.setCommand(GpsOldRequest.CMD_GET_DEVICES);
        JSONObject params = Device.createParams();
        mRequest.setParams(params);
        mRequest.setResponseHandler(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                showProgress(false);
                MyResponse mRes = new MyResponse(response);
                if (mRes.isError()) {
                    return;
                }
                ArrayList<Device> deviceArrayList = new ArrayList<Device>();
                JSONArray mJsonArray = (JSONArray) mRes.getData();
                for (int i=0; i< mJsonArray.length(); i++) {
                    try {
                        Device d = new Device(mJsonArray.getJSONObject(i));
                        deviceArrayList.add(d);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                expListView.setClickable(true);
                final ExpListAdapter expListAdapter = new ExpListAdapter(getActivity(), deviceArrayList);
                if (expListAdapter != null) {
                    expListView.setAdapter(expListAdapter);
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

    private void setBottomToolbar(){
        TextView icAddDevice = (TextView) view.findViewById(R.id.ic_add_device);
        icAddDevice.setTypeface(mTf);
        icAddDevice.setText(String.valueOf((char) 0xe717));
        TextView txtAddDevice = (TextView) view.findViewById(R.id.txt_add_device);
        txtAddDevice.setText(R.string.add);

        TextView icEditDevice = (TextView) view.findViewById(R.id.ic_edit_device);
        icEditDevice.setTypeface(mTf);
        icEditDevice.setText(String.valueOf((char) 0xe714));
        TextView txtEditDevice = (TextView) view.findViewById(R.id.txt_edit_device);
        txtEditDevice.setText(R.string.edit);

        TextView icDeleteDevice = (TextView) view.findViewById(R.id.ic_delete_device);
        icDeleteDevice.setTypeface(mTf);
        icDeleteDevice.setText(String.valueOf((char) 0xe608));
        TextView txtDeleteDevice = (TextView) view.findViewById(R.id.txt_delete_device);
        txtDeleteDevice.setText(R.string.delete);

        //-- init toolbar control button
        addDevice = view.findViewById(R.id.add_device);
        editDevice = view.findViewById(R.id.edit_device);
        deleteDevice = view.findViewById(R.id.delete_device);


        layoutAdd = view.findViewById(R.id.l_add_device);
        layoutEdit = view.findViewById(R.id.l_edit_device);
        layoutDelete = view.findViewById(R.id.l_delete_device);

        addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mApplication.getAclAdminDevice() < 3) {
                    Toast.makeText(getActivity(), getResources().getText(R.string.you_dont_have_permission), Toast.LENGTH_LONG).show();
                    return;
                }

                if (layoutAdd.getVisibility() == View.VISIBLE) {
                    layoutAdd.setVisibility(View.GONE);
                    expListView.setVisibility(View.VISIBLE);
                    addDevice.setBackgroundColor(getResources().getColor(R.color.base_color));
                } else {
                    layoutDelete.setVisibility(View.GONE);
                    layoutEdit.setVisibility(View.GONE);
                    layoutAdd.setVisibility(View.VISIBLE);
                    expListView.setVisibility(View.GONE);
                    addDevice.setBackgroundColor(getResources().getColor(R.color.base_color_dark));
                    editDevice.setBackgroundColor(getResources().getColor(R.color.base_color));
                    deleteDevice.setBackgroundColor(getResources().getColor(R.color.base_color));
                    setupLayoutAdd();
                }
            }
        });
        editDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mApplication.getAclAdminDevice() < 2 ) {
                    Toast.makeText(getActivity(), getResources().getText(R.string.you_dont_have_permission), Toast.LENGTH_LONG).show();
                    return;
                }
                if (layoutEdit.getVisibility() == View.VISIBLE) {
                    layoutEdit.setVisibility(View.GONE);
                    expListView.setVisibility(View.VISIBLE);
                    editDevice.setBackgroundColor(getResources().getColor(R.color.base_color));
                } else {
                    if (previousGroup == -1) {
                        Toast.makeText(getActivity(), getResources().getText(R.string.you_must_select_a_device), Toast.LENGTH_LONG).show();
                    } else {
                        layoutDelete.setVisibility(View.GONE);
                        layoutEdit.setVisibility(View.VISIBLE);
                        layoutAdd.setVisibility(View.GONE);
                        expListView.setVisibility(View.GONE);
                        addDevice.setBackgroundColor(getResources().getColor(R.color.base_color));
                        editDevice.setBackgroundColor(getResources().getColor(R.color.base_color_dark));
                        deleteDevice.setBackgroundColor(getResources().getColor(R.color.base_color));
                        setupLayoutEdit();
                    }
                }
            }
        });
        deleteDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mApplication.getAclAdminDevice() < 3 ) {
                    Toast.makeText(getActivity(), getResources().getText(R.string.you_dont_have_permission), Toast.LENGTH_LONG).show();
                    return;
                }
                if (layoutDelete.getVisibility() == View.VISIBLE) {
                    layoutDelete.setVisibility(View.GONE);
                    expListView.setVisibility(View.VISIBLE);
                    deleteDevice.setBackgroundColor(getResources().getColor(R.color.base_color));
                } else {
                    if (previousGroup == -1) {
                        Toast.makeText(getActivity(), getResources().getText(R.string.you_must_select_a_device), Toast.LENGTH_LONG).show();
                    } else {
                        layoutDelete.setVisibility(View.VISIBLE);
                        layoutEdit.setVisibility(View.GONE);
                        layoutAdd.setVisibility(View.GONE);
                        expListView.setVisibility(View.GONE);
                        addDevice.setBackgroundColor(getResources().getColor(R.color.base_color));
                        editDevice.setBackgroundColor(getResources().getColor(R.color.base_color));
                        deleteDevice.setBackgroundColor(getResources().getColor(R.color.base_color_dark));
                        setupLayoutDelete();
                    }
                }
            }
        });
    }

    private void setupLayoutAdd(){
        Button btnSave = (Button) layoutAdd.findViewById(R.id.btn_save);
        Button btnCancel = (Button) layoutAdd.findViewById(R.id.btn_cancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edtUniqueID = (EditText) layoutAdd.findViewById(R.id.txt_device_unique_id_content);
                String uniqueID = edtUniqueID.getText().toString();
                if (StringTools.isBlank(uniqueID)){
                    Toast.makeText(getActivity(), getResources().getText(R.string._must_input_unique_id), Toast.LENGTH_LONG).show();
                    return;
                }

                EditText edtDeviceID = (EditText) layoutAdd.findViewById(R.id.txt_device_id_content);
                String deviceID = StringTools.isBlank(edtDeviceID.getText().toString()) ? uniqueID :
                        edtDeviceID.getText().toString();

                EditText edtDescription = (EditText) layoutAdd.findViewById(R.id.txt_device_desc_content);
                String description = edtDescription.getText().toString();

                EditText edtDisplayName = (EditText) layoutAdd.findViewById(R.id.txt_device_display_content);
                String displayName = edtDisplayName.getText().toString();

                Switch swActivated = (Switch) layoutAdd.findViewById(R.id.txt_is_active_content);

                EditText edtSerialNumber = (EditText) layoutAdd.findViewById(R.id.txt_device_serial_number_content);
                String serialNumber = edtSerialNumber.getText().toString();

                EditText edtSimNumber = (EditText) layoutAdd.findViewById(R.id.txt_device_sim_number_content);
                String simNumber = edtSimNumber.getText().toString();

                EditText edtSmsLimited = (EditText) layoutAdd.findViewById(R.id.txt_device_sms_limited_content);
                String mSmsLimited = edtSmsLimited.getText().toString();
                int smsLimited = Integer.parseInt(StringTools.isBlank(mSmsLimited) ? "0" : mSmsLimited);

                EditText edtGroupID = (EditText) layoutAdd.findViewById(R.id.txt_group_id_content);
                String groupID = edtGroupID.getText().toString();

                EditText edtNotes = (EditText) layoutAdd.findViewById(R.id.txt_device_notes_content);
                String notes = edtNotes.getText().toString();

                Device d = new Device(getActivity());
                d.setUniqueID(uniqueID);
                d.setDeviceID(deviceID);
                d.setDescription(description);
                d.setDisplayName(displayName);
                d.setActive(swActivated.isChecked());
                d.setSerialNumber(serialNumber);
                d.setSimNumber(simNumber);
                d.setSmsLimit(smsLimited);
                d.setGroupIDs(groupID);
                d.setNotes(notes);

                GpsOldRequest crtRequest = d.getRequestCreate();

                crtRequest.setResponseHandler(new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        MyResponse myResponse = new MyResponse(response);
                        if (myResponse.isSuccess()) {
                            layoutAdd.setVisibility(View.GONE);
                            expListView.setVisibility(View.VISIBLE);
                            addDevice.setBackgroundColor(getResources().getColor(R.color.base_color));
                        }
                        Toast.makeText(getActivity(), myResponse.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
                crtRequest.setErrorHandler(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), getActivity().getResources().getText(R.string.failure_create_device), Toast.LENGTH_LONG).show();
                    }
                });
                crtRequest.exec();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutAdd.setVisibility(View.GONE);
                expListView.setVisibility(View.VISIBLE);
                addDevice.setBackgroundColor(getResources().getColor(R.color.base_color));
            }
        });
    }

    private void setupLayoutEdit() {
        ExpListAdapter adt = (ExpListAdapter) expListView.getExpandableListAdapter();//.getAdapter();
        Device d = (Device)adt.getGroup(previousGroup);
        //--
        final EditText edtUniqueID = (EditText) layoutEdit.findViewById(R.id.txt_device_unique_id_content);
        edtUniqueID.setText(d.getUniqueID());

        final EditText edtDeviceID = (EditText) layoutEdit.findViewById(R.id.txt_device_id_content);
        edtDeviceID.setText(d.getDeviceID());

        final EditText edtDescription = (EditText) layoutEdit.findViewById(R.id.txt_device_desc_content);
        edtDescription.setText(d.getDescription());

        final EditText edtDisplayName = (EditText) layoutEdit.findViewById(R.id.txt_device_display_content);
        edtDisplayName.setText(d.getDisplayName());

        final Switch swActive = (Switch) layoutEdit.findViewById(R.id.txt_is_active_content);
        swActive.setChecked(d.isActive());

        final EditText edtSerialNumber = (EditText) layoutEdit.findViewById(R.id.txt_device_serial_number_content);
        edtSerialNumber.setText(d.getSerialNumber());

        final EditText edtSimNumber = (EditText) layoutEdit.findViewById(R.id.txt_device_sim_number_content);
        edtSimNumber.setText(d.getSimNumber());

        final EditText edtSmsLimited = (EditText) layoutEdit.findViewById(R.id.txt_device_sms_limited_content);
        edtSmsLimited.setText(String.valueOf(d.getSmsLimit()));

        final EditText edtGroupID = (EditText) layoutEdit.findViewById(R.id.txt_group_id_content);
        edtGroupID.setText(d.getGroupIDs());

        final EditText edtNotes = (EditText) layoutEdit.findViewById(R.id.txt_device_notes_content);
        edtNotes.setText(d.getNotes());

        Button btnSave = (Button) layoutEdit.findViewById(R.id.btn_save);
        Button btnCancel = (Button) layoutEdit.findViewById(R.id.btn_cancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uniqueID = edtUniqueID.getText().toString();
                if (StringTools.isBlank(uniqueID)){
                    Toast.makeText(getActivity(), getResources().getText(R.string._must_input_unique_id), Toast.LENGTH_LONG).show();
                }
                String deviceID = StringTools.isBlank(edtDeviceID.getText().toString()) ? uniqueID : edtDeviceID.getText().toString();
                String description = edtDescription.getText().toString();
                String displayName = edtDisplayName.getText().toString();
                String serialNumber = edtSerialNumber.getText().toString();
                String simNumber = edtSimNumber.getText().toString();
                int smsLimited = Integer.parseInt(edtSmsLimited.getText().toString());
                String groupID = edtGroupID.getText().toString();
                String notes = edtNotes.getText().toString();

                boolean activated = swActive.isChecked();

                Device d = new Device(getActivity());
                d.setUniqueID(uniqueID);
                d.setDeviceID(deviceID);
                d.setDescription(description);
                d.setDisplayName(displayName);
                d.setActive(activated);
                d.setSerialNumber(serialNumber);
                d.setSimNumber(simNumber);
                d.setSmsLimit(smsLimited);
                d.setGroupIDs(groupID);
                d.setNotes(notes);

                GpsOldRequest edtRequest = d.getRequestEdit();
                edtRequest.setResponseHandler(new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        MyResponse myResponse = new MyResponse(response);
                        if (myResponse.isSuccess()) {
                            layoutEdit.setVisibility(View.GONE);
                            expListView.setVisibility(View.VISIBLE);
                            editDevice.setBackgroundColor(getResources().getColor(R.color.base_color));
                        }
                        Toast.makeText(getActivity(), myResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                edtRequest.setErrorHandler(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), getResources().getText(R.string.failure_create_device), Toast.LENGTH_LONG).show();
                    }
                });

                edtRequest.exec();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutEdit.setVisibility(View.GONE);
                expListView.setVisibility(View.VISIBLE);
                editDevice.setBackgroundColor(getResources().getColor(R.color.base_color));
            }
        });
    }

    private void setupLayoutDelete() {
        ExpListAdapter adt = (ExpListAdapter) expListView.getExpandableListAdapter();//.getAdapter();
        Device d = (Device)adt.getGroup(previousGroup);
        TextView txtConfirm = (TextView)layoutDelete.findViewById(R.id.delete_confirm);
        StringBuffer sb = new StringBuffer();
        sb.append(getResources().getString(R.string.are_you_sure));
        sb.append(d.getDescription());

        txtConfirm.setText(sb);

        Button btnDelete = (Button) layoutDelete.findViewById(R.id.btn_delete);
        Button btnCancel = (Button) layoutDelete.findViewById(R.id.btn_cancel);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO

                //Delete device
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutDelete.setVisibility(View.GONE);
                expListView.setVisibility(View.VISIBLE);
                layoutDelete.setBackgroundColor(getResources().getColor(R.color.base_color));
            }
        });
    }

    public class ExpListAdapter extends BaseExpandableListAdapter {
        private static final String TAG = "ExpListAdapter";
        Typeface mTf = null;
        private Context context;
        private ArrayList<Device> objects;
        LayoutInflater mInflater;
        View groupView;
        View detailsView;
        TextView icIndicator;

        public ExpListAdapter(Context context, ArrayList<Device> objects){
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
                groupView = mInflater.inflate(R.layout.device_header, null);
            } else {
                groupView = convertView;
            }

            if (objects == null) return groupView;

            Device md = objects.get(groupPosition);
            String deviceID     = md.getDeviceID();
            String description  = md.getDescription();
            String displayName  = md.getDisplayName();
            String uniqueID     = md.getUniqueID();
            String groupID      = md.getGroupIDs();
            String driverID     = md.getDriverID();
            int driverStatus    = md.getDriverStatus();
            String pushpinID    = md.getPushpinID();
            String serialNumber = md.getSerialNumber();
            String notes        = md.getNotes();
            double lastLat      = md.getLastLatitude();
            double lastLng      = md.getLastLongitude();
            boolean isActive    = md.isActive();
            int smsCount        = md.getSmsCount();
            int smsLimit        = md.getSmsLimit();
            long creationTime   = md.getCreationTime();

            TextView txtDeviceId    = (TextView) groupView.findViewById(R.id.txt_device_id);
            txtDeviceId.setText(deviceID);
            TextView txtDeviceDesc = (TextView) groupView.findViewById(R.id.txt_device_desc);
            txtDeviceDesc.setText(description);

            TextView txtDeviceActive = (TextView) groupView.findViewById(R.id.txt_device_active);
            txtDeviceActive.setTypeface(mTf);
            txtDeviceActive.setText(String.valueOf((char) 0xe60b));
            if (isActive) {
                txtDeviceActive.setTextColor(getResources().getColor(R.color.green));
            } else {
                txtDeviceActive.setTextColor(getResources().getColor(R.color.bad));
            }
            icIndicator = (TextView) groupView.findViewById(R.id.ic_indicator);
            icIndicator.setTypeface(mTf);
            icIndicator.setText(String.valueOf((char) 0xe6ee));

            return groupView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if(convertView == null) {
                detailsView = mInflater.inflate(R.layout.device_details, null);
            } else {
                detailsView = convertView;
            }

            Device md = objects.get(groupPosition);
            String deviceID     = md.getDeviceID();
            String description  = md.getDescription();
            String displayName  = md.getDisplayName();
            String uniqueID     = md.getUniqueID();
            String groupID      = md.getGroupIDs();
            String driverID     = md.getDriverID();
            int driverStatus    = md.getDriverStatus();
            String pushpinID    = md.getPushpinID();
            String serialNumber = md.getSerialNumber();
            String notes        = md.getNotes();
            double lastLat      = md.getLastLatitude();
            double lastLng      = md.getLastLongitude();
            boolean isActive    = md.isActive();
            int smsCount        = md.getSmsCount();
            int smsLimit        = md.getSmsLimit();
            long creationTime   = md.getCreationTime();

            TextView txtAccountId = (TextView) detailsView.findViewById(R.id.txt_device_id_content);
            txtAccountId.setText(deviceID);

            TextView txtAccountDesc = (TextView) detailsView.findViewById(R.id.txt_device_desc_content);
            txtAccountDesc.setText(description);

            TextView txtAccountDisplay = (TextView) detailsView.findViewById(R.id.txt_device_display_content);
            txtAccountDisplay.setText(displayName);

            TextView txtIsActive = (TextView) detailsView.findViewById(R.id.txt_is_active_content);
            txtIsActive.setText(String.valueOf(isActive));

            TextView txtSerialNumber = (TextView) detailsView.findViewById(R.id.txt_device_serial_number_content);
            txtSerialNumber.setText(serialNumber);

            TextView txtSmsSent = (TextView) detailsView.findViewById(R.id.txt_device_sms_sent_content);
            txtSmsSent.setText(String.valueOf(smsCount));

            TextView txtSmsLimit = (TextView) detailsView.findViewById(R.id.txt_device_sms_limited_content);
            txtSmsLimit.setText(String.valueOf(smsLimit));

            TextView txtCreatedOn = (TextView) detailsView.findViewById(R.id.txt_device_creation_content);
            Date createdOn = new Date(creationTime * 1000);
            txtCreatedOn.setText(createdOn.toString());
            TextView txtLastLogin = (TextView) detailsView.findViewById(R.id.txt_device_last_update_content);
            Date lastLogin = new Date(creationTime * 1000);
            txtLastLogin.setText(lastLogin.toString());

            TextView txtGroupId = (TextView) detailsView.findViewById(R.id.txt_group_id_content);
            txtGroupId.setText(String.valueOf(groupID));

            TextView txtNotes = (TextView) detailsView.findViewById(R.id.txt_device_notes_content);
            txtNotes.setText(notes);

            return detailsView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}

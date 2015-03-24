package com.umaps.gpshandleclient.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.model.Device;
import com.umaps.gpshandleclient.model.Group;
import com.umaps.gpshandleclient.settings.SessionState;
import com.umaps.gpshandleclient.settings.Utilities;
import com.umaps.gpshandleclient.views.GroupExpandableListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.umaps.gpshandleclient.util.StringTools.createDevicesInGroup;
import static com.umaps.gpshandleclient.util.StringTools.createGroupList;

/**
 * Created by vu@umaps.vn on 31/01/2015.
 */
public class GroupListFragment extends DialogFragment{
    private static final String TAG = "GroupListFragment";
    private GroupListCallback mCallback;

    ExpandableListView expandableGroupList;
    private Dialog dialog;
    public static GroupListFragment newInstance(int title) {
        GroupListFragment dialogFragment = new GroupListFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (GroupListCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
        /*jsonRequest = createJSONRequest();
        createGetGroupsRequest(jsonRequest);*/

        View v = getActivity().getLayoutInflater().inflate(R.layout.groups_expandable_dialog, null);
        expandableGroupList = (ExpandableListView) v.findViewById(R.id.expandable_group_list);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v)
                .setPositiveButton(R.string.gotit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setTitle(R.string.device_list);
        dialog =  builder.create();
        showListViewOfGroup();
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NORMAL;
        int theme = 0;
        setStyle(style, theme);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        return dialog;
    }

    private void showListViewOfGroup(){
        expandableGroupList.setClickable(true);
        //--crete GroupExpandableListAdapter
        JSONObject dgObject =null;
        try {
            dgObject = SessionState.getGroupList(getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<Group> groupsList = null;
        HashMap<String, ArrayList< Device >> devicesInGroup = null;
        try {
            groupsList = createGroupList(dgObject);
            devicesInGroup = createDevicesInGroup(dgObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final GroupExpandableListAdapter groupListAdapter =
                new GroupExpandableListAdapter(getActivity(), groupsList, devicesInGroup);
        if (groupListAdapter != null){
            groupListAdapter.setExpandableListView(expandableGroupList);
            expandableGroupList.setAdapter(groupListAdapter);
        }

        //--setOnclickListener
        expandableGroupList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Group group = (Group) groupListAdapter.getGroup(groupPosition);
                Log.i("ABC", "OnGroupClicked!+" + group.getGroupId());
                mCallback.onGroupItemSelected(group.getGroupId(), group.getDescription());
                getDialog().dismiss();
                return true;
            }
        });
        expandableGroupList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.i(TAG, "onChildClick");
                //-- Move to latest position of this devices
                Device device = (Device) groupListAdapter.getChild(groupPosition, childPosition);
                mCallback.onChildItemSelected(device.getDeviceID(), device.getDescription());
                getDialog().dismiss();
                return true;
            }
        });
        Utilities.HideProgress();
    }

    public interface GroupListCallback{
        void onGroupItemSelected(String groupId, String desc);
        void onChildItemSelected(String deviceId, String desc);
    }
}

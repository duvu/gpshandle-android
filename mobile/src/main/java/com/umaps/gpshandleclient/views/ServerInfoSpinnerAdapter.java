package com.umaps.gpshandleclient.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.model.ServerInfo;

import java.util.ArrayList;


/**
 * Created by vu@umaps.vn on 13/02/2015.
 */
public class ServerInfoSpinnerAdapter extends ArrayAdapter<ServerInfo> {
    private Context mContext;
    private ArrayList<ServerInfo> mServerList;
    public ServerInfoSpinnerAdapter(Context context, ArrayList<ServerInfo> listServer) {
        super(context, R.layout.server_info_row, R.id.server_name, listServer);
        this.mContext = context;
        this.mServerList = listServer;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(mContext).inflate(R.layout.server_info_row, parent, false);
        }
        ServerInfo serverInfo = mServerList.get(position);
        if (serverInfo == null) return null;

        ImageView iconServer = (ImageView) v.findViewById(R.id.server_image);
        iconServer.setImageResource(R.drawable.ic_public_grey600_48dp);
        TextView nameTextView = (TextView) v.findViewById(R.id.server_name);
        TextView urlTextView = (TextView) v.findViewById(R.id.server_url);
        nameTextView.setText(serverInfo.getName());
        urlTextView.setText(serverInfo.getWebUrl());
        return v;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(mContext).inflate(R.layout.server_info_row, parent, false);
        }
        ServerInfo serverInfo = mServerList.get(position);
        if (serverInfo == null) return null;
        ImageView iconServer = (ImageView) v.findViewById(R.id.server_image);
        iconServer.setImageResource(R.drawable.ic_swap_vert_circle_grey600_48dp);
        TextView nameTextView = (TextView) v.findViewById(R.id.server_name);
        TextView urlTextView = (TextView) v.findViewById(R.id.server_url);
        nameTextView.setText(serverInfo.getName());
        urlTextView.setText(serverInfo.getWebUrl());

        return v;
    }
}

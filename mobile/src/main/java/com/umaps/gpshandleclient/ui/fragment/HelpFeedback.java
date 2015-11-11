package com.umaps.gpshandleclient.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.event.UpdateEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by beou on 19/08/2015.
 */
public class HelpFeedback extends GenericViewFragment {

    private View view;

    public static HelpFeedback newInstance() {
        HelpFeedback frg = new HelpFeedback();
        return frg;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_help_feedback, container, false);
        EventBus.getDefault().post(new UpdateEvent.OnLive(false));

        Button btnSendMessage = (Button) view.findViewById(R.id.send_us_a_message);
        Button btnContactInfo = (Button) view.findViewById(R.id.contact_info);

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });

        btnContactInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });

        return view;
    }
}

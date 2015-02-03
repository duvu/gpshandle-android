package com.umaps.vtrack.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.umaps.vtrack.R;

/**
 * Created by vu@umaps.vn on 31/01/2015.
 */
public class AlertDialogFragment extends DialogFragment{
    public static AlertDialogFragment newInstance(int title) {
        AlertDialogFragment dialogFragment = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        int title = getArguments().getInt("title");
        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.bubble_mask)
                .setTitle(title)
                .setPositiveButton(R.string.gotit,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        })
                .setNegativeButton(R.string.gotit,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        })
                .create();
    }
}

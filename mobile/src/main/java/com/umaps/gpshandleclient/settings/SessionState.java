package com.umaps.gpshandleclient.settings;

import android.app.Application;
import android.app.ProgressDialog;

/**
 * Created by vu@umaps.vn on 31/01/2015.
 */
public class SessionState extends Application {
    public static final String ACL_ADMIN_ACCOUNT        =   "acl.service.admin.account";
    private static ProgressDialog progressDialog;

    public static ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public static void setProgressDialog(ProgressDialog progressDialog) {
        SessionState.progressDialog = progressDialog;
    }
}

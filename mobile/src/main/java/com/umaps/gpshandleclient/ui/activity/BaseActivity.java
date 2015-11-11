package com.umaps.gpshandleclient.ui.activity;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.umaps.gpshandleclient.MyApplication;
import com.umaps.gpshandleclient.R;
import com.umaps.gpssdk.Query;

/**
 * GPSHandleClient
 * com.umaps.gpshandleclient.ui
 * Created by beou on 06/11/2015.
 */
public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        /// Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map_monitor) {
            if (this instanceof MonitorActivity) {

            } else {
                dispatch(MonitorActivity.class);
            }
        } else if (id == R.id.nav_report) {
            if (!(this instanceof ReportActivity)) {
                dispatch(ReportActivity.class);
            }
        } else if (id == R.id.nav_administration) {
            if (!(this instanceof AdminActivity)) {
                dispatch(AdminActivity.class);
            }
        } else if (id == R.id.nav_tools) {
            // TODO: 06/11/2015 tools - ex: sms, commands
        } else if (id == R.id.nav_settings) {
            //// TODO: 06/11/2015 settings
        } else if (id == R.id.nav_logout) {
            doLogout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void dispatch(Class cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    private void doLogout(){
        MyApplication.setIsSignedIn(false);
        Query.getInstance().cancelAll();
        startLoginActivity();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

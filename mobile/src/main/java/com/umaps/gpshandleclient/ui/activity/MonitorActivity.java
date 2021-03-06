package com.umaps.gpshandleclient.ui.activity;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.umaps.gpshandleclient.R;
import com.umaps.gpshandleclient.event.UpdateEvent;
import com.umaps.gpshandleclient.model.ParseGroup;
import com.umaps.gpshandleclient.ui.fragment.MapFragment;
import com.umaps.gpshandleclient.util.EBus;
import com.umaps.gpssdk.GpsSdk;

import java.util.List;

import de.greenrobot.event.EventBus;

public class MonitorActivity extends BaseActivity{

    private static final String TAG = "MainActivity";
    private Toolbar toolbar;

    private void RegisterEventBus() {
        EventBus.getDefault().register(this);
    }

    private void UnregisterEventBus(){
        try {
            EventBus.getDefault().unregister(this);
        } catch (Throwable t){
            //this may crash if registration did not go through. just be safe
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RegisterEventBus();

        setToolbar();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        // Update your UI here.
                    }
                });

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, MapFragment.newInstance())
                .addToBackStack(null).commit();
    }
    @Override
    protected void onDestroy() {
        UnregisterEventBus();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    private void setToolbar(){
        //-- set toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //add group to spinner
        final Spinner sp = (Spinner) toolbar.findViewById(R.id.spinner_group);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ParseGroup pg = (ParseGroup)(parent.getAdapter().getItem(position));
                if (pg!=null){
                    String groupId = pg.getGroupId();
                    GpsSdk.setGroupPosition(position);
                    GpsSdk.setSelectedGroup(groupId);
                    EventBus.getDefault().post(new UpdateEvent.GroupChanged());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //no op
            }
        });

        ParseQuery<ParseGroup> query = ParseQuery.getQuery(ParseGroup.class);
        query.fromLocalDatastore();
        query.setLimit(100);
        onUpdating(true);
        query.findInBackground(new FindCallback<ParseGroup>() {
            @Override
            public void done(List<ParseGroup> objects, ParseException e) {
                onUpdating(false);
                ParseGroup all = ParseGroup.getGroupAll();
                all.setDeviceCount(GpsSdk.getTotalDevices());
                objects.add(all);
                GroupSpinner adapter = new GroupSpinner(getApplicationContext(), R.layout.spinner_group, objects);
                sp.setAdapter(adapter);
                sp.setSelection(GpsSdk.getGroupPosition());
            }
        });
    }

    private void SetBulbStatus(boolean started) {
        ImageView bulb = (ImageView) findViewById(R.id.notification_bulb);
        bulb.setImageResource(started ? R.drawable.circle_green : R.drawable.circle_none);
    }

    @EBus
    public void onEventMainThread(UpdateEvent.OnLive onLive) {
        SetBulbStatus(onLive.isLive());
    }

    @EBus
    public void onEventMainThread(UpdateEvent.OnLoading onLoading) {
        onUpdating(onLoading.isLoading());
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void onUpdating(final boolean show) {
        final ProgressBar mBarProgress = (ProgressBar) findViewById(R.id.progress);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mBarProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mBarProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mBarProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mBarProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }




    private class GroupSpinner extends BaseAdapter {
        private LayoutInflater mInflater;
        private int mResource;
        private List<ParseGroup> items;

        public GroupSpinner(Context context, int resource, List<ParseGroup> objects) {
            mInflater = LayoutInflater.from(context);
            mResource = resource;
            items = objects;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return (items!=null && items.size()>position) ? items.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(mResource, parent, false);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.spinner_group_name);
                holder.size = (TextView) convertView.findViewById(R.id.spinner_group_size);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            if ((items !=null) && (items.size() > position)) {
                holder.name.setText(items.get(position).getName());
                holder.size.setText(String.valueOf(items.get(position).getDeviceCount()));
            }
            return convertView;
        }
    }
    private class ViewHolder {
        TextView name;
        TextView size;
    }
}

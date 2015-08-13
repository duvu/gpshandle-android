/*******************************************************************************
 * This file is part of GPSLogger for Android.
 *
 * GPSLogger for Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * GPSLogger for Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GPSLogger for Android.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.umaps.gpshandleclient.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umaps.gpshandleclient.event.UpdateEvent;
import com.umaps.gpshandleclient.util.EBus;

import de.greenrobot.event.EventBus;


/**
 * Common class for communicating with the parent for the
 * GpsViewCallbacks
 */
public abstract class GenericViewFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RegisterEventBus();
    }

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
    public void onDestroy() {
        UnregisterEventBus();
        super.onDestroy();
    }
    @EBus
    public void onEventMainThread(UpdateEvent.GotNewData newData){

    }
}

package com.umaps.gpshandleclient.event;

/**
 * Created by beou on 13/08/2015.
 */
public class UpdateEvent {
    public static class GotNewData{}

    public static class OnLoading{
        public OnLoading(boolean load) {
            isLoading = load;
        }
        boolean isLoading;

        public boolean isLoading() {
            return isLoading;
        }

        public void setIsLoading(boolean isLoading) {
            this.isLoading = isLoading;
        }
    }
}

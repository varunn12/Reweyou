package in.reweyou.reweyou.classes;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationAddress {
    private static final String TAG = "LocationAddress";
    private static String mycity="Unknown";

    UserSessionManager session;
    public LocationAddress(Context context) {
        session=new UserSessionManager(context);
        mycity=session.getLoginLocation();
    }

    public static void getAddressFromLocation(final double latitude, final double longitude,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = mycity;
                String add=mycity;
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        StringBuilder ad= new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            ad.append(address.getAddressLine(i)).append("\n");
                        }
                         sb.append(address.getLocality()).append("\n");
                        // sb.append(address.getPostalCode()).append("\n");
                        // sb.append(address.getCountryName());
                        result = sb.toString();
                        add=ad.toString();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable connect to Geocoder", e);
                    result=mycity;
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                     //   result = "Latitude: " + latitude + " Longitude: " + longitude +
                             //   "\n\nAddress:\n" + result;
                        bundle.putString("address", result);
                        bundle.putString("add",add);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = mycity;
                        add=mycity;
                        bundle.putString("address", result);
                        bundle.putString("add",add);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }
}

package in.reweyou.reweyou.gcm;

/**
 * Created by Reweyou on 5/8/2016.
 */
import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;


/**
 * Created by Belal on 4/15/2016.
 */
public class GCMTokenRefreshListenerService extends InstanceIDListenerService {

    //If the token is changed registering the device again
    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, GCMRegistrationIntentService.class);
        startService(intent);
    }
}

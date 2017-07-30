package grab.com.thuexetoancau.Fcm;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import grab.com.thuexetoancau.utilities.SharePreference;

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {
    private String token;
    private SharePreference preference;
    @Override
    public void onTokenRefresh() {

        token = FirebaseInstanceId.getInstance().getToken();
        preference = new SharePreference(this);

        preference.saveRegId(token);
        final Intent intent = new Intent("tokenReceiver");
        // You can also include some extra data.
        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        intent.putExtra("token",token);
        broadcastManager.sendBroadcast(intent);
    }



}

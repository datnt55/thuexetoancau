package grab.com.thuexetoancau.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.model.Trip;
import grab.com.thuexetoancau.model.User;
import grab.com.thuexetoancau.utilities.ApiUtilities;
import grab.com.thuexetoancau.utilities.BaseService;
import grab.com.thuexetoancau.utilities.CommonUtilities;
import grab.com.thuexetoancau.utilities.DialogUtils;
import grab.com.thuexetoancau.utilities.Global;
import grab.com.thuexetoancau.utilities.Defines;
import grab.com.thuexetoancau.utilities.PermissionUtils;
import grab.com.thuexetoancau.utilities.SharePreference;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class SplashActivity extends AppCompatActivity {
    private SharePreference preference;
    private ImageView imgLoading;
    private LinearLayout layoutLoading;
    private User user;
    private Context mContext;
    private ApiUtilities mApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            // w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        super.onCreate(savedInstanceState);
        if (PermissionUtils.checkAndRequestPermissions(this)){
            initComponents();
        }
    }
    private void initComponents(){
        setContentView(R.layout.activity_splash);
        mContext = this;
        preference = new SharePreference(this);
        imgLoading = (ImageView) findViewById(R.id.img_loading);
        AnimationDrawable frameAnimation = (AnimationDrawable) imgLoading.getBackground();
        frameAnimation.start();
        FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("test");
        CommonUtilities.dimensionScreen(this);
        CommonUtilities.getListPhone(this);
        mApi = new ApiUtilities(this);
        checkOnline();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtils.REQUEST_ID_MULTIPLE_PERMISSIONS)
            if ((grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED))
                initComponents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        layoutLoading = (LinearLayout) findViewById(R.id.layout_loading);
    }

    private void checkOnline() {
        if (!CommonUtilities.isOnline(this)){
            DialogUtils.showDialogNetworkError(this, new DialogUtils.TryAgain() {
                @Override
                public void onTryAgain() {
                    checkOnline();
                }
            });
            return;
        }

        mApi.getCurrentTime(new ApiUtilities.ServerTimeListener() {
            @Override
            public void onSuccess(long time) {
                DateTime current = new DateTime();
                Global.serverTimeDiff = time - current.getMillis();
                layoutLoading.setVisibility(View.VISIBLE);
                if (preference.getRegId().equals("")) {
                    LocalBroadcastManager.getInstance(mContext).registerReceiver(tokenReceiver, new IntentFilter("tokenReceiver"));
                }else {
                    goToApplication();
                }
            }
        });
    }
    BroadcastReceiver tokenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String token = intent.getStringExtra("token");
            if(token != null)
            {
                Intent i = new Intent(SplashActivity.this, SelectMethodLoginActivity.class);
                startActivity(i);
                finish();
            }


        }
    };

    private void goToApplication(){
        if (preference.getToken() != null){
            mApi.checkTokenLogin(new ApiUtilities.ResponseLoginListener() {
                @Override
                public void onSuccess(Trip trip, User user) {
                    Intent intent = new Intent(SplashActivity.this, PassengerSelectActionActivity.class);
                    intent.putExtra(Defines.BUNDLE_LOGIN_USER, user);
                    if (trip != null) {
                        intent.putExtra(Defines.BUNDLE_LOGIN_TRIP, trip);
                        if (trip.getDriverId() != 0)
                            intent.putExtra(Defines.BUNDLE_LOGIN_DRIVER,true);
                    }
                    startActivity(intent);
                    finish();
                }
            });
        }else{
            Intent i = new Intent(SplashActivity.this, SelectMethodLoginActivity.class);
            startActivity(i);
            finish();
        }

    }

}

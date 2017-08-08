package grab.com.thuexetoancau.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Handler;
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
import grab.com.thuexetoancau.model.User;
import grab.com.thuexetoancau.utilities.ApiUtilities;
import grab.com.thuexetoancau.utilities.BaseService;
import grab.com.thuexetoancau.utilities.CommonUtilities;
import grab.com.thuexetoancau.utilities.DialogUtils;
import grab.com.thuexetoancau.utilities.Global;
import grab.com.thuexetoancau.utilities.Defines;
import grab.com.thuexetoancau.utilities.SharePreference;

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

    }

    @Override
    protected void onResume() {
        super.onResume();
        layoutLoading = (LinearLayout) findViewById(R.id.layout_loading);
        checkOnline();

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
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            goToApplication();
                        }
                    }, 2000);
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
            checkTokenLogin();
        }else{
            Intent i = new Intent(SplashActivity.this, SelectMethodLoginActivity.class);
            startActivity(i);
            finish();
        }

    }

    private void checkTokenLogin() {
        final SharePreference preference = new SharePreference(this);
        RequestParams params;
        params = new RequestParams();
        params.put("token", preference.getToken());
        params.put("regId", preference.getRegId());
        params.put("os", 1);
        DateTime current = new DateTime();
        long key = (current.getMillis() + Global.serverTimeDiff)*13 + 27;
        params.put("key", key);
        /*if (user.getEmail() != null)
            params.put("custom_email", edtCustomerEmail.getText().toString());*/
        Log.e("TAG",params.toString());
        BaseService.getHttpClient().post(Defines.URL_CHECK_TOKEN, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    if (json.getString("status").equals("success")){
                        JSONArray array = json.getJSONArray("data");
                        JSONObject data = array.getJSONObject(0);
                       // preference.saveToken(data.getString("token"));
                        int  id = data.getInt("user_id");
                        String email = data.getString("custom_email");
                        String phone = data.getString("custom_phone");
                        String name = data.getString("custom_name");

                        user = new User(id, name,phone,email,"");
                        Intent intent = new Intent(mContext, PassengerSelectActionActivity.class);
                        intent.putExtra(Defines.BUNDLE_USER, user);
                        startActivity(intent);
                        finish();
                    }else {
                        DialogUtils.showCheckTokenDialog((Activity) mContext, new DialogUtils.YesNoListenter() {
                            @Override
                            public void onYes() {
                                preference.clearToken();
                                Intent intent = new Intent(mContext, SelectMethodLoginActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onNo() {

                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(mContext, getResources().getString(R.string.register_error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Toast.makeText(mContext, getResources().getString(R.string.register_error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

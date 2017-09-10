package grab.com.thuexetoancau.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.accountkit.ui.ThemeUIManager;
import com.facebook.accountkit.ui.UIManager;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.UUID;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.model.Trip;
import grab.com.thuexetoancau.model.User;
import grab.com.thuexetoancau.utilities.ApiUtilities;
import grab.com.thuexetoancau.utilities.CommonUtilities;
import grab.com.thuexetoancau.utilities.Defines;

import static grab.com.thuexetoancau.utilities.Defines.FRAMEWORK_REQUEST_CODE;

public class SelectMethodLoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private CallbackManager callbackManager ;
    private AccessTokenTracker accessTokenTracker ;
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private User user;
    private LinearLayout btnGoogle, btnFacebook, btnLoginPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.addFlags(WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AccountKit.initialize(this);
        btnFacebook = (LinearLayout) findViewById(R.id.btn_login_facebook);
        btnGoogle = (LinearLayout) findViewById(R.id.btn_login_google);
        btnLoginPhone = (LinearLayout) findViewById(R.id.btn_login_phone);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                graphRequest(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                LoginManager.getInstance().logInWithReadPermissions(SelectMethodLoginActivity.this, Arrays.asList("public_profile","email","user_friends"));

            }
        });
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        btnLoginPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(SelectMethodLoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();*/
                onLogin(LoginType.PHONE);
            }
        });
    }


    private void onLogin(final LoginType loginType) {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        final AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder
                = createAccountKitConfiguration(loginType);
        final AccountKitConfiguration configuration = configurationBuilder.build();
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configuration);
        startActivityForResult(intent, FRAMEWORK_REQUEST_CODE);
    }

    private AccountKitConfiguration.AccountKitConfigurationBuilder createAccountKitConfiguration(
            final LoginType loginType) {
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder
                = new AccountKitConfiguration.AccountKitConfigurationBuilder(
                loginType, AccountKitActivity.ResponseType.TOKEN);
        final UIManager uiManager;
        uiManager = new ThemeUIManager(R.style.AppLoginTheme_Bicycle);
        configurationBuilder.setUIManager(uiManager);
        return configurationBuilder;
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
          //  Toast.makeText(getApplicationContext(),acct.getEmail(),Toast.LENGTH_LONG).show();
            user = new User(1,acct.getDisplayName(),null, acct.getEmail(), String.valueOf(acct.getPhotoUrl()));
            Intent intent = new Intent(SelectMethodLoginActivity.this, RegisterActivity.class);
            intent.putExtra(Defines.BUNDLE_USER, user);
            startActivity(intent);
            finish();
        }
    }

    public void graphRequest(AccessToken token){
        GraphRequest request = GraphRequest.newMeRequest(token,new GraphRequest.GraphJSONObjectCallback(){

            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.e("JSON",object.toString());
               // Toast.makeText(getApplicationContext(),object.toString(),Toast.LENGTH_LONG).show();
                parseFacebookProfile(object);
                Intent intent = new Intent(SelectMethodLoginActivity.this, RegisterActivity.class);
                intent.putExtra(Defines.BUNDLE_USER, user);
                startActivity(intent);
                finish();
            }
        });

        Bundle b = new Bundle();
        b.putString("fields","id,email,first_name,last_name,name, birthday ,picture.type(large)");
        request.setParameters(b);
        request.executeAsync();

    }

    private void parseFacebookProfile(JSONObject profile) {
        try {
            String email = profile.getString("email");
            String fullName = profile.getString("name");
            JSONObject picture = profile.getJSONObject("picture");
            JSONObject data = picture.getJSONObject("data");
            String url = data.getString("url");
            user = new User(1,fullName,null,email,url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loginCustomer(final String number){
        ApiUtilities mApi = new ApiUtilities(this);
        mApi.loginCustomer(number, null, new ApiUtilities.ResponseLoginListener() {
            @Override
            public void onSuccess(Trip trip, User user) {
                Intent intent = new Intent(SelectMethodLoginActivity.this, PassengerSelectActionActivity.class);
                intent.putExtra(Defines.BUNDLE_LOGIN_USER, user);
                if (trip != null) {
                    intent.putExtra(Defines.BUNDLE_LOGIN_TRIP, trip);
                    if (trip.getDriverId() != 0)
                        intent.putExtra(Defines.BUNDLE_LOGIN_DRIVER,true);
                }
                startActivity(intent);
                finish();
            }

            @Override
            public void onFail() {
                Intent intent = new Intent(SelectMethodLoginActivity.this, RegisterActivity.class);
                user = new User(1,null,number, null,null);
                intent.putExtra(Defines.BUNDLE_USER, user);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FRAMEWORK_REQUEST_CODE){
            final AccountKitLoginResult loginResult = AccountKit.loginResultWithIntent(data);
            if (loginResult == null || loginResult.wasCancelled()) {
               return;
            } else if (loginResult.getError() != null) {
                Toast.makeText(this, "Lỗi xác thực", Toast.LENGTH_LONG).show();
            } else {
                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(final Account account) {
                        final PhoneNumber number = account.getPhoneNumber();
                        String realNumber = "0"+number.toString().substring(3);
                        loginCustomer(realNumber);
                    }

                    @Override
                    public void onError(final AccountKitError error) {
                    }
                });
            }


        }else if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }else
            callbackManager.onActivityResult(requestCode,resultCode,data);


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

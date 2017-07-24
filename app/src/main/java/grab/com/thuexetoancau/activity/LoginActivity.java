package grab.com.thuexetoancau.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.model.User;
import grab.com.thuexetoancau.utilities.Constants;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private CallbackManager callbackManager ;
    private AccessTokenTracker accessTokenTracker ;
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private User user;
    private LinearLayout btnGoogle, btnFacebook, btnLoginPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(getApplicationContext());
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


                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile","email","user_friends"));

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
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Toast.makeText(getApplicationContext(),acct.getEmail(),Toast.LENGTH_LONG).show();
            user = new User(acct.getDisplayName(),"", acct.getEmail(), String.valueOf(acct.getPhotoUrl()));
            Intent intent = new Intent(LoginActivity.this, PassengerSelectActionActivity.class);
            intent.putExtra(Constants.BUNDLE_USER, user);
            startActivity(intent);
        }

        btnLoginPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, PassengerSelectActionActivity.class);
                startActivity(intent);
            }
        });

    }

    public void graphRequest(AccessToken token){
        GraphRequest request = GraphRequest.newMeRequest(token,new GraphRequest.GraphJSONObjectCallback(){

            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.e("JSON",object.toString());
                Toast.makeText(getApplicationContext(),object.toString(),Toast.LENGTH_LONG).show();
                parseFacebookProfile(object);
                Intent intent = new Intent(LoginActivity.this, PassengerSelectActionActivity.class);
                intent.putExtra(Constants.BUNDLE_USER, user);
                startActivity(intent);
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
            user = new User(fullName,"",email,url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }else
            callbackManager.onActivityResult(requestCode,resultCode,data);


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

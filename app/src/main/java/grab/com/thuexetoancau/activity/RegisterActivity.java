package grab.com.thuexetoancau.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.model.Trip;
import grab.com.thuexetoancau.model.User;
import grab.com.thuexetoancau.utilities.ApiUtilities;
import grab.com.thuexetoancau.utilities.Defines;
import grab.com.thuexetoancau.utilities.SharePreference;
import grab.com.thuexetoancau.widget.CustomProgress;
import grab.com.thuexetoancau.widget.OtpView;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView txtPolicy, txtNext, txtRegister, txtTitle, txtTitleDigit;
    private User user;
    private TextInputLayout textInputName, textInputEmail, textInputPhone;
    private EditText edtCustomerName, edtCustomerEmail, edtCustomerPhone;
    private ImageView imgBack;
    private String customerPhone;
    private CountryCodePicker ccpPhone;
    private Context mContext;
    private boolean isLogin = true;
    private ApiUtilities mApi;
    private LinearLayout layoutRegister, layoutDigits;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private CustomProgress btnResend;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private OtpView edtCode;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = this;
        initComponent();

    }

    private void initComponent() {
        layoutRegister = (LinearLayout) findViewById(R.id.layout_login);
        layoutDigits = (LinearLayout) findViewById(R.id.layout_digit);
        btnResend = (CustomProgress) findViewById(R.id.btn_resend);
        edtCode = (OtpView) findViewById(R.id.edt_code);
        btnResend.setOnClickListener(this);
        txtPolicy  = (TextView) findViewById(R.id.text_policy);
        txtNext = (TextView) findViewById(R.id.txt_next);
        txtRegister  = (TextView) findViewById(R.id.text_register);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitleDigit = (TextView) findViewById(R.id.txt_title_digit);
        edtCustomerName = (EditText) findViewById(R.id.edt_customer_name);
        edtCustomerEmail = (EditText) findViewById(R.id.edt_customer_email);
        edtCustomerPhone = (EditText) findViewById(R.id.edt_customer_phone);
        ccpPhone = (CountryCodePicker) findViewById(R.id.ccp_phone);
        textInputName = (TextInputLayout) findViewById(R.id.text_input_name);
        textInputEmail = (TextInputLayout) findViewById(R.id.text_input_email);
        textInputPhone = (TextInputLayout) findViewById(R.id.text_input_phone);
        imgBack = (ImageView) findViewById(R.id.btn_back);
        // Set policy text
        SpannableString content = new SpannableString(getString(R.string.register_policy));
        content.setSpan(new UnderlineSpan(), 44, 62, 0);
        content.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_light)),  44, 62, 0);
        content.setSpan(new UnderlineSpan(), 66, content.length(), 0);
        content.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_light)),66, content.length(), 0);
        txtPolicy.setText(content);

        // Set register text
        SpannableString contentRegister = new SpannableString(getString(R.string.not_have_account));
        contentRegister.setSpan(new UnderlineSpan(), 23, contentRegister.length(), 0);
        contentRegister.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_light)),  23, contentRegister.length(), 0);
        txtRegister.setText(contentRegister);
        txtRegister.setOnClickListener(this);
        if (getIntent().hasExtra(Defines.BUNDLE_USER)) {
            //receive
            user = (User) getIntent().getSerializableExtra(Defines.BUNDLE_USER);
            edtCustomerName.setText(user.getName());
            edtCustomerEmail.setText(user.getEmail());
        }
        txtNext.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        mApi = new ApiUtilities(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_next:
                if (layoutDigits.isShown()){
                    if (isLogin)
                        loginCustomer();
                    else
                        registerCustomer();
                }else {
                    accountKitCheck();
                }
                break;
            case R.id.btn_back:
                if (layoutDigits.isShown()){
                    showLoginLayout();
                    layoutRegister.setVisibility(View.VISIBLE);
                    layoutDigits.setVisibility(View.GONE);
                    btnResend.updateView();
                    isLogin = true;
                }else {
                    if (!isLogin) {
                        showLoginLayout();
                        isLogin = true;
                    } else
                        finish();
                }
                break;
            case R.id.text_register:
                isLogin = false;
                showRegisterLayout();
                break;
            case R.id.btn_resend:
                if (btnResend.getCurrentPercentage() == 100) {
                    resendVerificationCode("+" + customerPhone, mResendToken);
                    btnResend.updateView();
                }
                break;
        }

    }

    private void showLoginLayout() {
        txtRegister.setVisibility(View.VISIBLE);
        textInputEmail.setVisibility(View.GONE);
        textInputName.setVisibility(View.GONE);
        txtPolicy.setVisibility(View.GONE);
        txtTitle.setText(getString(R.string.login_message));
    }

    private void showRegisterLayout() {
        txtRegister.setVisibility(View.GONE);
        textInputEmail.setVisibility(View.VISIBLE);
        textInputName.setVisibility(View.VISIBLE);
        txtPolicy.setVisibility(View.VISIBLE);
        txtTitle.setText(getString(R.string.register_policy));
    }

    private boolean checkValidData(){
        textInputName.setErrorEnabled(false);
        textInputEmail.setErrorEnabled(false);
        textInputPhone.setErrorEnabled(false);
        if (edtCustomerName.getText().toString().equals("") || edtCustomerName.getText().toString() == null){
            textInputName.setError("Bạn chưa nhập tên");
            requestFocus(edtCustomerName);
            return false;
        }
        if (edtCustomerEmail.getText().toString().equals("") || edtCustomerEmail.getText().toString() == null){
            textInputEmail.setError("Bạn chưa nhập email");
            requestFocus(edtCustomerEmail);
            return false;
        }else {
            if (!isValidEmail(edtCustomerEmail.getText().toString())){
                textInputEmail.setError("Vui lòng nhập email hợp lệ");
                requestFocus(edtCustomerEmail);
                return false;
            }
        }
        if (edtCustomerPhone.getText().toString().equals("") || edtCustomerPhone.getText().toString() == null){
            textInputPhone.setError("Bạn chưa nhập số điện thoại");
            requestFocus(edtCustomerPhone);
            return false;
        }
        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void registerCustomer() {
        customerPhone = ccpPhone.getSelectedCountryCode() + edtCustomerPhone.getText().toString();
        mApi.registerCustomer(customerPhone, edtCustomerEmail.getText().toString(), edtCustomerName.getText().toString(), new ApiUtilities.ResponseRegisterListener() {
            @Override
            public void onSuccess(User mUser) {
                user = mUser;
                Intent intent = new Intent(mContext, PassengerSelectActionActivity.class);
                intent.putExtra(Defines.BUNDLE_LOGIN_USER, user);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loginCustomer() {
        customerPhone = ccpPhone.getSelectedCountryCode() + edtCustomerPhone.getText().toString();
        if (edtCustomerPhone.getText().toString().equals("") || edtCustomerPhone.getText().toString() == null){
            textInputPhone.setError("Bạn chưa nhập số điện thoại");
            requestFocus(edtCustomerPhone);
            return;
        }
        mApi.loginCustomer(customerPhone, edtCustomerEmail.getText().toString(), new ApiUtilities.ResponseLoginListener() {
            @Override
            public void onSuccess(Trip trip, User mUser) {
                user = mUser;
                Intent intent = new Intent(mContext, PassengerSelectActionActivity.class);
                intent.putExtra(Defines.BUNDLE_LOGIN_USER, user);
                if (trip != null) {
                    intent.putExtra(Defines.BUNDLE_LOGIN_TRIP, trip);
                    if (trip.getDriverId() != 0)
                        intent.putExtra(Defines.BUNDLE_LOGIN_DRIVER,true);
                }
                startActivity(intent);
                Log.e("TOKEN",new SharePreference(mContext).getToken());
                finish();
            }
        });
    }


    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void accountKitCheck() {
        customerPhone = ccpPhone.getSelectedCountryCode() + edtCustomerPhone.getText().toString();
        layoutDigits.setVisibility(View.VISIBLE);
        txtTitleDigit.setText( String.format(getString(R.string.digit_message, customerPhone)));
        layoutRegister.setVisibility(View.GONE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+"+customerPhone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks


    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Toast.makeText(mContext,phoneAuthCredential.getSmsCode(),Toast.LENGTH_SHORT).show();
            edtCode.setOTP(phoneAuthCredential.getSmsCode());

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isLogin)
                        loginCustomer();
                    else
                    if (checkValidData())
                        registerCustomer();
                }
            },1500);
            mVerificationInProgress = false;
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // [START_EXCLUDE]
                Toast.makeText(mContext,mContext.getString(R.string.invalid_phone_number),Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // [START_EXCLUDE]
                Toast.makeText(mContext,mContext.getString(R.string.exceeded_request),Toast.LENGTH_SHORT).show();
            }

            mVerificationInProgress = false;
        }
        @Override
        public void onCodeSent(String verificationId,
                               PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d("TAG", "onCodeSent:" + verificationId);
            Toast.makeText(mContext,"onCodeSent:" + verificationId,Toast.LENGTH_SHORT).show();
            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId;
            mResendToken = token;


            // ...
        }
    };

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("Noi Car", "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.e("Noi Car", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }
}

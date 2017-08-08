package grab.com.thuexetoancau.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
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

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.model.Position;
import grab.com.thuexetoancau.model.Trip;
import grab.com.thuexetoancau.model.User;
import grab.com.thuexetoancau.utilities.ApiUtilities;
import grab.com.thuexetoancau.utilities.BaseService;
import grab.com.thuexetoancau.utilities.Global;
import grab.com.thuexetoancau.utilities.Defines;
import grab.com.thuexetoancau.utilities.SharePreference;
import grab.com.thuexetoancau.widget.CustomProgress;
import grab.com.thuexetoancau.widget.OtpView;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView txtPolicy, txtNext, txtRegister, txtTitle;
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
                    loginCustomer();
                }else {
                    accountKitCheck();
                }
                break;
            case R.id.btn_back:
                if (!isLogin) {
                    showLoginLayout();
                    isLogin = true;
                }else
                    finish();
                break;
            case R.id.text_register:
                isLogin = false;
                showRegisterLayout();
                break;
            case R.id.btn_resend:
                resendVerificationCode("+"+customerPhone, mResendToken);
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
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(getString(R.string.register_message));
        dialog.show();

        final SharePreference preference = new SharePreference(this);
        RequestParams params;
        params = new RequestParams();
        params.put("custom_phone", customerPhone);
        params.put("custom_email", edtCustomerEmail.getText().toString());
        params.put("custom_name", edtCustomerName.getText().toString());
        params.put("regId", preference.getRegId());
        params.put("os",1);
        Log.e("TAG",params.toString());
        BaseService.getHttpClient().post(Defines.URL_REGISTER, params, new AsyncHttpResponseHandler() {

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
                        preference.saveCustomerId(data.getString("id"));
                        preference.saveToken(data.getString("token"));
                        int  id = data.getInt("id");
                        String customerName = data.getString("custom_name");
                        String customerEmail = data.getString("custom_email");
                        String customerPhone = data.getString("custom_phone");
                        user = new User(id, customerName,customerPhone,customerEmail,null);
                        Intent intent = new Intent(mContext, PassengerSelectActionActivity.class);
                        intent.putExtra(Defines.BUNDLE_USER, user);
                        startActivity(intent);
                        finish();
                    }
                    Toast.makeText(mContext,json.getString("message"),Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(mContext, getResources().getString(R.string.register_error), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Toast.makeText(mContext, getResources().getString(R.string.register_error), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
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
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(getString(R.string.login_message_dialog));
        dialog.show();

        final SharePreference preference = new SharePreference(this);
        RequestParams params;
        params = new RequestParams();
        params.put("custom_phone", customerPhone);
        DateTime current = new DateTime();
        long key = (current.getMillis() + Global.serverTimeDiff)*13 + 27;
        params.put("key", key);
        /*if (user.getEmail() != null)
            params.put("custom_email", edtCustomerEmail.getText().toString());*/
        params.put("regId", preference.getRegId());
        params.put("os",1);
        Log.e("TAG",params.toString());
        BaseService.getHttpClient().post(Defines.URL_LOGIN, params, new AsyncHttpResponseHandler() {

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
                        preference.saveCustomerId(data.getString("user_id"));
                        preference.saveToken(data.getString("token"));
                        int  useId = data.getInt("user_id");
                        String customerName = data.getString("custom_name");
                        String customerEmail = data.getString("custom_email");
                        String customerPhone = data.getString("custom_phone");
                        String sBooking = data.getString("booking_data");
                        Trip trip = null;
                        if (!sBooking.equals("null")) {
                            JSONObject booking = data.getJSONObject("booking_data");
                            trip = parseBookingData(booking,customerName,useId);
                        }
                        user = new User(useId, customerName,customerPhone,customerEmail,null);
                        Intent intent = new Intent(mContext, PassengerSelectActionActivity.class);
                        intent.putExtra(Defines.BUNDLE_USER, user);
                        if (trip != null)
                            intent.putExtra(Defines.BUNDLE_TRIP, trip);
                        startActivity(intent);
                        finish();
                    }
                    Toast.makeText(mContext,json.getString("message"),Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(mContext, getResources().getString(R.string.register_error), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Toast.makeText(mContext, getResources().getString(R.string.register_error), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private Trip parseBookingData(JSONObject booking, String customerName, int useId){
        int id = 0;
        Trip trip = null;
        try {
            id = booking.getInt("id");
            int carSize = booking.getInt("car_size");
            String startPointName = booking.getString("start_point_name");
            String listEndPointName = booking.getString("list_end_point_name");
            long startPointLon = booking.getLong("start_point_lon");
            long startPointLat = booking.getLong("start_point_lat");
            String listEndPointLon = booking.getString("list_end_point_lon");
            String listEndPointLat = booking.getString("list_end_point_lat");
            String listEndPoin = booking.getString("list_end_point");
            int isOneWay = booking.getInt("is_one_way");
            int isMineTrip = booking.getInt("is_mine_trip");
            int price = booking.getInt("price");
            int distance = booking.getInt("distance");
            String startTime = null ,backTime = null, note = null ;
            if (booking.getString("start_time")!= null)
                startTime = booking.getString("start_time");
            if (booking.getString("back_time")!= null)
                backTime = booking.getString("back_time");
            if (booking.getString("note")!= null)
                note = booking.getString("note");
            String bookingTime = booking.getString("book_time");
            String bookDateId = booking.getString("book_date_id");
            int statusBooking = booking.getInt("status_booking");
            int statusPayment = booking.getInt("status_payment");
            String cancelReason = null, guestPhone = null , guestName = null;
            if (booking.getString("cancel_reason")!= null)
                cancelReason = booking.getString("cancel_reason");
            if (booking.getString("guest_phone")!= null)
                guestPhone = booking.getString("guest_phone");
            if (booking.getString("guest_name")!= null)
                guestName = booking.getString("guest_name");
            int driverId = booking.getInt("driver_id");
            int carType = booking.getInt("car_type");
            int realDistance = booking.getInt("real_distance");
            int realPrice = booking.getInt("real_price");
            ArrayList<Position> listStopPoint = new ArrayList<Position>();
            Position from = new Position(startPointName,new LatLng(startPointLat,startPointLon));
            listStopPoint.add(from);
            String[] arrEndPointName = listEndPointName.split("_");
            String[] arrEndPointGeo = listEndPoin.split("_");
            for (int i = 0 ; i <arrEndPointName.length; i++){
                double lat = Double.valueOf(arrEndPointGeo[i].split(",")[0]);
                double lon = Double.valueOf(arrEndPointGeo[i].split(",")[1]);
                Position position = new Position(arrEndPointName[i],new LatLng(lat,lon));
                listStopPoint.add(position);
            }
            trip = new Trip(id,useId,listStopPoint,carSize,isOneWay,distance,price,startTime,backTime,isMineTrip,customerName,customerPhone,guestName,guestPhone,note);
            trip.setBookingDateId(bookDateId);
            trip.setBookingTime(bookingTime);
            trip.setStatusBooking(statusBooking);
            trip.setStatusPayment(statusPayment);
            trip.setCancelReason(cancelReason);
            trip.setDriverId(driverId);
            trip.setCarType(carType);
            trip.setRealDistance(realDistance);
            trip.setRealPrice(realPrice);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trip;
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
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // [START_EXCLUDE]
                Toast.makeText(mContext,"Invalid phone number",Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // [START_EXCLUDE]
                Toast.makeText(mContext,"Quota exceeded",Toast.LENGTH_SHORT).show();
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
}

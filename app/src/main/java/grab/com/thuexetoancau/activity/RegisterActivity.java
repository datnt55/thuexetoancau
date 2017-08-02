package grab.com.thuexetoancau.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.Preference;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.model.User;
import grab.com.thuexetoancau.utilities.ApiUtilities;
import grab.com.thuexetoancau.utilities.BaseService;
import grab.com.thuexetoancau.utilities.Constants;
import grab.com.thuexetoancau.utilities.Defines;
import grab.com.thuexetoancau.utilities.SharePreference;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = this;
        initComponent();
    }

    private void initComponent() {
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
        if (getIntent().hasExtra(Constants.BUNDLE_USER)) {
            //receive
            user = (User) getIntent().getSerializableExtra(Constants.BUNDLE_USER);
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
                if (isLogin)
                    loginCustomer();
                else
                if (checkValidData())
                    registerCustomer();
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
                        if (user == null) {
                            user = new User(edtCustomerName.getText().toString(),customerPhone,edtCustomerEmail.getText().toString(),null);
                        }else
                            user.setPhone(customerPhone);
                        Intent intent = new Intent(mContext, PassengerSelectActionActivity.class);
                        intent.putExtra(Constants.BUNDLE_USER, user);
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
        if (edtCustomerPhone.getText().toString().equals("") || edtCustomerPhone.getText().toString() == null){
            textInputPhone.setError("Bạn chưa nhập số điện thoại");
            requestFocus(edtCustomerPhone);
            return;
        }
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
                        preference.saveCustomerId(data.getString("id"));
                        preference.saveToken(data.getString("token"));
                        if (user == null) {
                            user = new User(edtCustomerName.getText().toString(),customerPhone,edtCustomerEmail.getText().toString(),null);
                        }else
                            user.setPhone(customerPhone);
                        Intent intent = new Intent(mContext, PassengerSelectActionActivity.class);
                        intent.putExtra(Constants.BUNDLE_USER, user);
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
}

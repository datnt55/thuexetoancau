package grab.com.thuexetoancau.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.model.User;
import grab.com.thuexetoancau.utilities.BaseService;
import grab.com.thuexetoancau.utilities.Constants;
import grab.com.thuexetoancau.utilities.Defines;
import grab.com.thuexetoancau.utilities.SharePreference;

public class ConfigureAccountActivity extends AppCompatActivity implements View.OnClickListener{
    private CountryCodePicker edtDialCode;
    private ImageView mAvatar;
    private TextView mProfileName, mSave;
    private EditText mName,mEmail ,mPhone;
    private User user;
    private Context mContext;
    private FrameLayout layoutChangeAvatar;
    private CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
           // w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_account);
        mContext = this;
        initComponents();
    }

    private void initComponents() {
        mAvatar = (ImageView) findViewById(R.id.img_avatar);
        mProfileName = (TextView) findViewById(R.id.txt_name);
        mName = (EditText) findViewById(R.id.edt_name);
        mEmail = (EditText) findViewById(R.id.edt_email);
        mPhone = (EditText) findViewById(R.id.edt_phone);
        mSave = (TextView) findViewById(R.id.txt_save);
        mSave.setOnClickListener(this);
        countryCodePicker = (CountryCodePicker) findViewById(R.id.ccp);
        layoutChangeAvatar = (FrameLayout) findViewById(R.id.layout_change_avatar);
        layoutChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.create(ConfigureAccountActivity.this)
                        .folderMode(true) // folder mode (false by default)
                        .folderTitle("Chọn thư mục ảnh") // folder selection title
                        .imageTitle("Chọn ảnh từ thư viện") // image selection title
                        .single()
                        .showCamera(true) // show camera or not (true by default)
                        .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
                        .start(Defines.REQUEST_CODE_PICKER); // start image picker activity with request code
            }
        });
        if (getIntent().hasExtra(Constants.BUNDLE_USER)) {
            //receive
            user = (User) getIntent().getSerializableExtra(Constants.BUNDLE_USER);
            mProfileName.setText(user.getName());
            mName.setText(user.getName());
            mEmail.setText(user.getEmail());
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.loading)
                    .showImageForEmptyUri(R.drawable.loading)
                    .showImageOnFail(R.drawable.loading)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            ImageLoader.getInstance().displayImage(user.getUrl(), mAvatar, options, new SimpleImageLoadingListener());
        }
    }

    private void editCustomerInformation() {
        String customerPhone = countryCodePicker.getSelectedCountryCode() + mPhone.getText().toString();
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(getString(R.string.edit_customer_message));
        dialog.show();

        final SharePreference preference = new SharePreference(this);
        RequestParams params;
        params = new RequestParams();
        params.put("custom_phone", customerPhone);
        params.put("custom_email", mEmail.getText().toString());
        params.put("custom_name", mName.getText().toString());
        params.put("regId", preference.getRegId());
        params.put("os",1);
        params.put("os",preference.getCustomerId());
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
                        Intent intent = new Intent(mContext, PassengerSelectActionActivity.class);
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
                Toast.makeText(mContext, getResources().getString(R.string.edit_customer_error), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Toast.makeText(mContext, getResources().getString(R.string.edit_customer_error), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Defines.REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.loading)
                    .showImageForEmptyUri(R.drawable.loading)
                    .showImageOnFail(R.drawable.loading)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            for (Image image : images)
                ImageLoader.getInstance().displayImage("file:///"+image.getPath(), mAvatar, options, new SimpleImageLoadingListener());

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_save:
                editCustomerInformation();
                break;
        }
    }
}

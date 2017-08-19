package grab.com.thuexetoancau.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.utilities.BaseService;
import grab.com.thuexetoancau.utilities.CommonUtilities;
import grab.com.thuexetoancau.utilities.Defines;
import grab.com.thuexetoancau.utilities.SharePreference;

public class AuctionBookingResultActivity extends AppCompatActivity {
    private TextView txtName, txtPhone, txtFrom, txtTo, txtCarSize, txtCarHire, txtTarget, txtDateTime, txtDistance, txtPrice, txtNote;
    private ArrayList<String> result;
    private SharePreference preference;
    private ProgressDialog dialog;
    HashMap<Integer, String> hNotice = new HashMap<Integer, String>();
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction_booking_result);
        preference = new SharePreference(this);
        getNoticeFromServer();
        initComponents();
    }
    private void getNoticeFromServer() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Đang tải dữ liệu");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        BaseService.getHttpClient().get(Defines.URL_NOTICE, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                try {
                    JSONArray data = new JSONArray(new String(responseBody));
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonobject = data.getJSONObject(i);
                        hNotice.put(jsonobject.getInt("id"),jsonobject.getString("name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
                initComponents();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                //Toast.makeText(getContext(), getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                //Toast.makeText(getContext(), getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
    private void initComponents() {
        txtName     = (TextView) findViewById(R.id.txt_name);
        txtPhone    = (TextView) findViewById(R.id.txt_phone);
        txtFrom     = (TextView) findViewById(R.id.txt_from);
        txtTo       = (TextView) findViewById(R.id.txt_to);
        txtCarSize  = (TextView) findViewById(R.id.txt_size);
        txtCarHire  = (TextView) findViewById(R.id.txt_hire_type);
        txtTarget   = (TextView) findViewById(R.id.txt_target_hire);
        txtDateTime = (TextView) findViewById(R.id.txt_date_time);
        txtDistance = (TextView) findViewById(R.id.txt_km_estimate);
        txtPrice    = (TextView) findViewById(R.id.txt_price_estimate);
        txtNote     = (TextView) findViewById(R.id.txt_note);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Kết quả");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            result = extras.getStringArrayList("RESULT");
        }


        txtName.setText(preference.getName());
        txtPhone.setText(result.get(8));
        txtFrom.setText(result.get(0));
        txtTo.setText(result.get(1));
        txtCarSize.setText(result.get(2));
        txtCarHire.setText(result.get(3));
        txtTarget.setText(result.get(4));
        txtDateTime.setText("Từ ngày: " +result.get(5)+"\n"+"Đến ngày: "+result.get(6));
        txtFrom.setText(result.get(0));
        String[] arrResult = result.get(7).split("_");


        txtDistance.setText(arrResult[1]+ "km");
        txtPrice.setText(CommonUtilities.convertCurrency(Integer.valueOf(arrResult[0]))+" VNĐ");

        String sNote = "";
        if (Integer.valueOf(arrResult[2]) >0)
            sNote+="- Giá dựa trên cơ bản là "+CommonUtilities.convertCurrency(Integer.valueOf(arrResult[2]))+" đồng/km"+"\n";


        if (Integer.valueOf(arrResult[3]) >0)
            sNote+="- Số tiền tính thêm khi đi quá 1 ngày là "+CommonUtilities.convertCurrency(Integer.valueOf(arrResult[3]))+" đồng"+"\n";
        if (!result.get(3).equals("Sân bay")) {
            sNote += "- " + hNotice.get(1)+"\n";
            // sNote += "- " + hNotice.get(2)+"\n";
            sNote += "- " + hNotice.get(3)+"\n";
        }
        if (result.get(4).equals("Khách thuê xe"))
            sNote += "- " + hNotice.get(4);
        if (!sNote.equals(""))
            txtNote.setText(sNote);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_finish) {
            Intent intent = new Intent(this, PassengerSelectActionActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}

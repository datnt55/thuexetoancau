package grab.com.thuexetoancau.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by DatNT on 10/27/2016.
 */

public class GetAllCarData {
    private onDataReceived onReceived;
    private Context mContext;
    private ProgressDialog dialog;
    private ArrayList<String> aTargetHire, aHireType, aVehicleType, aAirport;
    public GetAllCarData(Context mContext, onDataReceived onDataReceived){
        this.mContext = mContext;
        this.onReceived = onDataReceived;
        getAllData();
    }
    private void getAllData(){
        dialog = new ProgressDialog(mContext);
        dialog.setMessage("Đang tải dữ liệu");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        aHireType = new ArrayList<>();
        BaseService.getHttpClient().get(Defines.URL_GET_CAR_HIRE_TYPE, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                try {
                    JSONArray arrayresult = new JSONArray(new String(responseBody));
                    for (int i = 0; i < arrayresult.length(); i++) {
                        String result = arrayresult.getString(i);
                        aHireType.add(result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getAllCarType();


            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("JSON", new String(responseBody));
                //Toast.makeText(mContext, mContext.getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                //Toast.makeText(mContext, mContext.getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getAllCarType() {
        aVehicleType = new ArrayList<>();
        BaseService.getHttpClient().get(Defines.URL_GET_CAR_SIZE, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                try {
                    JSONArray arrayresult = new JSONArray(new String(responseBody));
                    for (int i = 0; i < arrayresult.length(); i++) {
                        String result = arrayresult.getString(i);
                        aVehicleType.add(result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getAllAirport();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("JSON", new String(responseBody));
                //Toast.makeText(mContext, mContext.getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                //Toast.makeText(mContext, mContext.getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getAllAirport() {
        aAirport = new ArrayList<>();
        BaseService.getHttpClient().get(Defines.URL_GET_AIRPORT, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                try {
                    JSONArray arrayresult = new JSONArray(new String(responseBody));
                    for (int i = 0; i < arrayresult.length(); i++) {
                        JSONObject result = arrayresult.getJSONObject(i);
                        String name = result.getString("name");
                        aAirport.add(name);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getTargetHire();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("JSON", new String(responseBody));
                //Toast.makeText(mContext, mContext.getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                //Toast.makeText(mContext, mContext.getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getTargetHire() {
        aTargetHire = new ArrayList<>();
        BaseService.getHttpClient().get(Defines.URL_GET_WHO_HIRE, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                try {
                    JSONArray arrayresult = new JSONArray(new String(responseBody));
                    for (int i = 0; i < arrayresult.length(); i++) {
                        String result = arrayresult.getString(i);
                        aTargetHire.add(result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (onReceived != null)
                    onReceived.onReceived(aHireType, aVehicleType, aTargetHire, aAirport);
                /*ArrayAdapter<String> adapterProvinceFrom = new ArrayAdapter<>(mContext,android.R.layout.simple_list_item_1, aVehicleType);
                txtCarName.setAdapter(adapterProvinceFrom);
                txtCarName.setThreshold(1);*/
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("JSON", new String(responseBody));
                //Toast.makeText(mContext, mContext.getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                //Toast.makeText(mContext, mContext.getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface onDataReceived{
        public void onReceived(ArrayList<String> categories, ArrayList<String> types, ArrayList<String> targetHire, ArrayList<String> aAirport);
    }
}

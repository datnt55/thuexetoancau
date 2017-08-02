package grab.com.thuexetoancau.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.activity.PassengerSelectActionActivity;
import grab.com.thuexetoancau.model.User;

/**
 * Created by DatNT on 8/2/2017.
 */

public class ApiUtilities {
    private Context mContext;

    public ApiUtilities(Context mContext) {
        this.mContext = mContext;
    }

    private ArrayList<Integer> getPostage(final int carSize, final int carType){
        final ArrayList<Integer> arrayPrice = new ArrayList<>();
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(mContext.getString(R.string.prepare_cost_of_trip));
        dialog.show();
        RequestParams params;
        params = new RequestParams();
        params.put("car_size", carSize);
        params.put("car_type", carType);
        Log.e("TAG",params.toString());
        BaseService.getHttpClient().post(Defines.URL_GET_POSTAGE, params, new AsyncHttpResponseHandler() {

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
                        if (carType == 0){
                            int price2Way = data.getInt("price2Way");
                            int price1Way = data.getInt("price1Way");
                            arrayPrice.add(price2Way);
                            arrayPrice.add(price1Way);
                        }else if (carType == 1){
                            int price = data.getInt("price");
                            arrayPrice.add(price);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(mContext, mContext.getResources().getString(R.string.register_error), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Toast.makeText(mContext, mContext.getResources().getString(R.string.register_error), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        return arrayPrice;
    }
}

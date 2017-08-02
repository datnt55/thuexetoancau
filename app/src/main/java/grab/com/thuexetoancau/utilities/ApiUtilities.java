package grab.com.thuexetoancau.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.model.Car;

/**
 * Created by DatNT on 8/2/2017.
 */

public class ApiUtilities {
    private Context mContext;

    public ApiUtilities(Context mContext) {
        this.mContext = mContext;
    }

    public ArrayList<Car> getPostage(){
        if (!CommonUtilities.isOnline(mContext)) {
            CommonUtilities.showDialogNetworkError(mContext, null);
            return null;
        }
        final ArrayList<Car> arrayPrice = new ArrayList<>();
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(mContext.getString(R.string.prepare_data));
        dialog.show();
        BaseService.getHttpClient().get(Defines.URL_GET_POSTAGE, new AsyncHttpResponseHandler() {

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
                        JSONArray listPrice = data.getJSONArray("listprice");
                        for (int i = 0 ; i < listPrice.length(); i++){
                            JSONObject car = listPrice.getJSONObject(i);
                            int carSize = car.getInt("car_size");
                            int price01way = car.getInt("price01way");
                            int price02way = car.getInt("price02way");
                            int price11way = car.getInt("price11way");
                            arrayPrice.add(new Car(carSize,CommonUtilities.getCarName(carSize), CommonUtilities.getCarImage(carSize), price01way, price02way, price11way));
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

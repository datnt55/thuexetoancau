package grab.com.thuexetoancau.utilities;

import android.app.Activity;
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
import grab.com.thuexetoancau.activity.SplashActivity;
import grab.com.thuexetoancau.model.Car;
import grab.com.thuexetoancau.model.Trip;

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
            DialogUtils.showDialogNetworkError(mContext, null);
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

    public void logOut(){
        if (!CommonUtilities.isOnline(mContext)) {
            DialogUtils.showDialogNetworkError(mContext, null);
            return;
        }
        final ArrayList<Car> arrayPrice = new ArrayList<>();
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(mContext.getString(R.string.log_out));
        dialog.show();
        final SharePreference preference = new SharePreference(mContext);
        RequestParams params;
        params = new RequestParams();
        params.put("token", preference.getToken());
        Log.e("TAG",params.toString());
        BaseService.getHttpClient().post(Defines.URL_LOG_OUT,params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    String status = jsonObject.getString("status");
                    if (status.equals("success")){
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.log_out_success), Toast.LENGTH_SHORT).show();
                        preference.clearToken();
                        Intent intent = new Intent(mContext, SplashActivity.class);
                        mContext.startActivity(intent);
                        ((Activity)mContext).finish();
                    }else{
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.log_out_error), Toast.LENGTH_SHORT).show();
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
        return;
    }

    public void bookingCar(Trip trip, final BookingCarListener listener){
        if (!CommonUtilities.isOnline(mContext)) {
            DialogUtils.showDialogNetworkError(mContext, null);
            return;
        }
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(mContext.getString(R.string.booking_car_message));
        dialog.show();
        RequestParams params;
        params = new RequestParams();
        params.put("user_id",trip.getUserId());
        if (trip.getCustomerType() == 0){
            params.put("guest_name", trip.getGuestName());
            params.put("guest_phone", trip.getGuestPhone());
        }
        params.put("custom_name", trip.getCustomerName());
        params.put("custom_phone", trip.getCustomerPhone());
        params.put("car_size", trip.getCarSize());
        params.put("start_point_name", trip.getListStopPoints().get(0).getFullPlace());

        params.put("start_point", trip.getListStopPoints().get(0).getLatLngToString());
        String listEndPoint="", listEndName = "";
        for (int i = 1; i < trip.getListStopPoints().size(); i++)
            if (i < trip.getListStopPoints().size()-1 ) {
                listEndPoint += trip.getListStopPoints().get(i).getLatLngToString();
                listEndName  += trip.getListStopPoints().get(i).getFullPlace();
            }else {
                listEndPoint = listEndPoint + trip.getListStopPoints().get(i).getLatLngToString() + "_";
                listEndName = listEndName + trip.getListStopPoints().get(i).getFullPlace() + "_";
            }
        params.put("list_end_point_name",listEndName);
        params.put("list_end_point", listEndPoint);
        params.put("isOneWay", trip.getTripType());
        params.put("isMineTrip", trip.getCustomerType());
        params.put("estimated_price", trip.getPrice());
        params.put("estimated_distance", trip.getDistance());

        if (trip.getDistance() > Defines.MAX_DISTANCE) {
            if (trip.getTripType() == 1) {
                params.put("start_time", trip.getStartTime());
            } else {
                params.put("start_time", trip.getStartTime());
                params.put("come_back_time", trip.getEndTime());
            }
        }else {
            params.put("start_time", "null");
            params.put("come_back", "null");
        }
        params.put("custom_note", trip.getNote());

        Log.e("TAG",params.toString());
        BaseService.getHttpClient().post(Defines.URL_BOOKING,params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    String status = jsonObject.getString("status");
                    if (status.equals("success")){
                        JSONArray array = jsonObject.getJSONArray("data");
                        JSONObject data = array.getJSONObject(0);
                       if (listener != null)
                           listener.onSuccess(data.getString("id_booking"));
                    }else{
                        if (listener != null)
                            listener.onFail();
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
    }

    public void cancelTrip(String bookingId, String customerPhone, String reason, final CancelTripCarListener listener){
        RequestParams params;
        params = new RequestParams();
        params.put("id_booking", bookingId);
        params.put("custom_phone",customerPhone);
        params.put("cancel_reason",reason);

        Log.e("TAG",params.toString());
        BaseService.getHttpClient().post(Defines.URL_CANCEL_TRIP,params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    String status = jsonObject.getString("status");
                    if (status.equals("success")){
                        if (listener != null)
                            listener.onSuccess();
                    }else{
                        if (listener != null)
                            listener.onFail();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(mContext, mContext.getResources().getString(R.string.register_error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Toast.makeText(mContext, mContext.getResources().getString(R.string.register_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface BookingCarListener{
        void onSuccess(String id);
        void onFail();
    }

    public interface CancelTripCarListener{
        void onSuccess();
        void onFail();
    }
}

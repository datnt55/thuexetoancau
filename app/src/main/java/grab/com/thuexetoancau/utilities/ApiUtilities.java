package grab.com.thuexetoancau.utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.activity.PassengerSelectActionActivity;
import grab.com.thuexetoancau.activity.SelectMethodLoginActivity;
import grab.com.thuexetoancau.activity.SplashActivity;
import grab.com.thuexetoancau.model.AroundCar;
import grab.com.thuexetoancau.model.Car;
import grab.com.thuexetoancau.model.Position;
import grab.com.thuexetoancau.model.Trip;
import grab.com.thuexetoancau.model.User;

/**
 * Created by DatNT on 8/2/2017.
 */

public class ApiUtilities {
    private Context mContext;

    public ApiUtilities(Context mContext) {
        this.mContext = mContext;
    }

    public void registerCustomer(final String customerPhone, final String customerEmail, final String customerName, final ResponseRegisterListener listener ){
        if (!CommonUtilities.isOnline(mContext)) {
            DialogUtils.showDialogNetworkError(mContext, null);
            return;
        }
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(mContext.getString(R.string.register_message));
        dialog.show();

        final SharePreference preference = new SharePreference(mContext);
        RequestParams params;
        params = new RequestParams();
        params.put("custom_phone", customerPhone);
        params.put("custom_email", customerEmail);
        params.put("custom_name", customerName);
        params.put("regId", preference.getRegId());
        params.put("os",1);
        DateTime current = new DateTime();
        long key = (current.getMillis() + Global.serverTimeDiff)*13 + 27;
        params.put("key", key);
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
                        SharePreference preference = new SharePreference(mContext);
                        preference.saveCustomerId(data.getString("id"));
                        preference.saveToken(data.getString("token"));
                        int  id = data.getInt("id");
                        User user = new User(id, customerName,customerPhone,customerEmail,null);
                        if (listener != null)
                            listener.onSuccess(user);
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
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    public void loginCustomer (String customerPhone, String customerEmail, final ResponseLoginListener listener  ){
        if (!CommonUtilities.isOnline(mContext)) {
            DialogUtils.showDialogNetworkError(mContext, null);
            return;
        }
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(mContext.getString(R.string.login_message_dialog));
        dialog.show();

        final SharePreference preference = new SharePreference(mContext);
        RequestParams params;
        params = new RequestParams();
        if (customerPhone != null)
            params.put("custom_phone", customerPhone);
        DateTime current = new DateTime();
        long key = (current.getMillis() + Global.serverTimeDiff)*13 + 27;
        params.put("key", key);
        if (customerEmail != null)
            params.put("custom_email", customerEmail);
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
                        SharePreference preference = new SharePreference(mContext);
                        preference.saveToken(data.getString("token"));
                        JSONObject customData  = data.getJSONObject("custom_data");
                        preference.saveCustomerId(customData.getString("id"));
                        int  useId = customData.getInt("id");
                        String customerName = customData.getString("custom_name");
                        String customerEmail = customData.getString("custom_email");
                        String customerPhone = customData.getString("custom_phone");
                        String sBooking = data.getString("booking_data");
                        Trip trip = null;
                        if (!sBooking.equals("null")) {
                            JSONObject booking = data.getJSONObject("booking_data");
                            trip = parseBookingData(booking);
                        }
                        User user = new User(useId, customerName,customerPhone,customerEmail,null);
                        if (data.has("driver_car_model")){
                            user.setCarModel(data.getString("driver_car_model"));
                        }
                        if (listener != null)
                            listener.onSuccess(trip, user);
                    }else
                    if (listener != null)
                        listener.onFail();
                    Toast.makeText(mContext,json.getString("message"),Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Toast.makeText(mContext,mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    public void checkTokenLogin(final ResponseLoginListener listener ) {
        final SharePreference preference = new SharePreference(mContext);
        RequestParams params;
        params = new RequestParams();
        params.put("token", preference.getToken());
        params.put("regId", preference.getRegId());
        params.put("os", 1);
        DateTime current = new DateTime();
        long key = (current.getMillis() + Global.serverTimeDiff)*13 + 27;
        params.put("key", key);
        /*if (user.getEmail() != null)
            params.put("custom_email", edtCustomerEmail.getText().toString());*/
        Log.e("TAG",params.toString());
        BaseService.getHttpClient().post(Defines.URL_CHECK_TOKEN, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                Log.i("JSON", new String(responseBody));
                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    if (json.getString("status").equals("success")){
                        JSONArray array = json.getJSONArray("data");
                        JSONObject data = array.getJSONObject(0);
                        SharePreference preference = new SharePreference(mContext);
                       // preference.saveToken(data.getString("token"));
                        JSONObject customData  = data.getJSONObject("custom_data");
                        preference.saveCustomerId(customData.getString("id"));
                        int  useId = customData.getInt("id");
                        String customerName = customData.getString("custom_name");
                        String customerEmail = customData.getString("custom_email");
                        String customerPhone = customData.getString("custom_phone");
                        String sBooking = data.getString("booking_data");
                        Trip trip = null;
                        if (!sBooking.equals("null")) {
                            JSONObject booking = data.getJSONObject("booking_data");
                            trip = parseBookingData(booking);
                        }

                        User user = new User(useId, customerName,customerPhone,customerEmail,null);
                        if (data.has("driver_car_model")){
                            user.setCarModel(data.getString("driver_car_model"));
                        }
                        if (listener != null)
                            listener.onSuccess(trip, user);
                    }else {
                        DialogUtils.showCheckTokenDialog(((Activity)mContext), new DialogUtils.YesNoListenter() {
                            @Override
                            public void onYes() {
                                preference.clearToken();
                                Intent intent = new Intent(mContext, SelectMethodLoginActivity.class);
                                mContext.startActivity(intent);
                                ((Activity)mContext).finish();
                            }

                            @Override
                            public void onNo() {

                            }
                        });
                    }
                    //Toast.makeText(mContext,json.getString("message"),Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Trip parseBookingData(JSONObject booking){
        Trip trip = null;
        try {
            int id = booking.getInt("id");
            int useId = booking.getInt("user_id");
            String customerPhone = booking.getString("custom_phone");
            String customerName = booking.getString("custom_name");
            int carSize = booking.getInt("car_size");
            String startPointName = booking.getString("start_point_name");
            String listEndPointName = booking.getString("list_end_point_name");
            double startPointLon = booking.getDouble("start_point_lon");
            double startPointLat = booking.getDouble("start_point_lat");
            String listEndPointLon = booking.getString("list_end_point_lon");
            String listEndPointLat = booking.getString("list_end_point_lat");
            String listEndPoin = booking.getString("list_end_point");
            int isOneWay = booking.getInt("is_one_way");
            int isMineTrip = booking.getInt("is_mine_trip");
            long price = booking.getLong("price");
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
            int carType = booking.getInt("car_type");
            int realDistance = 0, realPrice = 0, driverId = 0;
            if (!booking.getString("real_distance").equals("null"))
                realDistance = booking.getInt("real_distance");
            if (!booking.getString("real_price").equals("null"))
                realPrice = booking.getInt("real_price");
            String x = booking.getString("driver_id");
            if (!booking.getString("driver_id").equals("null"))
                driverId = booking.getInt("driver_id");
            String driverName = booking.getString("driver_name");
            String driverPhone = booking.getString("driver_phone");
            String driverCarNumber = booking.getString("driver_car_number");
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
            int status = booking.getInt("status_booking");
            trip = new Trip(id,useId,listStopPoint,carSize,isOneWay,distance,price,startTime,backTime,isMineTrip,customerName,customerPhone,guestName,guestPhone,note);
            trip.setBookingDateId(bookDateId);
            trip.setBookingTime(bookingTime);
            trip.setStatusBooking(statusBooking);
            trip.setStatusPayment(statusPayment);
            trip.setCancelReason(cancelReason);
            trip.setCarType(carType);
            trip.setRealDistance(realDistance);
            trip.setRealPrice(realPrice);
            trip.setDriverId(driverId);
            trip.setDriverName(driverName);
            trip.setDriverPhone(driverPhone);
            trip.setDriverCarNumber(driverCarNumber);
            trip.setStatus(status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trip;
    }
    public ArrayList<Car> getPostage(final ResponseRequestListener listener){
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
                            int price01way2 = car.getInt("price01way2");
                            int price02way2 = car.getInt("price02way2");
                            int price11way2 = car.getInt("price11way2");
                            if (carSize == 5 || carSize == 8) {
                                arrayPrice.add(new Car(carSize,true, CommonUtilities.getCarName(carSize,true), CommonUtilities.getCarImage(carSize), price01way, price02way, price11way));
                                arrayPrice.add(new Car(carSize,false, CommonUtilities.getCarName(carSize,false), CommonUtilities.getTaxiImage(carSize), price01way2, price02way2, price11way2));
                            }else
                                arrayPrice.add(new Car(carSize,true, CommonUtilities.getCarName(carSize,true), CommonUtilities.getCarImage(carSize), price01way, price02way, price11way));
                        }
                        if (listener != null)
                            listener.onSuccess();

                    }else
                    if (listener != null)
                        listener.onFail();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        return arrayPrice;
    }

    public void logOut(final ResponseRequestListener listener){
        if (!CommonUtilities.isOnline(mContext)) {
            DialogUtils.showDialogNetworkError(mContext, null);
            return;
        }
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
                        if (listener != null)
                            listener.onSuccess();
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
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
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
            if (i == trip.getListStopPoints().size()-1 ) {
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
            DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
            params.put("start_time", dtf.print(new DateTime()));
        }
        params.put("custom_note", trip.getNote());
        int isCar = trip.isCar() ? 1 : 0;
        params.put("is_car", isCar);
        DateTime current = new DateTime();
        long key = (current.getMillis() + Global.serverTimeDiff)*13 + 27;
        params.put("key", key);
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
                           listener.onSuccess(data.getInt("id_booking"));
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
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    public void cancelTrip(int bookingId, String customerPhone, String reason, final ResponseRequestListener listener){
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
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getCurrentTime(final ServerTimeListener listener){
        if (!CommonUtilities.isOnline(mContext)) {
            DialogUtils.showDialogNetworkError(mContext, null);
            return ;
        }
        BaseService.getHttpClient().get(Defines.URL_GET_SERVER_TIME, new AsyncHttpResponseHandler() {

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
                        long serverTime = data.getLong("TotalMilliseconds");
                        if (listener != null)
                            listener.onSuccess(serverTime);
                    }else{
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
        return;
    }

    public void reviewTrip(int userId, final int idBooking, int  customRating, String customComment, final ResponseRequestListener listener){
        if (!CommonUtilities.isOnline(mContext)) {
            DialogUtils.showDialogNetworkError(mContext, null);
            return ;
        }
        RequestParams params;
        params = new RequestParams();
        params.put("user_id", userId);
        params.put("id_booking",idBooking);
        params.put("custom_rating",customRating);
        params.put("custom_comment",customComment);
        Log.e("TAG",params.toString());
        BaseService.getHttpClient().post(Defines.URL_REVIEW_TRIP,params, new AsyncHttpResponseHandler() {

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
                    if (status.equals("success")) {
                        if (listener != null)
                            listener.onSuccess();
                    }
                    else{
                        if (listener != null)
                            listener.onFail();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void likeTrip(int userId, final int idBooking, final ResponseRequestListener listener){
        if (!CommonUtilities.isOnline(mContext)) {
            DialogUtils.showDialogNetworkError(mContext, null);
            return ;
        }
        RequestParams params;
        params = new RequestParams();
        params.put("user_id", userId);
        params.put("id_booking",idBooking);
        Log.e("TAG",params.toString());
        BaseService.getHttpClient().post(Defines.URL_LIKE_TRIP,params, new AsyncHttpResponseHandler() {

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
                    if (status.equals("success")) {
                        if (listener != null)
                            listener.onSuccess();
                    }
                    else{
                        if (listener != null)
                            listener.onFail();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getLikeTrip(int userId, final ResponseTripListener listener){
        if (!CommonUtilities.isOnline(mContext)) {
            DialogUtils.showDialogNetworkError(mContext, null);
            return ;
        }
        RequestParams params;
        params = new RequestParams();
        params.put("user_id", userId);
        Log.e("TAG",params.toString());
        BaseService.getHttpClient().post(Defines.URL_GET_LIKE_TRIP,params, new AsyncHttpResponseHandler() {

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
                        ArrayList<Trip> arrayTrip = new ArrayList<Trip>();
                        JSONArray array = json.getJSONArray("data");
                        JSONObject data = array.getJSONObject(0);
                        JSONArray bookingList = data.getJSONArray("list");
                        for (int i = 0 ; i < bookingList.length(); i++) {
                            JSONObject booking = bookingList.getJSONObject(i);
                            Trip trip = parseBookingData(booking);
                            arrayTrip.add(trip);
                        }
                        if (listener != null)
                            listener.onSuccess(arrayTrip);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (listener != null)
                        listener.onSuccess(null);
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void getScheduleTrip(int userId, final ResponseTripListener listener){
        if (!CommonUtilities.isOnline(mContext)) {
            DialogUtils.showDialogNetworkError(mContext, null);
            return ;
        }
        RequestParams params;
        params = new RequestParams();
        params.put("user_id", userId);
        Log.e("TAG",params.toString());
        BaseService.getHttpClient().post(Defines.URL_SCHEDULE_TRIP,params, new AsyncHttpResponseHandler() {

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
                        ArrayList<Trip> arrayTrip = new ArrayList<Trip>();
                        JSONArray array = json.getJSONArray("data");
                        JSONObject data = array.getJSONObject(0);
                        JSONArray bookingList = data.getJSONArray("list");
                        for (int i = 0 ; i < bookingList.length(); i++) {
                            JSONObject booking = bookingList.getJSONObject(i);
                            Trip trip = parseBookingData(booking);
                            arrayTrip.add(trip);
                        }
                        if (listener != null)
                            listener.onSuccess(arrayTrip);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (listener != null)
                        listener.onSuccess(null);
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getHistoryTrip(int userId, final ResponseTripListener listener){
        if (!CommonUtilities.isOnline(mContext)) {
            DialogUtils.showDialogNetworkError(mContext, null);
            return ;
        }
        RequestParams params;
        params = new RequestParams();
        params.put("user_id", userId);
        Log.e("TAG",params.toString());
        BaseService.getHttpClient().post(Defines.URL_GET_HISTORY_TRIP,params, new AsyncHttpResponseHandler() {

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
                        ArrayList<Trip> arrayTrip = new ArrayList<Trip>();
                        JSONArray array = json.getJSONArray("data");
                        JSONObject data = array.getJSONObject(0);
                        JSONArray bookingList = data.getJSONArray("list");
                        for (int i = 0 ; i < bookingList.length(); i++) {
                            JSONObject booking = bookingList.getJSONObject(i);
                            Trip trip = parseBookingData(booking);
                            arrayTrip.add(trip);
                        }
                        if (listener != null)
                            listener.onSuccess(arrayTrip);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (listener != null)
                        listener.onSuccess(null);
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getUserPoint(int userId, final UserPointListener listener){
        if (!CommonUtilities.isOnline(mContext)) {
            DialogUtils.showDialogNetworkError(mContext, null);
            return ;
        }
        RequestParams params;
        params = new RequestParams();
        params.put("user_id", userId);
        Log.e("TAG",params.toString());
        BaseService.getHttpClient().post(Defines.URL_POINT_FOR_USER,params, new AsyncHttpResponseHandler() {

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
                        ArrayList<Trip> arrayTrip = new ArrayList<Trip>();
                        JSONArray array = json.getJSONArray("data");
                        JSONObject data = array.getJSONObject(0);
                        String point = data.getString("total_point");
                        if (listener != null)
                            listener.onSuccess(point);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (listener != null)
                        listener.onSuccess(null);
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    public void getCarAround(GPSTracker location, final AroundCarListener listener) {
        RequestParams params;
        params = new RequestParams();
        params.put("lat", location.getLatitude());
        params.put("lon", location.getLongitude());
        Log.e("TAG",params.toString());
        final ArrayList<AroundCar> aroundCars = new ArrayList<AroundCar>();
        BaseService.getHttpClient().post(Defines.URL_GET_CAR_AROUND, params, new AsyncHttpResponseHandler() {

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
                        AroundCar aroundCar = parseJsonResult(jsonobject);
                        aroundCars.add(aroundCar);
                    }
                    if (listener != null)
                        listener.onSuccess(aroundCars);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                //Toast.makeText(getContext(), getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                if (listener != null)
                    listener.onSuccess(aroundCars);
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                //Toast.makeText(getContext(), getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getTripInfo(int bookingId, final TripInformationListener listener) {
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Đang tải dữ liệu");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        RequestParams params;
        params = new RequestParams();
        params.put("id_booking", bookingId);
        Log.e("params deleteDelivery", params.toString());
        BaseService.getHttpClient().post(Defines.URL_TRIP_INFO, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    if (json.getString("status").equals("success")) {
                        JSONArray array = json.getJSONArray("data");
                        JSONObject data = array.getJSONObject(0);
                        JSONObject listTrip = data.getJSONObject("list");
                        Trip trip = parseBookingData(listTrip);
                        if (listener != null)
                            listener.onSuccess(trip);
                    }
                    //Toast.makeText(mContext, json.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                dialog.dismiss();
            }
        });
    }

    private AroundCar parseJsonResult(JSONObject jsonobject) {
       AroundCar aroundCars = null;
        try {
            double lon         = jsonobject.getDouble("lon");
            double lat         = jsonobject.getDouble("lat");
            double distance    = jsonobject.getDouble("D");
            aroundCars = new AroundCar(lat,lon,distance);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return aroundCars;
    }

    public interface ServerTimeListener{
        void onSuccess(long time);
    }

    public interface BookingCarListener{
        void onSuccess(int id);
        void onFail();
    }

    public interface ResponseRequestListener {
        void onSuccess();
        void onFail();
    }

    public interface ResponseLoginListener {
        void onSuccess(Trip trip, User user);
        void onFail();
    }

    public interface ResponseRegisterListener {
        void onSuccess(User user);
    }

    public interface ResponseTripListener {
        void onSuccess(ArrayList<Trip> arrayTrip);
    }

    public interface AroundCarListener {
        void onSuccess(ArrayList<AroundCar> aroundCars);
    }

    public interface TripInformationListener {
        void onSuccess(Trip trip);
    }

    public interface UserPointListener {
        void onSuccess(String point);
    }
}

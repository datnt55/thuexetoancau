package grab.com.thuexetoancau.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.model.Booking;
import grab.com.thuexetoancau.utilities.BaseService;
import grab.com.thuexetoancau.utilities.CommonUtilities;
import grab.com.thuexetoancau.utilities.Defines;
import grab.com.thuexetoancau.utilities.SharePreference;

/**
 * Created by DatNT on 11/17/2016.
 */
public class PassengerCarAdapter extends RecyclerView.Adapter<PassengerCarAdapter.ViewHolder>{


    private static final String LOG_TAG = PassengerCarAdapter.class.getSimpleName();
    private Context mContext;
    private List<Booking> mVehicle;
    private SharePreference preference;
    private int price;
    private onClickListener onClick;
    public PassengerCarAdapter(Context context, ArrayList<Booking> vehicle) {
        mContext = context;
        this.mVehicle = vehicle;
        preference = new SharePreference(mContext);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d(LOG_TAG, "ON create view holder " + i);

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_passenger_book_detail, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.txtCarFrom.setText(mVehicle.get(position).getCarFrom());
        holder.txtCarTo.setText(mVehicle.get(position).getCarTo());
        holder.txtHireType.setText(mVehicle.get(position).getCarHireType());

        DateTime jDateFrom = new DateTime(mVehicle.get(position).getFromDate());

        holder.txtDateFrom.setText(CommonUtilities.convertTime(jDateFrom));
        DateTime jDateTo = new DateTime(mVehicle.get(position).getToDate());
        holder.txtDateTo.setText(CommonUtilities.convertTime(jDateTo));
        holder.txtBookPrice.setText(CommonUtilities.convertCurrency(mVehicle.get(position).getBookPriceMax())+" Đ");
     //   holder.txtTimeReduce.setText(mVehicle.get(position).getTimeToReduce());
        if (mVehicle.get(position).getCarType().equals("4"))
            holder.txtCarSize.setText(mVehicle.get(position).getCarType()+" chỗ(giá siêu rẻ, không cốp)");
        else if (mVehicle.get(position).getCarType().equals("5"))
            holder.txtCarSize.setText(mVehicle.get(position).getCarType()+" chỗ(có cốp)");
        else
            holder.txtCarSize.setText(mVehicle.get(position).getCarType()+" chỗ");
        holder.btnBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookingTrip(position);
            }
        });
    }


    private void bookingTrip(int position) {
        RequestParams params;
        params = new RequestParams();
        params.put("name", preference.getName());
        params.put("phone", preference.getPhone());
        params.put("id_booking", mVehicle.get(position).getId());
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Đang đặt xe...");
        dialog.show();
        Log.e("TAG",params.toString());
        BaseService.getHttpClient().post(Defines.URL_BOOKING_LOG, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                int result = Integer.valueOf(new String(responseBody));
                if (result > 0)
                    Toast.makeText(mContext, "Đặt xe thành công. Vui lòng chờ tài xế gọi tới", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(mContext, "Đặt xe thất bại", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
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
    public void setOnRequestComplete(final onClickListener onClick)
    {
        this.onClick = onClick;
    }
    @Override
    public int getItemCount() {

        if (mVehicle == null) return 0;
        else return mVehicle.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void swapData(List<Booking> itemForMultipleSelectionList) {
        mVehicle = itemForMultipleSelectionList;
    }

    public List<Booking> getData() {
        if (mVehicle != null){
           return mVehicle;
        } else return null;
    };

    public class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardview;
        TextView txtCarFrom;
        TextView txtCarTo;
        TextView txtHireType;
        TextView txtDateFrom;
        TextView txtDateTo;
        TextView txtBookPrice;
        TextView txtCarSize;
        Button btnBooking;

        public ViewHolder(View itemView) {
            super(itemView);


            cardview        = (CardView)        itemView.findViewById(R.id.card_view);
            txtCarFrom      = (TextView)        itemView.findViewById(R.id.txt_from);
            txtCarTo        = (TextView)        itemView.findViewById(R.id.txt_to);
            txtHireType     = (TextView)        itemView.findViewById(R.id.txt_hire_type);
            txtDateFrom     = (TextView)        itemView.findViewById(R.id.txt_date_from);
            txtDateTo       = (TextView)        itemView.findViewById(R.id.txt_date_to);
            txtBookPrice    = (TextView)        itemView.findViewById(R.id.txt_price);
            txtCarSize      = (TextView)        itemView.findViewById(R.id.txt_car_size);
            btnBooking      = (Button)          itemView.findViewById(R.id.btn_booking);

        }
    }
    public interface onClickListener
    {
        public void onItemClick();

    }

}
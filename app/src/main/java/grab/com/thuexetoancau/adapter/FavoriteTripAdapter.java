package grab.com.thuexetoancau.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.listener.ItemTouchHelperAdapter;
import grab.com.thuexetoancau.listener.ItemTouchHelperViewHolder;
import grab.com.thuexetoancau.listener.SimpleItemTouchHelperCallback;
import grab.com.thuexetoancau.model.Trip;
import grab.com.thuexetoancau.utilities.AnimUtils;
import grab.com.thuexetoancau.utilities.CommonUtilities;
import grab.com.thuexetoancau.widget.TextDrawable;

/**
 * Created by DatNT on 7/19/2017.
 */

public class FavoriteTripAdapter extends RecyclerView.Adapter<FavoriteTripAdapter.ViewHolder> implements ItemTouchHelperAdapter {


    private static final String LOG_TAG = PassengerCarAdapter.class.getSimpleName();
    private Context mContext;
    private List<Trip> arrayTrip;
    private ItemClickListener listener;

    public FavoriteTripAdapter(Context context, ArrayList<Trip> trips) {
        mContext = context;
        this.arrayTrip = trips;
    }

    private int measureView(final View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredHeight();
    }

    public void setOnItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public FavoriteTripAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d(LOG_TAG, "ON create view holder " + i);

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_favorite_trip, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FavoriteTripAdapter.ViewHolder holder, final int position) {
        holder.txtSource.setText(arrayTrip.get(position).getListStopPoints().get(0).getFullPlace());
        int size = arrayTrip.get(position).getListStopPoints().size();
        holder.txtDestination.setText(arrayTrip.get(position).getListStopPoints().get(size -1).getFullPlace());
        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-dd-mm'T'hh:mm:ss.SSS");
        DateTime dateTime = dtf.parseDateTime(arrayTrip.get(position).getBookingTime());
        holder.txtDate.setText(dateTime.getDayOfMonth()+"/"+dateTime.getMonthOfYear()+"/"+dateTime.getYear());
        holder.txtTime.setText(dateTime.getHourOfDay()+":"+dateTime.getMinuteOfHour());
        holder.txtDistance.setText(arrayTrip.get(position).getDistance());
        holder.txtCarSize.setText(arrayTrip.get(position).getCarSize()+" chỗ");
        holder.txtPrice.setText(CommonUtilities.convertCurrency(arrayTrip.get(position).getDistance()));

    }

    @Override
    public int getItemCount() {
        if (arrayTrip == null) return 0;
        else return arrayTrip.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(arrayTrip, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        if (fromPosition == 0 || fromPosition == arrayTrip.size()-1 || toPosition == 0 || toPosition == arrayTrip.size()-1)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            },500);

        if(listener != null)
            listener.onSwapLocation(fromPosition,toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        if(listener != null)
            listener.onRemoveStopPoint(position);

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtSource;
        TextView txtDestination;
        TextView txtDate;
        TextView txtTime;
        TextView txtDistance;
        TextView txtCarSize;
        TextView txtPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            txtSource = (TextView) itemView.findViewById(R.id.txt_source);
            txtDestination = (TextView) itemView.findViewById(R.id.txt_destination);
            txtDate = (TextView) itemView.findViewById(R.id.txt_date);
            txtTime = (TextView) itemView.findViewById(R.id.txt_time);
            txtDistance = (TextView) itemView.findViewById(R.id.txt_distance);
            txtCarSize = (TextView) itemView.findViewById(R.id.txt_car_type);
            txtPrice = (TextView) itemView.findViewById(R.id.txt_price);
        }
    }

    public interface ItemClickListener{
        void onNewStopPoint();
        void onRemoveStopPoint(int position);
        void onChangeLocation(int postion);
        void onSwapLocation(int fromPosition, int postion);
    }
}
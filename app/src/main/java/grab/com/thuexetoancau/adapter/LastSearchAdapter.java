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
import grab.com.thuexetoancau.model.Location;
import grab.com.thuexetoancau.utilities.BaseService;
import grab.com.thuexetoancau.utilities.CommonUtilities;
import grab.com.thuexetoancau.utilities.Defines;
import grab.com.thuexetoancau.utilities.SharePreference;

/**
 * Created by DatNT on 11/17/2016.
 */
public class LastSearchAdapter extends RecyclerView.Adapter<LastSearchAdapter.ViewHolder>{


    private static final String LOG_TAG = LastSearchAdapter.class.getSimpleName();
    private Context mContext;
    private List<Location> arraySearch;
    private onClickListener onClick;
    public LastSearchAdapter(Context context, ArrayList<Location> vehicle) {
        mContext = context;
        this.arraySearch = vehicle;
    }

    @Override
    public int getItemCount() {

        if (arraySearch == null) return 0;
        else return arraySearch.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d(LOG_TAG, "ON create view holder " + i);

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_last_search, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.txtLocation.setText(arraySearch.get(position).getTxtLocation());
        holder.txtAddress.setText(arraySearch.get(position).getTxtAddress());
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtLocation;
        TextView txtAddress;

        public ViewHolder(View itemView) {
            super(itemView);
            txtAddress      = (TextView)        itemView.findViewById(R.id.text_address);
            txtLocation        = (TextView)        itemView.findViewById(R.id.text_location);
        }
    }
    public interface onClickListener
    {
        public void onItemClick();

    }

}
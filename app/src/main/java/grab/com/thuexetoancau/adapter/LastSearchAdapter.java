package grab.com.thuexetoancau.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.model.Position;

/**
 * Created by DatNT on 11/17/2016.
 */
public class LastSearchAdapter extends RecyclerView.Adapter<LastSearchAdapter.ViewHolder> implements Filterable {


    private static final String LOG_TAG = LastSearchAdapter.class.getSimpleName();
    private Context mContext;
    private List<Position> arraySearch;
    private onClickListener onClick;
    private GoogleApiClient mGoogleApiClient;
    private AutocompleteFilter mPlaceFilter;
    private LatLngBounds mBounds;

    public LastSearchAdapter(Context context, LatLngBounds bounds, AutocompleteFilter filter) {
        mContext = context;
        this.arraySearch = new ArrayList<>();
        this.mBounds = bounds;
        mPlaceFilter = filter;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        if (googleApiClient == null || !googleApiClient.isConnected()) {
            mGoogleApiClient = null;
        } else {
            mGoogleApiClient = googleApiClient;
        }
    }

    private ArrayList<Position> getPredictions(CharSequence constraint) {
        if (mGoogleApiClient != null) {
            PendingResult<AutocompletePredictionBuffer> results = Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, constraint.toString(), mBounds, mPlaceFilter);
            AutocompletePredictionBuffer autocompletePredictions = results.await(60, TimeUnit.SECONDS);
            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
                //Toast.makeText(mContext, "Error: " + status.toString(), Toast.LENGTH_SHORT).show();
                autocompletePredictions.release();
                return null;
            }
            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
            ArrayList resultList = new ArrayList<>(autocompletePredictions.getCount());
            while (iterator.hasNext()) {
                AutocompletePrediction prediction = iterator.next();
                resultList.add(new Position(prediction.getPlaceId().toString(), prediction.getPrimaryText(null).toString(), prediction.getSecondaryText(null).toString()));
            }
            // Buffer release
            autocompletePredictions.release();
            return resultList;
        }
        return null;
    }

    public void setOnClickListener(onClickListener listener){
        this.onClick = listener;
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
        holder.txtLocation.setText(arraySearch.get(position).getPrimaryText());
        holder.txtAddress.setText(arraySearch.get(position).getSecondText());
        holder.layoutRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClick != null){
                    onClick.onItemClick(arraySearch.get(position));
                }
            }
        });
        holder.imgLocation.setImageResource(R.drawable.ic_location_on_black_24dp);
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null) {
                    if(!constraint.equals("")){
                        // Query the autocomplete API for the entered constraint
                        arraySearch = getPredictions(constraint);
                        if (arraySearch != null) {
                            results.values = arraySearch;
                            results.count = arraySearch.size();
                        }
                    }else
                        arraySearch.clear();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtLocation;
        TextView txtAddress;
        LinearLayout layoutRoot;
        ImageView imgLocation;

        public ViewHolder(View itemView) {
            super(itemView);
            txtAddress          = (TextView)        itemView.findViewById(R.id.text_address);
            txtLocation         = (TextView)        itemView.findViewById(R.id.text_location);
            layoutRoot          = (LinearLayout)    itemView.findViewById(R.id.layout_root);
            imgLocation         = (ImageView)       itemView.findViewById(R.id.img_location);
        }
    }
    public interface onClickListener
    {
        void onItemClick(Position location);

    }

}
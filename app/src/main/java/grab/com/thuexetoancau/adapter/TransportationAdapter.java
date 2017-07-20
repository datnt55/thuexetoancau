package grab.com.thuexetoancau.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.listener.SimpleItemTouchHelperCallback;
import grab.com.thuexetoancau.model.Car;

/**
 * Created by DatNT on 7/19/2017.
 */

public class TransportationAdapter extends RecyclerView.Adapter<TransportationAdapter.ViewHolder> {


    private static final String LOG_TAG = PassengerCarAdapter.class.getSimpleName();
    private Context mContext;
    private List<Car> arrayVehicle;
    private DisplayImageOptions options;

    public TransportationAdapter(Context context, ArrayList<Car> vehicle) {
        mContext = context;
        this.arrayVehicle = vehicle;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnFail(R.drawable.loading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    private void clearSelectAllCar(){
        for (Car car : arrayVehicle)
            car.setSelected(false);
    }

    @Override
    public TransportationAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d(LOG_TAG, "ON create view holder " + i);
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_transportation, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TransportationAdapter.ViewHolder holder, final int position) {
        ImageLoader.getInstance().displayImage(arrayVehicle.get(position).getImage(),holder.imgTransport, options, new SimpleImageLoadingListener());
        holder.txtCarName.setText(arrayVehicle.get(position).getName());
        holder.txtPrice.setText(arrayVehicle.get(position).getPrice()+"");
        if (arrayVehicle.get(position).isSelected()) {
            holder.layoutRoot.setBackground(ContextCompat.getDrawable(mContext, R.drawable.vehicle_shape_selected));
            holder.txtCarName.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            holder.txtPrice.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        }else {
            holder.layoutRoot.setBackground(ContextCompat.getDrawable(mContext, R.drawable.vehicle_shape));
            holder.txtCarName.setTextColor(ContextCompat.getColor(mContext, R.color.blue_light));
            holder.txtPrice.setTextColor(ContextCompat.getColor(mContext, R.color.blue_light));
        }
        holder.layoutRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSelectAllCar();
                arrayVehicle.get(position).setSelected(!arrayVehicle.get(position).isSelected());
                if (arrayVehicle.get(position).isSelected()) {
                    holder.layoutRoot.setBackground(ContextCompat.getDrawable(mContext, R.drawable.vehicle_shape_selected));
                    holder.txtCarName.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    holder.txtPrice.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                }else {
                    holder.layoutRoot.setBackground(ContextCompat.getDrawable(mContext, R.drawable.vehicle_shape));
                    holder.txtCarName.setTextColor(ContextCompat.getColor(mContext, R.color.blue_light));
                    holder.txtPrice.setTextColor(ContextCompat.getColor(mContext, R.color.blue_light));
                }
                notifyDataSetChanged();
            }
        });

    }



    @Override
    public int getItemCount() {
        if (arrayVehicle == null) return 0;
        else return arrayVehicle.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder  {
        ImageView imgTransport;
        TextView txtCarName;
        TextView txtPrice;
        LinearLayout layoutRoot;

        public ViewHolder(View itemView) {
            super(itemView);
            layoutRoot = (LinearLayout) itemView.findViewById(R.id.layout_root);
            imgTransport = (ImageView) itemView.findViewById(R.id.img_transport);
            txtCarName = (TextView) itemView.findViewById(R.id.txt_car_name);
            txtPrice = (TextView) itemView.findViewById(R.id.txt_price);
        }
    }

}
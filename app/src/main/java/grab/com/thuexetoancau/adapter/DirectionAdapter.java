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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.listener.ItemTouchHelperAdapter;
import grab.com.thuexetoancau.listener.ItemTouchHelperViewHolder;
import grab.com.thuexetoancau.listener.SimpleItemTouchHelperCallback;
import grab.com.thuexetoancau.utilities.AnimUtils;
import grab.com.thuexetoancau.utilities.CommonUtilities;
import grab.com.thuexetoancau.widget.TextDrawable;

/**
 * Created by DatNT on 7/19/2017.
 */

public class DirectionAdapter extends RecyclerView.Adapter<DirectionAdapter.ViewHolder> implements ItemTouchHelperAdapter {


    private static final String LOG_TAG = PassengerCarAdapter.class.getSimpleName();
    private Context mContext;
    private List<String> arrayDirection;
    private ItemClickListener listener;
    private SimpleItemTouchHelperCallback callBack;
    private RecyclerView listDirection;
    private int listHeight;
    private int mTopMargin;
    private int mBottomMargin;
    private int mLeftMargin;
    private int mRightMargin;

    public DirectionAdapter(Context context, ArrayList<String> vehicle,RecyclerView listDirection) {
        mContext = context;
        this.arrayDirection = vehicle;
        this.listDirection = listDirection;
    }

    public void setStandardHeight(int height){
        this.listHeight = height;
    }
    private void checkSizeOfRecyclerView(RecyclerView list) {
        int curHeight = measureView(list);
        if (curHeight > listHeight){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, listHeight);
            list.setLayoutParams(params);
        }
        if (curHeight < listHeight){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, curHeight);
            list.setLayoutParams(params);
        }
    }

    private int measureView(final View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredHeight();
    }


    public void setItemTouchCallBack(SimpleItemTouchHelperCallback callBack){
        this.callBack = callBack;
    }

    public void setOnItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public DirectionAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d(LOG_TAG, "ON create view holder " + i);

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_direction, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DirectionAdapter.ViewHolder holder, final int position) {
        holder.txtPlace.setText(arrayDirection.get(position));
        if (position == 0) {
            holder.imgRole.setVisibility(View.VISIBLE);
            holder.imgRole.setColorFilter(ContextCompat.getColor(mContext,R.color.white));
            holder.imgRole.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
            holder.layoutRouteDow.setVisibility(View.VISIBLE);
            holder.layoutRouteUp.setVisibility(View.INVISIBLE);
            holder.btnFunction.setImageResource(R.drawable.ic_autorenew_black_24dp);
        }else if (position == arrayDirection.size()-1) {
            holder.imgRole.setVisibility(View.VISIBLE);
            holder.imgRole.setColorFilter(ContextCompat.getColor(mContext,R.color.white));
            holder.imgRole.setImageResource(R.drawable.ic_location_on_black_24dp);
            holder.layoutRouteDow.setVisibility(View.INVISIBLE);
            holder.layoutRouteUp.setVisibility(View.VISIBLE);
            holder.btnFunction.setImageResource(R.drawable.ic_add_black_24dp);
            holder.btnFunction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null)
                        listener.onNewStopPoint();
                }
            });
        }else{
            holder.imgRole.setVisibility(View.VISIBLE);
            holder.layoutRouteDow.setVisibility(View.VISIBLE);
            holder.layoutRouteUp.setVisibility(View.VISIBLE);
            holder.btnFunction.setImageResource(R.drawable.ic_close_black_24dp);
            holder.btnFunction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null)
                        listener.onRemoveStopPoint(position);
                }
            });
            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .withBorder((int) CommonUtilities.convertDpToPixel(2,mContext))
                    .textColor(0xffffffff)
                    .width((int) CommonUtilities.convertDpToPixel(16,mContext))  // width in px
                    .height((int) CommonUtilities.convertDpToPixel(16,mContext)) // height in px
                    .fontSize(28)
                    .endConfig()
                    .buildRound(CommonUtilities.getCharacterDirection(position), 0xff4285f4);
            holder.imgRole.setImageDrawable(drawable);
        }
        holder.txtPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onChangeLocation(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (arrayDirection == null) return 0;
        else return arrayDirection.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(arrayDirection, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        if (fromPosition == 0 || fromPosition == arrayDirection.size()-1 || toPosition == 0 || toPosition == arrayDirection.size()-1)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            },500);

    }

    @Override
    public void onItemDismiss(int position) {
        arrayDirection.remove(position);
        notifyDataSetChanged();
        checkSizeOfRecyclerView(listDirection);
        if (arrayDirection.size()<= 2){
            callBack.setItemSwipe(false);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        TextView txtPlace;
        ImageView btnFunction;
        ImageView imgRole;
        LinearLayout layoutRouteUp;
        LinearLayout layoutRouteDow;
        FrameLayout layoutPlace;
        LinearLayout layoutRoot;
        public ViewHolder(View itemView) {
            super(itemView);
            txtPlace = (TextView) itemView.findViewById(R.id.txt_direction);
            btnFunction = (ImageView) itemView.findViewById(R.id.btn_action);
            imgRole = (ImageView) itemView.findViewById(R.id.img_role);
            layoutRouteUp = (LinearLayout) itemView.findViewById(R.id.layout_route_up);
            layoutRouteDow = (LinearLayout) itemView.findViewById(R.id.layout_route_down);
            layoutPlace = (FrameLayout) itemView.findViewById(R.id.layout_place);
            layoutRoot = (LinearLayout) itemView.findViewById(R.id.layout_root);
        }
        @Override
        public void onItemSelected() {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) txtPlace.getLayoutParams();
            mTopMargin = params.topMargin;
            mBottomMargin = params.bottomMargin;
            mLeftMargin = params.leftMargin;
            mRightMargin = params.rightMargin;
            itemView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.shadow));
            /*layoutPlace.animate().scaleX(1.02f).setInterpolator(AnimUtils.EASE_OUT_EASE_IN).setDuration(100);
            layoutPlace.animate().scaleY(1.2f).setInterpolator(AnimUtils.EASE_OUT_EASE_IN).setDuration(100);
            txtPlace.animate().scaleX(0.98f).setInterpolator(AnimUtils.EASE_OUT_EASE_IN).setDuration(100);
            txtPlace.animate().scaleY(0.8f).setInterpolator(AnimUtils.EASE_OUT_EASE_IN).setDuration(100);*/
            imgRole.setVisibility(View.INVISIBLE);
            layoutRouteUp.setVisibility(View.INVISIBLE);
            layoutRouteDow.setVisibility(View.INVISIBLE);
            increaseMargin(txtPlace);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
            layoutPlace.animate().scaleX(1f).setInterpolator(AnimUtils.EASE_OUT_EASE_IN).setDuration(100);
            layoutPlace.animate().scaleY(1f).setInterpolator(AnimUtils.EASE_OUT_EASE_IN).setDuration(100);
            txtPlace.animate().scaleX(1f).setInterpolator(AnimUtils.EASE_OUT_EASE_IN).setDuration(100);
            txtPlace.animate().scaleY(1f).setInterpolator(AnimUtils.EASE_OUT_EASE_IN).setDuration(100);
//            notifyDataSetChanged();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) txtPlace.getLayoutParams();
            imgRole.setVisibility(View.VISIBLE);
            layoutRouteUp.setVisibility(View.VISIBLE);
            layoutRouteDow.setVisibility(View.VISIBLE);
            decreaseMargin(txtPlace);
        }
    }

    public void increaseMargin(final View view){
        ValueAnimator mAnimator = ValueAnimator.ofFloat(1f, 1.5f);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final Float fraction = (Float) animation.getAnimatedValue();
                setMargins(fraction, view);
            }
        });
        mAnimator.setDuration(400);
        mAnimator.start();
    }

    public void decreaseMargin(final View view){
        ValueAnimator mAnimator = ValueAnimator.ofFloat(1.5f, 1f);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final Float fraction = (Float) animation.getAnimatedValue();
                setMargins(fraction, view);
            }
        });
        mAnimator.setDuration(400);
        mAnimator.start();
    }

    private void setMargins(float fraction, View view) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.topMargin = (int) (mTopMargin * fraction);
        params.bottomMargin = (int) (mBottomMargin * fraction);
        params.leftMargin = (int) (mLeftMargin * fraction);
        params.rightMargin = (int) (mRightMargin * fraction);
        view.requestLayout();
    }

    public interface ItemClickListener{
        void onNewStopPoint();
        void onRemoveStopPoint(int position);
        void onChangeLocation(int postion);
    }
}
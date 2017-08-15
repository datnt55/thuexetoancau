package grab.com.thuexetoancau.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dd.CircularProgressButton;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.model.Trip;
import grab.com.thuexetoancau.utilities.AnimUtils;
import grab.com.thuexetoancau.utilities.ApiUtilities;
import grab.com.thuexetoancau.utilities.DialogUtils;
import grab.com.thuexetoancau.utilities.Global;

import static grab.com.thuexetoancau.utilities.AnimUtils.EASE_IN;

/**
 * Created by DatNT on 7/31/2017.
 */

public class SearchingCarLayout extends LinearLayout {
    private Context mContext;
    private ImageView imgCircle, imgCircle1;
    private CircularProgressButton btnCancel;
    private SearchingCallBack callBack;
    private String bookingId;
    private Trip trip;
    private TextView txtSource, txtDestination, txtCarSize, txtNote;
    public SearchingCarLayout(Context context, SearchingCallBack callBack, String bookingId, Trip trip) {
        super(context);
        this.mContext = context;
        this.callBack = callBack;
        this.bookingId = bookingId;
        this.trip = trip;
        initLayout();
    }

    public SearchingCarLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initLayout();
    }

    private void initLayout() {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(R.layout.layout_searching_car, this, true);
        imgCircle = (ImageView) view.findViewById(R.id.img_circle);
        imgCircle1 = (ImageView) view.findViewById(R.id.img_circle2);
        // imgCircle2 = (ImageView) view.findViewById(R.id.img_circle_2);
        startAnimation(imgCircle, 0);
        startAnimation(imgCircle1, 1000);
        txtSource = (TextView) findViewById(R.id.txt_source);
        txtDestination = (TextView) findViewById(R.id.txt_destination);
        txtCarSize = (TextView) findViewById(R.id.txt_car_size);
        txtNote = (TextView) findViewById(R.id.txt_note);
        txtSource.setText(trip.getListStopPoints().get(0).getFullPlace());
        int size =trip.getListStopPoints().size();
        txtDestination.setText(trip.getListStopPoints().get(size -1).getFullPlace());
        txtNote.setText(trip.getNote());
        txtCarSize.setText(trip.getCarSize()+" chỗ");
        btnCancel = (CircularProgressButton) findViewById(R.id.btn_cancel);
        btnCancel.setIndeterminateProgressMode(true);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showCancelTripConfirm((Activity) mContext, new DialogUtils.ConfirmListenter() {
                    @Override
                    public void onConfirm(String reason) {
                        if (btnCancel.getProgress() == 0) {
                            btnCancel.setProgress(50);
                        }
                        requestCancelTrip(reason);
                    }
                });
            }
        });
    }

    private void requestCancelTrip(String reason) {
        ApiUtilities mApi = new ApiUtilities(mContext);
        mApi.cancelTrip(bookingId, trip.getCustomerPhone(), reason, new ApiUtilities.ResponseRequestListener() {
            @Override
            public void onSuccess() {
                if (callBack!= null)
                    callBack.onSearchCarCancel();
                btnCancel.setProgress(100);
                AnimUtils.slideDown(SearchingCarLayout.this, Global.APP_SCREEN_HEIGHT);
            }

            @Override
            public void onFail() {
                btnCancel.setProgress(0);

            }
        });
    }

    private void startAnimation(final View view, int delay) {
        view.setScaleX(0);
        view.setScaleY(0);
        view.setAlpha(1);
        final ViewPropertyAnimator animator = view.animate();
        animator.cancel();
        animator.setInterpolator(EASE_IN)
                .scaleX(1)
                .scaleY(1)
                .alpha(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startAnimation(view, 0);
                    }
                })
                .withLayer();

        animator.setDuration(2000);
        animator.setStartDelay(delay);

        animator.start();
    }

    public interface SearchingCallBack{
        void onSearchCarSuccess();
        void onSearchCarError();
        void onSearchCarCancel();
    }
}
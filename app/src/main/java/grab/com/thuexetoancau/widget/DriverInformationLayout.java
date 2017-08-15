package grab.com.thuexetoancau.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dd.CircularProgressButton;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.model.Trip;
import grab.com.thuexetoancau.utilities.AnimUtils;
import grab.com.thuexetoancau.utilities.Defines;

import static grab.com.thuexetoancau.utilities.AnimUtils.EASE_IN;

/**
 * Created by DatNT on 7/31/2017.
 */

public class DriverInformationLayout extends LinearLayout {
    private Context mContext;
    private ImageView imgCircle, imgCircle1;
    private CircularProgressButton btnCancel;
    private Trip trip;

    public DriverInformationLayout(Context context, int bookingId) {
        super(context);
        mContext = context;
        trip = new Trip();
        trip.setId(bookingId);
        initLayout();
    }
    public DriverInformationLayout(Context context, Trip trip) {
        super(context);
        mContext = context;
        this.trip = trip;
        initLayout();
    }
    public DriverInformationLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initLayout();
    }

    private void initLayout() {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(R.layout.layout_driver_information, this, true);
    }
}
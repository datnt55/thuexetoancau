package grab.com.thuexetoancau.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import grab.com.thuexetoancau.model.User;
import grab.com.thuexetoancau.utilities.AnimUtils;
import grab.com.thuexetoancau.utilities.Defines;

import static grab.com.thuexetoancau.utilities.AnimUtils.EASE_IN;

/**
 * Created by DatNT on 7/31/2017.
 */

public class DriverInformationLayout extends LinearLayout {
    private Context mContext;
    private TextView customerName, customerCarType, customerLicense;
    private ImageView btnCall;
    private User driver;
    public DriverInformationLayout(Context context, User user) {
        super(context);
        mContext = context;
        this.driver = user;
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
        initComponents(view);
    }

    private void initComponents(View view) {
        customerName = (TextView) view.findViewById(R.id.txt_driver_name);
        customerCarType = (TextView) view.findViewById(R.id.txt_driver_car_type);
        customerLicense = (TextView) view.findViewById(R.id.txt_driver_car_license);
        btnCall = (ImageView) view.findViewById(R.id.img_call);
        customerLicense.setText(driver.getLicense());
        customerName.setText(driver.getName());
        customerCarType.setText(driver.getCarModel());
        btnCall.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+driver.getPhone()));
                mContext.startActivity(callIntent);
            }
        });
    }
}
package grab.com.thuexetoancau.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.utilities.Constants;
import grab.com.thuexetoancau.utilities.Defines;

/**
 * Created by DatNT on 7/18/2017.
 */

public class DirectionLayout extends LinearLayout implements View.OnClickListener {
    private Context mContext;
    public ImageView imgBack;
    private DirectionCallback mCallback;
    private LinearLayout layoutOneWay, layoutRoundTrip;
    private TextView txtOneWay, txtRoundTrip;
    private TextView txtDirectionStart, txtDirectionEnd;
    private ImageView imgRoundTrip, imgOneWay, imgReverse;
    private String endLocation , startLocation;
    public DirectionLayout(Context context, String endLocation) {
        super(context);
        this.mContext = context;
        this.endLocation = endLocation;
        initLayout();
    }

    public DirectionLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initLayout();
    }

    private void initLayout() {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(R.layout.control_direction_layout, this, true);
        imgBack = (ImageView) view.findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);
        layoutOneWay = (LinearLayout) view.findViewById(R.id.layout_one_way);
        layoutRoundTrip = (LinearLayout) view.findViewById(R.id.layout_round_trip);
        txtOneWay = (TextView) view.findViewById(R.id.txt_one_way);
        txtRoundTrip = (TextView) view.findViewById(R.id.txt_round_trip);
        imgOneWay = (ImageView) view.findViewById(R.id.img_one_way);
        imgRoundTrip = (ImageView) view.findViewById(R.id.img_round_trip);
        layoutOneWay.setOnClickListener(this);
        layoutRoundTrip.setOnClickListener(this);
        txtDirectionStart = (TextView) view.findViewById(R.id.txt_direction_start);
        txtDirectionEnd = (TextView) view.findViewById(R.id.txt_direction_end);
        txtDirectionStart.setOnClickListener(this);
        txtDirectionEnd.setOnClickListener(this);
        txtDirectionEnd.setText(endLocation);
        imgReverse = (ImageView) view.findViewById(R.id.btn_reverse);
        imgReverse.setOnClickListener(this);
    }

    public void setOnCallBackDirection(DirectionCallback callback){
        this.mCallback = callback;
    }

    public void updateLocation(String location, int typeLocation){
        if (typeLocation == Constants.DIRECTION_ENDPOINT) {
            endLocation = location;
            txtDirectionEnd.setText(endLocation);
        }else{
            startLocation = location;
            txtDirectionStart.setText(startLocation);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                if (mCallback != null)
                    mCallback.onBackDirectionClicked();
                break;
            case R.id.layout_one_way:
                layoutOneWay.setBackground(ContextCompat.getDrawable(mContext,R.drawable.direction_type_shape));
                txtOneWay.setTextColor(ContextCompat.getColor(mContext,R.color.blue));
                imgOneWay.setColorFilter(ContextCompat.getColor(mContext,R.color.blue));
                layoutRoundTrip.setBackground(null);
                txtRoundTrip.setTextColor(ContextCompat.getColor(mContext,R.color.white));
                imgRoundTrip.setColorFilter(ContextCompat.getColor(mContext,R.color.white));
                break;
            case R.id.layout_round_trip:
                layoutRoundTrip.setBackground(ContextCompat.getDrawable(mContext,R.drawable.direction_type_shape));
                txtRoundTrip.setTextColor(ContextCompat.getColor(mContext,R.color.blue));
                imgRoundTrip.setColorFilter(ContextCompat.getColor(mContext,R.color.blue));
                layoutOneWay.setBackground(null);
                txtOneWay.setTextColor(ContextCompat.getColor(mContext,R.color.white));
                imgOneWay.setColorFilter(ContextCompat.getColor(mContext,R.color.white));
                break;
            case R.id.btn_reverse:
                String start = txtDirectionStart.getText().toString();
                String end = txtDirectionEnd.getText().toString();
                txtDirectionStart.setText(end);
                txtDirectionEnd.setText(start);
                break;
            case R.id.txt_direction_end:
                if (mCallback != null)
                    mCallback.onDirectionClicked(Constants.DIRECTION_ENDPOINT);
                break;
            case R.id.txt_direction_start:
                if (mCallback != null)
                    mCallback.onDirectionClicked(Constants.DIRECTION_START_POINT);
                break;
        }
    }
    public interface DirectionCallback {
        void onBackDirectionClicked();
        void onDirectionClicked(int type);
        void onSearchViewClicked();
        void onSearchViewSearching();
        void onChangeTextSearch(CharSequence s, AutoCompleteTextView edtSearch);
        void getLayoutSearchHeight(int height);
    }
}

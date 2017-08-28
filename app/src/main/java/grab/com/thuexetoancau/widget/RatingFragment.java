package grab.com.thuexetoancau.widget;

import android.app.Dialog;
import android.graphics.Point;
import android.media.Rating;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.model.Trip;
import grab.com.thuexetoancau.model.User;
import grab.com.thuexetoancau.utilities.ApiUtilities;
import grab.com.thuexetoancau.utilities.CommonUtilities;
import grab.com.thuexetoancau.utilities.Defines;

/**
 * Created by DatNT on 8/2/2017.
 */

public class RatingFragment extends DialogFragment implements View.OnClickListener {
    private String title, driverName;
    private TextView btnOK, txtDriverName;
    private CheckBox cbFavorite;
    private User user;
    private int bookingId;
    private RatingBar rating;
    private EditText edtReview;

    private RatingCallBackListener listener;
    public interface RatingCallBackListener{
        void onRatingSuccess();
    }

    //---empty constructor required
    public RatingFragment() {

    }

    public void setOnRatingCallBack(RatingCallBackListener listener){
        this.listener = listener;
    }

    public void setDialogTitle(String title) {
        this.title = title;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable(Defines.BUNDLE_USER);
        bookingId = bundle.getInt(Defines.BUNDLE_TRIP);
        driverName = bundle.getString(Defines.BUNDLE_DRIVER_NAME);
        View view = inflater.inflate(R.layout.dialog_rating, container);
        initComponents(view);
        getDialog().setTitle(title);
        return view;
    }

    private void initComponents(View view) {
        btnOK = (TextView) view.findViewById(R.id.btn_ok);
        btnOK.setOnClickListener(this);
        cbFavorite = (CheckBox) view.findViewById(R.id.cb_favorite);
        txtDriverName = (TextView) view.findViewById(R.id.txt_driver_name);
        rating = (RatingBar) view.findViewById(R.id.ratingBar);
        edtReview = (EditText) view.findViewById(R.id.edt_review);
        txtDriverName.setText(driverName);
    }

    @Override
    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x ), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                return ;
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ok:
                if (rating.getRating() == 0){
                    Toast.makeText(getActivity(),"Bạn chưa cho tài xế sao",Toast.LENGTH_SHORT).show();
                    return;
                }
                setCustomerRating();
                this.dismissAllowingStateLoss();
                break;
        }
    }

    private void setCustomerRating() {
        final ApiUtilities mApi = new ApiUtilities(getActivity());
        if (cbFavorite.isChecked()){
            mApi.likeTrip(user.getId(), bookingId, new ApiUtilities.ResponseRequestListener() {
                @Override
                public void onSuccess() {
                    ratingAction(mApi);
                }

                @Override
                public void onFail() {
                    ratingAction(mApi);
                }
            });
        }else
            ratingAction(mApi);
    }

    private void ratingAction(ApiUtilities mApi) {
        mApi.reviewTrip(user.getId(),bookingId, (int) rating.getRating(), edtReview.getText().toString(), new ApiUtilities.ResponseRequestListener() {
            @Override
            public void onSuccess() {
                if (listener != null)
                    listener.onRatingSuccess();
            }

            @Override
            public void onFail() {

            }
        });
    }
}
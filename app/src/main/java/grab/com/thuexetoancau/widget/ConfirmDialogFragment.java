package grab.com.thuexetoancau.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.model.Trip;
import grab.com.thuexetoancau.utilities.CommonUtilities;
import grab.com.thuexetoancau.utilities.Defines;

/**
 * Created by DatNT on 8/2/2017.
 */

public class ConfirmDialogFragment extends DialogFragment {
    private String title;
    private Trip mTrip;
    private TextView txtSource, txtDestination, txtTypeTrip, txtPrice, txtDistance,txtStartTime ,txtBackTime, txtCaution;
    private EditText edtCustomerName, edtCustomerPhone;
    private View mDividerStart, mDividerBack, viewDividerFriend;
    private LinearLayout layoutStart, layoutBack;
    private RadioGroup radioGroup;
    private RadioButton rOwne, rFriend;
    private Button btnConfirm;
    private int totalDistance;
    private ConfirmDialogListener callback;
    private DateTime startTime, endTime;
    public interface ConfirmDialogListener {
        void onConfirmed(Trip mTrip);
    }

    //---empty constructor required
    public ConfirmDialogFragment() {

    }

    public void setOnCallBack(ConfirmDialogListener callback){
        this.callback = callback;
    }
    //---set the title of the dialog window
    public void setDialogTitle(String title) {
        this.title = title;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        Bundle bundle = getArguments();
        mTrip = (Trip) bundle.getSerializable(Defines.DIALOG_CONFIRM_TRIP);

        View view = inflater.inflate(R.layout.dialog_booking, container);
        //---set the title for the dialog
        getDialog().setTitle(title);
        initComponents(view);
        return view;
    }

    private void initComponents(View view) {
        txtSource = (TextView) view.findViewById(R.id.txt_source);
        txtDestination = (TextView) view.findViewById(R.id.txt_destination);
        txtTypeTrip = (TextView) view.findViewById(R.id.trip_type);
        txtDistance = (TextView) view.findViewById(R.id.distance);
        txtStartTime = (TextView) view.findViewById(R.id.start_time);
        txtBackTime = (TextView) view.findViewById(R.id.back_time);
        txtPrice = (TextView) view.findViewById(R.id.price);
        txtCaution= (TextView) view.findViewById(R.id.txt_caution);
        edtCustomerName = (EditText) view.findViewById(R.id.edt_friend_name);
        edtCustomerPhone = (EditText) view.findViewById(R.id.edt_friend_phone);

        mDividerStart = view.findViewById(R.id.divider_start_time);
        mDividerBack = view.findViewById(R.id.divider_back_time);
        viewDividerFriend = view.findViewById(R.id.divider_friend);

        layoutStart = (LinearLayout) view.findViewById(R.id.layout_start);
        layoutBack = (LinearLayout) view.findViewById(R.id.layout_back);

        radioGroup = (RadioGroup) view.findViewById(R.id.radio_select_customer);
        rFriend = (RadioButton) view.findViewById(R.id.radio_friend);
        rOwne = (RadioButton) view.findViewById(R.id.radio_mine);

        btnConfirm = (Button) view.findViewById(R.id.btn_confirm);

        txtSource.setText(mTrip.getListStopPoints().get(0).getFullPlace());
        txtDestination.setText(mTrip.getListStopPoints().get(mTrip.getListStopPoints().size()-1).getFullPlace());
        txtTypeTrip.setText(CommonUtilities.getTripType(mTrip.getTripType()));
        totalDistance = mTrip.getDistance();
        txtDistance.setText(CommonUtilities.convertToKilometer(totalDistance));
        txtPrice.setText(CommonUtilities.convertCurrency(mTrip.getPrice())+ " vnđ");
        mTrip.setCustomerType(1);


        if (totalDistance < Defines.MAX_DISTANCE) {
            layoutStart.setVisibility(View.GONE);
            layoutBack.setVisibility(View.GONE);
            mDividerStart.setVisibility(View.GONE);
            mDividerBack.setVisibility(View.GONE);
        }else if (mTrip.getTripType() == 1){
            layoutBack.setVisibility(View.GONE);
            mDividerBack.setVisibility(View.GONE);
            mDividerStart.setVisibility(View.VISIBLE);
            layoutStart.setVisibility(View.VISIBLE);
        }else {
            mDividerStart.setVisibility(View.VISIBLE);
            mDividerBack.setVisibility(View.VISIBLE);
            layoutStart.setVisibility(View.VISIBLE);
            layoutBack.setVisibility(View.VISIBLE);
        }

        startTime = new DateTime();
        txtStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(txtStartTime);
                DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");
                startTime = dtf.parseDateTime(txtStartTime.getText().toString());
                checkLongTripExceed2day();
            }
        });

        txtBackTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(txtBackTime);
                DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");
                endTime = dtf.parseDateTime(txtBackTime.getText().toString());
                checkLongTripExceed2day();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.radio_friend:
                        edtCustomerName.setVisibility(View.VISIBLE);
                        edtCustomerPhone.setVisibility(View.VISIBLE);
                        viewDividerFriend.setVisibility(View.VISIBLE);
                        break;
                    case R.id.radio_mine:
                        edtCustomerName.setVisibility(View.GONE);
                        edtCustomerPhone.setVisibility(View.GONE);
                        viewDividerFriend.setVisibility(View.GONE);
                        break;
                }
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rFriend.isChecked()) {
                    mTrip.setCustomerType(0);
                    mTrip.setGuestName(edtCustomerName.getText().toString());
                    mTrip.setGuestPhone(edtCustomerPhone.getText().toString());
                }else if (rOwne.isChecked()){
                    mTrip.setCustomerType(1);
                }
                if (totalDistance > Defines.MAX_DISTANCE) {
                    if (mTrip.getTripType() == 1) {
                        if (txtStartTime.getText().toString().equals(getString(R.string.error_time_start))){
                            DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-dd-MM'T'HH:mm:ss.SSS");
                            mTrip.setStartTime(dtf.print(new DateTime()));
                        }else {
                            DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");
                            DateTime startTime = dtf.parseDateTime(txtStartTime.getText().toString());
                            DateTimeFormatter dtfTrip = DateTimeFormat.forPattern("yyyy-dd-MM'T'HH:mm:ss.SSS");
                            mTrip.setStartTime(dtfTrip.print(startTime));
                        }
                    } else {
                        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");
                        DateTimeFormatter dtfTrip = DateTimeFormat.forPattern("yyyy-dd-MM'T'HH:mm:ss.SSS");
                        DateTime startTime;
                        if (txtStartTime.getText().toString().equals(getString(R.string.error_time_start))){
                            startTime = new DateTime();
                            mTrip.setStartTime(dtf.print(new DateTime()));
                        }else{
                            startTime = dtf.parseDateTime(txtStartTime.getText().toString());
                            mTrip.setStartTime(dtfTrip.print(startTime));
                        }
                        DateTime endTime = dtf.parseDateTime(txtBackTime.getText().toString());
                        mTrip.setEndTime(dtfTrip.print(endTime));
                        int days = Days.daysBetween(endTime.toLocalDate(), startTime.toLocalDate()).getDays();
                        if (days > 2 )
                            mTrip.setPrice(mTrip.getPrice()*days);
                    }
                }
                if (callback != null)
                    callback.onConfirmed(mTrip);
                ConfirmDialogFragment.this.dismiss();
            }
        });
    }

    private void checkLongTripExceed2day(){
        if (endTime == null)
            return;
        if (startTime == null)
            return;
        int days = Days.daysBetween(endTime.toLocalDate(), startTime.toLocalDate()).getDays();
        if (days > 2 ) {
            txtCaution.setVisibility(View.VISIBLE);
            txtPrice.setText(CommonUtilities.convertCurrency(mTrip.getPrice()*days)+ " vnđ");
        }else {
            txtCaution.setVisibility(View.GONE);
            txtPrice.setText(CommonUtilities.convertCurrency(mTrip.getPrice())+ " vnđ");
        }
    }
    private void showDateTimeDialog(final TextView txtDate){
        final View dialogView = View.inflate(getActivity(), R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datepicker);
        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.timepicker);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int mHour, int mMinute) {
                Calendar now = Calendar.getInstance();
                int year = now.get(Calendar.YEAR);
                int month = now.get(Calendar.MONTH); // Note: zero based!
                int day = now.get(Calendar.DAY_OF_MONTH);
                int hour = now.get(Calendar.HOUR_OF_DAY);
                int minutes = now.get(Calendar.MINUTE);
                if (datePicker.getYear() == year && datePicker.getMonth() == month && datePicker.getDayOfMonth() == day ){
                    if (mHour <= hour) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                            if (hour > 22)
                                timePicker.setHour(hour);
                            else
                                timePicker.setHour(hour+1);
                            timePicker.setMinute(minutes);
                        }else {
                            if (hour > 22)
                                timePicker.setCurrentHour(hour);
                            else
                                timePicker.setCurrentHour(hour+1);

                            timePicker.setCurrentMinute(minutes);
                        }
                    }
                }
            }
        });
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (calendar.get(Calendar.HOUR_OF_DAY) > 22){
                timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            }else
                timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY)+1);
        }else {
            if (calendar.get(Calendar.HOUR_OF_DAY) > 22)
                timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
            else
                timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY)+1);
        }
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Calendar now = Calendar.getInstance();
                int cYear = now.get(Calendar.YEAR);
                int cMonth = now.get(Calendar.MONTH);
                int cDay = now.get(Calendar.DAY_OF_MONTH);
                int hour = now.get(Calendar.HOUR_OF_DAY);
                int minutes = now.get(Calendar.MINUTE);
                if (cYear == year && cMonth == month && cDay == dayOfMonth ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (hour > 22)
                            timePicker.setHour(hour);
                        else
                            timePicker.setHour(hour+1);
                        timePicker.setMinute(minutes);
                    }else {
                        if (hour > 22)
                            timePicker.setCurrentHour(hour);
                        else
                            timePicker.setCurrentHour(hour+1);

                        timePicker.setCurrentMinute(minutes);
                    }
                }

            }
        });
        dialogView.findViewById(R.id.datetimeset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute());
                SimpleDateFormat mSDF = new SimpleDateFormat("HH:mm:ss");
                String time = mSDF.format(calendar.getTime());
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                String formatedDate = sdf.format(new Date(year-1900, month, day));
                txtDate.setText(formatedDate + ' ' + time);
                alertDialog.dismiss();

            }
        });

        alertDialog.setView(dialogView);
        alertDialog.show();
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
                ConfirmDialogFragment.this.dismiss();
            }
        };
    }
}
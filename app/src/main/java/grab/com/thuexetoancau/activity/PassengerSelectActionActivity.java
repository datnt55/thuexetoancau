package grab.com.thuexetoancau.activity;

import android.animation.ValueAnimator;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.fragment.LastSearchFragment;
import grab.com.thuexetoancau.utilities.CommonUtilities;
import grab.com.thuexetoancau.utilities.Defines;

public class PassengerSelectActionActivity extends AppCompatActivity {
    private Button btnBooking, btnInfor;
    private EditText edtSearch;
    private FrameLayout layoutLastSearch, layoutMenu;
    private RelativeLayout layoutRoot;
    private LinearLayout layoutSearch;
    private ImageView imgMenu;
    private boolean showLastSearch =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_select_action);


        btnBooking  = (Button)      findViewById(R.id.btn_booking);
        btnInfor    = (Button)      findViewById(R.id.btn_infor);
        layoutRoot  = (RelativeLayout) findViewById(R.id.root);

        layoutLastSearch = (FrameLayout)  findViewById(R.id.fragment_last_search);
        edtSearch   = (EditText)   findViewById(R.id.edt_search);
        layoutSearch = (LinearLayout) findViewById(R.id.layout_search);
        imgMenu = (ImageView) findViewById(R.id.img_menu);
        layoutMenu = (FrameLayout) findViewById(R.id.layout_menu);
        edtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!showLastSearch) {
                    showLastSearch = true;
                    showLastSearchFragment();
                }
            }
        });
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //here is your code

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PassengerSelectActionActivity.this, FormPassengerBookingActivity.class);
                startActivity(intent);
            }
        });

        btnInfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PassengerSelectActionActivity.this, ListPassengerBookingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showLastSearchFragment() {
        showLastSearch = true;
        layoutRoot.setBackgroundColor(ContextCompat.getColor(this, R.color.bg));
        FragmentTransaction fragmentManager =  getSupportFragmentManager().beginTransaction();
        fragmentManager.replace(R.id.fragment_last_search, new LastSearchFragment()).commit();

        int header = measureView(layoutSearch);
        int height = Defines.APP_SCREEN_HEIGHT - header - (int)CommonUtilities.convertDpToPixel(20, this) - CommonUtilities.getStatusBarHeight(this);
        ValueAnimator mAnimator = ValueAnimator.ofFloat(0, height);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float a = (Float) animation.getAnimatedValue();
                int x = a.intValue();
                layoutLastSearch.getLayoutParams().height = x;
                layoutLastSearch.requestLayout();
            }
        });
        mAnimator.setDuration(400);
        mAnimator.start();

        imgMenu.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        layoutMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideLastSearhFragment();
            }
        });
    }

    private void hideLastSearhFragment() {
        showLastSearch = false;
        imgMenu.setImageResource(R.drawable.ic_menu_black_24dp);
        layoutMenu.setOnClickListener(null);
        layoutRoot.setBackgroundResource(R.drawable.bg_passenger_infor);
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        int header = measureView(layoutSearch);
        int height = Defines.APP_SCREEN_HEIGHT - header - (int)CommonUtilities.convertDpToPixel(20, this) - CommonUtilities.getStatusBarHeight(this);
        ValueAnimator mAnimator = ValueAnimator.ofFloat(height,0);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float a = (Float) animation.getAnimatedValue();
                int x = a.intValue();
                layoutLastSearch.getLayoutParams().height = x;
                layoutLastSearch.requestLayout();
            }
        });
        mAnimator.setDuration(400);
        mAnimator.start();
    }

    private int measureView(final View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredHeight();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}

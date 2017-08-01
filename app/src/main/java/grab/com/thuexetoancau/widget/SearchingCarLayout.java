package grab.com.thuexetoancau.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dd.CircularProgressButton;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.utilities.AnimUtils;
import grab.com.thuexetoancau.utilities.Defines;

import static grab.com.thuexetoancau.utilities.AnimUtils.EASE_IN;

/**
 * Created by DatNT on 7/31/2017.
 */

public class SearchingCarLayout extends LinearLayout {
    private Context mContext;
    private ImageView imgCircle, imgCircle1;
    private CircularProgressButton btnCancel;
    private SearchingCallBack callBack;

    public SearchingCarLayout(Context context, SearchingCallBack callBack) {
        super(context);
        this.mContext = context;
        this.callBack = callBack;
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

        btnCancel = (CircularProgressButton) findViewById(R.id.btn_cancel);
        btnCancel.setIndeterminateProgressMode(true);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnCancel.getProgress() == 0) {
                    btnCancel.setProgress(50);
                } else if (btnCancel.getProgress() == 100) {
                    btnCancel.setProgress(0);
                } else {
                    btnCancel.setProgress(100);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AnimUtils.slideDown(SearchingCarLayout.this, Defines.APP_SCREEN_HEIGHT);
                        if (callBack!= null)
                            callBack.onSearchCarSuccess();
                    }
                },2000);
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
        void onSearchCarBack();
    }
}
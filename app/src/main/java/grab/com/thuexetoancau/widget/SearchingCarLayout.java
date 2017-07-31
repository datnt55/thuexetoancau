package grab.com.thuexetoancau.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
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

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.utilities.AnimUtils;

import static grab.com.thuexetoancau.utilities.AnimUtils.EASE_IN;

/**
 * Created by DatNT on 7/31/2017.
 */

public class SearchingCarLayout extends LinearLayout {
    private Context mContext;
    private ImageView imgCircle, imgCircle1, imgCircle2;

    public SearchingCarLayout(Context context) {
        super(context);
        mContext = context;
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
       // imgCircle1 = (ImageView) view.findViewById(R.id.img_circle_1);
       // imgCircle2 = (ImageView) view.findViewById(R.id.img_circle_2);
        startAmination(imgCircle);

    }

    private void startAmination(View view){
        view.setScaleX(0);
        view.setScaleY(0);
        view.setAlpha(1);
        final ViewPropertyAnimator animator = view.animate();
        animator.cancel();
        animator.setInterpolator(EASE_IN)
                .scaleX(1)
                .scaleY(1)
                .alpha(0)
                .setListener(listener)
                .withLayer();

        animator.setDuration(1500);
        animator.setStartDelay(0);

        animator.start();
    }
    AnimatorListenerAdapter listener = (new AnimatorListenerAdapter() {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            startAmination(imgCircle);
        }
    });
}
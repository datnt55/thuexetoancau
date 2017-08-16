package grab.com.thuexetoancau.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;

import grab.com.thuexetoancau.R;

/**
 * Created by DatNT on 8/8/2017.
 */

public class CustomProgress extends TextView {

    private final static int SHAPE_RECTANGLE = 0;
    private final static int SHAPE_ROUNDED_RECTANGLE = 1;
    private final static int DEFAULT_TEXT_MARGIN = 10;

    private ShapeDrawable progressDrawable;
    private TextView textView;
    private int width = 0;
    private float realWidth = 0f;
    private int maxWidth = 0;
    private int maxHeight = 0;
    private int progressColor;
    private int progressBackgroundColor;
    private int progressShape = SHAPE_ROUNDED_RECTANGLE;
    private float maximumPercentage = 1.0f;
    private float cornerRadius = 15.0f;
    private boolean showingPercentage = true;
    private int speed = 1;
    private boolean resetToZero = false;
    private Context mContext;
    //Constructor

    public CustomProgress(Context context) {
        super(context);
        this.mContext = context;
        setDefaultValue();
    }

    public CustomProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        setDefaultValue();
    }

    public CustomProgress(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        setDefaultValue();
    }

    private void setDefaultValue() {
        // default color
        progressColor = ContextCompat.getColor(mContext,R.color.blue);
        progressBackgroundColor = ContextCompat.getColor(mContext,R.color.grey);
    }

    //View Lifecycle

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed) {
            initView();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        progressDrawable.setBounds(0, 0, width, this.getHeight());
        progressDrawable.draw(canvas);
       /* if(isShowingPercentage()) {
            this.setText(getCurrentPercentage()+"%");
        }
        setTextColor(ContextCompat.getColor(mContext,R.color.white));*/
        //if the we want to reset to 0
        if(resetToZero) {
            width = 0;
            resetToZero = false;
            invalidate();
        } else if(width < maxWidth) {
            realWidth+= ((float)this.speed/10);
            width = (int) realWidth;
            invalidate();
        }
    }

    /**
     * Initialize the view before it will be drawn
     */
    private void initView() {
        Shape progressShapeDrawable = null;
        Shape backgroundProgressShapeDrawable = null;
        switch (progressShape) {
            case SHAPE_RECTANGLE:
                progressShapeDrawable = new RectShape();
                backgroundProgressShapeDrawable = new RectShape();
                break;
            case SHAPE_ROUNDED_RECTANGLE:
                float[] outerRadius = new float[8];
                Arrays.fill(outerRadius, cornerRadius);
                progressShapeDrawable = new RoundRectShape(outerRadius, null, null);
                backgroundProgressShapeDrawable = new RoundRectShape(outerRadius, null, null);
                break;
        }

        //Progress
        progressDrawable = new ShapeDrawable(progressShapeDrawable);
        progressDrawable.getPaint().setColor(progressColor);
      /*  if((this.getText().length() > 0) || isShowingPercentage()) {
            progressDrawable.setAlpha(100);
        }*/

        //Background
        ShapeDrawable backgroundDrawable = new ShapeDrawable(backgroundProgressShapeDrawable);
        backgroundDrawable.getPaint().setColor(progressBackgroundColor);
        backgroundDrawable.setBounds(0, 0, this.getWidth(), this.getHeight());
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.setBackground(backgroundDrawable);
        } else {
            this.setBackground(backgroundDrawable);
        }

        this.maxWidth = (int) (this.getWidth() * maximumPercentage);

       /* //Percentage
        if(isShowingPercentage()) {
            this.setTextSize(20);
            this.setTextColor(Color.WHITE);
            this.setGravity(Gravity.CENTER);
        }*/
    }

    //Helper

    /**
     * Set the progress color
     * @param color
     */
    public void setProgressColor(int color) {
        this.progressColor = color;
    }

    /**
     * Set the background color
     * @param color
     */
    public void setProgressBackgroundColor(int color) {
        this.progressBackgroundColor = color;
    }

    /**
     * Reset the progress to 0
     */
    public void resetWidth() {
        width = 0;
    }

    /**
     * Set the maximum percentage for the progress
     * @param percentage
     */
    public void setMaximumPercentage(float percentage) {
        this.maximumPercentage = percentage;
    }

    /**
     * Get current percentage based on current width
     * @return
     */
    public int getCurrentPercentage() {
        return (int) Math.ceil((width/(maxWidth*1.0f))*100);
    }

    /**
     * Set the shape of custom progress to rectangle
     */
    public void useRectangleShape() {
        this.progressShape = SHAPE_RECTANGLE;
    }

    /**
     * Set the shape of custom progress to rounded rectangle
     * @param cornerRadius radius of the corner
     */
    public void useRoundedRectangleShape(float cornerRadius) {
        this.progressShape = SHAPE_ROUNDED_RECTANGLE;
        this.cornerRadius = cornerRadius;
    }

    /**
     * If this returns true the custom progress
     * will show progress based on getCurrentPercentage()
     * @return true for showing percentage false for not showing anything
     */
    public boolean isShowingPercentage() {
        return showingPercentage;
    }

    /**
     * Set if the custom progress will show percentage or not
     * @param showingPercentage true for showing percentage false for not showing anything
     */
    public void setShowingPercentage(boolean showingPercentage) {
        this.showingPercentage = showingPercentage;
    }
    /**
     * Set the speed of the movement of the progress
     * @param speed as an int it should range from [1,100]
     */
    public void setSpeed(int speed){
        this.speed = speed;
    }
    /**
     * call the function when you want to update view
     */
    public void updateView() {
        resetToZero = true;
        initView();
        invalidate();
    }
}
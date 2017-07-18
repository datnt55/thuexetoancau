package grab.com.thuexetoancau.widget;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;

/**
 * Created by Duc on 3/10/2017.
 */

public class SubStringTextView extends android.support.v7.widget.AppCompatTextView{
    private int mOriginalTextSize;

    public SubStringTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mOriginalTextSize = (int) getTextSize();
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        substringText();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        substringText();
    }

    private void substringText(){
        final Paint paint = getPaint();
        final int width = getWidth();
        if (width == 0)
            return;
        float ratio = width / paint.measureText(getText().toString());
        String content = getText().toString();
        if (ratio <= 1.0f) {
            int index = width/mOriginalTextSize;
            setText(content.substring(0,index)+" ...");
        }
    }
}

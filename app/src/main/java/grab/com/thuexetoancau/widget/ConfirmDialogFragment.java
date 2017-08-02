package grab.com.thuexetoancau.widget;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import grab.com.thuexetoancau.R;

/**
 * Created by DatNT on 8/2/2017.
 */

public class ConfirmDialogFragment extends DialogFragment {
    private String title;

    public interface InputNameDialogListener {
        void onFinishInputDialog(String inputText);
    }

    //---empty constructor required
    public ConfirmDialogFragment() {

    }

    //---set the title of the dialog window
    public void setDialogTitle(String title) {
        this.title = title;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.dialog_booking, container);
        //---set the title for the dialog
        getDialog().setTitle(title);

        return view;
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
}
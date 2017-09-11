package grab.com.thuexetoancau.widget;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import grab.com.thuexetoancau.R;

/**
 * Created by DatNT on 7/18/2017.
 */

public class SearchBarLayout extends LinearLayout implements View.OnClickListener{
    private Context mContext;
    private FrameLayout layoutMenu;
    private AutoCompleteTextView edtSearch;
    private ImageView imgMenu;
    private Callback mCallback;
    private boolean showLastSearch =false;
    public SearchBarLayout(Context context) {
        super(context);
        mContext = context;
        initLayout();
    }

    public SearchBarLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initLayout();
    }

    private void initLayout() {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(R.layout.search_bar_layout, this, true);

        edtSearch   = (AutoCompleteTextView)   view.findViewById(R.id.edt_search);

        imgMenu = (ImageView) view.findViewById(R.id.img_menu);
        layoutMenu = (FrameLayout) view.findViewById(R.id.layout_menu);
        layoutMenu.setOnClickListener(this);
        edtSearch.setOnClickListener(this);
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (mCallback != null)
                        mCallback.onSearchViewSearching();
                    return true;
                }
                return false;
            }
        });
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mCallback != null)
                    mCallback.onChangeTextSearch(s,edtSearch);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setCallback(Callback listener) {
        mCallback = listener;
        mCallback.getLayoutSearchHeight(measureView(this));
    }

    public void setHint(int position){
        if (position != 0)
            edtSearch.setHint("Nhập vào điểm đến");
        else
            edtSearch.setHint("Nhập vào điểm đi");
    }

    public void setHint(){
        edtSearch.setHint("Nhập vào điểm đến");
    }

    public void setShowLastSearch(boolean status){
        if (!status)
            imgMenu.setImageResource(R.drawable.ic_menu_black_24dp);
        else
            imgMenu.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        this.showLastSearch = status;
    }

    public void requestForcus(){
        edtSearch.requestFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtSearch, InputMethodManager.SHOW_IMPLICIT);
        Activity activity = (Activity) mContext;
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    public void removeSearchText() {
        edtSearch.setText("");
    }

    private int measureView(final View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredHeight();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_menu:
                if (showLastSearch) {
                    imgMenu.setImageResource(R.drawable.ic_menu_black_24dp);
                    if (mCallback != null)
                        mCallback.onBackButtonClicked();
                }else
                    if (mCallback != null)
                        mCallback.onMenuButtonClicked();
                break;
            case R.id.edt_search:
                if (!showLastSearch) {
                    showLastSearch = true;
                    if (mCallback != null)
                         mCallback.onSearchViewClicked();
                    imgMenu.setImageResource(R.drawable.ic_arrow_back_black_24dp);
                }
                break;
        }
    }

    /**
     * Listener for the back button next to the search view being pressed
     */
    public interface Callback {
        void onBackButtonClicked();
        void onMenuButtonClicked();
        void onSearchViewClicked();
        void onSearchViewSearching();
        void onChangeTextSearch(CharSequence s, AutoCompleteTextView edtSearch);
        void getLayoutSearchHeight(int height);
    }



}

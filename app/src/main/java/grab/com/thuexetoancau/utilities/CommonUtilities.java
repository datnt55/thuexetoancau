package grab.com.thuexetoancau.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;

import org.joda.time.DateTime;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;

import grab.com.thuexetoancau.R;

/**
 * Created by DatNT on 11/9/2016.
 */

public class CommonUtilities {
    public static final int flagsKitkat = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }
    public static int getStatusBarHeight(Activity activity) {
        int result = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    public static void systemUiVisibility(Activity activity) {
//         This work only for android 4.4+
        final View decorView = activity.getWindow().getDecorView();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().getDecorView().setSystemUiVisibility(flagsKitkat);

            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flagsKitkat);
                    }
                }
            });
        }
    }
    public static String convertTime(DateTime current){
        String dateFrom = current.getDayOfMonth()+"/"+current.getMonthOfYear();
        dateFrom+=" ";

        if (current.getHourOfDay()>=10)
            dateFrom+=current.getHourOfDay();
        else
            dateFrom+="0"+current.getHourOfDay();
        dateFrom+=":";
        if (current.getMinuteOfHour()>=10)
            dateFrom+=current.getMinuteOfHour();
        else
            dateFrom+="0"+current.getMinuteOfHour();

        return dateFrom;
    }
    public static String convertTime2Lines(DateTime current){
        String dateFrom = current.getDayOfMonth()+"/"+current.getMonthOfYear()+"/"+current.getYear();
        dateFrom+="\n";

        if (current.getHourOfDay()>=10)
            dateFrom+=current.getHourOfDay();
        else
            dateFrom+="0"+current.getHourOfDay();
        dateFrom+=":";
        if (current.getMinuteOfHour()>=10)
            dateFrom+=current.getMinuteOfHour();
        else
            dateFrom+="0"+current.getMinuteOfHour();
        dateFrom+=":";
        if (current.getSecondOfMinute()>=10)
            dateFrom+=current.getSecondOfMinute();
        else
            dateFrom+="0"+current.getSecondOfMinute();
        return dateFrom;
    }
    public static String convertCurrency(int currency){
        NumberFormat formatter = NumberFormat.getInstance();
        String moneyString = formatter.format(currency);
        if (moneyString.endsWith(".00")) {
            int centsIndex = moneyString.lastIndexOf(".00");
            if (centsIndex != -1) {
                moneyString = moneyString.substring(0, centsIndex);
            }
        }
        return moneyString;
    }
    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static int changeTimeFromStringToInt(String timeRemaining){
        int second = 1000;
        if(!(timeRemaining.equals("Đã hết hạn") || timeRemaining.equals("00:00"))){
            int hour  = Integer.parseInt(timeRemaining.split(":")[0]);
            int minute = Integer.parseInt(timeRemaining.split(":")[1]);
            second = (Integer.parseInt(timeRemaining.split(":")[2]) + minute*60+hour*60*60)*1000;
        }
        return second;
    }

    public static void dimensionScreen(Activity activity){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Defines.APP_SCREEN_HEIGHT = displayMetrics.heightPixels;
        Defines.APP_SCREEN_WIDTH = displayMetrics.widthPixels;
    }

    public int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static String getCharacterDirection(int index){
        switch (index){
            case 1:
                return "A";
            case 2:
                return "B";
            case 3:
                return "C";
            case 4:
                return "D";
            case 5:
                return "E";
            case 6:
                return "F";
            case 7:
                return "G";
            case 8:
                return "H";
            case 9:
                return "I";
            case 10:
                return "J";
            default:
                return "#";
        }
    }

    public static String getTripType(int type){
        switch (type){
            case 1:
                return "Một chiều";
            case 2:
                return "Khứ hồi";
        }
        return null;
    }

    // Show dialog request turn on gps
    public static void settingRequestTurnOnLocation(final Activity mActivity) {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        alertDialogBuilder.setTitle(R.string.notice);  // GPS not found
        alertDialogBuilder.setMessage(R.string.gps_notice_content)
                .setCancelable(false)
                .setPositiveButton(R.string.gps_continue,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                mActivity.startActivityForResult(callGPSSettingIntent,1000);
                            }
                        });
        alertDialogBuilder.setNegativeButton(R.string.gps_no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}

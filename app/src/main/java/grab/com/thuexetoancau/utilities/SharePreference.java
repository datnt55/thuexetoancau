package grab.com.thuexetoancau.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SharePreference {

    private Context activity;
    private String REGID        = "reg_id";
    private String CUSTOMER_ID  = "customer_id";
    private String THE_FIRST    = "the_first";
    private String TOKEN        = "token";
    private String NAME    = "customer_name";
    private String PHONE         = "customer phone";
    private String CAR_NUMBER   = "car number";
    private String STATUS       = "status";
    private String TEMP_PHONE   = "temp phone";
    // constructor
    public SharePreference(Context activity) {
        this.activity = activity;
    }
    public void saveRegId(String token) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(REGID, token);
        editor.apply();
    }
    public String getRegId() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getString(REGID, "");
    }

    public void saveCustomerId(String phone) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CUSTOMER_ID, phone);
        editor.apply();
    }
    public String getCustomerId() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getString(CUSTOMER_ID, "");
    }

    public void saveFirst() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(THE_FIRST, false);
        editor.apply();
    }
    public boolean getFirst() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getBoolean(THE_FIRST, true);
    }
    public void saveToken(String token) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(TOKEN, token);
        editor.apply();
    }
    public String getToken() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getString(TOKEN, null);
    }
    public void clearToken() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(TOKEN);
        editor.apply();
    }
    public void saveName(String name) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(NAME, name);
        editor.apply();
    }
    public String getName() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getString(NAME,"");
    }
    public void savePhone(String phone) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PHONE, phone);
        editor.apply();
    }
    public String getPhone() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getString(PHONE,"");
    }
    public void saveCarNumber(String carNumber) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CAR_NUMBER, carNumber);
        editor.apply();
    }
    public String getCarNumber() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getString(CAR_NUMBER,"");
    }
    public void saveStatus(int status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(STATUS, status);
        editor.apply();
    }
    public int getStatus() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getInt(STATUS,0);
    }
    public void saveTempPhone(String phone) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(TEMP_PHONE, phone);
        editor.apply();
    }
    public String getTempPhone() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getString(TEMP_PHONE, "");
    }

}

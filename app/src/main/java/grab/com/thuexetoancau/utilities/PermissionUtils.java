package grab.com.thuexetoancau.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DatNT on 7/17/2017.
 */

public class PermissionUtils {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    public static boolean checkAndRequestPermissions(Activity mActivity) {
        int phone = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CALL_PHONE);
        int receiveSms = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.RECEIVE_SMS);
        int readSms = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_SMS);
        int broadcastSms = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.BROADCAST_SMS);
        int loc = ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int loc2 = ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (readSms != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_SMS);
        }

        if (phone != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CALL_PHONE);
        }
        if (loc2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(mActivity,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
}

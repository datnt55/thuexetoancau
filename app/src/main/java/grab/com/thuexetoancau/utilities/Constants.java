package grab.com.thuexetoancau.utilities;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by DatNT on 11/11/2016.
 */

public class Constants {
    public static  final int REQUEST_CODE_LOCATION_PERMISSIONS = 234;
    public static  final int REQUEST_CODE_CONTACT_PERMISSIONS = 235;
    public static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(8.412730, 102.144410), new LatLng(23.393395, 109.468975));
    public static final int GOOGLE_API_CLIENT_ID = 0;
    public static final int DIRECTION_START_POINT = 1;
    public static final int DIRECTION_ENDPOINT = 2;
    public static final String TYPE_POINT = "type direction point";
}
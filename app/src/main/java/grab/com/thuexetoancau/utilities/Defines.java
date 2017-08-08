package grab.com.thuexetoancau.utilities;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

import grab.com.thuexetoancau.model.Phone;

/**
 * Created by DatNT on 6/29/2016.
 */
public class Defines {
    public static  final String HOSTNAME                        = "http://thuexetoancau.vn/";
    public static  final String URL_REGISTER                    = HOSTNAME + "api2/customRegister";
    public static  final String URL_LOGIN                       = HOSTNAME + "api2/customerLogin";
    public static  final String URL_CHECK_TOKEN                 = HOSTNAME + "api2/customerCheckToken";
    public static  final String URL_GET_POSTAGE                 = HOSTNAME + "api2/getPostage";
    public static  final String URL_LOG_OUT                     = HOSTNAME + "api2/customerLogOut";
    public static  final String URL_BOOKING                     = HOSTNAME + "api2/bookingGrab";
    public static  final String URL_CANCEL_TRIP                 = HOSTNAME + "api2/cancelTrip";
    public static  final String URL_GET_SERVER_TIME             = HOSTNAME + "api2/getServerTime";
    public static  final String URL_GET_LIKE_TRIP               = HOSTNAME + "api2/getLikeTrip";
    public static  final String URL_REVIEW_TRIP                 = HOSTNAME + "api2/reviewTrip";
    public static  final String URL_LIKE_TRIP                   = HOSTNAME + "api2/likeTrip";
    public static  final String URL_GET_LONLAT_AIRPORT      = HOSTNAME + "api/getLonLatAirport";
    public static  final String URL_GET_WHO_HIRE            = HOSTNAME + "api/getCarWhoHire ";
    public static  final String URL_GET_AIRPORT             = HOSTNAME + "api/getAirportName";
    public static  final String URL_BOOKING_LOG             = HOSTNAME + "api/bookingLog";
    public static  final String URL_GET_CAR_SIZE            = HOSTNAME + "api/getCarSize";
    public static  final String URL_GET_CAR_HIRE_TYPE       = HOSTNAME + "api/getCarHireType";
    public static  final String URL_BOOKING_TICKET          = HOSTNAME + "api/booking";
    public static  final String URL_GET_BOOKING_LOG         = HOSTNAME + "api/getBookingLog";
    public static  final String URL_GET_BOOKING_CUSTOMER    = HOSTNAME + "api/getBookingForCustomer";
    public static  final String URL_GET_LIST_BOOKING_LOG    = HOSTNAME + "api/getlistbookinglog";
    public static  final String URL_GET_DRIVER_BY_ID        = HOSTNAME + "api/getDriverById";
    public static  final String URL_GET_MONEY_DRIVER        = HOSTNAME + "api/getMoneyDriver";
    public static  final String URL_CONFIRM                 = HOSTNAME + "api/confirm";
    public static  final String URL_WHO_WIN                 = HOSTNAME + "api/whoWin";
    public static  final String URL_POST_DRIVER_GPS         = HOSTNAME + "api/postDriverGPS";
    public static  final String URL_GET_CAR_AROUND          = HOSTNAME + "api/getaround";
    public static  final String URL_GET_STATUS_DRIVER       = HOSTNAME + "api/getStatusDriver";
    public static  final String URL_BOOKING_SUCCESS         = HOSTNAME + "api/getBookingSuccess";
    public static  final String URL_CAR_REGISTATION         = HOSTNAME + "api/searchCarNumber";
    public static  final String URL_STATISTIC               = HOSTNAME + "api/searchTran";
    public static  final String URL_SALARY                  = HOSTNAME + "api/searchTranSalary";
    public static  final String URL_PHONE                     = "http://country.io/phone.json";


    public static  final String DIALOG_CONFIRM_TRIP              = "trip";

    public static int MAX_DISTANCE                              = 50000;
    public static int MIN_CURRENT_DISTANCE                      = 20;
    public static final int REQUEST_CODE_PICKER                 = 100;
    public static final int TIME_BEFORE_AUCTION_LONG                 = 5*60*60*1000;
    public static final int TIME_BEFORE_AUCTION_SHORT                 = 1*60*60*1000;
    public static final int REQUEST_CODE_LOCATION_PERMISSIONS = 234;
    public static final int REQUEST_CODE_CONTACT_PERMISSIONS = 235;
    public static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(8.412730, 102.144410), new LatLng(23.393395, 109.468975));
    public static final int GOOGLE_API_CLIENT_ID = 0;
    public static final int FRAMEWORK_REQUEST_CODE = 1;
    public static final int DIRECTION_ENDPOINT = 2;
    public static final int DIRECTION_NEW_STOP_POINT = 3;
    public static final String TYPE_POINT = "type direction point";
    public static final String POSITION_POINT = "position direction point";
    public static final String BUNDLE_USER = "bundle user";
    public static final String BUNDLE_TRIP = "bundle trip";
    // Main Screen Dimension, will be set when app startup


}

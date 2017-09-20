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
    public static  final String URL_GET_HISTORY_TRIP            = HOSTNAME + "api2/getHistoryTrip";
    public static  final String URL_GET_LIKE_TRIP               = HOSTNAME + "api2/getLikeTrip";
    public static  final String URL_REVIEW_TRIP                 = HOSTNAME + "api2/reviewTrip";
    public static  final String URL_LIKE_TRIP                   = HOSTNAME + "api2/likeTrip";
    public static  final String URL_SCHEDULE_TRIP               = HOSTNAME + "api2/getTripSchedule";
    public static  final String URL_TRIP_INFO                   = HOSTNAME + "api2/getTripInfo";
    public static  final String URL_POINT_FOR_USER              = HOSTNAME + "api2/getPointForUser";
    public static  final String     URL_NOTICE                  = HOSTNAME + "api/getNotice";
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
    public static final int CONFIGURE_CODE = 2;
    public static final int DIRECTION_NEW_STOP_POINT = 3;
    public static final String TYPE_POINT = "type direction point";
    public static final String POSITION_POINT = "position direction point";
    public static final String BUNDLE_LOGIN_USER = "bundle login user";
    public static final String BUNDLE_LOGIN_TRIP = "bundle login trip";
    public static final String BUNDLE_LOGIN_DRIVER = "bundle login driver";
    public static final String BUNDLE_USER = "bundle user";
    public static final String BUNDLE_TRIP = "bundle trip";
    public static final String BUNDLE_TRIP_TYPE = "bundle trip type";
    public static final String BUNDLE_TRIP_ID = "bundle trip id";
    public static final String BUNDLE_DRIVER= "bundle driver";
    public static final String BUNDLE_DRIVER_NAME= "bundle driver name";
    public static final String BUNDLE_PRICE= "bundle price";
    public static final String BUNDLE_LAT = "bundle lat";
    public static final String BUNDLE_LON = "bundle lon";

    // Main Screen Dimension, will be set when app startup

    public static final String BUNDLE_CONFIRM_TRIP = "bundle confirm trip";
    public static final String BUNDLE_FOUND_DRIVER = "bundle found driver";
    public static final String BUNDLE_NOT_FOUND_DRIVER = "bundle not found driver";
    public static final String BUNDLE_CATCH_TRIP = "bundle catch trip";

    public static final String DRIVER_CATCH_TRIP = "CatchTrip";
    public static final String DRIVER_AUTO_POST_GPS = "autoPostGpsToCustomerAndDriver";
    public static final String DRIVER_CANCEL_TRIP = "driverCancelTrip";
    public static final String BOOKING_GRAB= "bookingGrab";
    public static final String NOT_FOUND_DRIVER= "notfounddriver";
    public static final String RECEIVED_TRIP= "receivedTrip";
    public static final String CONFIRM_TRIP= "confirmTrip";
    public static final String RATE_TRIP= "rate";

    public static final String SUCCESS = "success";
    public static final String TO_CUSTOMER = "toCustomer";

    public static final String BROADCAST_RECEIVED_TRIP= "broadcast received Trip";
    public static final String BROADCAST_NOT_FOUND_DRIVER = "broadcast not found driver";
    public static final String BROADCAST_CANCEL_TRIP= "broadcast cancel Trip";
    public static final String BROADCAST_CONFFIRM_TRIP = "confirm trip";
    public static final String BROADCAST_CATCH_TRIP = "broascast catch trip";
    public static final String BROADCAST_AUTO_GPS = "broascast auto gps";

    // Notification tag
    public static final String NOTIFY_TAG = "notify tag";

    public static final int BOOKING_IN_PROGRESS = 99 ;
    public static final int BOOKING_WELCOME_CUSTOMER = 1 ;

}

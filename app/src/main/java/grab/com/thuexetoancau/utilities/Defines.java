package grab.com.thuexetoancau.utilities;


import java.util.ArrayList;

import grab.com.thuexetoancau.model.Phone;

/**
 * Created by DatNT on 6/29/2016.
 */
public class Defines {
    public static  final String HOSTNAME                        = "http://thuexetoancau.vn/";
    public static  final String URL_REGISTER                    = HOSTNAME + "api2/customRegister";
    public static  final String URL_GET_CAR_SIZE            = HOSTNAME + "api/getCarSize";
    public static  final String URL_GET_CAR_TYPE            = HOSTNAME + "api/getListCarType";
    public static  final String URL_GET_CAR_MADE            = HOSTNAME + "api/getCarMadeList";
    public static  final String URL_GET_CAR_MODEL           = HOSTNAME + "api/getCarModelListFromMade";
    public static  final String URL_GET_WHO_HIRE            = HOSTNAME + "api/getCarWhoHire ";
    public static  final String URL_BOOKING_TICKET          = HOSTNAME + "api/booking";
    public static  final String URL_GET__BOOKING            = HOSTNAME + "api/getBooking";
    public static  final String URL_BOOKING_FINAL           = HOSTNAME + "api/bookingFinal";
    public static  final String URL_REGISTER_DRIVER         = HOSTNAME + "api/driverRegister";
    public static  final String URL_GET_AIRPORT             = HOSTNAME + "api/getAirportName";
    public static  final String URL_GET_LONLAT_AIRPORT      = HOSTNAME + "api/getLonLatAirport";
    public static  final String URL_LOGIN                   = HOSTNAME + "api/login";
    public static  final String URL_NOTICE                  = HOSTNAME + "api/getNotice";
    public static  final String URL_BOOKING_LOG             = HOSTNAME + "api/bookingLog";
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

    public static  final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 123;
    public static  final int REQUEST_CODE_LOCATION_PERMISSIONS = 234;
    public static  final int REQUEST_CODE_COARSE_LOCATION_PERMISSIONS = 345;
    public static  final int REQUEST_CODE_TELEPHONE_PERMISSIONS = 124;

    public static  final String VEHICLE_PASS_ACTION         = "1";
    public static  final String CAR_TYPE_FROM_ACTION        = "2";
    public static  final String CAR_MADE_TO_ACTION          = "3";
    public static  final String CAR_MODEL_ACTION            = "4";
    public static  final String CAR_SIZE_ACTION             = "7";
    public static  final String VEHICLE_ID_ACTION           = "5";
    public static  final String OWNER_ID_ACTION             = "6";
    public static  final String CAR_NAME_ACTION             = "8";
    public static String token                 = "";


    public static  final int        LOOP_TIME                   = 5*1000;

    public static ArrayList<Phone> listPhone;
    public static ArrayList<String> provinceFrom;

    public static int MAX_DISTANCE                 = 50;
    public static int MIN_CURRENT_DISTANCE                 = 20;
    public static int TIME_BEFORE_AUCTION_LONG                 = 5*60*60*1000;
    public static int TIME_BEFORE_AUCTION_SHORT                 = 1*60*60*1000;

    public static boolean startThread = false;
    public static final int REQUEST_CODE_PICKER = 100;
    // Main Screen Dimension, will be set when app startup
    public static int APP_SCREEN_HEIGHT = 0;
    public static int APP_SCREEN_WIDTH = 0;

}

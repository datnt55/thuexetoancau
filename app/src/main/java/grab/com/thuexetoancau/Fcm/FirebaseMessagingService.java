package grab.com.thuexetoancau.Fcm;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.RemoteMessage;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.activity.PassengerSelectActionActivity;
import grab.com.thuexetoancau.activity.ScheduleTripActivity;
import grab.com.thuexetoancau.model.User;
import grab.com.thuexetoancau.utilities.Defines;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String function = remoteMessage.getData().get("function");
        if (function.equals(Defines.DRIVER_CANCEL_TRIP)) {
            String functionCase = remoteMessage.getData().get("case");
            if (functionCase.equals(Defines.SUCCESS)) {
                Intent intent = new Intent(Defines.BROADCAST_CANCEL_TRIP);
                LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
                broadcastManager.sendBroadcast(intent);
                if (!isAppInForeground(this)) {
                    String bookingId = remoteMessage.getData().get("id_booking");
                    String driverName = remoteMessage.getData().get("driver_name");
                    String driverPhone = remoteMessage.getData().get("driver_phone");
                    responseCancelTripHandle(Integer.valueOf(bookingId), "Tài xế " + driverName + " đã hủy chuyến đi");
                }else {
                    if (!isScreenOn()){
                        String bookingId = remoteMessage.getData().get("id_booking");
                        String driverName = remoteMessage.getData().get("driver_name");
                        String driverPhone = remoteMessage.getData().get("driver_phone");
                        responseCancelTripHandle(Integer.valueOf(bookingId), "Tài xế " + driverName + " đã hủy chuyến đi");
                    }
                }
            }
        } else if (function.equals(Defines.BOOKING_GRAB)) {
            String bookingCase = remoteMessage.getData().get("case");
            if (bookingCase.equals(Defines.NOT_FOUND_DRIVER)) {
                Intent intent = new Intent(Defines.BROADCAST_NOT_FOUND_DRIVER);
                LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
                broadcastManager.sendBroadcast(intent);
                if (!isAppInForeground(this)) {
                    String bookingId = remoteMessage.getData().get("id_booking");
                    notFoundDriverNotify(Integer.valueOf(bookingId), "Rất tiếc, chúng tôi không tìm thấy xe cho bạn");
                }else {
                    if (!isScreenOn()){
                        String bookingId = remoteMessage.getData().get("id_booking");
                        notFoundDriverNotify(Integer.valueOf(bookingId), "Rất tiếc, chúng tôi không tìm thấy xe cho bạn");
                    }
                }
            }
        } else if (function.equals(Defines.RECEIVED_TRIP)) {
            String receiveCase = remoteMessage.getData().get("case");
            if (receiveCase.equals(Defines.SUCCESS)) {
                String dName = remoteMessage.getData().get("driver_name");
                String dPhone = remoteMessage.getData().get("driver_phone");
                String bookingId = remoteMessage.getData().get("id_booking");
                String carType = remoteMessage.getData().get("car_type");
                String carNumber = remoteMessage.getData().get("driver_car_number");
                String carModel = remoteMessage.getData().get("driver_car_model");
                User user = new User(0, dName, dPhone, "", "");
                user.setLicense(carNumber);
                user.setCarModel(carModel);
                if (Integer.valueOf(carType) == 0) {
                    foundDriver(user, Integer.valueOf(bookingId), Integer.valueOf(carType), "Tài xế " + dName + " đã chấp nhận chuyến đi của bạn");
                }else {
                    final Intent intent = new Intent(Defines.BROADCAST_RECEIVED_TRIP);
                    final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
                    intent.putExtra(Defines.BUNDLE_DRIVER, user);
                    intent.putExtra(Defines.BUNDLE_TRIP, Integer.valueOf(bookingId));
                    intent.putExtra(Defines.BUNDLE_TRIP_TYPE, Integer.valueOf(carType));
                    broadcastManager.sendBroadcast(intent);
                    if (!isAppInForeground(this)) {
                        foundDriver(user, Integer.valueOf(bookingId), Integer.valueOf(carType), "Tài xế " + dName + " đang trên đường đón bạn");
                    }else {
                        if (!isScreenOn())
                            foundDriver(user, Integer.valueOf(bookingId), Integer.valueOf(carType), "Tài xế " + dName + " đang trên đường đón bạn");
                    }
                }
            }
        } else if (function.equals(Defines.CONFIRM_TRIP)) {
            String receiveCase = remoteMessage.getData().get("case");
            if (receiveCase.equals(Defines.RATE_TRIP)) {
                String bookingId = remoteMessage.getData().get("id_booking");
                String driverName = remoteMessage.getData().get("driver_name");
                String price = remoteMessage.getData().get("real_price");
                final Intent intent = new Intent(Defines.BROADCAST_CONFFIRM_TRIP);
                final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
                intent.putExtra(Defines.BUNDLE_TRIP, Integer.valueOf(bookingId));
                intent.putExtra(Defines.BUNDLE_DRIVER_NAME, driverName);
 //               intent.putExtra(Defines.BUNDLE_PRICE, Integer.valueOf(price));
                broadcastManager.sendBroadcast(intent);
                if (!isAppInForeground(this)) {
                    confrimTrip(Integer.valueOf(bookingId),driverName);
                }else {
                    if (!isScreenOn())
                        confrimTrip(Integer.valueOf(bookingId),driverName);
                }
            }
        }else if (function.equals(Defines.DRIVER_CATCH_TRIP)) {
            String receiveCase = remoteMessage.getData().get("case");
            if (receiveCase.equals(Defines.SUCCESS)) {
                String bookingId = remoteMessage.getData().get("id_booking");
                final Intent intent = new Intent(Defines.BROADCAST_CATCH_TRIP);
                final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
                //intent.putExtra(Defines.BUNDLE_TRIP, Integer.valueOf(bookingId));
                //intent.putExtra(Defines.BUNDLE_DRIVER_NAME, driverName);
                broadcastManager.sendBroadcast(intent);
                if (!isAppInForeground(this)) {
                    catchTrip(Integer.valueOf(bookingId));
                }else {
                    if (!isScreenOn())
                        catchTrip(Integer.valueOf(bookingId));
                }
            }
        }else if (function.equals(Defines.DRIVER_AUTO_POST_GPS)) {
            String receiveCase = remoteMessage.getData().get("case");
            if (receiveCase.equals(Defines.TO_CUSTOMER)) {
                String driverLat = remoteMessage.getData().get("driver_lat");
                String driverLon = remoteMessage.getData().get("driver_lon");

                final Intent intent = new Intent(Defines.BROADCAST_AUTO_GPS);
                final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
                intent.putExtra(Defines.BUNDLE_LAT, Double.valueOf(driverLat));
                intent.putExtra(Defines.BUNDLE_LON, Double.valueOf(driverLon));
                broadcastManager.sendBroadcast(intent);
            }
        }
    }

    private void responseCancelTripHandle(int bookingId, String message) {
        turnOnScreen();
        Intent intent = new Intent(this, PassengerSelectActionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("Thuê xe toàn cầu driver")
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1, 1, 1});


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(Defines.NOTIFY_TAG,bookingId, builder.build());
    }

    private void confrimTrip(int bookingId, String driverName) {
        turnOnScreen();
        Intent intent = new Intent(this, PassengerSelectActionActivity.class);
        intent.putExtra(Defines.BUNDLE_CONFIRM_TRIP, true);
        intent.putExtra(Defines.BUNDLE_TRIP_ID, bookingId);
        intent.putExtra(Defines.BUNDLE_DRIVER_NAME, driverName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("Thuê xe toàn cầu")
                .setContentText("Bạn đã hoàn thành chuyến đi")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{0, 100, 200, 300 });
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(Defines.NOTIFY_TAG, bookingId, builder.build());
    }

    private void foundDriver(User user, int bookingId,int carType, String message) {
        turnOnScreen();
        Intent intent;
        if (carType == 1 )
            intent = new Intent(this, PassengerSelectActionActivity.class);
        else
            intent = new Intent(this, ScheduleTripActivity.class);
        intent.putExtra(Defines.BUNDLE_FOUND_DRIVER, true);
        intent.putExtra(Defines.BUNDLE_TRIP_ID, bookingId);
        intent.putExtra(Defines.BUNDLE_DRIVER, user);
        intent.putExtra(Defines.BUNDLE_TRIP_TYPE, carType);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("Thuê xe toàn cầu")
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{ 0, 100, 200, 300 });
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(Defines.NOTIFY_TAG, bookingId, builder.build());
    }

    private void notFoundDriverNotify(int bookingId, String message) {
        turnOnScreen();
        Intent intent = new Intent(this, PassengerSelectActionActivity.class);
        intent.putExtra(Defines.BUNDLE_NOT_FOUND_DRIVER, true);
        intent.putExtra(Defines.BUNDLE_TRIP_ID, bookingId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("Thuê xe toàn cầu")
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{1, 1, 1});
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(Defines.NOTIFY_TAG, bookingId, builder.build());
    }

    private void catchTrip(int bookingId){
        turnOnScreen();
        Intent intent = new Intent(this, PassengerSelectActionActivity.class);
        intent.putExtra(Defines.BUNDLE_CATCH_TRIP, true);
        intent.putExtra(Defines.BUNDLE_TRIP_ID, bookingId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("Thuê xe toàn cầu")
                .setContentText("Tài xế đã đón bạn. Chuyến đã sẵn sàng để bắt đầu")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{1, 1, 1});
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(Defines.NOTIFY_TAG, bookingId, builder.build());
    }

    private boolean isAppInForeground(Context context) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
            String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();
            return foregroundTaskPackageName.toLowerCase().equals(context.getPackageName().toLowerCase());
        } else {
            ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
            ActivityManager.getMyMemoryState(appProcessInfo);
            if (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE) {
                return true;
            }

            KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            // App is foreground, but screen is locked, so show notification
            return km.inKeyguardRestrictedInputMode();
        }
    }

    private boolean isScreenOn(){
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if (!isScreenOn)
            return false;
        return true;
    }

    private void turnOnScreen(){
        //Turn on screen
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if (isScreenOn == false) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
            wl.acquire(10000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock");
            wl_cpu.acquire(10000);

        }

    }
}

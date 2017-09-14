package grab.com.thuexetoancau.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by DatNT on 8/2/2017.
 */

public class Trip implements Serializable{
    private int id;
    private int userId;
    private ArrayList<Position> listStopPoints;
    private int carSize;
    private int tripType;
    private int distance;
    private long price;
    private String startTime;
    private String endTime;
    private int customerType;
    private String customerName;
    private String customerPhone;
    private String guestName;
    private String guestPhone;
    private String note;
    private String bookingTime;
    private String bookingDateId;
    private int statusBooking;
    private int statusPayment;
    private String cancelReason;
    private int driverId;
    private int carType;
    private int realDistance;
    private long realPrice;
    private String driverName;
    private String driverPhone;
    private String driverCarNumber;
    private boolean isCar;
    private int status;

    public Trip() {
    }

    public Trip(int id, int userId, ArrayList<Position> listStopPoints, int carSize, int tripType, int distance, long price, String startTime, String endTime, int customerType, String customerName, String customerPhone, String guestName, String guestPhone, String note) {
        this.id = id;
        this.userId = userId;
        this.listStopPoints = listStopPoints;
        this.carSize = carSize;
        this.tripType = tripType;
        this.distance = distance;
        this.price = price;
        this.startTime = startTime;
        this.endTime = endTime;
        this.customerType = customerType;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.guestName = guestName;
        this.guestPhone = guestPhone;
        this.note = note;
    }

    public Trip(int userId, String customerName, String customerPhone, ArrayList<Position> listStopPoints, int tripType, int distance, int carSize, long price) {
        this.userId = userId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.listStopPoints = listStopPoints;
        this.tripType = tripType;
        this.distance = distance;
        this.price = price;
        this.carSize = carSize;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Position> getListStopPoints() {
        return listStopPoints;
    }

    public void setListStopPoints(ArrayList<Position> listStopPoints) {
        this.listStopPoints = listStopPoints;
    }

    public int getTripType() {
        return tripType;
    }

    public void setTripType(int tripType) {
        this.tripType = tripType;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getCustomerType() {
        return customerType;
    }

    public void setCustomerType(int customerType) {
        this.customerType = customerType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCarSize() {
        return carSize;
    }

    public void setCarSize(int carSize) {
        this.carSize = carSize;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestPhone() {
        return guestPhone;
    }

    public void setGuestPhone(String guestPhone) {
        this.guestPhone = guestPhone;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getBookingDateId() {
        return bookingDateId;
    }

    public void setBookingDateId(String bookingDateId) {
        this.bookingDateId = bookingDateId;
    }

    public int getStatusBooking() {
        return statusBooking;
    }

    public void setStatusBooking(int statusBooking) {
        this.statusBooking = statusBooking;
    }

    public int getStatusPayment() {
        return statusPayment;
    }

    public void setStatusPayment(int statusPayment) {
        this.statusPayment = statusPayment;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public int getCarType() {
        return carType;
    }

    public void setCarType(int carType) {
        this.carType = carType;
    }

    public int getRealDistance() {
        return realDistance;
    }

    public void setRealDistance(int realDistance) {
        this.realDistance = realDistance;
    }

    public long getRealPrice() {
        return realPrice;
    }

    public void setRealPrice(long realPrice) {
        this.realPrice = realPrice;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getDriverCarNumber() {
        return driverCarNumber;
    }

    public void setDriverCarNumber(String driverCarNumber) {
        this.driverCarNumber = driverCarNumber;
    }

    public boolean isCar() {
        return isCar;
    }

    public void setCar(boolean car) {
        isCar = car;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

package grab.com.thuexetoancau.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by DatNT on 8/2/2017.
 */

public class Trip implements Serializable{
    int userId;
    private ArrayList<Position> listStopPoints;
    private int carSize;
    private int tripType;
    private int distance;
    private int price;
    private String startTime;
    private String endTime;
    private int customerType;
    private String customerName;
    private String customerPhone;
    private String guestName;
    private String guestPhone;
    private String note;


    public Trip(int userId, String customerName, String customerPhone, ArrayList<Position> listStopPoints, int tripType, int distance,int carSize,  int price) {
        this.userId = userId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.listStopPoints = listStopPoints;
        this.tripType = tripType;
        this.distance = distance;
        this.price = price;
        this.carSize = carSize;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
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
}

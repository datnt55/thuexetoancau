package grab.com.thuexetoancau.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by DatNT on 11/10/2016.
 */

public class Booking {
    private int id;
    private String carFrom;
    private String carTo;
    private String carType;
    private String carHireType;
    private String carWhoHire;
    private String fromDate;
    private String toDate;
    private String dateBook;
    private int bookPrice;
    private int bookPriceMax;
    private int currentPrice;
    private String timeToReduce;
    private LatLng localtionFrom;
    private LatLng localtionTo;
    private double distance;
    public Booking() {
    }

    public Booking(int id, String carFrom, String carTo, String carType, String carHireType, String carWhoHire, String fromDate, String fromTo, String dateBook, int bookPrice, int bookPriceMax, int currentPrice, String timeToReduce, LatLng localtionFrom, LatLng localtionTo, double distance) {
        this.id = id;
        this.carFrom = carFrom;
        this.carTo = carTo;
        this.carType = carType;
        this.carHireType = carHireType;
        this.carWhoHire = carWhoHire;
        this.fromDate = fromDate;
        this.toDate = fromTo;
        this.dateBook = dateBook;
        this.bookPrice = bookPrice;
        this.bookPriceMax = bookPriceMax;
        this.timeToReduce = timeToReduce;
        this.localtionFrom = localtionFrom;
        this.localtionTo = localtionTo;
        this.distance = distance;
        this.currentPrice = currentPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getCarFrom() {
        return carFrom;
    }

    public void setCarFrom(String carFrom) {
        this.carFrom = carFrom;
    }

    public String getCarTo() {
        return carTo;
    }

    public void setCarTo(String carTo) {
        this.carTo = carTo;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getCarHireType() {
        return carHireType;
    }

    public void setCarHireType(String carHireType) {
        this.carHireType = carHireType;
    }

    public String getCarWhoHire() {
        return carWhoHire;
    }

    public void setCarWhoHire(String carWhoHire) {
        this.carWhoHire = carWhoHire;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getDateBook() {
        return dateBook;
    }

    public void setDateBook(String dateBook) {
        this.dateBook = dateBook;
    }

    public int getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(int bookPrice) {
        this.bookPrice = bookPrice;
    }

    public int getBookPriceMax() {
        return bookPriceMax;
    }

    public void setBookPriceMax(int bookPriceMax) {
        this.bookPriceMax = bookPriceMax;
    }

    public String getTimeToReduce() {
        return timeToReduce;
    }

    public void setTimeToReduce(String timeToReduce) {
        this.timeToReduce = timeToReduce;
    }

    public LatLng getLocaltionFrom() {
        return localtionFrom;
    }

    public void setLocaltionFrom(LatLng localtionFrom) {
        this.localtionFrom = localtionFrom;
    }

    public LatLng getLocaltionTo() {
        return localtionTo;
    }

    public void setLocaltionTo(LatLng localtionTo) {
        this.localtionTo = localtionTo;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(int currentPrice) {
        this.currentPrice = currentPrice;
    }
}

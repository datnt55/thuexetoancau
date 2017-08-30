package grab.com.thuexetoancau.model;

import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by DatNT on 7/18/2017.
 */

public class Position implements Serializable{
    private String placeId;
    private String primaryText;
    private String secondText;
    private double latitude;
    private double longitude;
    private String fullPlace;

    public Position() {
    }

    public Position(String placeId, String primaryText, String secondText) {
        this.placeId = placeId;
        this.primaryText = primaryText;
        this.secondText = secondText;
        this.fullPlace = primaryText +", "+secondText;
    }

    public Position(String fullPath, LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
        this.fullPlace = fullPath;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPrimaryText() {
        return primaryText;
    }

    public void setPrimaryText(String primaryText) {
        this.primaryText = primaryText;
    }

    public String getSecondText() {
        return secondText;
    }

    public void setSecondText(String secondText) {
        this.secondText = secondText;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public void setLatLng(LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }

    public String getFullPlace() {
        return fullPlace;
    }

    public void setFullPlace(String fullPlace) {
        this.fullPlace = fullPlace;
    }

    public String getLatLngToString() {
        return latitude + ","+ longitude;
    }
}

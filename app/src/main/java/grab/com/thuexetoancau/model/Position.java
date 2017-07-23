package grab.com.thuexetoancau.model;

import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by DatNT on 7/18/2017.
 */

public class Position {
    private String placeId;
    private String primaryText;
    private String secondText;
    private LatLng latLng;
    public Position(String placeId, String primaryText, String secondText) {
        this.placeId = placeId;
        this.primaryText = primaryText;
        this.secondText = secondText;
    }

    public Position(LatLng latLng) {
        this.latLng = latLng;
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
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}

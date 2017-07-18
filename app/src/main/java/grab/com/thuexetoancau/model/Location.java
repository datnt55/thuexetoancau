package grab.com.thuexetoancau.model;

import android.widget.TextView;

/**
 * Created by DatNT on 7/18/2017.
 */

public class Location {
    private String placeId;
    private String primaryText;
    private String secondText;

    public Location(String placeId, String primaryText, String secondText) {
        this.placeId = placeId;
        this.primaryText = primaryText;
        this.secondText = secondText;
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
}

package grab.com.thuexetoancau.model;

import android.widget.TextView;

/**
 * Created by DatNT on 7/18/2017.
 */

public class Location {
    private String txtLocation;
    private String txtAddress;

    public Location(String txtLocation, String txtAddress) {
        this.txtLocation = txtLocation;
        this.txtAddress = txtAddress;
    }

    public String getTxtLocation() {
        return txtLocation;
    }

    public void setTxtLocation(String txtLocation) {
        this.txtLocation = txtLocation;
    }

    public String getTxtAddress() {
        return txtAddress;
    }

    public void setTxtAddress(String txtAddress) {
        this.txtAddress = txtAddress;
    }
}

/*
package grab.com.thuexetoancau.thread;

import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import static grab.com.thuexetoancau.DirectionFinder.GoogleMapUtis.bearingBetweenLatLngs;

*/
/**
 * Created by DatNT on 8/3/2017.
 *//*


public class DriverMotionThread implements Runnable {

    private static final int ANIMATE_SPEEED = 1500;
    private static final int ANIMATE_SPEEED_TURN = 1000;
    private static final int BEARING_OFFSET = 20;
    private final Interpolator interpolator = new LinearInterpolator();
    private GoogleMap mMap;
    int currentIndex = 0;

    float tilt = 90;
    float zoom = 15.5f;
    boolean upward=true;

    long start = SystemClock.uptimeMillis();

    LatLng endLatLng = null;
    LatLng beginLatLng = null;

    private Marker trackingMarker;


    public void initialize() {
        setupCameraPositionForMovement(beginLatLng, endLatLng);

    }

    private void setupCameraPositionForMovement(LatLng markerPos, LatLng secondPos) {

        float bearing = bearingBetweenLatLngs(markerPos,secondPos);

        trackingMarker = mMap.addMarker(new MarkerOptions().position(markerPos)
                .title("title")
                .snippet("snippet"));

        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(markerPos)
                        .bearing(bearing + BEARING_OFFSET)
                        .tilt(90)
                        .zoom(mMap.getCameraPosition().zoom >=16 ? mMap.getCameraPosition().zoom : 16)
                        .build();

        mMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(cameraPosition),
                ANIMATE_SPEEED_TURN,
                new GoogleMap.CancelableCallback() {

                    @Override
                    public void onFinish() {
                        System.out.println("finished camera");
                    }

                    @Override
                    public void onCancel() {
                        System.out.println("cancelling camera");
                    }
                }
        );
    }


    @Override
    public void run() {

        long elapsed = SystemClock.uptimeMillis() - start;
        double t = interpolator.getInterpolation((float)elapsed/ANIMATE_SPEEED);

        LatLng intermediatePosition = SphericalUtil.interpolate(beginLatLng, endLatLng, t);

        trackingMarker.setPosition(newPosition);

        // It's not possible to move the marker + center it through a cameraposition update while another camerapostioning was already happening.
        //navigateToPoint(newPosition,tilt,bearing,currentZoom,false);
        //navigateToPoint(newPosition,false);

        if (t< 1) {
            mHandler.postDelayed(this, 16);
        } else {

            //System.out.println("Move to next marker.... current = " + currentIndex + " and size = " + markers.size());
            // imagine 5 elements -  0|1|2|3|4 currentindex must be smaller than 4
            if (currentIndex<markers.size()-2) {

                currentIndex++;

                endLatLng = getEndLatLng();
                beginLatLng = getBeginLatLng();


                start = SystemClock.uptimeMillis();

                LatLng begin = getBeginLatLng();
                LatLng end = getEndLatLng();

                float bearingL = bearingBetweenLatLngs(begin, end);

                highLightMarker(currentIndex);

                CameraPosition cameraPosition =
                        new CameraPosition.Builder()
                                .target(end) // changed this...
                                .bearing(bearingL  + BEARING_OFFSET)
                                .tilt(tilt)
                                .zoom(mMap.getCameraPosition().zoom)
                                .build();


                mMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(cameraPosition),
                        ANIMATE_SPEEED_TURN,
                        null
                );

                start = SystemClock.uptimeMillis();
                mHandler.postDelayed(animator, 16);

            } else {
                currentIndex++;
                highLightMarker(currentIndex);
                stopAnimation();
            }

        }
    }
}*/

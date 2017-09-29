package grab.com.thuexetoancau.utilities;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import grab.com.thuexetoancau.R;

/**
 * Created by DatNT on 8/31/2017.
 */

public class MarkerAnimation {
    private  LatLng prevLatLng, currLatLng, nextLatLng;
    private Context mContext;

    public MarkerAnimation(Context mContext) {
        this.mContext = mContext;
    }

    public  void animateMarker(final Location destination, final Marker marker) {
        if (marker != null) {
            currLatLng = marker.getPosition();
            nextLatLng = new LatLng(destination.getLatitude(), destination.getLongitude());

            final float startRotation = marker.getRotation();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000); // duration 1 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, currLatLng, nextLatLng);
                        marker.setPosition(newPosition);
                        float angle = computeRotation(v, startRotation, destination.getBearing());
                     /*   if (angle > 180){
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.car));
                            marker.setRotation(angle-180);
                        }else if (angle <= 180 && angle >= 160) {
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.car));
                        }else*/
                            marker.setRotation(angle);
                    } catch (Exception ex) {
                        // I don't care atm..
                    }
                }
            });
            valueAnimator.start();
            float beginAngle;
            if (prevLatLng != null)
                beginAngle = (float)(180 * getAngle(prevLatLng, currLatLng) / Math.PI);
            else
                beginAngle = (float)(180 * getAngle(currLatLng, nextLatLng) / Math.PI);
            float endAngle = (float)(180 * getAngle(currLatLng, nextLatLng) / Math.PI);
            animateCarTurn(marker,beginAngle,endAngle,1000);
        }
    }

    private double getAngle(LatLng beginLatLng, LatLng endLatLng) {
        double f1 = Math.PI * beginLatLng.latitude / 180;
        double f2 = Math.PI * endLatLng.latitude / 180;
        double dl = Math.PI * (endLatLng.longitude - beginLatLng.longitude) / 180;
        return Math.atan2(Math.sin(dl) * Math.cos(f2) , Math.cos(f1) * Math.sin(f2) - Math.sin(f1) * Math.cos(f2) * Math.cos(dl));
    }

    private void animateCarTurn(final Marker marker, final float startAngle, final float endAngle, final long duration) {
        final Bitmap mMarkerIcon;
        mMarkerIcon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.car);

        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final Interpolator interpolator = new LinearInterpolator();

        final float dAndgle = endAngle - startAngle;

        Matrix matrix = new Matrix();
        matrix.postRotate(startAngle);
        Bitmap rotatedBitmap = Bitmap.createBitmap(mMarkerIcon, 0, 0, mMarkerIcon.getWidth(), mMarkerIcon.getHeight(), matrix, true);
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(rotatedBitmap));

        handler.post(new Runnable() {
            @Override
            public void run() {

                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                Matrix m = new Matrix();
                m.postRotate(startAngle + dAndgle * t);
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(mMarkerIcon, 0, 0, mMarkerIcon.getWidth(), mMarkerIcon.getHeight(), m, true)));

                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }else{
                    prevLatLng = marker.getPosition();
                }
            }
        });
    }

    /**
     * Method to compute rotation (or marker's bearing) for specified fraction of animation.
     * Marker is rotated in the direction which is closer from start to end.
     *
     * @param fraction  Fraction of animation completed b/w start and end location
     * @param start 	Rotation (or Bearing) for animation's start location
     * @param end 		Rotation (or Bearing) for animation's end location
     **/
    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }

    private interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolator {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }

    }
}

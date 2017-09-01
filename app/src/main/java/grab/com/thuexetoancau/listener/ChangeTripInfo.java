package grab.com.thuexetoancau.listener;

/**
 * Created by DatNT on 8/3/2017.
 */

public interface ChangeTripInfo {
    void onChangeDistance(int distance);
    void onChangeTrip(int tripType);
    void onResetTrip();
}

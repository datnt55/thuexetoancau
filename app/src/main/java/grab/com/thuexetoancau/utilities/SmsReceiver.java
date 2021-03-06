package grab.com.thuexetoancau.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import grab.com.thuexetoancau.activity.RegisterActivity;

/**
 * Created by DatNT on 8/21/2017.
 */

public class SmsReceiver extends BroadcastReceiver {

    //interface
    private static SmsListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();

        Object[] pdus = (Object[]) data.get("pdus");

        for (int i = 0; i < pdus.length; i++) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

            String sender = smsMessage.getDisplayOriginatingAddress();
            //Check the sender to filter messages which we require to read

            String messageBody = smsMessage.getMessageBody();
            if (messageBody.contains("Your Firebase App verification code is"))
                //Pass the message text to interface
                mListener.messageReceived(messageBody);
        }

    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }

    public interface SmsListener {
        public void messageReceived(String messageText);
    }

}


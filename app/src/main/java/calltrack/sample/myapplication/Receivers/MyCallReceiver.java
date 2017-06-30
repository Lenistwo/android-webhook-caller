package calltrack.sample.myapplication.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.telephony.TelephonyManager;

import java.util.Date;

import calltrack.sample.myapplication.Server.Constants;
import calltrack.sample.myapplication.Server.PrefStore;
import calltrack.sample.myapplication.Server.SyncManager;


public class MyCallReceiver extends BroadcastReceiver {

    //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;  //because the passed incoming is only valid in ringing

    private static Context ctx;
    private boolean onHold;
    private boolean oldRinging;


    @Override
    public void onReceive(Context context, Intent intent) {
        ctx = context;
        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        if (intent == null)
            return;
        if (intent.getAction() == null)
            return;
        if (intent.getExtras() == null)
            return;

        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
            System.out.println("outgoing call " + savedNumber + "   " + getContactName(savedNumber));

        } else {

            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                state = TelephonyManager.CALL_STATE_IDLE;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                state = TelephonyManager.CALL_STATE_RINGING;
            }


            onCallStateChanged(context, state, number);
        }
    }

    //Derived classes should override these to respond to specific events of interest
    protected void onIncomingCallStarted(Context ctx, final String number, final Date start, int state) {
        if (new PrefStore(ctx).getBoolean("oldringing")) {
            new PrefStore(ctx).saveBoolean("onhold",true);
            new SyncManager().send(new PrefStore(ctx).getString(Constants.INBOUND_URL), number, getContactName(number), "2");
        } else {
            new PrefStore(ctx).saveBoolean("oldringing",true);
            new SyncManager().send(new PrefStore(ctx).getString(Constants.INBOUND_URL), number, getContactName(number), "1");
        }
        System.out.println("call ringing called");
    }


    private void onIncomingCallPickedup(Context context, String number, Date callStartTime) {
        if(new PrefStore(ctx).getBoolean("onhold")) {
            new PrefStore(ctx).saveBoolean("onhold",false);
            new SyncManager().send(new PrefStore(ctx).getString(Constants.PICKEDUP_URL), number, getContactName(number), "2");
        }else {
            new SyncManager().send(new PrefStore(ctx).getString(Constants.PICKEDUP_URL), number, getContactName(number), "1");

        }
        System.out.println("call pickup");
    }

    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        new PrefStore(ctx).saveBoolean("onhold",false);
            new PrefStore(ctx).saveBoolean("oldringing",false);
            onHold=false;
            new SyncManager().send(new PrefStore(ctx).getString(Constants.CALLEDND_URL), number, getContactName(number), "1");
       System.out.println("call end called");
    }

    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
    }


    protected void onMissedCall(Context ctx, String number, Date start) {
    }


    public void onCallStateChanged(Context context, int state, String number) {
        if (lastState == state) {
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                onIncomingCallStarted(context, number, callStartTime, state);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    // isIncoming = false;
                    callStartTime = new Date();
                    onIncomingCallPickedup(context, number, callStartTime);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
                    onMissedCall(context, savedNumber, callStartTime);
                } else if (isIncoming) {
                    onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                } else {
                    onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                }
                break;
        }
        lastState = state;
    }



    public String getContactName(final String phoneNumber) {
        Uri uri;
        String[] projection;
        Uri mBaseUri = Contacts.Phones.CONTENT_FILTER_URL;
        projection = new String[]{android.provider.Contacts.People.NAME};
        try {
            Class<?> c = Class.forName("android.provider.ContactsContract$PhoneLookup");
            mBaseUri = (Uri) c.getField("CONTENT_FILTER_URI").get(mBaseUri);
            projection = new String[]{"display_name"};
        } catch (Exception e) {
        }
        uri = Uri.withAppendedPath(mBaseUri, Uri.encode(phoneNumber));
        Cursor cursor = ctx.getContentResolver().query(uri, projection, null, null, null);

        String contactName = "Unknown";

        if (cursor.moveToFirst()) {
            contactName = cursor.getString(0);
        }

        cursor.close();
        cursor = null;

        return contactName;
    }


}
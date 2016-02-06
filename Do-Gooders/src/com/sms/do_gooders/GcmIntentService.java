package com.sms.do_gooders;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
    
	private int MSG_TYPE= -1;
	private final int MSG_REGISTRATION_COMPLETE = 0;
	private final int MSG_REGISTRATION_DENIED = 1;
	private final int MSG_DATA_ASSIGNED = 2;
	private final int MSG_DATA_REVOKED = 3;
	
	//Project-Number
	private static final String PROJECT_ID = "1094217396255";
	
    // wakelock
    private static final String WAKELOCK_KEY = "GCM_LIB";
	private static PowerManager.WakeLock sWakeLock;
	
	private static final boolean ISDEBUG = true;
	
    // Java lock used to synchronize access to sWakelock
    private static final Object LOCK = GcmIntentService.class;
		
	int dot = 200;      // Length of a Morse Code "dot" in milliseconds
	int dash = 500;     // Length of a Morse Code "dash" in milliseconds
	int short_gap = 200;    // Length of Gap Between dots/dashes
	int medium_gap = 500;   // Length of Gap Between Letters
	int long_gap = 1000;    // Length of Gap Between Words
	long[] pattern = {
	    0,  // Start immediately
	    dot, short_gap, dot, short_gap, dot,    // s
	    medium_gap,
	    dash, short_gap, dash, short_gap, dash, // o
	    medium_gap,
	    dot, short_gap, dot, short_gap, dot,    // s
	    long_gap
	};
	
	public static final String TAG = "GCM";
	public static final int NOTIFICATION_ID = 1;    
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super(PROJECT_ID);
        if(ISDEBUG)
        	Log.d("Do Good","GCMIntentService......................");
    }
  

    @Override
    protected void onHandleIntent(Intent intent) {
 
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        if(ISDEBUG)
        	Log.d("Do Good", "GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType) is "+GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType));
        if (!extras.isEmpty())
        { 
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType))
            {
            	//sendNotification("Send error: " + extras.toString());
            } 
            else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType))
            {
                // sendNotification("Deleted messages on server: " + extras.toString());
            	// If it's a regular GCM message, do some work.
            } 
            else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
            {
//                Log.d("Do-Gooders", "Completed work @ " + SystemClock.elapsedRealtime());
//                Log.d("Do-Gooders","data is  "+extras);
//                Log.d("Do-Gooders","JSON  data is ..... "+extras.get("message").toString());
//                sendNotification(extras.get("message").toString(),extras.get("GUID").toString(),extras.get("UserId").toString());
//                
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
        
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg,String GUID,String UserId ) {
    	Log.d("Do Good","notification is ...........................");
        PendingIntent contentIntent = null; 
        
        Intent notificationIntent = null;
        	
        Log.d("Do Good","msg is .. "+msg);
       // String[] decodeMsg = msg.split("-");

    	String message = "";
        Log.d("Do Good","MSG_TYPE is  "+MSG_TYPE);

		message = msg;
		
		notificationIntent = new Intent(this, SplashScreen.class);
    	notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
    	
    	mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        
    	Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    	
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle(getResources().getString(R.string.app_name))
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(message))
        .setPriority(Notification.PRIORITY_HIGH)
        .setContentText(message)
        .setVibrate(pattern)
        .setSound(alarmSound);
        
        WakeLock screenLock = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(
        	     PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        screenLock.acquire();
        mBuilder.setContentIntent(contentIntent);        
        Notification notification = mBuilder.build();   
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
        							Intent.FLAG_ACTIVITY_SINGLE_TOP |
        							Notification.FLAG_ONGOING_EVENT |
        							Notification.FLAG_AUTO_CANCEL | 
        							Notification.FLAG_SHOW_LIGHTS |
        							Notification.DEFAULT_SOUND);   

      
//        if(LaunchBrowser._Current != null )
//        {
//        	 Log.d("CARMA","LaunchBrowser._Current != null  ");
//        	Intent i = new Intent("com.sms.carma.USER_ACTION");
//        	i.putExtra("push_message", message);
//			i.putExtra("GUID", GUID);
//			i.putExtra("UserName", UserId);
//			i.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES); 
//			i.setFlags(32);
//			sendBroadcast(i);
//	        screenLock.release();
//			return;
//        }
//        else
//        {
//        	Log.d("CARMA","LaunchBrowser._Current == null  ");
//        	SharedPreferencesClass.setNotificationReceived("true");
//        	SharedPreferencesClass.setNotificationMessage(message);
//        	SharedPreferencesClass.setNotificationGUID(GUID);
//        	SharedPreferencesClass.saveUserName(UserId);
//        	mNotificationManager.notify(NOTIFICATION_ID, notification);
//        }
        
        if((MSG_TYPE == MSG_DATA_ASSIGNED || MSG_TYPE == MSG_DATA_REVOKED))
        {
			Intent localBroadcastIntent = new Intent("RefreshList");
			if(MSG_TYPE == MSG_DATA_REVOKED)
			{
//				localBroadcastIntent.putExtra("TASKID",decodeMsg[1]);
			}else{
				localBroadcastIntent.putExtra("TASKID","");
			}
			LocalBroadcastManager.getInstance(this).sendBroadcast(localBroadcastIntent);
		}
    }
}

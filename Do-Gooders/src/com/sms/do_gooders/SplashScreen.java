package com.sms.do_gooders;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;


public class SplashScreen extends Activity {

    private static int SPLASH_TIME_OUT = 2000;
    String deviceToken = "";
	static final String SENDER_ID = "824227325298";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ActionBar actionBar = getActionBar();
	    actionBar.setDisplayOptions(actionBar.getDisplayOptions()
	            | ActionBar.DISPLAY_SHOW_CUSTOM);
	    actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.navigation));
	    actionBar.setDisplayShowHomeEnabled(true);

        new Handler().postDelayed(
        		new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(!NetworkSettings.isOnline(getApplicationContext()))
						{
							AlertDialog.Builder builder1 = new AlertDialog.Builder(SplashScreen.this);
							TextView title = new TextView(SplashScreen.this);
							title.setText("Error");
							title.setPadding(10, 10, 10, 10);
							title.setGravity(Gravity.CENTER); // this is required to bring it to center.
							title.setTextSize(22);
							builder1.setCustomTitle(title);

							TextView message = new TextView(SplashScreen.this);
							message.setText("No internet Connection. Please check your network connections. ");
							message.setPadding(10, 10, 10, 10);
							message.setGravity(Gravity.CENTER); // this is required to bring it to center.
							message.setTextSize(22);

							builder1.setView(message);
			                    builder1.setCancelable(false);
			                    builder1.setPositiveButton("WIFI Settings", 
			                    		new DialogInterface.OnClickListener() {
	                                public void onClick(DialogInterface dialog, int id) {
	                                    Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
	                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
	                                    startActivityForResult(intent, 0);
	                                }
	                            });
			                    builder1.setPositiveButton("Cancel", 
			                    		new DialogInterface.OnClickListener() {
	                                public void onClick(DialogInterface dialog, int id) {
	                                    finish();
	                                }
	                            });
			                    builder1.show();
						}
						else
						{
							//Check if This is first time or not.
							//If first time go to Profile page else go to Home Page
							
							 deviceToken = GCMHelper.getRegistrationId(getApplicationContext());
							 Log.d("Do Good","Token is  "+deviceToken);
		               	   	 if (deviceToken.isEmpty()) {
		               	   		 	registerInBG();
		               	   	 }
		               	   	 else {
		               	   		 if(DBClass.isProfilePresent()) {
		               	   			 Intent intent = new Intent(SplashScreen.this,HomeScreen.class);
		               	   			 startActivity(intent);
		               	   			 finish();
		               	   		 }
		               	   		 else {
									Intent intent = new Intent(SplashScreen.this,ProfileScreen.class);
									intent.putExtra("isInitialSetup", true);
									startActivity(intent);
									finish();
								}
		               	   	}
							
							
						}
						
					}
				}
        		, SPLASH_TIME_OUT);
    
      //Font
      		final Typeface mFont = Typeface.createFromAsset(getAssets(),
      				"fonts/OpenSans-Regular.ttf"); 
      		final ViewGroup mContainer = (ViewGroup) findViewById(android.R.id.content).getRootView();
      		ServerConfig.setAppFont(mContainer, mFont,true);
      		
    }
    void registerInBG() {

	  	  new AsyncTask<Void, Void, Void>()
	  	  {
	  	      @Override
	  	      protected Void doInBackground(Void... params)
	  	      {
	  	    	  //Toast.makeText(getApplicationContext(), "About to register...", Toast.LENGTH_LONG).show();
	  	    	  deviceToken = GCMHelper.register(SplashScreen.this, SENDER_ID);
	  	    	 // DBClass.saveDeviceToken("TEST_DEMO");
	  	    	  Log.d("Do Good","DeviceToken is "+deviceToken);
	  	    	  return null;
	  	      }
	  	      protected void onPostExecute(Void result) {
	  	    	if(DBClass.isProfilePresent()) {
					Intent intent = new Intent(SplashScreen.this, HomeScreen.class);
                    startActivity(intent);
                    finish();
				}
				else {
					Intent intent = new Intent(SplashScreen.this,ProfileScreen.class);
					intent.putExtra("isInitialSetup", true);
					startActivity(intent);
                    finish();
				}
	  	      };
	  	  }.execute(null, null, null);
	 }

}

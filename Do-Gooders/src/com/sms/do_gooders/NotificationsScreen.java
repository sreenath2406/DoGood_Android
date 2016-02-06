package com.sms.do_gooders;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

public class NotificationsScreen extends Activity {
	
	ListView itemsList;
	Context context;
	ArrayList<NotifiedItemsList> notifiedItems;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notifications_screen);
		
		
		context = this;
		 ActionBar actionBar = getActionBar();
	    actionBar.setDisplayOptions(actionBar.getDisplayOptions()
	            | ActionBar.DISPLAY_SHOW_CUSTOM);
	    actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.navigation));
	    actionBar.setDisplayShowHomeEnabled(true);

	    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	    
		getNotificationsListFromServer();
		
		//Font
		final Typeface mFont = Typeface.createFromAsset(getAssets(),
				"fonts/OpenSans-Regular.ttf"); 
		final ViewGroup mContainer = (ViewGroup) findViewById(android.R.id.content).getRootView();
		ServerConfig.setAppFont(mContainer, mFont,true);
	}
	public void reloadData()
	{
		Log.d("Do Good","Reload Data .... ");
		itemsList = (ListView)findViewById(R.id.listView);
		if(notifiedItems != null &&  notifiedItems.size() > 0)
			itemsList.setAdapter(new NotificationAdapter(getApplicationContext(),notifiedItems));
		else {
			AlertDialog.Builder builder1 = new AlertDialog.Builder(NotificationsScreen.this);
			TextView title = new TextView(NotificationsScreen.this);
			title.setText("Do Good");
			title.setPadding(10, 10, 10, 10);
			title.setGravity(Gravity.CENTER); // this is required to bring it to center.
			title.setTextSize(22);
			builder1.setCustomTitle(title);

			TextView message = new TextView(NotificationsScreen.this);
			message.setText("You don't have any Notifications to show.");
			message.setPadding(10, 10, 10, 10);
			message.setGravity(Gravity.CENTER); // this is required to bring it to center.
			message.setTextSize(22);

			builder1.setView(message);
            builder1.setCancelable(false);
            builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                  onBackPressed();
                }
            });
            builder1.show();
		}
	}
	
	public void getNotificationsListFromServer() {
		  
		Profile mProfile = Profile.getProfile();
		String url = ServerConfig.SERVER_URL+"GetDonorNotifications?DeviceToken="+DBClass.getDeviceToken()+"&DonorId="+mProfile.profileID;
		Log.d("Do Good", "url is  "+url);
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(7);
		nameValuePair.add(new BasicNameValuePair("DeviceToken",DBClass.getDeviceToken()));
		new JSONFromURL(context, new BgAsyncCallBack() {
			
			@Override
			public void processData(Object data) {
				// TODO Auto-generated method stub
				JSONObject obj = null;
				JSONArray EventsArray = (JSONArray)data;
				if(EventsArray != null && EventsArray.length() > 0) 
				{
					
					try {
						notifiedItems = new ArrayList<NotifiedItemsList>();
						for(int i = 0;i < EventsArray.length() ; i ++) {
							JSONObject object = (JSONObject)EventsArray.get(i);
							NotifiedItemsList notObj = new NotifiedItemsList();
							if(object.getString("RequestStatus").toString().equalsIgnoreCase("Requested")) {
									notObj.ItemId = object.getString("ItemId");
									//itemsDonated[i].PhotoLink = object.getString("ItemPhoto");
									//notObj.ItemPic = "http://www.ml.hospitalityformula.com/wp-content/uploads/2013/01/android-300x300.png";
									notObj.ItemPic = object.getString("ItemPhoto");
									notObj.ItemTitle = object.getString("ItemName");
									notObj.ItemDescription = object.getString("ItemDescription");
									notObj.RequestId = object.getString("RequestId");
									notObj.OrgLogo = object.getString("RecOrgLogo");
									notObj.OrgName = object.getString("RecOrgName");
									notObj.OrgUserId = object.getString("RecOrgId");;
									notObj.OrgUserName = object.getString("FullName");
									notifiedItems.add(notObj);
									notObj = null;
							}
						}
						reloadData();
					} 
					catch (Exception e) {
						// TODO Auto-generated catch block
						Log.d("Go-Dooders","Exception in reading data.");
						e.printStackTrace();
					}
				}
				else
				{
					AlertDialog.Builder builder1 = new AlertDialog.Builder(NotificationsScreen.this);
					TextView title = new TextView(NotificationsScreen.this);
					title.setText("Do Good");
					title.setPadding(10, 10, 10, 10);
					title.setGravity(Gravity.CENTER); // this is required to bring it to center.
					title.setTextSize(22);
					builder1.setCustomTitle(title);

					TextView message = new TextView(NotificationsScreen.this);
					message.setText("You don't have any Notifications to show.");
					message.setPadding(10, 10, 10, 10);
					message.setGravity(Gravity.CENTER); // this is required to bring it to center.
					message.setTextSize(22);

					builder1.setView(message);
                    builder1.setCancelable(false);
                    builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                          onBackPressed();
                        }
                    });
                    builder1.show();
				}
			}
			
			@Override
			public void onError() {
				// TODO Auto-generated method stub
				
			}
		},nameValuePair,true).execute(url);	
		
	}
	
}

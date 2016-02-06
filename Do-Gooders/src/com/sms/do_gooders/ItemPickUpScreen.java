package com.sms.do_gooders;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class ItemPickUpScreen extends Activity {

	EditText pickDate,pickTime,phoneNumber,address;
	private SimpleDateFormat dateFormatter;
	Button postDetails;
	String ItemID=0+"",RequestID=0+"";
	Context cntx;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_pick_up_screen);
		
		cntx = this;
		 ActionBar actionBar = getActionBar();
		    actionBar.setDisplayOptions(actionBar.getDisplayOptions()
		            | ActionBar.DISPLAY_SHOW_CUSTOM);
		    actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.navigation));
		    actionBar.setDisplayShowHomeEnabled(true);

		    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		    Bundle extras = getIntent().getExtras();
			if (extras != null) {
				ItemID = extras.getString("ItemID");
				RequestID = extras.getString("RequestID");
			}
		

		pickDate = (EditText)findViewById(R.id.pickDate);
		pickTime = (EditText)findViewById(R.id.pickTime);
		phoneNumber = (EditText)findViewById(R.id.phoneNumber);
		address = (EditText)findViewById(R.id.address);
		postDetails = (Button)findViewById(R.id.acceptDonate);
		
		Profile mProfile = Profile.getProfile();
		phoneNumber.setText(mProfile.profileNumber);
		address.setText(mProfile.profileAddress);
		
		dateFormatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);

		
		pickDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Calendar newCalendar = Calendar.getInstance();
				new DatePickerDialog(v.getContext(), new OnDateSetListener() {

			        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			            Calendar newDate = Calendar.getInstance();
			            newDate.set(year, monthOfYear, dayOfMonth);
			            pickDate.setText(dateFormatter.format(newDate.getTime()));
			        }

			    },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH)).show();

			}
		});
		
		pickTime.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 Calendar mcurrentTime = Calendar.getInstance();
		            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
		            int minute = mcurrentTime.get(Calendar.MINUTE);
		            TimePickerDialog mTimePicker;
		            mTimePicker = new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
		                @Override
		                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
		                	if(selectedMinute <= 9)
		                		pickTime.setText( selectedHour + ":0" + selectedMinute);
		                	else
		                		pickTime.setText( selectedHour + ":" + selectedMinute);
		                	
		                	if(selectedHour <= 9)
		                		pickTime.setText( "0"+selectedHour + ":" + selectedMinute);
		                }
		            }, hour, minute, true);//Yes 24 hour time
		            mTimePicker.setTitle("Select Time");
		            mTimePicker.show();
			}
		});
		
		postDetails.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(pickDate.getText().toString().length() == 0 
						|| pickTime.getText().toString().length() == 0 
						|| phoneNumber.getText().toString().length() == 0 
						|| address.getText().toString().length() == 0)
				{
					AlertDialog.Builder builder1 = new AlertDialog.Builder(ItemPickUpScreen.this);
					TextView title = new TextView(ItemPickUpScreen.this);
					title.setText("Error");
					title.setPadding(10, 10, 10, 10);
					title.setGravity(Gravity.CENTER); // this is required to bring it to center.
					title.setTextSize(22);
					builder1.setCustomTitle(title);

					TextView message = new TextView(ItemPickUpScreen.this);
					message.setText("Please fill the entries");
					message.setPadding(10, 10, 10, 10);
					message.setGravity(Gravity.CENTER); // this is required to bring it to center.
					message.setTextSize(22);

					builder1.setView(message);
                    builder1.setCancelable(false);
                    builder1.setPositiveButton("WIFI Settings", 
                    		new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                           
                        }
                    });
                    builder1.show();
				}
				else {
					//send Details to Server and Go to home Page
					sendDetailsToServer();
					//finish();
				}
			}
		});
		
		
		//Font
				final Typeface mFont = Typeface.createFromAsset(getAssets(),
						"fonts/OpenSans-Regular.ttf"); 
				final ViewGroup mContainer = (ViewGroup) findViewById(android.R.id.content).getRootView();
				ServerConfig.setAppFont(mContainer, mFont,true);
				
	}

	
	
	public void sendDetailsToServer()
	{
		
		String url = ServerConfig.SERVER_URL+"SavePickUpDetails";
		
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(6);
		nameValuePair.add(new BasicNameValuePair("DeviceToken",DBClass.getDeviceToken()));
		nameValuePair.add(new BasicNameValuePair("ItemId", ItemID));
		nameValuePair.add(new BasicNameValuePair("RequestId",RequestID));
		nameValuePair.add(new BasicNameValuePair("PickupDate", pickDate.getText().toString()));
		nameValuePair.add(new BasicNameValuePair("PickupTime", pickTime.getText().toString()));
		nameValuePair.add(new BasicNameValuePair("Phone", phoneNumber.getText().toString()));
		nameValuePair.add(new BasicNameValuePair("Address", address.getText().toString()));
		
		//DBClass.saveProfile(name.getText().toString(), email.getText().toString(), phone.getText().toString(), address.getText().toString(), bm);
		new JSONFromURL(cntx, new BgAsyncCallBack() {
			
			@Override
			public void processData(Object data) {
				// TODO Auto-generated method stub
				JSONObject obj = null;
				JSONArray EventsArray = (JSONArray)data;
				try {
					AlertDialog.Builder builder1 = new AlertDialog.Builder(ItemPickUpScreen.this);
					TextView title = new TextView(ItemPickUpScreen.this);
					title.setText("Do Good");
					title.setPadding(10, 10, 10, 10);
					title.setGravity(Gravity.CENTER); // this is required to bring it to center.
					title.setTextSize(22);
					builder1.setCustomTitle(title);

					TextView message = new TextView(ItemPickUpScreen.this);
					message.setText("Thanks for Donating!");
					message.setPadding(10, 10, 10, 10);
					message.setGravity(Gravity.CENTER); // this is required to bring it to center.
					message.setTextSize(22);

					builder1.setView(message);
                    builder1.setCancelable(false);
                    builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        	Intent intent = new Intent(ItemPickUpScreen.this,HomeScreen.class);
        					startActivity(intent);
        					finish();
                        }
                    });
                    builder1.show();					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.d("Go-Good","Exception in reading data.");
					e.printStackTrace();
				}
				
			}
			
			@Override
			public void onError() {
				// TODO Auto-generated method stub
				
			}
		},nameValuePair,true,true).execute(url);		
	}
}

package com.sms.do_gooders;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class DetailedItemView extends Activity {
String itemName,itemId,itemDesc,itemLink;
	
	TextView mItemName,mItemDesc;
	ImageView mItemPic;
	ListView orgList;
	boolean fromNotifications;
	ImageView imageView;
	LinearLayout orgLayout;
	String type = "";
	ArrayList<OrganisationsList> orgAccepted;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detailed_item_view);
		Bundle details = getIntent().getExtras();
		
		
		 ActionBar actionBar = getActionBar();
		    actionBar.setDisplayOptions(actionBar.getDisplayOptions()
		            | ActionBar.DISPLAY_SHOW_CUSTOM);
		    actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.navigation));
		    actionBar.setDisplayShowHomeEnabled(true);

		    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		    
		    
		mItemName = (TextView)findViewById(R.id.itemName);
		mItemDesc = (TextView)findViewById(R.id.itemDescription);
		mItemPic = (ImageView)findViewById(R.id.personimage);
		orgLayout = (LinearLayout)findViewById(R.id.organizations);
		orgList = (ListView)findViewById(R.id.organizationList);
		if(details != null)
		{
			itemName = details.getString("ItemName");
			itemDesc = details.getString("ItemDescription");
			itemId = details.getString("ItemID");
			itemLink = details.getString("ItemImageLink");
			fromNotifications = details.getBoolean("fromNotifications");
			type = details.getString("selectionType");

		}
		Log.d("Do Good","fromNotifications is  "+fromNotifications);
		
		mItemName.setText(itemName);
		mItemDesc.setText(itemDesc);
		BitmapWorkerTask task = new BitmapWorkerTask(mItemPic);
	    task.execute(itemLink);
	    
	    getOrganizationsList();
	    
	    
	  //Font
	  		final Typeface mFont = Typeface.createFromAsset(getAssets(),
	  				"fonts/OpenSans-Regular.ttf"); 
	  		final ViewGroup mContainer = (ViewGroup) findViewById(android.R.id.content).getRootView();
	  		ServerConfig.setAppFont(mContainer, mFont,true);
	  		
		
	}
	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
	    private final WeakReference<ImageView> imageViewReference;
	    private String data;

	    public BitmapWorkerTask(ImageView imageView) {
	        // Use a WeakReference to ensure the ImageView can be garbage
	        // collected
	        imageViewReference = new WeakReference<ImageView>(imageView);
	    }

	    // Decode image in background.
	    @Override
	    protected Bitmap doInBackground(String... params) {
	        data = params[0];
	        try {
	            return BitmapFactory.decodeStream((InputStream) new URL(data)
	                    .getContent());
	        } catch (MalformedURLException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return null;
	    }

	    // Once complete, see if ImageView is still around and set bitmap.
	    @Override
	    protected void onPostExecute(Bitmap bitmap) {
	        if (imageViewReference != null && bitmap != null) {
	            final ImageView imageView = imageViewReference.get();
	            if (imageView != null) {
	                imageView.setImageBitmap(bitmap);
	            }
	        }
	    }
	}

	
	public void getOrganizationsList() {
		Profile mProfile = Profile.getProfile();
		String url = ServerConfig.SERVER_URL+"GetRequestList/?DeviceToken="+DBClass.getDeviceToken()+"&ItemId="+itemId;
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair("DeviceToken",DBClass.getDeviceToken()));
		nameValuePair.add(new BasicNameValuePair("DonorId", mProfile.profileID));
		new JSONFromURL(DetailedItemView.this, new BgAsyncCallBack() {
			
			@Override
			public void processData(Object data) {
				// TODO Auto-generated method stub
				JSONObject obj = null;
				if(data != null) 
				{
					
					JSONArray EventsArray = (JSONArray)data;
					Log.d("Do Good","data is not null and length is ...... "+EventsArray.length());
					if(EventsArray.length() > 0) {
						orgAccepted = new ArrayList<OrganisationsList>();
						try {
							for(int i = 0;i < EventsArray.length() ; i ++) {
								JSONObject object = (JSONObject)EventsArray.get(i);
								//Check the Values first and write Code
								OrganisationsList orgList = new OrganisationsList();
								orgList.OrgID = object.getString("RecOrgId");
								orgList.OrgReqId = object.getString("RequestId");
								orgList.OrgLogoLink = object.getString("RecOrgLogo");
								orgList.OrgUser = object.getString("FullName");
								orgList.OrgStatus = object.getString("RequestStatus");
								orgList.OrgName = object.getString("RecOrgName");
								orgList.OrgReqTime = object.getString("RequestedDt");
								orgAccepted.add(orgList);
								Log.d("Do Good","asdfasdfasdfaasdfasdfa     "+orgList.OrgReqTime);
								orgList = null;
							}
							Log.d("Do Good","Reload the data now..........");
						
							
						} 
						catch (Exception e) {
							// TODO Auto-generated catch block
							Log.d("Go-Dooders","Exception in reading data.");
							e.printStackTrace();
						}
						reloadData();
					}
					else {
						reloadData();
					}
				}
				else
				{
					AlertDialog.Builder builder1 = new AlertDialog.Builder(DetailedItemView.this);
					TextView title = new TextView(DetailedItemView.this);
					title.setText("Error");
					title.setPadding(10, 10, 10, 10);
					title.setGravity(Gravity.CENTER); // this is required to bring it to center.
					title.setTextSize(22);
					builder1.setCustomTitle(title);

					TextView message = new TextView(DetailedItemView.this);
					message.setText("No Donations Yet..!! ");
					message.setPadding(10, 10, 10, 10);
					message.setGravity(Gravity.CENTER); // this is required to bring it to center.
					message.setTextSize(22);

					builder1.setView(message);
                    builder1.setCancelable(false);
                    builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                          onBackPressed();
                          finish();
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
	
	public void reloadData() {
		if(orgAccepted != null && orgAccepted .size() > 0)
		{
			TextView tview = (TextView)findViewById(R.id.empty);
			tview.setVisibility(View.GONE);
			orgList.setAdapter(new OrgListCustomAdapter(getApplicationContext(), orgAccepted,itemId,type));
		}
		else
		{
			orgList.setEmptyView((TextView)findViewById(R.id.empty));
		}
		
	}
	public static String[] splitDateTime(String str) {
		return null;
	}
}

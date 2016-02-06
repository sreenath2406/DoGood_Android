package com.sms.do_gooders;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class PendingDonations extends Fragment implements OnRefreshListener{
	ListView itemsList;
	TextView tview;
	Context context;
	View rootView;
	boolean isFromNotification = false;
	ArrayList<DonatedItem> itemsDonated;
	private SwipeRefreshLayout swipeView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.pending_list, container, false);
		context = getActivity().getApplicationContext();

		itemsDonated = new ArrayList<DonatedItem>();
		//first get the list of Donations / Notifications
		callDonationsListService();

		//Initialise the resources
		itemsList = (ListView)rootView.findViewById(R.id.listView);
		tview = (TextView)rootView.findViewById(R.id.empty);

		//Swipe down to refresh
		swipeView = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe_view);
		swipeView.setOnRefreshListener(this);
		swipeView.setColorSchemeColors(getResources().getColor(R.color.appColor), Color.GREEN, Color.BLUE,
				Color.RED);

		//Font
		final Typeface mFont = Typeface.createFromAsset(context.getAssets(),
				"fonts/OpenSans-Regular.ttf"); 
		final ViewGroup mContainer = (ViewGroup)getActivity().findViewById(android.R.id.content).getRootView();
		ServerConfig.setAppFont(mContainer, mFont,true);

		return rootView;
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d("Do Good","OnResume.. Pending..");
	}
	public void reloadData()
	{
		Log.d("Do Good","Relaod the data..............");

		if(itemsDonated.size() == 0)
		{
			Log.d("Do Good","Empty..............");
			itemsList.setEmptyView(tview);
		}
		else
			itemsList.setAdapter(new ItemList(getActivity().getApplicationContext(), itemsDonated, isFromNotification,"Pending_Donations"));

		swipeView.setRefreshing(false);
	}
	public void callDonationsListService()
	{
		itemsDonated = null;
		itemsDonated = new ArrayList<DonatedItem>();
		DBClass.getProfileDetails();
		Profile mProfile = Profile.getProfile();
		Log.d("Do Good","mProfile id is .. "+mProfile.profileID);
		String url = ServerConfig.SERVER_URL+"GetDonatedItemsList/?DeviceToken="+DBClass.getDeviceToken()+"&DonorId="+mProfile.profileID;
		Log.d("Do Good","url is.... "+url);
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(7);
		nameValuePair.add(new BasicNameValuePair("DeviceToken",DBClass.getDeviceToken()));
		nameValuePair.add(new BasicNameValuePair("DonorId", mProfile.profileID));
		new JSONFromURL(getActivity(), new BgAsyncCallBack() {

			@Override
			public void processData(Object data) {
				// TODO Auto-generated method stub
				JSONObject obj = null;
				JSONArray EventsArray = (JSONArray)data;
				if(EventsArray != null && EventsArray.length() > 0) 
				{

					try {
						for(int i = 0;i < EventsArray.length() ; i ++) {
							JSONObject object = (JSONObject)EventsArray.get(i);
							DonatedItem dItem = new DonatedItem();
							dItem.ItemId = object.getString("ItemId");
							dItem.PhotoLink =  object.getString("Photo");
							dItem.DonorId = object.getString("DonorId");
							dItem.ItemTitle = object.getString("Title");
							dItem.ItemDescription = object.getString("ItemDescription");
							dItem.ItemStatus = object.getString("RequestStatus");
							Log.d("Do-Good","DItem is   "+dItem.ItemTitle);
							if(dItem.ItemStatus.equalsIgnoreCase(""))
								itemsDonated.add(dItem);
							dItem = null;
						}
						for(int i = 0;i <itemsDonated.size() ; i ++) {
							Log.d("Do-Good","Item Object at "+i+"  is "+itemsDonated.get(i).ItemTitle);
						}
					} 
					catch (Exception e) {
						// TODO Auto-generated catch block
						Log.d("Go-Dooders","Exception in reading data.");
						e.printStackTrace();
					}
					reloadData();
				}
				else
				{
					//Show some text here....
					reloadData(); 
				}
			}

			@Override
			public void onError() {
				// TODO Auto-generated method stub

			}
		},nameValuePair,true).execute(url);	
	}
	@Override
	public void onRefresh() {
		callDonationsListService();
	}
}

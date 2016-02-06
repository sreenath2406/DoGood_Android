package com.sms.do_gooders;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class OrgListCustomAdapter extends BaseAdapter {
    private static LayoutInflater inflater=null;
	ArrayList<OrganisationsList> orgList;
	Context cntx;
	String type;
	String itemId;
	Typeface mFont;
	public OrgListCustomAdapter (Context cntx,ArrayList<OrganisationsList> orgList,String itemId,String type) {
		this.cntx = cntx;
		this.orgList = orgList;
		this.itemId = itemId;
		this.type = type;
		mFont = Typeface.createFromAsset(cntx.getAssets(),
  				"fonts/OpenSans-Regular.ttf"); 
		Log.d("Do Good","Item Id is "+this.itemId);
		inflater = ( LayoutInflater )cntx.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return orgList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return orgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Holder holder=new Holder();
        View rowView; 
        Log.d("Do Good","GetView for this now...type is "+type);
        rowView = inflater.inflate(R.layout.orglist_custom_row, null);
        holder.itemName = (TextView)rowView.findViewById(R.id.itemName);
        holder.itemPic = (ImageView)rowView.findViewById(R.id.itemImage);
        holder.awardBtn  = (Button)rowView.findViewById(R.id.awardbutton);
        holder.pickTime = (TextView)rowView.findViewById(R.id.picktimedetails);
        
        
        holder.itemName.setTypeface(mFont);
        holder.awardBtn.setTypeface(mFont);
        holder.pickTime.setTypeface(mFont);
        
        OrganisationsList orgObj = orgList.get(position);
        Log.d("Do Good","OrgStatus is "+orgObj.OrgStatus);
        if((orgObj.OrgStatus).equalsIgnoreCase("Awarded") || (orgObj.OrgStatus).equalsIgnoreCase("Completed")) //&& type.equalsIgnoreCase("awarded_items"))
        {
        	holder.awardBtn.setText("Awarded");
        	holder.awardBtn.setEnabled(false);
        	holder.awardBtn.setBackgroundColor(Color.GRAY);
        	holder.pickTime.setText("Pick-up at: "+getDateTimeFromObj(orgObj.OrgReqTime));
            holder.itemName.setText("This item has been awarded to "+orgObj.OrgName);
            if((orgObj.OrgStatus).equalsIgnoreCase("Completed"))
            	holder.pickTime.setVisibility(View.GONE);
        }
        else
        {
        	holder.pickTime.setVisibility(View.GONE);
        	holder.awardBtn.setTag(position);
            holder.itemName.setText(orgObj.OrgUser+ " from "+orgObj.OrgName+" has requested this item.");
        	holder.awardBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.d("Do Good","Navigate to Last Screen of App...");
		        	//itemId
		        	 Intent intent =  new Intent(cntx,ItemPickUpScreen.class);
		             intent.putExtra("ItemID", itemId);
		             intent.putExtra("RequestID",orgList.get((Integer) v.getTag()).OrgReqId);
		             intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		             cntx.startActivity(intent);
				}
			});
        }
       // holder.itemDescription.setText(orgObj.OrgUser+ " from "+orgObj.OrgName);
        BitmapWorkerTask task = new BitmapWorkerTask(holder.itemPic);
        task.execute(orgObj.OrgLogoLink);
//        if(type.equalsIgnoreCase("Pending_Donations")) {
//		    rowView.setOnClickListener(new OnClickListener() {            
//		        @Override
//		        public void onClick(View v) {
//		        	Log.d("Do-Good","Navigate to Last Screen of App...");
//		        	//itemId
//		        	 Intent intent =  new Intent(cntx,ItemPickUpScreen.class);
//		             intent.putExtra("ItemID", itemId);
//		             intent.putExtra("RequestID",orgList.get(position).OrgReqId);
//		             intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		             cntx.startActivity(intent);
//		        }
//		    });
//        }
        return rowView;
	}
	public class Holder
    {
        TextView itemName;
        ImageView itemPic;
        TextView pickTime;
        Button awardBtn;
    }
	
	public String getDateTimeFromObj(String dateTime)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		try
		{
			 Date date = format.parse(dateTime); 
			 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
			 String temp = df.format(new Date());
			 Log.d("Sreenath","111111111111111111111  "+temp);
			 return temp;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	public void callDonationsListService()
	{

		DBClass.getProfileDetails();
		Profile mProfile = Profile.getProfile();
		String url = ServerConfig.SERVER_URL+"ConfirmPickup/";
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(7);
		nameValuePair.add(new BasicNameValuePair("Itemid",itemId));
		
		new JSONFromURL(cntx, new BgAsyncCallBack() {

			@Override
			public void processData(Object data) {
				// TODO Auto-generated method stub
				((Activity) cntx).finish();
			}

			@Override
			public void onError() {
				// TODO Auto-generated method stub

			}
		},nameValuePair,true,false).execute(url);	
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

}

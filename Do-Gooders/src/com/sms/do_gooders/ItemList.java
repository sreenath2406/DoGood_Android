package com.sms.do_gooders;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemList extends BaseAdapter{
	  private static LayoutInflater inflater=null;
	    ArrayList<DonatedItem> itemsDoanted;
	    Context cntc;
	    String type = "";
	    static class ViewHolder
	    {
	        TextView itemName;
	        TextView itemDescription;
	        ImageView itemPic;
	    }
		private LayoutInflater mInflater;
	    boolean isFromNotification;

		public ItemList(Context context,ArrayList<DonatedItem> donatedItems,boolean isFromNotification,String type) {
			cntc = context;
			inflater = ( LayoutInflater )context.
	                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			itemsDoanted = donatedItems;
			this.isFromNotification = isFromNotification;
			this.type = type;
			Log.d("Do Good","1111 type is........ "+type);
			mInflater = LayoutInflater.from(cntc);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return itemsDoanted.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return itemsDoanted.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null)
	            convertView = inflater.inflate(R.layout.detailed_view_custom_row, null);
			TextView itemNameTV = (TextView)convertView.findViewById(R.id.itemName);
			ImageView itemPIC = (ImageView)convertView.findViewById(R.id.itemImage);
			TextView itemDescTV = (TextView)convertView.findViewById(R.id.itemDescription);
			DonatedItem item = itemsDoanted.get(position);
			Log.d("Do Good","item is ...."+item.ItemTitle);
			
			final Typeface mFont = Typeface.createFromAsset(cntc.getAssets(),
	  				"fonts/OpenSans-Regular.ttf"); 
			itemNameTV.setTypeface(mFont);
			itemDescTV.setTypeface(mFont);
			
			itemNameTV.setText(item.ItemTitle);
			itemDescTV.setText(item.ItemDescription);
			Log.d("Do Good","holder.itemNAme is "+itemNameTV.getText());
			Log.d("Do Good","Photo link is "+item.PhotoLink);
			BitmapWorkerTask task = new BitmapWorkerTask(itemPIC);
	        task.execute(item.PhotoLink);
		        convertView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
					Intent intent = null;
					intent = new Intent(cntc,DetailedItemView.class);
					Log.d("Do Good","ItemID is "+itemsDoanted.get(position).ItemId);
		              intent.putExtra("ItemID", itemsDoanted.get(position).ItemId);
		              intent.putExtra("ItemName", itemsDoanted.get(position).ItemTitle);
		              intent.putExtra("ItemDescription", itemsDoanted.get(position).ItemDescription);
		              intent.putExtra("ItemImageLink", itemsDoanted.get(position).PhotoLink);
		              intent.putExtra("fromNotifications", isFromNotification);
		              intent.putExtra("selectionType", type);
		              intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		              cntc.startActivity(intent);
					}
				});
	        return convertView;
		}
		class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
		   // private final WeakReference<ImageView> imageViewReference;
		    final ImageView imageView;
		    private String data;

		    public BitmapWorkerTask(ImageView imageView) {
		        // Use a WeakReference to ensure the ImageView can be garbage
		        // collected
		        //imageViewReference = new WeakReference<ImageView>(imageView);
		    	this.imageView = imageView;
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
		    	Log.d("Do Good","Bitmap is received and its "+bitmap);
		        if ( bitmap != null) {
		            Log.d("Do Good","imageView is  ... "+imageView);
		            if (this.imageView != null) {
		                this.imageView.setImageBitmap(bitmap);
		            }
		        }
		        
		    }
		}

	}

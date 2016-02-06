package com.sms.do_gooders;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NotificationAdapter extends BaseAdapter  
{

    private static LayoutInflater inflater=null;
    ArrayList<NotifiedItemsList> notifiedItems;
    Context cntc;
    boolean isFromNotification;
	public NotificationAdapter(Context context,ArrayList<NotifiedItemsList> notifiedItems) {
		// TODO Auto-generated constructor stub
		cntc = context;
		inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.notifiedItems = notifiedItems;
		this.isFromNotification = true;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return notifiedItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
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
        rowView = inflater.inflate(R.layout.detailed_view_custom_row, null);
        holder.itemName = (TextView)rowView.findViewById(R.id.itemName);
        holder.itemDescription = (TextView)rowView.findViewById(R.id.itemDescription);
        holder.itemPic = (ImageView)rowView.findViewById(R.id.itemImage);
        NotifiedItemsList notificationObj = notifiedItems.get(position);
        holder.itemName.setText(notificationObj.ItemTitle);
        //holder.itemDescription.setText(notificationObj.ItemDescription);
        holder.itemName.setText(notificationObj.OrgUserName+ " from "+notificationObj.OrgName+" has requested this item. ");
        
        final Typeface mFont = Typeface.createFromAsset(cntc.getAssets(),
  				"fonts/OpenSans-Regular.ttf"); 
        holder.itemName.setTypeface(mFont);
        holder.itemDescription.setTypeface(mFont);
        
        BitmapWorkerTask task = new BitmapWorkerTask(holder.itemPic);
        task.execute(notificationObj.ItemPic);
    
        rowView.setOnClickListener(new OnClickListener() {            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(cntc,DetailedItemView.class);
                
                intent.putExtra("ItemID", notifiedItems.get(position).ItemId);
                intent.putExtra("ItemName", notifiedItems.get(position).ItemTitle);
                intent.putExtra("ItemDescription", notifiedItems.get(position).ItemDescription);
                intent.putExtra("ItemImageLink", notifiedItems.get(position).ItemPic);
                intent.putExtra("fromNotifications", isFromNotification);
                intent.putExtra("selectionType", "Pending_Donations");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                cntc.startActivity(intent);
            }
        });   
		return rowView;
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
	 public class Holder
	    {
	        TextView itemName;
	        TextView itemDescription;
	        ImageView itemPic;
	    }
}

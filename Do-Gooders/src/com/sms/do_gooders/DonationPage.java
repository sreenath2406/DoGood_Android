package com.sms.do_gooders;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class DonationPage extends Activity {
	ImageView  menuImg;
	ImageView imageView,itemPic;
	Bitmap bm;
	EditText itemName,itemDescription;
	protected static final int REQUEST_CAMERA = 1;
	protected static final int SELECT_FILE = 2;
	
	public static Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_donation_page);context = this;
		int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
		TextView abTitle = (TextView) findViewById(titleId);
		abTitle.setTextColor(Color.WHITE);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.custom_top_bar, null);


		 ActionBar actionBar = getActionBar();
		 actionBar.setDisplayOptions(actionBar.getDisplayOptions()
		            | ActionBar.DISPLAY_SHOW_CUSTOM);
		 actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.navigation));
		    
		    imageView = new ImageView(actionBar.getThemedContext());
		    imageView.setScaleType(ImageView.ScaleType.CENTER);
		    imageView.setImageResource(R.drawable.save);
		    ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
		            ActionBar.LayoutParams.WRAP_CONTENT,
		            ActionBar.LayoutParams.WRAP_CONTENT, Gravity.RIGHT
		                    | Gravity.CENTER_VERTICAL);
		    layoutParams.rightMargin = 40;
		    imageView.setLayoutParams(layoutParams);
		    actionBar.setCustomView(imageView);
		    actionBar.setDisplayShowHomeEnabled(false);
		    
		    itemName = (EditText)findViewById(R.id.itemName);
		    itemDescription = (EditText)findViewById(R.id.itemDescription);
		    imageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(bm != null)
					{
						//if(itemName.getText().toString().length() > 0 && itemDescription.getText().toString().length() > 0 )
						if(itemName.getText().toString().length() > 0 )
						{
							//send the details to server and go back to home page
							sendDetailsToServer();
						}
						else
						{
							AlertDialog.Builder builder1 = new AlertDialog.Builder(DonationPage.this);
							TextView title = new TextView(DonationPage.this);
							title.setText("Error");
							title.setPadding(10, 10, 10, 10);
							title.setGravity(Gravity.CENTER); // this is required to bring it to center.
							title.setTextSize(22);
							builder1.setCustomTitle(title);

							TextView message = new TextView(DonationPage.this);
							message.setText("Please fill the entries");
							message.setPadding(10, 10, 10, 10);
							message.setGravity(Gravity.CENTER); // this is required to bring it to center.
							message.setTextSize(22);

							builder1.setView(message);
		                    builder1.setCancelable(false);
		                    builder1.setPositiveButton("OK", 
		                    		new DialogInterface.OnClickListener() {
	                            public void onClick(DialogInterface dialog, int id) {
	                                
	                            }
	                        });
		                    builder1.show();
						}
					}
					else
					{
						AlertDialog.Builder builder1 = new AlertDialog.Builder(DonationPage.this);
						TextView title = new TextView(DonationPage.this);
						title.setText("Error");
						title.setPadding(10, 10, 10, 10);
						title.setGravity(Gravity.CENTER); // this is required to bring it to center.
						title.setTextSize(22);
						builder1.setCustomTitle(title);

						TextView message = new TextView(DonationPage.this);
						message.setText("Please upload the image.");
						message.setPadding(10, 10, 10, 10);
						message.setGravity(Gravity.CENTER); // this is required to bring it to center.
						message.setTextSize(22);
						builder1.setView(message);
	                   
	                    builder1.setCancelable(false);
	                    builder1.setPositiveButton("OK", 
	                    		new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                
                            }
                        });
	                    builder1.show();
					}
				}
			});
		
		    itemPic = (ImageView)findViewById(R.id.donateImg);
		    itemPic.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					selectImage();
					
				}
			});

		  //Font
			final Typeface mFont = Typeface.createFromAsset(getAssets(),
					"fonts/OpenSans-Regular.ttf"); 
			final ViewGroup mContainer = (ViewGroup) findViewById(android.R.id.content).getRootView();
			ServerConfig.setAppFont(mContainer, mFont,true);
			
	}

	
	//Image Selection code
		void selectImage()
		{
			final CharSequence[] items = { "Take Photo", "Choose from Library", "Cancel" };
			AlertDialog.Builder builder = new AlertDialog.Builder(DonationPage.this);
			TextView title = new TextView(DonationPage.this);
			title.setText("Add Photo!");
			title.setPadding(10, 10, 10, 10);
			title.setGravity(Gravity.CENTER); // this is required to bring it to center.
			title.setTextSize(22);
			builder.setCustomTitle(title);

			builder.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					if (items[item].equals("Take Photo")) {
							Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							startActivityForResult(intent, REQUEST_CAMERA);
					} 
					else if (items[item].equals("Choose from Library")) {
						Intent intent = new Intent(
													Intent.ACTION_PICK,
													android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
													intent.setType("image/*");
					    startActivityForResult(
					    		Intent.createChooser(intent, "Select File"),
					    			SELECT_FILE);
						} 
					else if (items[item].equals("Cancel")) {
								dialog.dismiss();
						}
					}
				});
			 builder.show();
		}
		
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			
			if (resultCode == RESULT_OK) {
				if(requestCode == REQUEST_CAMERA) {
				    bm = (Bitmap) data.getExtras().get("data");
					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
					bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
					File destination = new File(Environment.getExternalStorageDirectory(),
					System.currentTimeMillis() + ".jpg");
					FileOutputStream fo;
					try {
						destination.createNewFile();
						fo = new FileOutputStream(destination);
						fo.write(bytes.toByteArray());
						fo.close();
					}
					catch (FileNotFoundException e) {
						e.printStackTrace();
					} 
					catch (IOException e) {
						e.printStackTrace();
					}
					itemPic.setImageBitmap(bm);
					
				}
				else if (requestCode == SELECT_FILE) {
					Uri selectedImageUri = data.getData();
					String[] projection = { MediaColumns.DATA };
					Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
					null);
					int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
					cursor.moveToFirst();
					String selectedImagePath = cursor.getString(column_index);
					
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(selectedImagePath, options);
					final int REQUIRED_SIZE = 200;
					int scale = 1;
					while (options.outWidth / scale / 2 >= REQUIRED_SIZE
					&& options.outHeight / scale / 2 >= REQUIRED_SIZE)
					scale *= 2;
					options.inSampleSize = scale;
					options.inJustDecodeBounds = false;
					bm = BitmapFactory.decodeFile(selectedImagePath, options);
					itemPic.setImageBitmap(bm);
					
				}
			}
		}
		
		public void sendDetailsToServer ()
		{
			String imageName = System.currentTimeMillis()+".jpg";
			if(bm != null) {
				ImageUploadTask imTask = new ImageUploadTask(context,bm,ServerConfig.SERVER_URL+"/UploadFile",imageName);
				imTask.execute();
			}
			Bitmap bmp = bm;
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			String url = ServerConfig.SERVER_URL+"SaveDonationItem";
			Profile mProfile = Profile.getProfile();
			DBClass.getProfileDetails();
			List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(7);
			nameValuePair.add(new BasicNameValuePair("DeviceToken",DBClass.getDeviceToken()));
			nameValuePair.add(new BasicNameValuePair("DonorId",mProfile.profileID));
			nameValuePair.add(new BasicNameValuePair("ItemId","0"));
			nameValuePair.add(new BasicNameValuePair("Title", itemName.getText().toString()));
			nameValuePair.add(new BasicNameValuePair("Description", itemDescription.getText().toString()));
			nameValuePair.add(new BasicNameValuePair("Photo", bm==null ? "": imageName));
			new JSONFromURL(context, new BgAsyncCallBack() {
				
				@Override
				public void processData(Object data) {
					// TODO Auto-generated method stub
					JSONObject obj = null;
					JSONArray EventsArray = (JSONArray)data;
					try {
						AlertDialog.Builder builder1 = new AlertDialog.Builder(DonationPage.this);
						TextView title = new TextView(DonationPage.this);
						title.setText("Do Good");
						title.setPadding(10, 10, 10, 10);
						title.setGravity(Gravity.CENTER); // this is required to bring it to center.
						title.setTextSize(22);
						builder1.setCustomTitle(title);

						TextView message = new TextView(DonationPage.this);
						message.setText("Your item has been successfully posted.");
						message.setPadding(10, 10, 10, 10);
						message.setGravity(Gravity.CENTER); // this is required to bring it to center.
						message.setTextSize(22);

						builder1.setView(message);
	                    builder1.setCancelable(false);
	                    builder1.setPositiveButton("OK", 
	                    		new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                				Intent intent = new Intent(DonationPage.this,HomeScreen.class);
                				startActivity(intent);
                				finish();
                            }
                        });
	                    builder1.show();
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Log.d("Go-Dood","Exception in reading data.");
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

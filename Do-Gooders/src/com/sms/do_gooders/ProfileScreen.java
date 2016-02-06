package com.sms.do_gooders;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProfileScreen extends Activity {

	protected static final int REQUEST_CAMERA = 1;
	protected static final int SELECT_FILE = 2;
	static String TAG = "Do Good";
	EditText name,email,phone,address;
	RoundedImageView profilePic;
	Button saveDetails;
	Bitmap bm;
	Context context;
	String profileID = "0";
	Button cancel;
	TextView centerPoint;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_screen);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(actionBar.getDisplayOptions()
				| ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.navigation));
		actionBar.setDisplayShowHomeEnabled(true);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		context = ProfileScreen.this;

		//Bind to local variables.
		name = (EditText)findViewById(R.id.name);
		email = (EditText)findViewById(R.id.email);
		phone = (EditText)findViewById(R.id.phonenumber);
		profilePic = (RoundedImageView)findViewById(R.id.personimage);
		address = (EditText)findViewById(R.id.address);
		saveDetails = (Button)findViewById(R.id.save);
		cancel = (Button)findViewById(R.id.cancel);
		centerPoint = (TextView)findViewById(R.id.centerPoint);
		phone.addTextChangedListener(new PhoneNumberTextWatcher(phone));

		//		phone.addTextChangedListener(new TextWatcher() {
		//			
		//			@Override
		//			public void onTextChanged(CharSequence s, int start, int before, int count) {
		//				// TODO Auto-generated method stub
		//				 boolean flag = true;
		//		         String eachBlock[] = phone.getText().toString().split("-");
		//		         for (int i = 0; i < eachBlock.length; i++) 
		//		         {
		//		             if (eachBlock[i].length() > 3)
		//		             {
		//		                 Log.v("11111111111111111111","cc"+flag + eachBlock[i].length());
		//		             }
		//		         }
		//		         String a = "";
		//		         int keyDel = 0;;
		//				 if (flag) {
		//		             if (keyDel == 0) {
		//
		//		                 if (((phone.getText().length() + 1) % 4) == 0) 
		//		                 {
		//		                     if (phone.getText().toString().split("-").length <= 2) 
		//		                     {
		//		                    	 phone.setText(phone.getText() + "-");
		//		                    	 phone.setSelection(phone.getText().length());
		//		                     }
		//		                 }
		//		                 a = phone.getText().toString();
		//		             }
		//		             else
		//		              {
		//		                 a = phone.getText().toString();
		//		                 keyDel = 0;
		//		             }
		//
		//		         } else {
		//		        	 phone.setText(a);
		//		         }
		//			}
		//			
		//			@Override
		//			public void beforeTextChanged(CharSequence s, int start, int count,
		//					int after) {
		//				// TODO Auto-generated method stub
		//				
		//			}
		//			
		//			@Override
		//			public void afterTextChanged(Editable s) {
		//				// TODO Auto-generated method stub
		//				
		//			}
		//		});

		Profile mPrf = Profile.getProfile();
		DBClass.getProfileDetails();
		if(mPrf.profileID != null) {
			Log.d("Do Good","mProfileID is "+mPrf.profileID);
			profileID = mPrf.profileID;
		}
		//bm = BitmapFactory.decodeResource(getResources(), R.drawable.round);
		//profilePic.setImageBitmap(bm);
		//Code for clicking profile pic.
		profilePic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectImage();

			}
		});

		//Code for clicking save Button
		saveDetails.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//				if(bm == null ) {
				//					 AlertDialog.Builder builder1 = new AlertDialog.Builder(ProfileScreen.this);
				//	                    builder1.setTitle("Error");
				//	                    builder1.setMessage("Please select a profile picture.!");
				//	                    builder1.setCancelable(false);
				//	                    builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				//                         public void onClick(DialogInterface dialog, int id) {
				//                           
				//                         }
				//                     });
				//				}
				final String emailAdd = email.getText().toString();
				if (!isValidEmail(emailAdd)) {
					email.setError("Invalid Email");
				}
				else if(name.getText().length() == 0 || 
						email.getText().length() == 0 ||
						phone.getText().length() == 0 || 
						address.getText().length() ==0)
				{
					AlertDialog.Builder builder1 = new AlertDialog.Builder(ProfileScreen.this);
					TextView title = new TextView(ProfileScreen.this);
					title.setText("Error");
					title.setPadding(10, 10, 10, 10);
					title.setGravity(Gravity.CENTER); // this is required to bring it to center.
					title.setTextSize(22);
					builder1.setCustomTitle(title);

					TextView message = new TextView(ProfileScreen.this);
					message.setText("Please fill the entries");
					message.setPadding(10, 10, 10, 10);
					message.setGravity(Gravity.CENTER); // this is required to bring it to center.
					message.setTextSize(22);

					builder1.setView(message);

					builder1.setCancelable(false);
					builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {

						}
					});
					builder1.show();
				}
				else
				{
					// Go to Home Page i.e., Donate Page
					Log.d(TAG,"bm is "+bm);
					if(bm != null)
					{
						sendImagetoServer();
					}
					sendDetailsToServer();

				}
			}
		});

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			boolean isInitialSetup = extras.getBoolean("isInitialSetup");
			if(!isInitialSetup) {
				DBClass.getProfileDetails();
				Profile profileDetails = Profile.getProfile();
				name.setText(profileDetails.profileName);
				email.setText(profileDetails.profileEmail);
				phone.setText(profileDetails.profileNumber);
				address.setText(profileDetails.profileAddress);
				bm = profileDetails.profilePic;
				profileID =profileDetails.profileID;
				if(bm != null)
					profilePic.setImageBitmap(bm);
				cancel.setVisibility(View.VISIBLE);
			}
			else {
				cancel.setVisibility(View.GONE);
				centerPoint.setVisibility(View.GONE);
				RelativeLayout.LayoutParams testLP = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				testLP.addRule(RelativeLayout.CENTER_IN_PARENT);
				saveDetails.setLayoutParams(testLP);
			}

		}
		else
			cancel.setVisibility(View.GONE);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ProfileScreen.this,HomeScreen.class);
				startActivity(intent);
				finish();

			}
		});
		//Font
		final Typeface mFont = Typeface.createFromAsset(getAssets(),
				"fonts/OpenSans-Regular.ttf"); 
		final ViewGroup mContainer = (ViewGroup) findViewById(android.R.id.content).getRootView();
		ServerConfig.setAppFont(mContainer, mFont,true);

	}

	// validating email id
	private boolean isValidEmail(String email) {
		String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}


	//Image Selection code
	void selectImage()
	{
		final CharSequence[] items = { "Take Photo", "Choose from Library", "Cancel" };
		AlertDialog.Builder builder = new AlertDialog.Builder(ProfileScreen.this);
		TextView title = new TextView(ProfileScreen.this);
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
						"ProfilePic" + ".jpg");
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
				profilePic.setImageBitmap(bm);

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
				profilePic.setImageBitmap(bm);

			}
		}
	}
	public void sendImagetoServer() {
		try {
			ImageUploadTask imTask = new ImageUploadTask(context,bm,ServerConfig.SERVER_URL+"/UploadFile",name.getText().toString()+".jpg");
			imTask.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendDetailsToServer()
	{
		Bitmap bmp = bm;
		byte[] byteArray = "".getBytes();
		String fileName = "";
		if(bmp != null) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byteArray = null;
			byteArray = stream.toByteArray();
			fileName = name.getText().toString()+".jpg";
		}
		String url = ServerConfig.SERVER_URL+"SaveDonorProfile";

		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(7);
		nameValuePair.add(new BasicNameValuePair("DeviceToken",DBClass.getDeviceToken()));
		nameValuePair.add(new BasicNameValuePair("DonorId", profileID));
		nameValuePair.add(new BasicNameValuePair("Name", name.getText().toString()));
		nameValuePair.add(new BasicNameValuePair("Phone", phone.getText().toString()));
		nameValuePair.add(new BasicNameValuePair("Email", email.getText().toString()));
		nameValuePair.add(new BasicNameValuePair("Address", address.getText().toString()));
		nameValuePair.add(new BasicNameValuePair("Photo",bm == null ? "": name.getText().toString()+".jpg"));
		//nameValuePair.add(new BasicNameValuePair("FileName", fileName));

		//DBClass.saveProfile(name.getText().toString(), email.getText().toString(), phone.getText().toString(), address.getText().toString(), bm);
		new JSONFromURL(context, new BgAsyncCallBack() {

			@Override
			public void processData(Object data) {
				// TODO Auto-generated method stub
				JSONObject obj = null;
				JSONArray EventsArray = (JSONArray)data;
				try {
					obj = (JSONObject)EventsArray.get(0);
					Log.d("Do Good","Obj is "+obj);
					if(obj != null) {
						profileID = obj.getString("DonorId");
					}
					Log.d("Do Good","profileID is ...... "+profileID);
					DBClass.saveProfile(profileID,name.getText().toString(), email.getText().toString(), phone.getText().toString(), address.getText().toString(), bm);
					Log.d("Do Good","Go to Home Page....");
					Intent intent = new Intent(ProfileScreen.this,HomeScreen.class);
					startActivity(intent);
					finish();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.d("Do Good","Exception in reading data.");
					e.printStackTrace();
				}

			}

			@Override
			public void onError() {
				// TODO Auto-generated method stub

			}
		},nameValuePair,true,true).execute(url);		
	}
	public class PhoneNumberTextWatcher implements TextWatcher {

		private final String TAG = PhoneNumberTextWatcher.class
				.getSimpleName();
		private EditText edTxt;
		private boolean isDelete;

		public PhoneNumberTextWatcher(EditText edTxtPhone) {
			this.edTxt = edTxtPhone;
			edTxt.setOnKeyListener(new OnKeyListener() {

				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_DEL) {
						isDelete = true;
					}
					return false;
				}
			});
		}

		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void afterTextChanged(Editable s) {

			if (isDelete) {
				isDelete = false;
				return;
			}
			String val = s.toString();
			String a = "";
			String b = "";
			String c = "";
			if (val != null && val.length() > 0 && val.length() <= 10) {
				val = val.replace("-", "");
				if (val.length() >= 3) {
					a = val.substring(0, 3);
				} else if (val.length() < 3) {
					a = val.substring(0, val.length());
				}
				if (val.length() >= 6) {
					b = val.substring(3, 6);
					c = val.substring(6, val.length());
				} else if (val.length() > 3 && val.length() < 6) {
					b = val.substring(3, val.length());
				}
				StringBuffer stringBuffer = new StringBuffer();
				if (a != null && a.length() > 0) {
					stringBuffer.append(a);
					if (a.length() == 3) {
						stringBuffer.append("-");
					}
				}
				if (b != null && b.length() > 0) {
					stringBuffer.append(b);
					if (b.length() == 3) {
						stringBuffer.append("-");
					}
				}
				if (c != null && c.length() > 0) {
					stringBuffer.append(c);
				}
				edTxt.removeTextChangedListener(this);
				edTxt.setText(stringBuffer.toString());
				edTxt.setSelection(edTxt.getText().toString().length());
				edTxt.addTextChangedListener(this);
			}

			else if(val != null && val.length() <= 10){
				edTxt.removeTextChangedListener(this);
				edTxt.setText("");
				edTxt.addTextChangedListener(this);
			}

		}
	}

}

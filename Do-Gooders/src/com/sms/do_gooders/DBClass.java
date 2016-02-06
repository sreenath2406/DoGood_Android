package com.sms.do_gooders;

import java.io.ByteArrayOutputStream;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

public class DBClass extends Application{
	 public static SharedPreferences sharedPfrs = null;
	 public static Context appContext;
	 public static String file_Name = "DoGooders_Preferences";

	 //Device Token
	 public static String m_deviceToken = "DEVICE_TOKEN";
	 //Profile
	 public static String m_name = "NAME";
	 public static String m_email = "EMAIL";
	 public static String m_phone = "PHONE";
	 public static String m_address = "ADDRESS";
	 public static String m_profilePic = "PROFILE_PIC";
	 public static String isProfileSaved ="PROFILE_SAVED";
	 public static String m_profileID = "PROFILE_ID";
	 
	 @Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		appContext = getApplicationContext();
	}
	 //Convert Image to Byte array
	 public static String encodeTobase64(Bitmap image) {
		    Bitmap immage = image;
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
		    byte[] b = baos.toByteArray();
		    String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

		    Log.d("Image Log:", imageEncoded);
		    return imageEncoded;
	 }
	 
	 public static Bitmap decodeBase64(String input) {
	        byte[] decodedByte = Base64.decode(input, 0);
	        return BitmapFactory
	                .decodeByteArray(decodedByte, 0, decodedByte.length);
	    }
	 
	 
	public static void saveProfile(String profileID,String name,String email,String phone,String address,Bitmap profilePic)
	{
		  if (sharedPfrs == null) {
	            sharedPfrs = appContext.getSharedPreferences(file_Name, Context.MODE_PRIVATE);
	        }
	        Editor editor = sharedPfrs.edit();
	        editor.putString(m_profileID, profileID);
	        editor.putString(m_name, name);
	        editor.putString(m_email, email);
	        editor.putString(m_phone, phone);
	        editor.putString(m_address, address);
	        editor.putString(m_profilePic, profilePic == null?"": encodeTobase64(profilePic));
	        editor.putBoolean(isProfileSaved, true);
	        editor.commit();

	}
	//Get Profile Details Now
	
	public static boolean isProfilePresent()
	{
		 if (sharedPfrs == null) {
	            sharedPfrs = appContext.getSharedPreferences(file_Name, Context.MODE_PRIVATE);
	        }
		 return sharedPfrs.getBoolean(isProfileSaved, false);
	}
	
	public static void getProfileDetails()
	{
		if (sharedPfrs == null) {
            sharedPfrs = appContext.getSharedPreferences(file_Name, Context.MODE_PRIVATE);
        }
		Profile profile = Profile.getProfile();
		profile.profileID = sharedPfrs.getString(m_profileID, "0");
		profile.profileName = sharedPfrs.getString(m_name, "");
		profile.profileNumber = sharedPfrs.getString(m_phone, "");
		profile.profileEmail = sharedPfrs.getString(m_email, "");
		profile.profileAddress = sharedPfrs.getString(m_address, "");
		profile.profilePic = decodeBase64(sharedPfrs.getString(m_profilePic, ""));
		
	}
	public static String profileName()
	{
		 if (sharedPfrs == null) {
	            sharedPfrs = appContext.getSharedPreferences(file_Name, Context.MODE_PRIVATE);
	        }
		 return sharedPfrs.getString(m_name, "");
	}
	public static String profileEmail()
	{
		 if (sharedPfrs == null) {
	            sharedPfrs = appContext.getSharedPreferences(file_Name, Context.MODE_PRIVATE);
	        }
		 return sharedPfrs.getString(m_email, "");
	}
	public static String profilePhoneNum()
	{
		 if (sharedPfrs == null) {
	            sharedPfrs = appContext.getSharedPreferences(file_Name, Context.MODE_PRIVATE);
	        }
		 return sharedPfrs.getString(m_phone, "");
	}
	public static String profileAddress()
	{
		 if (sharedPfrs == null) {
	            sharedPfrs = appContext.getSharedPreferences(file_Name, Context.MODE_PRIVATE);
	        }
		 return sharedPfrs.getString(m_address, "");
	}
	public static Bitmap profilePic()
	{
		 if (sharedPfrs == null) {
	            sharedPfrs = appContext.getSharedPreferences(file_Name, Context.MODE_PRIVATE);
	        }
		return decodeBase64(sharedPfrs.getString(m_profilePic, ""));
	}
	
	//Save DeviceToken
	public static void saveDeviceToken(String token)
	{
		Log.d("Do Good"," DeviceToken to be saved is    "+token);
		if (sharedPfrs == null) {
            sharedPfrs = appContext.getSharedPreferences(file_Name, Context.MODE_PRIVATE);
        }
        Editor editor = sharedPfrs.edit();
        editor.putString(m_deviceToken, token);
        editor.commit();
	}
	public static String getDeviceToken()
	{
		 if (sharedPfrs == null) {
	            sharedPfrs = appContext.getSharedPreferences(file_Name, Context.MODE_PRIVATE);
	        }
		 return sharedPfrs.getString(m_deviceToken, "");
	}
}

package com.sms.do_gooders;

import android.graphics.Bitmap;

public class Profile {

	public static String profileName = "";
	public static String profileNumber = "";
	public static String profileEmail = "";
	public static String profileAddress = "";
	public static Bitmap profilePic;
	public static String profileID = "0";
	
	public static Profile myProfile = null;
	
	public static Profile getProfile() {
		if(myProfile == null)
		{
			myProfile = new Profile();
		}
		return myProfile;
	}
	
}

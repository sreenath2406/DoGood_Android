package com.sms.do_gooders;

import java.lang.reflect.Method;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ServerConfig {

	public static final String SERVER_URL = "http://68.178.255.27:8081/api/donor/";
	
	//Change font for whole class
	public static final void setAppFont(ViewGroup mContainer, Typeface mFont, boolean reflect)
	{
	    if (mContainer == null || mFont == null) return;

	    final int mCount = mContainer.getChildCount();

	    // Loop through all of the children.
	    for (int i = 0; i < mCount; ++i)
	    {
	        final View mChild = mContainer.getChildAt(i);
	        if (mChild instanceof TextView)
	        {
	            // Set the font if it is a TextView.
	            ((TextView) mChild).setTypeface(mFont);
	        }
	        else if (mChild instanceof ViewGroup)
	        {
	            // Recursively attempt another ViewGroup.
	            setAppFont((ViewGroup) mChild, mFont,reflect);
	        }
	        else if (reflect)
	        {
	            try {
	                Method mSetTypeface = mChild.getClass().getMethod("setTypeface", Typeface.class);
	                mSetTypeface.invoke(mChild, mFont); 
	            } catch (Exception e) { /* Do something... */ }
	        }
	    }
	}
}

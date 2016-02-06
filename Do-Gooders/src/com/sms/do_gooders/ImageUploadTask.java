package com.sms.do_gooders;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.util.Log;

public class ImageUploadTask extends AsyncTask<String, String, String>{
	
	Context context;
	Bitmap bm;
	String url;
	String fileName;
	public ImageUploadTask(Context context, Bitmap bm, String URL,String fileName) {
		this.context = context;
		this.bm =bm;
		this.url = URL;
		this.fileName = fileName;
	}
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			bm.compress(CompressFormat.JPEG, 75, bos);
			byte[] data = bos.toByteArray();
			HttpClient httpClient = new DefaultHttpClient();
			//URL url = new URL(ServerConfig.SERVER_URL+"/UploadFile");
			HttpPost postRequest = new HttpPost(url);
			ByteArrayBody bab = new ByteArrayBody(data, fileName);
			// File file= new File("/mnt/sdcard/forest.png");
			// FileBody bin = new FileBody(file);
			MultipartEntity reqEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			reqEntity.addPart("uploaded", bab);
			
			postRequest.setEntity(reqEntity);
			HttpResponse response = httpClient.execute(postRequest);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "UTF-8"));
			String sResponse;
			StringBuilder s = new StringBuilder();

			while ((sResponse = reader.readLine()) != null) {
				s = s.append(sResponse);
			}
			Log.d("Do Good","Response: " + s);
		} catch (Exception e) {
			// handle exception here
			//Log.e(e.getClass().getName(), e.getMessage());
			Log.d("Do Good","Esxception................");
			e.printStackTrace();
		}
		return null;
	}
}
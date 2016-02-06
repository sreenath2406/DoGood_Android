package com.sms.do_gooders;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


/**
 * 
 * @author Sreenath Reddy B
 *
 */
public class JSONFromURL extends AsyncTask<String, Integer, JSONArray>{
	
	ProgressDialog pgDialog = null;
	BgAsyncCallBack callBack = null;
	boolean progressBarRequired = false;
	Context context = null;
	List<NameValuePair> nameValuePair;
	boolean isPOSTMethod = false;
	
	public JSONFromURL(Context _context,BgAsyncCallBack _callBack, List<NameValuePair> nameValuePair,boolean isPostMethod,boolean _progressBarRequired)
	{
		context = _context;
		progressBarRequired = _progressBarRequired;
		this.nameValuePair = nameValuePair;
		if(progressBarRequired){
			pgDialog = new ProgressDialog(context);
			pgDialog.setMessage("Please wait...");
			pgDialog.setCancelable(false);
			pgDialog.setTitle("Do Good");
		}
		this.isPOSTMethod = isPostMethod;
		callBack = _callBack;
	}
	public JSONFromURL(Context _context,BgAsyncCallBack _callBack, List<NameValuePair> nameValuePair,boolean _progressBarRequired)
	{
		context = _context;
		progressBarRequired = _progressBarRequired;
		this.nameValuePair = nameValuePair;
		if(progressBarRequired){
			pgDialog = new ProgressDialog(context);
			pgDialog.setMessage("Please wait...");
			pgDialog.setCancelable(false);
			pgDialog.setTitle("Do Good");
		}
		callBack = _callBack;
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		
		if(progressBarRequired){
			pgDialog.show();
		}
	}

	@Override
	protected JSONArray doInBackground(String... params) {
		// TODO Auto-generated method stub
		if (params != null) {
			String data = loadDataFromURL(params);
			if(data != null)
			{
				Log.d("Do Good","DATA IS ..... "+data.toString());
				try {
					JSONArray Jarray = new JSONArray(data);
					//JSONObject obj = (JSONObject) Jarray.get(0);
					//return obj;
					return Jarray;
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("Do Good","the exception is .. "+e.toString());
				}
			}
		}
		return null;
	}
	
	
	protected String loadDataFromURL(String... params)
	{
		String url = params[0];
		url = url.replace(" ", "%20");
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse httpResponse = null;
		if(isPOSTMethod) {
			Log.d("Do-Good","it's post.........");
			HttpPost httpPost = new HttpPost(url);
			try 
			{
				if(nameValuePair != null ){
					UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePair);
					httpPost.setEntity(urlEncodedFormEntity);
					url = url+"?"+URLEncodedUtils.format(nameValuePair, "utf-8");
					Log.d("Sree","URL is  "+url);
					Log.d("Sree","data is  "+nameValuePair.toString());
				}
				 httpResponse = httpClient.execute(httpPost);
			}
			catch(Exception ee) {
				
			}
		}
			
		else 
		{
			HttpGet httpGet = new HttpGet(url);
			try
			{
				if(nameValuePair != null ){
					UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePair);
					//((HTTPGet) httpGet).setEntity(urlEncodedFormEntity);
					//url += "?"+ urlEncodedFormEntity;
					
					Log.d("Sree","URL is  "+url);
					Log.d("Sree","data is  "+nameValuePair.toString());
				}
				 httpResponse = httpClient.execute(httpGet);
			}
			catch(Exception ee) {
				Log.d("Sree","Exception. ..");
				ee.printStackTrace();
			}
		}
		try {
				InputStream inputStream = httpResponse.getEntity().getContent();
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				StringBuilder stringBuilder = new StringBuilder();
				String bufferedStrChunk = null;
				while((bufferedStrChunk = bufferedReader.readLine()) != null){
					stringBuilder.append(bufferedStrChunk);
				}
				return stringBuilder.toString();

			} catch (Exception ioe) {
				System.out.println("Exception at HttpResponse :" + ioe);
				ioe.printStackTrace();
			}

		
		return null;
	}
	
	
	@Override
	protected void onPostExecute(JSONArray result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
		if(progressBarRequired == true){
			
			try{
				pgDialog.dismiss();
			}catch(Exception e){}
			
		}
		if(result != null){
			//callBack.processData(result);
			Log.d("Do Good","Got some valid data from Backend Server......"+result);
			//Toast.makeText(context, "Got Some text from Backend", Toast.LENGTH_LONG).show();
			callBack.processData(result);
			
		}else{
			Log.d("Do Good","Data from Backend Server is null........");
			callBack.processData(null);
		}
	}
	
	public HttpClient getNewHttpClient() {
	     try {
	         KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	         trustStore.load(null, null);

	         SSLSocketFactory sf = new SSLSocketFactory(trustStore);
	         sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	         HttpParams params = new BasicHttpParams();
	         HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	         HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

	         SchemeRegistry registry = new SchemeRegistry();
	         registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	         registry.register(new Scheme("https", sf, 443));

	         ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

	         return new DefaultHttpClient(ccm, params);
	     } catch (Exception e) {
	         return new DefaultHttpClient();
	     }
	 }
	
}


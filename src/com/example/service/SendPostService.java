package com.example.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import android.util.Log;

public class SendPostService {
	
	private HttpClient httpClient = null;
	public static final String LOGON_URL = "http://202.118.31.197/ACTIONLOGON.APPPROCESS" ;
	public static final String SCORE_URL = "http://202.118.31.197/ACTIONQUERYSTUDENTSCORE.APPPROCESS" ;
	public static final String INPUT_ENCODING = "GB2312";
	public static final String OUTPUT_ENCODING = "UTF-8";
	
	public SendPostService(HttpClient httpClient){
		this.httpClient = httpClient;
	}
	
	public HttpResponse sendPostRequest(String url, NameValuePair...nameValuePairs){
		List<NameValuePair> lists = new ArrayList<NameValuePair>();
		HttpResponse response = null;
		for (NameValuePair tmp : nameValuePairs){
			lists.add(tmp);
		}
		
		try{
			HttpEntity httpEntity = new UrlEncodedFormEntity(lists, INPUT_ENCODING);
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(httpEntity);
			try{
				response = httpClient.execute(httpPost);
			}catch(Exception e){
				Log.d("DEBUG", e.getMessage());
			}
		}catch(Exception e){
			Log.d("DEBUG", e.getMessage());
		}
		return response;
	}
	
	public InputStream getContent(HttpResponse response){
		try{
			return response.getEntity().getContent();
		}catch(Exception e){
			Log.d("DEBUG", e.getMessage());
			return null;
		}
	}

}

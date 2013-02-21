package com.example.scoreList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.tidy.Tidy;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.SimpleAdapter;

import com.example.logon.UserDatabase;
import com.example.queryscore.MainActivity;
import com.example.queryscore.R;
import com.example.queryscore.ScoreListActivity;
import com.example.service.Course;
import com.example.service.SAXCourseService;
import com.example.service.SendPostService;

public class AsyncLoadScore extends AsyncTask<Void, Void, Void>{
	private Context context			  				= null;
	private ProgressDialog dialog	  				= null;
	private HttpClient httpClient 	 				= null;
	private final String ID			  				= "WebUserNO";
	private final String PASSWD		  				= "Password";
	private final String CODE		  				= "Agnomen";
	private final String APPLICANT	  				= "applicant";
	private final String REQUEST	  				= "ACTIONQUERYSTUDENTSCORE";
	private final String XML_FILE	 				= "score.xml";
	private final String CONFIG_FILE  				= "config";
	private String requestURL						= null;
	private final String YEAR_TERMNO  				= "YearTermNO";
	private String YearTermNO						= null;
	public static List<Course> scores				= null;
	public static List<HashMap<String, String>> options	= null;
	public static String title						= null;
	private String totalNO							= null;
	private final String CLASS_NAME 				= "name";
	private final String CLASS_SCORE 				= "score";
	private boolean isError 						= false;
	
	public AsyncLoadScore(Context context, String requestURL, String...params){
		this.context = context;
		this.httpClient = MainActivity.httpClient;
		this.requestURL = requestURL;
		if (params.length != 0)
			YearTermNO = params[0];
	}	

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog = ProgressDialog.show(context, 
				context.getString(R.string.loading), 
				context.getString(R.string.load_data));
	}

	@Override
	protected Void doInBackground(Void... params) {
		SendPostService sendPost = new SendPostService(httpClient);
		if (requestURL.equals(SendPostService.LOGON_URL)){
			/* to log on to the server*/
			HttpResponse response =
				sendPost.sendPostRequest(requestURL, 
					new BasicNameValuePair(ID, MainActivity.student_id.getText().toString()),
					new BasicNameValuePair(PASSWD, MainActivity.student_passwd.getText().toString()),
					new BasicNameValuePair(CODE, ""),
					new BasicNameValuePair(APPLICANT, REQUEST));
			/* Check whether logon success or not*/
			processData(sendPost, response);
			if (isError){
				publishProgress();
			}
			else{
				/* databases*/
				if (MainActivity.rememberMe && !isError){
					UserDatabase db = new UserDatabase(context);
					SQLiteDatabase query = db.getReadableDatabase();
					Cursor cursor = query.rawQuery(
							String.format("SELECT * FROM %s WHERE %s=%s", 
									UserDatabase.TABLE_NAME, UserDatabase.COLUMN_USER_NAME, 
									MainActivity.student_id.getText().toString()), null);
					
					if (cursor.getCount() == 0){
						/* Add new record*/
						SQLiteDatabase insert = db.getWritableDatabase();
						ContentValues values = new ContentValues();
						values.put(UserDatabase.COLUMN_USER_NAME, MainActivity.student_id.getText().toString());
						values.put(UserDatabase.COLUMN_USER_PASSWD, MainActivity.student_passwd.getText().toString());
						insert.insert(UserDatabase.TABLE_NAME, null, values);
						insert.close();
					}
					else{
						cursor.moveToFirst();
						String passwd = cursor.getString(cursor.getColumnIndex(UserDatabase.COLUMN_USER_PASSWD));
						if (!passwd.equals(MainActivity.student_passwd.getText().toString())){
							/* update a record.*/
							SQLiteDatabase update = db.getWritableDatabase();
							ContentValues values = new ContentValues();
							values.put(UserDatabase.COLUMN_USER_PASSWD, MainActivity.student_passwd.getText().toString());
							update.update(UserDatabase.TABLE_NAME, values, 
									UserDatabase.COLUMN_USER_NAME+"=?", new String[]{MainActivity.student_id.getText().toString()});
							update.close();
						}
					}
					query.close();
					db.close();
				}
				Intent intent = new Intent(context, ScoreListActivity.class);
				context.startActivity(intent);
			}
		}
		else{
			/*to request for a YearTermNO*/
			HttpResponse response =
				sendPost.sendPostRequest(requestURL, 
						new BasicNameValuePair(YEAR_TERMNO, YearTermNO));
		
			processData(sendPost, response);
			publishProgress();
		}
		
		return null;
	}
	
	private void processData(SendPostService sendPost, HttpResponse response){
		
		InputStream inputStream = sendPost.getContent(response);
		Tidy tidy = new Tidy();
		tidy.setInputEncoding(SendPostService.INPUT_ENCODING);
		tidy.setOutputEncoding(SendPostService.OUTPUT_ENCODING);
		tidy.setXmlOut(true);
		String path = createFile();
		
		try{
			tidy.setConfigurationFromFile(path + CONFIG_FILE);
			tidy.parse(inputStream, new FileOutputStream(path + XML_FILE));
			inputStream.close();
		}catch(Exception e){
			Log.d("DEBUG", e.getMessage());
		}
		
		SAXCourseService service = new SAXCourseService(new File(path + XML_FILE));
		scores = service.getScores();
		options = service.getYearTermNO();
		title = service.getTitle(context);
		totalNO = service.getTotalNO(context);
		if(scores.size() == 0 || options.size() == 0 || title.length() == 0){
			isError = true;
		}
	}
		
	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
		if(isError){
			MainActivity.error.setText(context.getText(R.string.error));
		}
		else{
			List<Map<String, String>> tmp = new ArrayList<Map<String, String>>();
			for (Course course : scores){
				Map<String, String> map = new HashMap<String, String>();
				map.put(CLASS_NAME, course.getName());
				map.put(CLASS_SCORE, course.getScore());
				tmp.add(map);
			}
			SimpleAdapter adapter = new SimpleAdapter(context, tmp, R.layout.score_list_holder, 
					new String[]{CLASS_NAME, CLASS_SCORE}, 
					new int[]{R.id.class_name, R.id.class_score});
			ScoreListActivity.scoresView.setAdapter(adapter);
			ScoreListActivity.totalNO.setText(totalNO);
		}
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		dialog.dismiss();
	}
	
	private boolean createDir(){
		File file = new File(Environment.getExternalStorageDirectory().getPath() +
				File.separator + "data" + File.separator + context.getPackageName());
		try{
			if (!file.exists()){
				file.mkdirs();
				file.createNewFile();
			}
		} catch (IOException e) {
				e.printStackTrace();
				return false;
		}
		return true;
	}
	
	private String createFile(){
		createDir();
		String path = Environment.getExternalStorageDirectory().getPath() +
				File.separator + "data" + File.separator + context.getPackageName() + 
				File.separator;
		File file = new File(path + CONFIG_FILE);
		try{
			if(!file.exists()){
				InputStream is = context.getAssets().open(CONFIG_FILE);
		        int size = is.available();
		        byte[] buffer = new byte[size];
		        is.read(buffer);
		        is.close();
		        FileOutputStream os = new FileOutputStream(file);
		        os.write(buffer);
		        os.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return path;
			
	}
}

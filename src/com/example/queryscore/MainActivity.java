package com.example.queryscore;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.example.logon.IDWatcher;
import com.example.logon.UserDatabase;
import com.example.scoreList.AsyncLoadScore;
import com.example.service.SendPostService;

public class MainActivity extends Activity implements OnClickListener, OnCheckedChangeListener{
	public static AutoCompleteTextView student_id 	  = null;
	public static EditText student_passwd 			  = null;
	private Button submit				  			  = null;
	private Button clear				  			  = null;
	private Button quit					  			  = null;
	private CheckBox checkBox						  = null;
	public static HttpClient httpClient   			  = null;
	public static TextView error		  			  = null;
	public static boolean rememberMe				  = true;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		httpClient = new DefaultHttpClient();
		student_id = (AutoCompleteTextView) findViewById(R.id.student_id);
		student_id.addTextChangedListener(new IDWatcher(this));
		student_id.setTextColor(Color.BLACK);
		student_passwd = (EditText) findViewById(R.id.student_passwd);
		submit = (Button) findViewById(R.id.button_submit);
		clear = (Button) findViewById(R.id.button_clear);
		quit = (Button) findViewById(R.id.button_quit);
		error = (TextView) findViewById(R.id.error);
		checkBox = (CheckBox) findViewById(R.id.remember_me);
		checkBox.setOnCheckedChangeListener(this);
		error.setTextColor(Color.RED);
		
		ConnectivityManager con = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
		boolean internet = con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
		if (!(wifi | internet)){
			submit.setEnabled(false);
			Toast.makeText(this, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
			error.setText(getString(R.string.no_network));
		}
		
		autoFill();
		submit.setOnClickListener(this);
		clear.setOnClickListener(this);
		quit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.button_submit:
			AsyncLoadScore async = new AsyncLoadScore(this, SendPostService.LOGON_URL);
			async.execute();
			break;
		case R.id.button_clear:
			student_id.setText("");
			student_passwd.setText("");
			break;
		case R.id.button_quit:
			finish();
			break;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_score_list, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(this, AboutActivity.class);
		startActivity(intent);
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView,
			boolean isChecked) {
		rememberMe = checkBox.isChecked() ? true : false;
	}
	
	private void autoFill(){
		UserDatabase db = new UserDatabase(this);
		SQLiteDatabase query = db.getReadableDatabase();
		Cursor cursor = query.rawQuery(
				"Select * from " + UserDatabase.TABLE_NAME + " where " +
						UserDatabase.COLUMN_ID +"=1", null);
		if(cursor.getCount() != 0){
			cursor.moveToFirst();
			String name = cursor.getString(cursor.getColumnIndex(UserDatabase.COLUMN_USER_NAME));
			String passwd = cursor.getString(cursor.getColumnIndex(UserDatabase.COLUMN_USER_PASSWD));
			student_id.setText(name);
			student_passwd.setText(passwd);
		}
		query.close();
		db.close();
	}
}

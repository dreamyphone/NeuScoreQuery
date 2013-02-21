package com.example.queryscore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.scoreList.AsyncLoadScore;
import com.example.service.SAXCourseService;
import com.example.service.SendPostService;

public class ScoreListActivity extends Activity implements OnItemSelectedListener, OnItemClickListener{
	private Spinner options			  	  = null;
	public static ListView scoresView 	  = null;
	public static TextView totalNO		  = null;
	public static final String POSITION	  = "position";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score_list);
		setTitle(AsyncLoadScore.title);
		options = (Spinner) findViewById(R.id.options);
		scoresView = (ListView) findViewById(R.id.scoreList);
		totalNO = (TextView) findViewById(R.id.totalNO);
		initData();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_score_list, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(this, AboutActivity.class);
		startActivity(intent);
		return super.onOptionsItemSelected(item);
	}

	private void initData(){
		/* for Spinner */
		List<String> objects = new ArrayList<String>();
		for (Map<String, String> tmp : AsyncLoadScore.options){
			objects.add(tmp.get(SAXCourseService.YearTermNO));
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_spinner_item, objects);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		options.setAdapter(adapter);
		options.setPrompt(getString(R.string.spinner_title));
		options.setSelection(1);
		options.setOnItemSelectedListener(this);
		
		/* for ListView */
		scoresView.setOnItemClickListener(this);
		
	}
	
	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view, int position,
			long id) {
		String YearTermNO = AsyncLoadScore.options.get(position).get(SAXCourseService.VALUE);
		AsyncLoadScore async = new AsyncLoadScore(this, SendPostService.SCORE_URL, YearTermNO);
		async.execute();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		Log.d("DEBUG", "Nothig selected.");
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position,
			long id) {
		Intent intent = new Intent(ScoreListActivity.this, ClassDetailActivity.class);
		intent.putExtra(POSITION, position);
		ScoreListActivity.this.startActivity(intent);
		
	}

}

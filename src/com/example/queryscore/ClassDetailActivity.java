package com.example.queryscore;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.scoreList.AsyncLoadScore;
import com.example.service.Course;

public class ClassDetailActivity extends Activity {
	private TextView properity;
	private TextView id;
	private TextView name;
	private TextView testType;
	private TextView length;
	private TextView scoreType;
	private TextView score;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_class_detail);
		
		properity = (TextView) findViewById(R.id.class_properity);
		id		  = (TextView) findViewById(R.id.class_id);
		name	  = (TextView) findViewById(R.id.class_name);
		testType  = (TextView) findViewById(R.id.class_testType);
		length	  = (TextView) findViewById(R.id.class_length);
		scoreType = (TextView) findViewById(R.id.class_scoreType);
		score	  = (TextView) findViewById(R.id.class_score);
		
		int position = getIntent().getIntExtra(ScoreListActivity.POSITION, 0);
		Course course = AsyncLoadScore.scores.get(position);
		properity.setText(course.getProperity());
		id.setText(course.getId());
		name.setText(course.getName());
		testType.setText(course.getTestType());
		length.setText(course.getLength());
		scoreType.setText(course.getScoreType());
		score.setText(course.getScore());
	}

}

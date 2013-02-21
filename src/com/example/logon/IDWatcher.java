package com.example.logon;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;

import com.example.queryscore.MainActivity;
import com.example.queryscore.R;

public class IDWatcher implements TextWatcher{
	private Context context 	= null;
	private int count			= 0;
	private final int LENGTH 	= 8;
	
	public IDWatcher(Context context){
		this.context = context;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		this.count = s.length();
	}
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		List<String> data = new ArrayList<String>();
		UserDatabase db = new UserDatabase(context);
		SQLiteDatabase query = db.getReadableDatabase();
		String sql = String.format("SELECT * FROM %s WHERE %s LIKE '%s", 
				UserDatabase.TABLE_NAME, UserDatabase.COLUMN_USER_NAME, s);
		Cursor cursor = query.rawQuery(sql+"%'", null);
		
		while (cursor.moveToNext()) {
			data.add(cursor.getString(cursor.getColumnIndex(UserDatabase.COLUMN_USER_NAME)));
		}
		if(data.size() != 0 && s.length() != LENGTH){
			if (this.count == LENGTH){
				MainActivity.student_passwd.setText("");
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.auto_complete_list, data);
			MainActivity.student_id.setAdapter(adapter);
		}
		query.close();
		db.close();
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		if(s.length() == LENGTH && this.count != LENGTH){
			UserDatabase db = new UserDatabase(context);
			SQLiteDatabase query = db.getReadableDatabase();
			String sql = String.format("SELECT %s FROM %s WHERE %s=%s",
					UserDatabase.COLUMN_USER_PASSWD, UserDatabase.TABLE_NAME, 
					UserDatabase.COLUMN_USER_NAME, MainActivity.student_id.getText().toString());
			Cursor cursor = query.rawQuery(sql, null);
			if (cursor.getCount() != 0){
				cursor.moveToFirst();
				MainActivity.student_passwd.setText(
						cursor.getString(cursor.getColumnIndex(UserDatabase.COLUMN_USER_PASSWD)));
			}
			query.close();
			db.close();
		}
			
	}

}

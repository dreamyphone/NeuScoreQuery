package com.example.logon;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserDatabase extends SQLiteOpenHelper {
	private final static int VERSION 				= 1;
	private final static String DATABASE_NAME 		= "databases";
	public final static String TABLE_NAME 			= "users";
	public final static String COLUMN_ID 			= "id";
	public final static String COLUMN_USER_NAME 	= "name";
	public final static String COLUMN_USER_PASSWD 	= "passwd";
	
	public UserDatabase(Context context, String name, 
			SQLiteDatabase.CursorFactory factory, int version){
		super(context, name, factory, version);
	}
	
	public UserDatabase(Context context, String name, int version){
		super(context, name, null, version);
	}
	
	public UserDatabase(Context context){
		super(context, DATABASE_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(
				String.format("CREATE TABLE IF NOT EXISTS %s(%s INTEGER PRIMARY KEY AUTOINCREMENT," +
						" %s nvarchar(8), %s nvarchar(25))", 
						TABLE_NAME, COLUMN_ID, COLUMN_USER_NAME, COLUMN_USER_PASSWD));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("DEBUG", "database updated.");
	}

}

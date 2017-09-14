package com.example.uriondb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

	public DBOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public DBOpenHelper(Context context) {
		super(context, "urion", null, 3);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table IF NOT EXISTS user(name varchar(50),"
				+ " sex varchar(10), age integer,height integer,weight integer,touxiang blob)");
		db.execSQL("create table IF NOT EXISTS sdp (name varchar(50),time date,sys integer,dia integer,pul integer)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		db.execSQL("drop table if exists user");
		db.execSQL("drop table if exists sdp");
		onCreate(db);
	}

}

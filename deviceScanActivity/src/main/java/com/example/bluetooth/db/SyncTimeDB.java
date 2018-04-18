package com.example.bluetooth.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class SyncTimeDB extends DataBaseHelper {
	private final static String TAG = "SyncTimeDB";
	public SyncTimeDB(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 插入同步时间
	 * @param db
	 * @param time
	 */
	public void insertSyncTime(String time)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			String id=null;
			db=getWritableDatabase();
			cursor=db.rawQuery("select id FROM synctime ORDER BY id desc limit 1", null);
			if (cursor.moveToFirst()) {
				id=cursor.getString(cursor.getColumnIndex("id"));
			}
			cursor.close();
			ContentValues values=new ContentValues();
			values.put("syncTime", time);
			if (id!=null) {
				db.update("synctime", values, "id= ?", new String[]{id});
			}
			else
			{
				db.insert("synctime", null, values);
			}
			db.close();
		}
		catch(Exception e)
		{
			if (db!=null) {
				db.close();
			}
			if (cursor!=null) {
				cursor.close();
			}
			Log.d(TAG, e.toString());
		
		}
	
	}
	/**
	 * 获取同步时间
	 * @return
	 */
	public String getSyncTime()
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			String lastedSyncTime=null;
			db=getWritableDatabase();
			cursor=db.query("synctime", null, null,null, null, null, null, null);
			if(cursor.moveToFirst())
			{
				do{
					lastedSyncTime=cursor.getString(cursor.getColumnIndex("syncTime"));//获取原始计时器名称
				}
				while(cursor.moveToNext());
			}
			cursor.close();
			db.close();
			return lastedSyncTime;
		}
		catch (Exception e) {
			if (db!=null) {
				db.close();
			}
			if (cursor!=null) {
				cursor.close();
			}
			Log.d(TAG, e.toString());
			return null;
		}
	}
	/**
	 * 将track插入到最后一行中
	 * @param track
	 */
	public void updateTrack(String track)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			String id=null;
			db=getWritableDatabase();
			cursor=db.rawQuery("select id FROM synctime ORDER BY id desc limit 1", null);
			if (cursor.moveToFirst()) {
				id=cursor.getString(cursor.getColumnIndex("id"));
			}
			cursor.close();
			Log.d(TAG, "id:"+id+",track:"+track);
			ContentValues values=new ContentValues();
			values.put("track", track);
			db.update("synctime", values, "id= ?", new String[]{id});
			db.close();
		}
		catch(Exception e)
		{
			if (db!=null) {
				db.close();
			}
			if (cursor!=null) {
				cursor.close();
			}
			Log.d(TAG, e.toString());
		}
	}
	/**
	 * 插入限时模式时间
	 * @param timeset
	 */
	public void updateSetTime(String timeset)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			String id=null;
			db=getWritableDatabase();
			cursor=db.rawQuery("select id FROM synctime ORDER BY id desc limit 1", null);
			if (cursor.moveToFirst()) {
				id=cursor.getString(cursor.getColumnIndex("id"));
			}
			cursor.close();
			ContentValues values=new ContentValues();
			values.put("timeset", timeset);
			db.update("synctime", values, "id= ?", new String[]{id});
			db.close();
		}
		catch(Exception e)
		{
			if (db!=null) {
				db.close();
			}
			if (cursor!=null) {
				cursor.close();
			}
			Log.d(TAG, e.toString());
		}
	}
	
	public String getTrack()
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			String track=null;
			db=getWritableDatabase();
			cursor=db.rawQuery("select track FROM synctime ORDER BY id desc limit 1", null);
			if (cursor.moveToFirst()) {
				track=cursor.getString(cursor.getColumnIndex("track"));
			}
			cursor.close();
			db.close();
			return track;
		}
		catch(Exception e)
		{
			if (db!=null) {
				db.close();
			}
			if (cursor!=null) {
				cursor.close();
			}
			Log.d(TAG, e.toString());
			return null;
		}
	}
	
	public String getTimeset()
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			String track=null;
			db=getWritableDatabase();
			cursor=db.rawQuery("select timeset FROM synctime ORDER BY id desc limit 1", null);
			if (cursor.moveToFirst()) {
				track=cursor.getString(cursor.getColumnIndex("timeset"));
			}
			cursor.close();
			db.close();
			return track;
		}
		catch(Exception e)
		{
			if (db!=null) {
				db.close();
			}
			if (cursor!=null) {
				cursor.close();
			}
			Log.d(TAG, e.toString());
			return null;
		}
		
	}
}

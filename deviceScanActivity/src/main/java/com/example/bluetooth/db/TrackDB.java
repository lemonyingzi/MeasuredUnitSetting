package com.example.bluetooth.db;

import java.util.ArrayList;
import java.util.List;

import com.example.bluetooth.list.Track;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class TrackDB extends DataBaseHelper {
	final private String TAG="TrackDB";
	public TrackDB(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 获取所有Track
	 * @return
	 */
	public List<Track> GetTracks()
	{
		SQLiteDatabase db=null;
		List<Track> result=new ArrayList<Track>();
		try{
			db=getWritableDatabase();
			Cursor cursor = db.query("track", null, null, null, null, null, null, null);
			if (cursor.moveToFirst()) {
				do{
					String trackStr=cursor.getString(cursor.getColumnIndex("track"));
					String selected=cursor.getString(cursor.getColumnIndex("selected"));
					String longitude=cursor.getString(cursor.getColumnIndex("longitude"));
					String latitude=cursor.getString(cursor.getColumnIndex("latitude"));
					Log.d(TAG, "track:"+trackStr);
					Track track=new Track(trackStr, selected,longitude,latitude);
					result.add(track);
				}while(cursor.moveToNext());
			}
			db.close();
			return result;
		}
		catch (Exception e) {
			Log.d(TAG, "异常："+e.toString());
			if (db!=null) {
				db.close();
			}
			return null;
		}
	}
	/**
	 * 获取所有Track
	 * @return
	 */
	public List<String> GetTrackNames()
	{
		Log.d(TAG, "get tracks");
		SQLiteDatabase db=null;
		List<String> result=new ArrayList<String>();
		try{
			db=getWritableDatabase();
			Cursor cursor = db.query("track", null, null, null, null, null, null, null);
			if (cursor.moveToFirst()) {
				do{
					String trackStr=cursor.getString(cursor.getColumnIndex("track"));
					Log.d(TAG, "取track:"+trackStr);
					result.add(trackStr);
				}while(cursor.moveToNext());
			}
			db.close();
			return result;
		}
		catch (Exception e) {
			Log.d(TAG, "异常："+e.toString());
			if (db!=null) {
				db.close();
			}
			return null;
		}
	}
	/**
	 * 插入track
	 * @param track
	 */
	public void insert(String track, String longitude,String latitude) {
		SQLiteDatabase db=getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("track", track);
		values.put("selected", "false");
		values.put("longitude", longitude);
		values.put("latitude", latitude);
		db.insert("track", null, values);
		db.close();
	}
	
	public void delete(String track)
	{
		SQLiteDatabase database=getWritableDatabase();
		try
		{
			database.execSQL("delete from track where track='"+track+"'");
			database.close();
		}
		catch(Exception ex)
		{
			Log.d(TAG, ex.toString());
		}
	}
	/**
	 * 根据track设置selected
	 * @param track
	 */
	public void updateSelected(String track)
	{
		SQLiteDatabase database=getWritableDatabase();
		try{
			database.execSQL("update track set selected='false'");//先将所有的track设置为false
			database.execSQL("update track set selected='true' where track='"+track+"'");
			database.close();
		}
		catch(Exception ex)
		{
			Log.d(TAG,ex.toString());
		}
	}
	
	/**
	 * 查找选择的track
	 * @return
	 */
	public String getSelectedTrack()
	{
		SQLiteDatabase database=getWritableDatabase();
		try{
			String trackName=null;
			Cursor cursor=database.rawQuery("select track from track where selected='true'",null);
			if (cursor.moveToFirst()) {
				trackName= cursor.getString(cursor.getColumnIndex("track"));
			}
			cursor.close();
			database.close();
			return trackName;
		}
		catch(Exception ex)
		{
			Log.d(TAG,ex.toString());
			return null;
		}
	}
	
}

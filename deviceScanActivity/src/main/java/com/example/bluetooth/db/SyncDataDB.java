package com.example.bluetooth.db;

import com.example.bluetooth.data.GlobalVariable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class SyncDataDB extends DataBaseHelper {
	private final static String TAG = "SyncDataDB";
	public SyncDataDB(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);

	}
	
	/**
	 * 插入数据同步表
	 * @param db
	 * @param logCounter
	 * @param roundCounter
	 * @param mode
	 * @param roundIRID
	 * @param LAP
	 * @param raceTime
	 * @param receiveOKCounter
	 * @param timerID
	 * @param timerVersion
	 */
	public void insert(int logCounter,int roundCounter,String mode,String roundIRID,int LAP,int raceTime,int receiveOKCounter,String calculated)
	{
		SQLiteDatabase db=getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("logCounter", logCounter);
		values.put("roundCounter", roundCounter);
		values.put("mode", mode);
		values.put("roundIRID", roundIRID);
		values.put("LAP", LAP);
		values.put("raceTime", raceTime);
		values.put("receiveOKCounter", receiveOKCounter);
		values.put("deviceID", GlobalVariable.getIrTimerID());
		Log.d(TAG, "insert irtimerid:"+GlobalVariable.getIrTimerID());
		values.put("calculated", calculated);
		db.insert("syncdata", null, values);
		db.close();
	}
	/**
	 * 查询表中是否存在
	 * @param logCounter
	 * @param roundCounter
	 * @return
	 */
	public boolean isLogcounterRoundcounterDeviceIdExist(int logCounter,int roundCounter)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			Log.d(TAG, "query getIrTimerID:"+GlobalVariable.getIrTimerID());
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from syncdata t where t.logCounter='"+logCounter+"' and t.roundCounter='"+roundCounter+"' and t.deviceID='"+GlobalVariable.getIrTimerID()+"'", null);
			if (cursor.moveToFirst()) {
				return true;//存在
			}
			return false;
		}
		catch(Exception e)
		{
			if (cursor!=null) {
				cursor.close();
			}
			if (db!=null) {
				db.close();
			}
			Log.d(TAG, "异常："+e.toString());
			return false;
		}
	}
	/**
	 * 更新同步数据表的是否计算字段
	 * @param db
	 * @param id
	 * @param caculated
	 */
	public void updateCaculated(String id,String caculated)
	{
		SQLiteDatabase database=getWritableDatabase();
		database.execSQL("update syncdata set calculated='"+caculated+"' where id='"+id+"' and deviceID='"+GlobalVariable.getIrTimerID()+"'");
//		ContentValues values=new ContentValues();
//		values.put("calculated", caculated);
//		database.update("syncdata", values, "id= ?", new String[]{id});
		database.close();
	}

	/**
	 * 查找calculated 为false和设备ID
	 * @return
	 */
	public Cursor queryCalculatedFalse()
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			Log.d(TAG, "query getIrTimerID:"+GlobalVariable.getIrTimerID());
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from syncdata t where t.calculated='false'", null);
//			db.close();
			return cursor;
		}
		catch(Exception e)
		{
			if (cursor!=null) {
				cursor.close();
			}
			if (db!=null) {
				db.close();
			}
			Log.d(TAG, "异常："+e.toString());
			return null;
		}
	}
	
	public void query()
	{
		SQLiteDatabase database=null;
		Cursor cursor=null;
		try{
			database=getWritableDatabase();
			cursor = database.query("syncdata", null, null, null, null, null, null, null);
		    if (cursor.moveToFirst()) {
				do {
					String logCounter=cursor.getString(cursor.getColumnIndex("logCounter"));
					String roundCounter=cursor.getString(cursor.getColumnIndex("roundCounter"));
					String LAP=cursor.getString(cursor.getColumnIndex("LAP"));
					String raceTime=cursor.getString(cursor.getColumnIndex("raceTime"));
					Log.d(TAG, "logCounter:"+logCounter+",roundcounter:"+roundCounter+",Lap:"+LAP+",raceTime:"+raceTime);
				} while (cursor.moveToNext());
			}
		}
		catch (Exception e) {
			Log.d(TAG, e.toString());
		}
	}
	/**
	 * 根据roundcounter和deviceid删除
	 * @param roundCounter
	 * @param deviceId
	 */
	public void deleteAccordingToRoundcounter(String roundCounter,String deviceId)
	{
		SQLiteDatabase database=null;
		try
		{
			database=getWritableDatabase();
			database.execSQL("delete from syncdata WHERE roundCounter='"+roundCounter+"' and deviceID='"+deviceId+"'");
			database.close();
		}
		catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		
	}
	/**
	 * 清空数据库
	 */
	public void clear()
	{
		SQLiteDatabase database=null;
		try
		{
			database=getWritableDatabase();
			database.execSQL("delete from syncdata");
			database.execSQL("update sqlite_sequence SET seq = 0 where name ='syncdata'");
			database.close();
		}
		catch (Exception e) {
			Log.d(TAG, e.toString());
		}
	}
}

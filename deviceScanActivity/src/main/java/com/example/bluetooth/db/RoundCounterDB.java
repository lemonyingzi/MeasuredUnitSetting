package com.example.bluetooth.db;

import java.util.ArrayList;
import java.util.List;

import com.example.bluetooth.data.GlobalVariable;
import com.example.bluetooth.list.DetailedResult;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class RoundCounterDB extends DataBaseHelper {
	private final static String TAG = "RoundCounterDB";
	public RoundCounterDB(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	/**
	 * ����roundcounter��
	 * @param db
	 * @param logCounter
	 * @param roundCounter
	 * @param IRID
	 * @param LAP
	 * @param time
	 * @param LAPTime
	 * @param receiveOKCounter
	 */
	public void insert(int logCounter,int roundCounter,String IRID,int LAP,int time,int LAPTime,int receiveOKCounter,
			String startDate,String startTime,String temperature,String humidity,String pressure,String calculated,String mode,String deviceId)
	{
		SQLiteDatabase db=null;
		try{
			db=getWritableDatabase();
			ContentValues values=new ContentValues();
			values.put("logCounter", logCounter);
			values.put("roundCounter", roundCounter);
			values.put("IRID", IRID);
			values.put("LAP", LAP);
			values.put("time", time);
			values.put("LAPTime", LAPTime);
			values.put("receiveOKCounter", receiveOKCounter);
			values.put("startDate", startDate);
			values.put("startTime", startTime);
			values.put("temperature", temperature);
			values.put("humidity",humidity);
			values.put("pressure", pressure);//ʪ��
			values.put("mode", mode);
			values.put("deviceID",deviceId);
			values.put("calculated", calculated);
			db.insert("roundcounter", null, values);
			db.close();
		}
		catch(Exception e)
		{
			Log.d(TAG, e.toString());
			if (db!=null) {
				db.close();
			}
		}
	}
	
	/**
	 * ��roundCounter���в�ѯ���roundCounter
	 * @return
	 */
	public Cursor query(String deviceId)
	{
		SQLiteDatabase db=null;
		try{
			db=getWritableDatabase();
			
			Cursor cursor = db.rawQuery("select * from roundcounter where deviceID='"+deviceId+"'", null);
			cursor.moveToLast();
			db.close();
			return cursor;
		}
		catch (Exception e) {
			Log.d(TAG, "�쳣��"+e.toString());
			if (db!=null) {
				db.close();
			}
			return null;
		}
	
	}
	public void deleteAccordingToRoundCounter(String roundCounter,String deviceId)
	{
		SQLiteDatabase database=null;
		try
		{
			database=getWritableDatabase();
			database.execSQL("delete from roundcounter WHERE roundCounter='"+roundCounter+"' and deviceID='"+deviceId+"'");
			database.close();
		}
		catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		
	}
	/**
	 * ��roundCounter���л�ȡ���һ�ε�ʱ��
	 * @return
	 */
	public int getTime()
	{
		int time=0;
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try{
			db=getWritableDatabase();
			cursor = db.rawQuery("select * from roundcounter where deviceID='"+GlobalVariable.getIrTimerID()+"'", null);
		    cursor.moveToLast();
		    time = cursor.getInt(cursor.getColumnIndex("time"));
		    cursor.close();
		    db.close();
			return time;
		}
		catch(Exception e)
		{
			Log.d(TAG, "�쳣��"+e.toString());
			if (db!=null) {
				db.close();
			}
			if (cursor!=null) {
				cursor.close();
			}
			return 0;
		}
	}
	
	/**
	 * ����RoundCounter��ȡDetailedResult
	 * @param roundCounter
	 * @return
	 */
	public List<DetailedResult> QueryDetailedResultAccordingToRoundCounter(String roundCounter,String deviceId)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			com.example.bluetooth.data.DataConvert dataConvert=new com.example.bluetooth.data.DataConvert();
			List<DetailedResult> resultList=new ArrayList<DetailedResult>(); 
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from roundcounter t where t.roundCounter='"+roundCounter+"' and deviceID='"+deviceId+"'", null);
			if (cursor.moveToFirst()) {
				do {
	   				String Lap=cursor.getString(cursor.getColumnIndex("LAP"));
	   				int Time=cursor.getInt(cursor.getColumnIndex("time"));
	   				int LapTime=cursor.getInt(cursor.getColumnIndex("LAPTime"));
	   				String timeStr=dataConvert.milsecondsToHHMMSS(Time);
	   				String LapTimeStr=dataConvert.milsecondsToHHMMSS(LapTime);
	   				String Receive=cursor.getString(cursor.getColumnIndex("receiveOKCounter"));
	   				DetailedResult result=new DetailedResult(Lap,timeStr,LapTimeStr,Receive);
	   				resultList.add(result);
				} while (cursor.moveToNext());
			}
			cursor.close();
			db.close();
			return resultList;
		}
		catch(Exception e)
		{
			Log.d(TAG, "�쳣��"+e.toString());
			if (cursor!=null) {
				cursor.close();
			}
			if (db!=null) {
				db.close();
			}
			return null;
		}
	}
	
	/**
	 * ��������calculatedΪfalse�Ĳ��ظ�������
	 * @return
	 */
	public Cursor queryCalculatedFalse(String deviceId)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try{
			db=getWritableDatabase();
			cursor=db.rawQuery("select distinct roundCounter from roundcounter t where t.calculated='false' and t.deviceID='"+deviceId+"'", null);
			return cursor;
		}
		catch(Exception e)
		{
			Log.d(TAG, "�쳣��"+e.toString());
			if (cursor!=null) {
				cursor.close();
			}
			if (db!=null) {
				db.close();
			}
			return null;
		}
	}
	public Cursor queryCalculatedFalseDeviceId()
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try{
			db=getWritableDatabase();
			cursor=db.rawQuery("select distinct deviceID from roundcounter t where t.calculated='false'", null);
			return cursor;
		}
		catch(Exception e)
		{
			Log.d(TAG, "�쳣��"+e.toString());
			if (cursor!=null) {
				cursor.close();
			}
			if (db!=null) {
				db.close();
			}
			return null;
		}
	}
	/**
	 * ����roundCounter��������roundcounter��Ӧ������
	 * @param roundCounter
	 * @return
	 */
	public Cursor queryAccordingToRoundCounter(int roundCounter)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from roundcounter t where t.roundCounter='"+roundCounter+"' and t.deviceID='"+GlobalVariable.getIrTimerID()+"'", null);
			return cursor;
		}
		catch(Exception e)
		{
			Log.d(TAG, "�쳣��"+e.toString());
			if (cursor!=null) {
				cursor.close();
			}
			if (db!=null) {
				db.close();
			}
			return null;
		}
	}
	/**
	 * ����roundCounter��������roundcounter��Ӧ������
	 * @param roundCounter
	 * @return
	 */
	public Cursor queryAccordingToRoundCounter(int roundCounter,String deviceId)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from roundcounter t where t.roundCounter='"+roundCounter+"' and t.deviceID='"+deviceId+"'", null);
			return cursor;
		}
		catch(Exception e)
		{
			Log.d(TAG, "�쳣��"+e.toString());
			if (cursor!=null) {
				cursor.close();
			}
			if (db!=null) {
				db.close();
			}
			return null;
		}
	}
	/**
	 * ������Сʱ��Ͷ�Ӧ��Ȧ��
	 * @return
	 */
	public Cursor queryMinTimeAndLap(String roundCounter)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			db=getWritableDatabase();
			cursor=db.rawQuery("select * FROM roundcounter where roundCounter='"+roundCounter+"' and deviceID='"+GlobalVariable.getIrTimerID()+"' ORDER BY LAPTime asc limit 1", null);
			return cursor;
		}
		catch(Exception e)
		{
			Log.d(TAG, "�쳣��"+e.toString());
			if (cursor!=null) {
				cursor.close();
			}
			if (db!=null) {
				db.close();
			}
			return null;
		}
	}
	
	/**
	 * ����calculated
	 * @param roundCounter
	 * @param calculated
	 */
	public void upateCalculated(String roundCounter,String calculated,String deviceId)
	{
		SQLiteDatabase database=getWritableDatabase();
		String sql="update roundcounter set calculated='"+calculated+"' where roundCounter='"+roundCounter+"' and deviceID='"+deviceId+"'";
		database.execSQL(sql);
//		ContentValues values=new ContentValues();
//		values.put("calculated", calculated);
//		database.update("roundcounter", values, "roundCounter= ?", new String[]{roundCounter});
		database.close();
	}
	/**
	 * ȡ��Ӧroundcounter��laptime���ֵ
	 * @param roundCounter
	 * @return
	 */
	public int getMaxLapTimeAccordingToRoundCounter(String roundCounter,String deviceId)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			db=getWritableDatabase();
//			Cursor cursor=db.rawQuery("select min(time),LAP from roundcounter group by LAP", null);
			cursor=db.rawQuery("select LAPTime FROM roundcounter t where t.roundCounter='"+roundCounter+"' and deviceID='"+deviceId+"' ORDER BY LAPTime desc limit 1", null);
			if (cursor.moveToFirst()) {
				int maxLapTime=cursor.getInt(cursor.getColumnIndex("LAPTime"));
				return maxLapTime;
			}
			db.close();
			cursor.close();
			return 0;
		}
		catch(Exception e)
		{
			Log.d(TAG, "�쳣��"+e.toString());
			if (cursor!=null) {
				cursor.close();
			}
			if (db!=null) {
				db.close();
			}
			return 0;
		}
	}
	/**
	 * ȡ��Ӧroundcounter��laptime��Сֵ
	 * @param roundCounter
	 * @return
	 */
	public int getMinLapTimeAccordingToRoundCounter(String roundCounter,String deviceId)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			int maxLapTime=0;
			db=getWritableDatabase();
//			Cursor cursor=db.rawQuery("select min(time),LAP from roundcounter group by LAP", null);
			cursor=db.rawQuery("select LAPTime FROM roundcounter t where t.roundCounter='"+roundCounter+"' and t.deviceID='"+deviceId+"' ORDER BY LAPTime asc limit 1", null);
			if (cursor.moveToFirst()) {
				maxLapTime=cursor.getInt(cursor.getColumnIndex("LAPTime"));
			}
			cursor.close();
			db.close();
			return maxLapTime;
		}
		catch(Exception e)
		{
			Log.d(TAG, "�쳣��"+e.toString());
			if (cursor!=null) {
				cursor.close();
			}
			if (db!=null) {
				db.close();
			}
			return 0;
		}
	}
	/**
	 * �������в��ظ���IRID
	 * @return
	 */
	public Cursor queryDistinctIRID()
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try{
			db=getWritableDatabase();
			cursor=db.query(true, "roundcounter", new String[]{"id","roundCounter","IRID"}, null, null, "IRID", null, null, null, null);
			db.close();
			return cursor;
		}
		catch(Exception e)
		{
			Log.d(TAG, "�쳣��"+e.toString());
			if (cursor!=null) {
				cursor.close();
			}
			if (db!=null) {
				db.close();
			}
			return null;
		}
	}
	
	/**
	 * ������ݿ�
	 */
	public void clear()
	{
		SQLiteDatabase database=null;
		try
		{
			database=getWritableDatabase();
			database.execSQL("delete from roundcounter");
			database.execSQL("update sqlite_sequence SET seq = 0 where name ='roundcounter'");
			database.close();
		}
		catch (Exception e) {
			Log.d(TAG, e.toString());
		}
	}
}

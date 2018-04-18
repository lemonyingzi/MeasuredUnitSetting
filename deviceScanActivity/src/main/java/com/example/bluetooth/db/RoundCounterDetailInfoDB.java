package com.example.bluetooth.db;

import java.util.ArrayList;
import java.util.List;

import com.example.bluetooth.data.GlobalVariable;
import com.example.bluetooth.le.R;
import com.example.bluetooth.list.Result;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class RoundCounterDetailInfoDB extends DataBaseHelper {
	private final static String TAG = "RoundCounterDetailInfoDB";
	private Context context;
	public RoundCounterDetailInfoDB(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		this.context=context;
	}
	/**
	 * 插入roundcounterdetailinfo表
	 * @param db
	 * @param time
	 * @param roundCounter
	 * @param RCCar
	 * @param Temperature
	 * @param Humidity
	 * @param Pressure
	 * @param LAPS
	 * @param TolTime
	 * @param BestTime
	 * @param BestLap
	 */
	public void insert(String startDate,String startTime,int roundCounter,String irid,String RCCar,String track,String Temperature,String Humidity,
			String Pressure,int laps,String tolTime,String averageTime,String bestTime,int bestLap,String mode,String deviceId)
	{
		SQLiteDatabase database=null;
		Log.d(TAG, "track:"+track);
		try{
			database=getWritableDatabase();
			ContentValues values=new ContentValues();
			values.put("startDate", startDate);
			values.put("startTime", startTime);
			values.put("roundcounter", roundCounter);
			values.put("irid", irid);
			values.put("RCCar", RCCar);
			values.put("track", track);
			values.put("Temperature", Temperature);
			values.put("Humidity",Humidity);
			values.put("Pressure", Pressure);
			values.put("LAPS", laps);
			values.put("TolTime", tolTime);
			values.put("AverageTime", averageTime);
			values.put("BestTime", bestTime);
			values.put("BestLap", bestLap);
			values.put("mode", mode);
			values.put("deviceID", deviceId);
			database.insert("roundcounterdetailinfo", null, values);
			Log.d(TAG, "insert success");
			database.close();
		}catch(Exception e)
		{
			Log.d(TAG, e.toString());
			if (database!=null) {
				database.close();
			}
		}
	}
	/**
	 * 查询，并实例化result
	 * @param db
	 * @return
	 */
	public List<Result> query()
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			db=getWritableDatabase();
			List<Result> resultList=new ArrayList<Result>(); 
			cursor=db.rawQuery("select * from roundcounterdetailinfo", null);
			if(cursor.moveToLast())
	   		{
	   			do{
	   				String roundCounter=cursor.getString(cursor.getColumnIndex("roundcounter"));
	   				String startDate=cursor.getString(cursor.getColumnIndex("startDate"));
	   				String startTime=cursor.getString(cursor.getColumnIndex("startTime"));
	   				String laps=cursor.getString(cursor.getColumnIndex("LAPS"));
	   				String tolTime=cursor.getString(cursor.getColumnIndex("TolTime"));
	   				String bestTime=cursor.getString(cursor.getColumnIndex("BestTime"));		
	   				String bestLap=cursor.getString(cursor.getColumnIndex("BestLap"));
	   				String track=cursor.getString(cursor.getColumnIndex("track"));
	   				String RCCar=cursor.getString(cursor.getColumnIndex("RCCar"));
	   				String temperature=cursor.getString(cursor.getColumnIndex("Temperature"));
	   				String humidity=cursor.getString(cursor.getColumnIndex("Humidity"));
	   				String averageTime=cursor.getString(cursor.getColumnIndex("AverageTime"));
	   				String deviceID=cursor.getString(cursor.getColumnIndex("deviceID"));
	   				Result result=new Result(roundCounter, startDate,startTime, laps, tolTime, bestTime,bestLap,
	   						track,RCCar,temperature,humidity,deviceID);
	   				resultList.add(result);
	   			}
	   			while(cursor.moveToPrevious());
	   			cursor.close();
	   			db.close();
		   		return resultList;
	   		}
	   		else {
				Log.d(TAG, "cursor 为空");
				cursor.close();
				db.close();
				return null;
			}
	    }
		catch(Exception e)
		{
			Log.d(TAG, e.toString());
			if (cursor!=null) {
				cursor.close();
			}
			if (db!=null) {
				db.close();
			}
			return null;
		}
	}
	
	public List<Result> query(String startDateStr,String endDateStr,String trackStr,String rc,String mode)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			if (mode.equals(context.getResources().getString(R.string.timeLimit))) {
				mode="Time";
			}
			else if (mode.equals(context.getResources().getString(R.string.freeStr))) {
				mode="Free";
			}
			db=getWritableDatabase();
			List<Result> resultList=new ArrayList<Result>(); 
			String sql="select * from roundcounterdetailinfo where startDate between '"+startDateStr+"' and '"+endDateStr+"'";
			String trackSql="";
			String rcSql="";
			String modeSql="";
			if (trackStr.equals(context.getResources().getString(R.string.all))) {
				trackSql="";
			}
			else if (trackStr.equals(context.getResources().getString(R.string.unknow))) {
				trackSql=" and (track='未指定' or track='Unknow' or track='アンノウン')";
			}
			else{
				trackSql=" and track='"+trackStr+"'";
			}
			if (!rc.equals(context.getResources().getString(R.string.all))) {
				rcSql=" and RCCar='"+rc+"'";
			}
			if (!mode.equals(context.getResources().getString(R.string.all))) {
				modeSql=" and mode='"+mode+"'";
			}
			
			Log.d(TAG, "List SQL:"+sql+trackSql+rcSql+modeSql);
			cursor=db.rawQuery(sql+trackSql+rcSql+modeSql+" order by startDate,startTime", null);
			if(cursor.moveToLast())
	   		{
	   			do{
	   				String roundCounter=cursor.getString(cursor.getColumnIndex("roundcounter"));
	   				String startDate=cursor.getString(cursor.getColumnIndex("startDate"));
	   				String startTime=cursor.getString(cursor.getColumnIndex("startTime"));
	   				String laps=cursor.getString(cursor.getColumnIndex("LAPS"));
	   				String tolTime=cursor.getString(cursor.getColumnIndex("TolTime"));
	   				String bestTime=cursor.getString(cursor.getColumnIndex("BestTime"));		
	   				String bestLap=cursor.getString(cursor.getColumnIndex("BestLap"));
	   				String track=cursor.getString(cursor.getColumnIndex("track"));
	   				String RCCar=cursor.getString(cursor.getColumnIndex("RCCar"));
	   				String temperature=cursor.getString(cursor.getColumnIndex("Temperature"));
	   				String humidity=cursor.getString(cursor.getColumnIndex("Humidity"));
	   				String averageTime=cursor.getString(cursor.getColumnIndex("AverageTime"));
	   				String deviceID=cursor.getString(cursor.getColumnIndex("deviceID"));
	   				Result result=new Result(roundCounter, startDate,startTime, laps, tolTime, bestTime,bestLap,
	   						track,RCCar,temperature,humidity,deviceID);
	   				resultList.add(result);
	   			}
	   			while(cursor.moveToPrevious());
	   			cursor.close();
	   			db.close();
		   		return resultList;
	   		}
	   		else {
				Log.d(TAG, "cursor 为空");
				cursor.close();
				db.close();
				return null;
			}
	    }
		catch(Exception e)
		{
			Log.d(TAG, e.toString());
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
	 * 根据roundCounter删除
	 * @param track
	 */
	public void deleteAccorrdingToRoundCounter(String roundCounter,String deviceID)
	{
		SQLiteDatabase database=getWritableDatabase();
		try
		{
			database.execSQL("delete from roundcounterdetailinfo where roundcounter='"+roundCounter+"' and deviceID='"+deviceID+"'");
			database.close();
		}
		catch(Exception ex)
		{
			Log.d(TAG, ex.toString());
		}
	}
	/**
	 * 更新roundCounterdetailinfo表
	 * @param db
	 * @param roundCounter
	 * @param LAPS
	 * @param TolTime
	 * @param BestTime
	 * @param BestLap
	 */
	public void update(int roundCounter,int laps,String tolTime,String averageTime,String bestTime,int bestLap,String deviceId)
	{
		SQLiteDatabase database=null;
		try{
			database=getWritableDatabase();
			database.execSQL("update roundcounterdetailinfo set LAPS='"+laps+"' and TolTime='"+tolTime+"' "
					+ "and AverageTime='"+averageTime+"' and BestTime='"+bestTime+"' and BestLap='"+bestLap+"' where roundcounter='"+roundCounter+"' and deviceID='"+deviceId+"'");
			
//			ContentValues values=new ContentValues();
//			values.put("LAPS", laps);
//			values.put("TolTime", tolTime);
//			values.put("AverageTime", averageTime);
//			values.put("BestTime", bestTime);
//			values.put("BestLap", bestLap);
//			database.update("roundcounterdetailinfo", values, "roundcounter= ?", new String[]{String.valueOf(roundCounter)});
		
			database.close();
			
		}catch(Exception e)
		{
			Log.d(TAG, e.toString());
			if (database!=null) {
				database.close();
			}
		}
	}
	
	/**
	 * 根据roundcounter更新track
	 * @param roundCounter
	 * @param track
	 */
	public void updateTrack(String roundCounter,String track,String deviceId)
	{
		SQLiteDatabase database=null;
		try{
			database=getWritableDatabase();
			database.execSQL("update roundcounterdetailinfo set track='"+track+"' where roundcounter='"+roundCounter+"' and deviceID='"+deviceId+"'");
//			ContentValues values=new ContentValues();
//			values.put("track", track);
//			database.update("roundcounterdetailinfo", values, "roundcounter= ?", new String[]{String.valueOf(roundCounter)});
			database.close();
			
		}catch(Exception e)
		{
			Log.d(TAG, e.toString());
			if (database!=null) {
				database.close();
			}
		}
	}
	/**
	 * 更新备注
	 * @param roundCounter
	 * @param remark
	 */
	public void updateRemark(String roundCounter,String remark,String deviceId)
	{
		SQLiteDatabase database=null;
		try{
			database=getWritableDatabase();
			ContentValues values=new ContentValues();
			values.put("remark", remark);
			database.update("roundcounterdetailinfo", values, "roundcounter= ?", new String[]{String.valueOf(roundCounter)});
			database.execSQL("update roundcounterdetailinfo set remark='"+remark+"' where roundcounter='"+roundCounter+"' and deviceID='"+deviceId+"'");
			database.close();
			
		}catch(Exception e)
		{
			Log.d(TAG, e.toString());
			if (database!=null) {
				database.close();
			}
		}
	}
	
	public String getRemark(String roundCounter,String deviceId)
	{
		 SQLiteDatabase db=null;
		 Cursor cursor=null;
		 try
		 {
			 String remark=null;
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from roundcounterdetailinfo t where t.roundcounter='"+roundCounter+"' and t.deviceID='"+deviceId+"'", null);
			if(cursor.moveToFirst())//说明存在一样的
			{
				remark=cursor.getString(cursor.getColumnIndex("remark"));
			
			}
			cursor.close();
			db.close();
			return remark;
		 }
		 catch(Exception e)
		 {
			 Log.d(TAG, e.toString());
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
	 * 根据roundcounter更新RC
	 * @param roundCounter
	 * @param track
	 */
	public void updateRC(String roundCounter,String RC,String deviceId)
	{
		SQLiteDatabase database=null;
//		try{
			database=getWritableDatabase();
//			ContentValues values=new ContentValues();
//			values.put("RCCar", RC);
//			database.update("roundcounterdetailinfo", values, "roundcounter= ?", new String[]{String.valueOf(roundCounter)});
			database.execSQL("update roundcounterdetailinfo set RCCar='"+RC+"' where roundcounter='"+roundCounter+"' and deviceID='"+deviceId+"'");
			database.close();
			Log.d(TAG, "insert rc success");

//		}catch(Exception e)
//		{
//			Log.d(TAG, e.toString());
//			if (database!=null) {
//				database.close();
//			}
//		}
	}
	/**
	 * 根据roundcounter更新RCCar
	 * @param roundCounter
	 * @param RCCar
	 */
	public void updateRCCarAccordingToRoundCounter(String roundCounter,String RCCar,String deviceId)
	{
		SQLiteDatabase database=null;
		try
		{
			database=getWritableDatabase();
//			ContentValues values=new ContentValues();
//			values.put("RCCar", RCCar);
//			database.update("roundcounterdetailinfo", values, "roundcounter= ?", new String[]{String.valueOf(roundCounter)});
			database.execSQL("update roundcounterdetailinfo set RCCar='"+RCCar+"' where roundcounter='"+roundCounter+"' and deviceID='"+deviceId+"' ");
			database.close();
			database.close();
		}
		catch (Exception e) {
			Log.d(TAG, e.toString());
			database.close();
		}
	}
	/**
	 * 根据roundcounter更新track
	 * @param roundCounter
	 * @param track
	 */
	public void updateTrackAccordingToRoundCounter(String roundCounter,String track,String deviceId)
	{
		SQLiteDatabase database=null;
		try
		{
			database=getWritableDatabase();
			database.execSQL("update roundcounterdetailinfo set track='"+track+"' where roundcounter='"+roundCounter+"' and deviceID='"+deviceId+"'");
			
//			ContentValues values=new ContentValues();
//			values.put("track", track);
//			database.update("roundcounterdetailinfo", values, "roundcounter= ?", new String[]{String.valueOf(roundCounter)});
			database.close();
		}
		catch (Exception e) {
			if (database!=null) {
				database.close();
			}
			Log.d(TAG, e.toString());
		}
	}
	/**
	 * 查询roundcounter是否存在
	 * @param roundCounter
	 * @return
	 */
	 public boolean IsRoundCounterExists(int roundCounter,String deviceID)
	 {
		 SQLiteDatabase db=null;
		 Cursor cursor=null;
		 try
		 {
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from roundcounterdetailinfo t where t.roundcounter='"+roundCounter+"' and t.deviceID='"+deviceID+"'", null);
			if(cursor.moveToFirst())//说明存在一样的
			{
				Log.d(TAG, "exist logCounter:"+cursor.getColumnIndex("roundcounter"));
				cursor.close();
				db.close();
				return true;
			}
			else
			{
				cursor.close();
				db.close();
				Log.d(TAG, "roundcounter不存在");
				return false;
			}

		 }
		 catch(Exception e)
		 {
			 Log.d(TAG, e.toString());
			 if (cursor!=null) {
				cursor.close();
			}
			 if (db!=null) {
				db.close();
			}
			 return false;
		 }
	 }

	 /**
	  * 根据roundcounter查询
	  * @param roundCounter
	  * @return
	  */
	 public Cursor QueryAccordingToRoundCounter(String roundCounter,String deviceID)
	 {
		 SQLiteDatabase db=null;
		 Cursor cursor=null;
		 try
		 {
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from roundcounterdetailinfo t where t.roundcounter='"+roundCounter+"' and t.deviceID='"+deviceID+"'", null);
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
			 Log.d(TAG, e.toString());
			 return null;
		 }
	 }
	 /**
	  * 根据roundc查询RC
	  * @param roundCounter
	  * @return
	  */
	 public String QueryRCAccordingToRoundCounter(String roundCounter,String deviceId)
	 {
		 SQLiteDatabase db=null;
		 Cursor cursor=null;
		 try
		 {
			String rccar=null;
			db=getWritableDatabase();
			cursor=db.rawQuery("select RCCar from roundcounterdetailinfo t where t.roundcounter='"+roundCounter+"' and t.deviceID='"+deviceId+"'", null);
			if (cursor.moveToFirst()) {
				rccar=cursor.getString(cursor.getColumnIndex("RCCar"));
			}
			cursor.close();
			db.close();
			return rccar;
		 }
		 catch(Exception e)
		 {
			 Log.d(TAG, e.toString());
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
	  * 查询不同的日期
	  * @return
	  */
	 public List<String> QueryDate()
	 {
		 SQLiteDatabase db=null;
		 Cursor cursor=null;
		 try
		 {
			 List<String> result=new ArrayList<String>();
			 db=getWritableDatabase();
			 cursor=db.rawQuery("select distinct startDate from roundcounterdetailinfo order by startDate asc", null);
			 if (cursor.moveToFirst()) {
				do {
					String date=cursor.getString(cursor.getColumnIndex("startDate"));
					if (date!=null && !date.equals("")) {
						result.add(date);
					}
					
				} while (cursor.moveToNext());
			}
			 cursor.close();
			 db.close();
			 return result;
		 }
		 catch(Exception e)
		 {
			 Log.d(TAG,e.toString());
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
	 * 根据日期进行查询
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	 public List<String> QueryDate(String startDate,String endDate)
	 {
		 SQLiteDatabase db=null;
		 Cursor cursor=null;
		 try
		 {
			 List<String> result=new ArrayList<String>();
			 db=getWritableDatabase();
			 cursor=db.rawQuery("select distinct startDate from roundcounterdetailinfo where startDate between '"+startDate+"' and '"+endDate+"' order by startDate DESC", null);
			 if (cursor.moveToFirst()) {
				do {
					String date=cursor.getString(cursor.getColumnIndex("startDate"));
					if (date!=null && !date.equals("")) {
						result.add(date);
					}
					
				} while (cursor.moveToNext());
			}
			 cursor.close();
			 db.close();
			 return result;
		 }
		 catch(Exception e)
		 {
			 Log.d(TAG,e.toString());
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
	  * 根据日期和track和rc查询数据
	  * @param date
	  * @return
	  */
	 public Cursor QueryAccordingDate(String date,String track,String rc,String mode)
	 {
		 if (mode.equals(R.string.timeLimit)) {
			mode="Time";
		}
		 else if (mode.equals(R.string.freeStr)) {
			mode="Free";
		}
		 SQLiteDatabase db=null;
		 Cursor cursor=null;
		 try
		 {
			 db=getWritableDatabase();
			 String sql="select * from roundcounterdetailinfo where startDate='"+date+"'";

			 String dateSql="";
			 String trackSql="";
			 String rcSql="";
			 String modeSql="";
		
			 if (!track.equals(context.getResources().getString(R.string.all))) {
				trackSql=" and track='"+track+"'";
			}
			 if (!rc.equals(context.getResources().getString(R.string.all))) {
				rcSql=" and RCCar='"+rc+"'";
			}
			 if (!mode.equals(context.getResources().getString(R.string.all))) {
				modeSql=" and mode='"+mode+"'";
			}
			 cursor=db.rawQuery(sql+dateSql+trackSql+rcSql+modeSql+" order by startDate,startTime asc",null);
			 return cursor;
		 }
		 catch(Exception e)
		 {
			 Log.d(TAG,e.toString());
			 if (cursor!=null) {
				cursor.close();
			}
			 if (db!=null) {
				db.close();
			}
			 return null;
		 }
	 }
	 
	 public Cursor QueryAccordingDate(String startDateStr,String endDateStr,String trackStr,String rc,String mode)
	 {

		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			//转换成英文
			if (mode.equals(context.getResources().getString(R.string.timeLimit))) {
				mode="Time";
			}
			else if (mode.equals(context.getResources().getString(R.string.freeStr))) {
				mode="Free";
			}
			
			db=getWritableDatabase();
			String sql="select * from roundcounterdetailinfo where startDate between '"+startDateStr+"' and '"+endDateStr+"'";
			String trackSql="";
			String rcSql="";
			String modeSql="";
			if (trackStr.equals(context.getResources().getString(R.string.all))) {//全部
				trackSql="";
			}
			else if (trackStr.equals(context.getResources().getString(R.string.unknow))) {//未指定
				trackSql=" and (track='未指定' or track='Unknow' or track='アンノウン')";
			}
			else {
				trackSql=" and track='"+trackStr+"'";
			}
	
			if (!rc.equals(context.getResources().getString(R.string.all))) {
				rcSql=" and RCCar='"+rc+"'";
			}
			if (!mode.equals(context.getResources().getString(R.string.all))) {
				modeSql=" and mode='"+mode+"'";
			}
			cursor=db.rawQuery(sql+trackSql+rcSql+modeSql+ "order by startDate,startTime asc", null);
			 return cursor;
		 }
		 catch(Exception e)
		 {
			 Log.d(TAG,e.toString());
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
	  * 查询不同的赛道
	  * @return
	  */
	 public List<String> QueryTrack()
	 {
		 SQLiteDatabase db=null;
		 Cursor cursor=null;
		 try{
			 List<String> result=new ArrayList<String>();
			 db=getWritableDatabase();
			 cursor=db.rawQuery("select distinct track from roundcounterdetailinfo", null);
			 if (cursor.moveToFirst()) {
				do {
					String date=cursor.getString(cursor.getColumnIndex("track"));
					result.add(date);
				} while (cursor.moveToNext());
			}
			 cursor.close();
			 db.close();
			 return result;
		 }
		 catch (Exception e) {
			 if (cursor!=null) {
				cursor.close();
			}
			 if (db!=null) {
				db.close();
			}
			Log.d(TAG, e.toString());
			return null;
		}
	 }
	 /**
	  * 根据track查询
	  * @param date
	  * @return
	  */
	 public Cursor QueryAccordingTrack(String track)
	 {
		 SQLiteDatabase db=null;
		 Cursor cursor=null;
		 try
		 {
			 db=getWritableDatabase();
			 if (track!=null) {
				 cursor=db.rawQuery("select * from roundcounterdetailinfo where track='"+track+"'", null);
			 }
			 else
			 {
				 cursor=db.rawQuery("select * from roundcounterdetailinfo where track is NULL", null);
			 }
			 return cursor;
		 }
		 catch(Exception e)
		 {
			 if (db!=null) {
				db.close();
			}
			 if (cursor!=null) {
				cursor.close();
			}
			 Log.d(TAG,e.toString());
			 return null;
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
			database.execSQL("delete from roundcounterdetailinfo");
			database.execSQL("update sqlite_sequence SET seq = 0 where name ='roundcounterdetailinfo'");
			database.close();
		}
		catch (Exception e) {
			Log.d(TAG, e.toString());
		}
	}
}

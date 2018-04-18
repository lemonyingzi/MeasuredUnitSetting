package com.measuredunitsetting.db;

import java.util.ArrayList;
import java.util.List;

import com.measuredunitsetting.entity.DisplacementMeasuredUnit;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
import com.measuredunitsetting.global.LogUtil;

public class DisplacementMeasuredUnitDB extends DataBaseHelper{
	private static final String TAG=DisplacementMeasuredUnitDB.class.getSimpleName();
	
	public DisplacementMeasuredUnitDB(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 插入
	 * @param userId
	 * @param projectName
	 * @param monitorNetworkNumber
	 */
	public void insert(DisplacementMeasuredUnit displacementMeasuredUnit)
	{
		SQLiteDatabase database=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("monitorPointId", displacementMeasuredUnit.getMonitorPointId());
        values.put("measuredUnitId", displacementMeasuredUnit.getMeasureUnitId());
        values.put("serialNumber", displacementMeasuredUnit.getSerialNumber());
        values.put("depth", displacementMeasuredUnit.getDepth());
        values.put("time", displacementMeasuredUnit.getTime());
        database.insert("displacementMeasuredUnit", null, values);
        database.close();
		
	}
	
	public DisplacementMeasuredUnit getMeasureUnit(int monitorPointId)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			DisplacementMeasuredUnit monitorNetwork = null;
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from displacementMeasuredUnit where monitorPointId='"+monitorPointId+"' order by id asc", null);
			if(cursor.moveToLast())
			{
				int serialNumber=cursor.getInt(cursor.getColumnIndex("serialNumber"));
				String measuredUnitId=cursor.getString(cursor.getColumnIndex("measuredUnitId"));
				float depth=cursor.getFloat(cursor.getColumnIndex("depth"));
				String time=cursor.getString(cursor.getColumnIndex("time"));
				monitorNetwork=new DisplacementMeasuredUnit(monitorPointId, serialNumber, measuredUnitId, depth, time);
			}
			else {
			}
			cursor.close();
			db.close();
			return monitorNetwork;
		}
		catch (Exception e) {
			LogUtil.e(TAG, "异常："+e.toString());
			return null;
		}
	}

	public String getMeasureUnitId(int serialNumber)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		String measuredUnitId=null;
		try
		{
			DisplacementMeasuredUnit monitorNetwork = null;
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from displacementMeasuredUnit where serialNumber='"+serialNumber+"'", null);
			if(cursor.moveToLast())
			{
				measuredUnitId=cursor.getString(cursor.getColumnIndex("measuredUnitId"));

			}
			else {
			}
			cursor.close();
			db.close();
			return measuredUnitId;
		}
		catch (Exception e) {
			LogUtil.e(TAG, "异常："+e.toString());
			return null;
		}
	}


	/**
	 * 根据ID查询
	 * @param monitorPointId
	 * @return
	 */
	public List<DisplacementMeasuredUnit> getMeasureUnits(int monitorPointId)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			List<DisplacementMeasuredUnit> measureUnits = new ArrayList<DisplacementMeasuredUnit>();
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from displacementMeasuredUnit where monitorPointId='"+monitorPointId+"' order by id asc", null);
			if(cursor.moveToFirst())
			{
				do {
					
					int serialNumber=cursor.getInt(cursor.getColumnIndex("serialNumber"));
					String measuredUnitId=cursor.getString(cursor.getColumnIndex("measuredUnitId"));
					float depth=cursor.getFloat(cursor.getColumnIndex("depth"));
					String time=cursor.getString(cursor.getColumnIndex("time"));
					DisplacementMeasuredUnit measuredUnit=new DisplacementMeasuredUnit(monitorPointId, serialNumber, measuredUnitId, depth, time);
					measureUnits.add(measuredUnit);
				} while (cursor.moveToNext());
			
			}
			else {
			}
			cursor.close();
			db.close();
			return measureUnits;
		}
		catch (Exception e) {
			LogUtil.e(TAG, "异常："+e.toString());
			return null;
		}
	}
	/**
	 * 根据序号查询
	 * @param monitorPointId
	 * @param serialNumber
	 * @return
	 */
	public boolean selectMeasureUnitAccordingToSerialNumber(int monitorPointId,int serialNumber)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			boolean result=false;
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from displacementMeasuredUnit where monitorPointId='"+monitorPointId+"' and serialNumber='"+serialNumber+"'", null);
			if(cursor.moveToFirst())
			{
				result= true;
			}
			cursor.close();
			db.close();
			return result;
		}
		catch (Exception e) {
			LogUtil.e(TAG, "异常："+e.toString());
			return false;
		}
	}
	
	/**
	 * 根据深度查询
	 * @param monitorPointId
	 * @param depth
	 * @return
	 */
	public boolean selectMeasureUnitAccordingToDepth(int monitorPointId,float depth)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			boolean result=false;
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from displacementMeasuredUnit where monitorPointId='"+monitorPointId+"' and depth='"+depth+"'", null);
			if(cursor.moveToFirst())
			{
				result= true;
			}
			cursor.close();
			db.close();
			return result;
		}
		catch (Exception e) {
			LogUtil.e(TAG, "异常："+e.toString());
			return false;
		}
	}
	
	public boolean selectMeasureUnitAccordingToMeasureUnitId(int monitorPointId,String measureUnitId)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			boolean result=false;
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from displacementMeasuredUnit where monitorPointId='"+monitorPointId+"' and measuredUnitId='"+measureUnitId+"'", null);
			if(cursor.moveToFirst())
			{
				result= true;
			}
			cursor.close();
			db.close();
			return result;
		}
		catch (Exception e) {
			LogUtil.e(TAG, "异常："+e.toString());
			return false;
		}
	}
	
	/**
	 * 根据监测网Id查询
	 * @param monitorNetworkNumberId
	 * @return
	 */
	public long selectCountAccordingToMonitorNetworkNumberId(int monitorNetworkNumberId)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			db=getWritableDatabase();
		    cursor = db.rawQuery("select count(*) from displacementMeasuredUnit where monitorPointId='"+monitorNetworkNumberId+"'", null);  
		    cursor.moveToFirst();  
		    long count = cursor.getLong(0);  
		    cursor.close();  
		    db.close();
		    return count;  
		}
		catch (Exception e) 
		{
			LogUtil.e(TAG, "异常："+e.toString());
			return 0;		
		}
	}
	/**
	 * 根据测量单元ID删除
	 * @param monitorNetworkNumberId
	 * @param measureUnitId
	 */
	public void deleteAccordingToMonitorNetworkNumberAndMeausreUnitId(int monitorNetworkNumberId,String measureUnitId)
	{
		SQLiteDatabase database=getWritableDatabase();
		try
		{
			database.execSQL("delete from displacementMeasuredUnit where monitorPointId='"+monitorNetworkNumberId+"' and measuredUnitId='"+measureUnitId+"'");
			database.close();
		}
		catch(Exception ex)
		{
			LogUtil.e(TAG, ex.toString());
		}
	}

	/**
	 * 根据序号ID删除
	 * @param monitorNetworkNumberId
	 * @param serialNumber
	 */
	public void deleteAccordingToMonitorNetworkNumberAndSerialNumber(int monitorNetworkNumberId,int serialNumber)
	{
		SQLiteDatabase database=getWritableDatabase();
		try
		{
			database.execSQL("delete from displacementMeasuredUnit where monitorPointId='"+monitorNetworkNumberId+"' and serialNumber='"+serialNumber+"'");
			database.close();
		}
		catch(Exception ex)
		{
			LogUtil.e(TAG, ex.toString());
		}
	}
	/**
	 * 根据序号查询
	 * @param monitorNetworkNumberId
	 * @param serialNumber
	 * @return
	 */
	public DisplacementMeasuredUnit selectMeasureUnitsAccordingToSerialNumber(int monitorNetworkNumberId,int serialNumber)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			DisplacementMeasuredUnit monitorPoint=null;
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from displacementMeasuredUnit where monitorPointId='"+monitorNetworkNumberId+"' and serialNumber='"+serialNumber+"'", null);
			if(cursor.moveToFirst())
			{
				String measuredUnitId=cursor.getString(cursor.getColumnIndex("measuredUnitId"));
				float depth=cursor.getFloat(cursor.getColumnIndex("depth"));
				String time=cursor.getString(cursor.getColumnIndex("time"));
				monitorPoint =new DisplacementMeasuredUnit(monitorNetworkNumberId,serialNumber,measuredUnitId,depth,time);
			}
			cursor.close();
			db.close();
			return monitorPoint;
		}
		catch (Exception e) {
			LogUtil.e(TAG, "异常："+e.toString());
			return null;
		}
	}

}

package com.measuredunitsetting.db;


import java.util.ArrayList;
import java.util.List;

import com.measuredunitsetting.entity.HydraulicMeasuredUnit;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
import com.measuredunitsetting.global.LogUtil;

public class HydraulicMeasuredUnitDB extends DataBaseHelper{
	private static final String TAG=HydraulicMeasuredUnitDB.class.getSimpleName();
	
	public HydraulicMeasuredUnitDB(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	
	
	
	public HydraulicMeasuredUnit getMeasureUnit(int monitorNetworkNumberId)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			HydraulicMeasuredUnit monitorNetwork = null;
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from hydraulicMeasuredUnit where monitorNetworkId='"+monitorNetworkNumberId+"' order by id asc", null);
			if(cursor.moveToLast())
			{
				int serialNumber=cursor.getInt(cursor.getColumnIndex("serialNumber"));
				String measurePointNum=cursor.getString(cursor.getColumnIndex("measurePointNum"));
				String measureType=cursor.getString(cursor.getColumnIndex("measureType"));
				String measuredUnitId=cursor.getString(cursor.getColumnIndex("measuredUnitId"));
				String time=cursor.getString(cursor.getColumnIndex("time"));
				monitorNetwork=new HydraulicMeasuredUnit(monitorNetworkNumberId,serialNumber,measurePointNum,measureType,measuredUnitId,time);
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
	
	public List<HydraulicMeasuredUnit> getMeasureUnits(int monitorNetworkNumberId)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			
			List<HydraulicMeasuredUnit> monitorNetworkList = new ArrayList<HydraulicMeasuredUnit>();
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from hydraulicMeasuredUnit where monitorNetworkId='"+monitorNetworkNumberId+"' order by id asc", null);
			if(cursor.moveToFirst())
			{
				do {
					int serialNumber=cursor.getInt(cursor.getColumnIndex("serialNumber"));
					String measurePointNum=cursor.getString(cursor.getColumnIndex("measurePointNum"));
					String measureType=cursor.getString(cursor.getColumnIndex("measureType"));
					String measuredUnitId=cursor.getString(cursor.getColumnIndex("measuredUnitId"));
					String time=cursor.getString(cursor.getColumnIndex("time"));
					HydraulicMeasuredUnit monitorNetwork =new HydraulicMeasuredUnit(monitorNetworkNumberId,serialNumber,measurePointNum,measureType,measuredUnitId,time);
					monitorNetworkList.add(monitorNetwork);
				} while (cursor.moveToNext());
		
			}

			cursor.close();
			db.close();
			return monitorNetworkList;
		}
		catch (Exception e) {
			LogUtil.e(TAG, "异常："+e.toString());
			return null;
		}
		
	}
	/**
	 * 根据测点编号查询
	 * @param monitorNetworkNumberId
	 * @return
	 */
	public boolean selectMeasureUnitAccordingToMeasurePointNum(int monitorNetworkNumberId,String measurePointNum)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			boolean result=false;
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from hydraulicMeasuredUnit where monitorNetworkId='"+monitorNetworkNumberId+"' and measurePointNum='"+measurePointNum+"'", null);
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
	 * 根据序号查询
	 * @param monitorNetworkNumberId
	 * @param serialNumber
	 * @return
	 */
	public boolean selectMeasureUnitAccordingToSerialNumber(int monitorNetworkNumberId,int serialNumber)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			boolean result=false;
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from hydraulicMeasuredUnit where monitorNetworkId='"+monitorNetworkNumberId+"' and serialNumber='"+serialNumber+"'", null);
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
	 * 根据序号查询
	 * @param monitorNetworkNumberId
	 * @param serialNumber
	 * @return
	 */
	public HydraulicMeasuredUnit selectMeasureUnitsAccordingToSerialNumber(int monitorNetworkNumberId,int serialNumber)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			HydraulicMeasuredUnit monitorNetwork=null;
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from hydraulicMeasuredUnit where monitorNetworkId='"+monitorNetworkNumberId+"' and serialNumber='"+serialNumber+"'", null);
			if(cursor.moveToFirst())
			{
				String measurePointNum=cursor.getString(cursor.getColumnIndex("measurePointNum"));
				String measureType=cursor.getString(cursor.getColumnIndex("measureType"));
				String measuredUnitId=cursor.getString(cursor.getColumnIndex("measuredUnitId"));
				String time=cursor.getString(cursor.getColumnIndex("time"));
				monitorNetwork =new HydraulicMeasuredUnit(monitorNetworkNumberId,serialNumber,measurePointNum,measureType,measuredUnitId,time);
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
	/**
	 * 根据测点类型查询
	 * @param monitorNetworkNumberId
	 * @param measureType
	 * @return
	 */
	public long selectMeasureUnitAccordingToMeasureType(int monitorNetworkNumberId,String measureType)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			db=getWritableDatabase();
			cursor=db.rawQuery("select count(*) from hydraulicMeasuredUnit where monitorNetworkId='"+monitorNetworkNumberId+"' and measureType='"+measureType+"'", null);
		    cursor.moveToFirst();  
		    long count = cursor.getLong(0);  
		    cursor.close();  
		    db.close();
		    return count;  
		}
		catch (Exception e) {
			LogUtil.e(TAG, "异常："+e.toString());
			return 0;
		}
	}
	/**
	 * 根据测量单元id查询
	 * @param monitorNetworkNumberId
	 * @param measureUnitId
	 * @return
	 */
	public boolean selectMeasureUnitAccordingToMeasureUnitId(int monitorNetworkNumberId,String measureUnitId)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			boolean result=false;
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from hydraulicMeasuredUnit where monitorNetworkId='"+monitorNetworkNumberId+"' and measuredUnitId='"+measureUnitId+"'", null);
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
	 * 根据测量单元ID删除
	 * @param monitorNetworkNumberId
	 * @param measureUnitId
	 */
	public void deleteAccordingToMonitorNetworkNumberAndMeausreUnitId(int monitorNetworkNumberId,String measureUnitId)
	{
		SQLiteDatabase database=getWritableDatabase();
		try
		{
			database.execSQL("delete from hydraulicMeasuredUnit where monitorNetworkId='"+monitorNetworkNumberId+"' and measuredUnitId='"+measureUnitId+"'");
			database.close();
		}
		catch(Exception ex)
		{
			LogUtil.e(TAG, ex.toString());
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
		    cursor = db.rawQuery("select count(*) from hydraulicMeasuredUnit where monitorNetworkId='"+monitorNetworkNumberId+"'", null);  
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
	 * 插入
	 * @param userId
	 * @param projectName
	 * @param monitorNetworkNumber
	 */
	public void insert(HydraulicMeasuredUnit hydraulicMeasuredUnit)
	{

		SQLiteDatabase database=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("monitorNetworkId", hydraulicMeasuredUnit.getMonitorNetwrokId());
        values.put("measuredUnitId", hydraulicMeasuredUnit.getMeasureUnitId());
        values.put("serialNumber", hydraulicMeasuredUnit.getSerialNumber());
        values.put("measurePointNum", hydraulicMeasuredUnit.getMeasurePointNumber());
        values.put("measureType", hydraulicMeasuredUnit.getMeasureType());
        database.insert("hydraulicMeasuredUnit", null, values);
        database.close();
		
	}
	
}

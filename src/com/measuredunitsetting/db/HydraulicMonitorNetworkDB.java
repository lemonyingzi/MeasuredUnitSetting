package com.measuredunitsetting.db;

import java.util.ArrayList;
import java.util.List;

import com.measuredunitsetting.entity.HydraulicMonitorNetwork;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
import com.measuredunitsetting.global.LogUtil;

public class HydraulicMonitorNetworkDB extends DataBaseHelper{
	private final static String TAG=HydraulicMonitorNetworkDB.class.getSimpleName();

	public HydraulicMonitorNetworkDB(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 根据工程名称和监测网编号查询
	 * @return
	 */
	public HydraulicMonitorNetwork selectAccordingToProjectNameAndMonitorNum(int userId,String projectName,String monitoringNetworkNumber)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try{
			HydraulicMonitorNetwork monitorNetwork=null;
			db=getWritableDatabase();
			cursor = db.rawQuery("select * from hydraulicMonitorNetwork where userId='"+userId+"' and projectName='"+projectName+"' and monitorNetworkNumber='"+monitoringNetworkNumber+"'", null);
			if (cursor.moveToFirst()) {
				int id=cursor.getInt(cursor.getColumnIndex("id"));
				int totalUnitNumber=cursor.getInt(cursor.getColumnIndex("totalUnitNumber"));//获取原始计时器物理地址
				monitorNetwork=new HydraulicMonitorNetwork(id,userId, projectName, monitoringNetworkNumber, totalUnitNumber);

			}
		    cursor.close();
		    db.close();
		    return monitorNetwork;
		}
		catch(Exception e)
		{
			LogUtil.e(TAG, "异常："+e.toString());
			if (db!=null) {
				db.close();
			}
			if (cursor!=null) {
				cursor.close();
			}
			return null;
		}
	}
	/**
	 * 根据ID查询
	 * @return
	 */
	public HydraulicMonitorNetwork selectAccordingToId(int id,int userId)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try{
			HydraulicMonitorNetwork monitorNetwork=null;
			db=getWritableDatabase();
			cursor = db.rawQuery("select * from hydraulicMonitorNetwork where id='"+id+"' and userId='"+userId+"'", null);
			if (cursor.moveToFirst()) {
				int totalUnitNumber=cursor.getInt(cursor.getColumnIndex("totalUnitNumber"));//获取原始计时器物理地址
				String projectName=cursor.getString(cursor.getColumnIndex("projectName"));
				String monitoringNetworkNumber=cursor.getString(cursor.getColumnIndex("monitorNetworkNumber"));
				monitorNetwork=new HydraulicMonitorNetwork(id,userId, projectName, monitoringNetworkNumber, totalUnitNumber);

			}
		    cursor.close();
		    db.close();
		    return monitorNetwork;
		}
		catch(Exception e)
		{
			LogUtil.e(TAG, "异常："+e.toString());
			if (db!=null) {
				db.close();
			}
			if (cursor!=null) {
				cursor.close();
			}
			return null;
		}
	}
	/**
	 * 插入
	 * @param userId
	 * @param projectName
	 * @param monitorNetworkNumber
	 */
	public void insertProjectNameAndMonitorNum(int userId,String projectName,String monitorNetworkNumber,int totalUnitNumber)
	{

		SQLiteDatabase database=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("userId", userId);
        values.put("projectName", projectName);
        values.put("monitorNetworkNumber", monitorNetworkNumber);
        values.put("totalUnitNumber", totalUnitNumber);
        database.insert("hydraulicMonitorNetwork", null, values);
        database.close();
		
	}
	/**
	 * 根据用户ID查询
	 * @param userId
	 * @return
	 */
	public List<HydraulicMonitorNetwork> getMonitorNetworkAccordingToUserId(int userId)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			List<HydraulicMonitorNetwork> monitorNetworks=new ArrayList<HydraulicMonitorNetwork>();
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from hydraulicMonitorNetwork where userId='"+userId+"' order by id desc", null);
			if(cursor.moveToFirst())
			{
				do{
					int id=cursor.getInt(cursor.getColumnIndex("id"));
					String projectName=cursor.getString(cursor.getColumnIndex("projectName"));//获取原始计时器物理地址
					String monitorNetworkNumber=cursor.getString(cursor.getColumnIndex("monitorNetworkNumber"));//获取原始计时器物理地址
					int totalUnitNumber=cursor.getInt(cursor.getColumnIndex("totalUnitNumber"));//获取原始计时器物理地址
					int userid=cursor.getInt(cursor.getColumnIndex("userId"));
					HydraulicMonitorNetwork monitorNetwork=new HydraulicMonitorNetwork(id,userId, projectName, monitorNetworkNumber, totalUnitNumber);
					monitorNetworks.add(monitorNetwork);
				}
				while(cursor.moveToNext());
			}

			cursor.close();
			db.close();
			return monitorNetworks;
			
		}
		catch (Exception e) {
			LogUtil.e(TAG, "异常："+e.toString());
			return null;
		}
		
	}
	/**
	 * 根据ID删除
	 * @param id
	 */
	public void deleteAccordingToId(int id,int userId)
	{
		SQLiteDatabase database=getWritableDatabase();
		try
		{
			database.execSQL("delete from hydraulicMonitorNetwork where id='"+id+"' and userId='"+userId+"'");
			database.close();
		}
		catch(Exception ex)
		{
			LogUtil.e(TAG, ex.toString());
		}
	}
	
}

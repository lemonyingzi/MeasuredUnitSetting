package com.measuredunitsetting.db;

import java.util.ArrayList;
import java.util.List;

import com.measuredunitsetting.entity.DisplacementMonitorPoint;
import com.measuredunitsetting.global.LogUtil;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class DisplacementMonitorPointDB extends DataBaseHelper{
	private static final String TAG=DisplacementMeasuredUnitDB.class.getSimpleName();
	
	public DisplacementMonitorPointDB(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 插入
	 * @param userId
	 * @param projectName
	 * @param monitorNetworkNumber
	 */
	public void insert(DisplacementMonitorPoint displacementMonitorPoint)
	{

		SQLiteDatabase database=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("userId", displacementMonitorPoint.getUserId());
        values.put("projectName", displacementMonitorPoint.getProjectName());
        values.put("monitorPointNumber", displacementMonitorPoint.getMonitorPointNumber());
        values.put("monitorDepth", displacementMonitorPoint.getMonitorDepth());
        values.put("unitSpacing", displacementMonitorPoint.getUnitSpacing());
        database.insert("displacementMonitorPoint", null, values);
        database.close();
		
	}
	
	/**
	 * 根据工程名称和监测点编号查询
	 * @return
	 */
	public DisplacementMonitorPoint selectAccordingToProjectNameAndMonitorNum(int userId,String projectName,String monitoringPointNumber)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try{
			DisplacementMonitorPoint monitorNetwork=null;
			db=getWritableDatabase();
			cursor = db.rawQuery("select * from displacementMonitorPoint where userId='"+userId+"' and projectName='"+projectName+"' and monitorPointNumber='"+monitoringPointNumber+"'", null);
			if (cursor.moveToFirst()) {
				int id=cursor.getInt(cursor.getColumnIndex("id"));
				float monitorDepth=cursor.getInt(cursor.getColumnIndex("monitorDepth"));//监测深度
				float unitSpacing=cursor.getFloat(cursor.getColumnIndex("unitSpacing"));//测量单元间 的间隔
				monitorNetwork=new DisplacementMonitorPoint(id,userId, projectName, monitoringPointNumber, monitorDepth,unitSpacing);
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
	 * 根据工程名称和监测点编号查询
	 * @return
	 */
	public DisplacementMonitorPoint selectAccordingToMonitorPointId(int monitorPointtId,int userId)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try{
			DisplacementMonitorPoint monitorNetwork=null;
			db=getWritableDatabase();
			cursor = db.rawQuery("select * from displacementMonitorPoint where id='"+monitorPointtId+"' and userId='"+userId+"'", null);
			if (cursor.moveToFirst()) {
				String projectName=cursor.getString(cursor.getColumnIndex("projectName"));
				String monitorPointNumber=cursor.getString(cursor.getColumnIndex("monitorPointNumber"));
				float monitorDepth=cursor.getInt(cursor.getColumnIndex("monitorDepth"));//监测深度
				float unitSpacing=cursor.getFloat(cursor.getColumnIndex("unitSpacing"));//测量单元间 的间隔
				monitorNetwork=new DisplacementMonitorPoint(monitorPointtId,userId, projectName, monitorPointNumber, monitorDepth,unitSpacing);

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
	 * 根据用户ID查询
	 * @param userId
	 * @return
	 */
	public List<DisplacementMonitorPoint> getMonitorNetworkAccordingToUserId(int userId)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			List<DisplacementMonitorPoint> monitorNetworks=new ArrayList<DisplacementMonitorPoint>();
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from displacementMonitorPoint where userId='"+userId+"' order by id desc", null);
			if(cursor.moveToFirst())
			{
				do{
					int id=cursor.getInt(cursor.getColumnIndex("id"));
					String projectName=cursor.getString(cursor.getColumnIndex("projectName"));//获取原始计时器物理地址
					String monitorNetworkNumber=cursor.getString(cursor.getColumnIndex("monitorPointNumber"));//监测点编号
					float monitorDepth=cursor.getFloat(cursor.getColumnIndex("monitorDepth"));
					float unitSpacing=cursor.getFloat(cursor.getColumnIndex("unitSpacing"));
					DisplacementMonitorPoint monitorNetwork=new DisplacementMonitorPoint(id,userId, projectName, monitorNetworkNumber, monitorDepth,unitSpacing);
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
			database.execSQL("delete from displacementMonitorPoint where id='"+id+"' and userId='"+userId+"'");
			database.close();
		}
		catch(Exception ex)
		{
			LogUtil.e(TAG, ex.toString());
		}
	}
	
}

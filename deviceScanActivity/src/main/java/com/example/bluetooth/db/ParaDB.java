package com.example.bluetooth.db;

import com.example.bluetooth.data.GlobalVariable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class ParaDB extends DataBaseHelper {
	private final static String TAG = "ParaDB";
	public ParaDB(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 插入参数表
	 * @param db
	 * @param IrTimerID
	 * @param timeLimit
	 * @param UVP
	 * @param LCDContrast
	 * @param LEDBrightness
	 */
	public void insertPara(String IrTimerID,int timeLimit,int UVP,int LCDContrast,int LEDBrightness,String IrTimerVersion)
	{
		SQLiteDatabase db=getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("IrTimerID", IrTimerID);
		values.put("timeLimit", timeLimit);
		values.put("UVP", UVP);
		values.put("LCDContrast", LCDContrast);
		values.put("LEDBrightness", LEDBrightness);
		values.put("IrTimerVersion",IrTimerVersion );
		db.insert("paradata", null, values);
		db.close();
	}
	/**
	 * 查询表中是否存在DEVICEID
	 * @param deviceId
	 * @return
	 */
	public boolean seletDeviceId(String deviceId)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try{
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from paradata where IrTimerID='"+deviceId+"'", null);
			if (cursor.moveToFirst()) {
				return true;
			}
			return false;
		}
		catch(Exception e)
		{
			Log.d(TAG, "异常："+e.toString());
			if (cursor!=null) {
				cursor.close();
			}
			if (db!=null) {
				db.close();
			}
			return false;
		}
	}
	public void update(String IrTimerID,int timeLimit,int UVP,int LCDContrast,int LEDBrightness,String IrTimerVersion)
	{
		SQLiteDatabase db=null;
//		try{
			db=getWritableDatabase();
			db.execSQL("update paradata set timeLimit='"+timeLimit+"' and UVP='"+UVP+"' and LCDContrast='"+LCDContrast+"' and LEDBrightness='"+LEDBrightness+"' and IrTimerVersion='"+IrTimerVersion+"' where IrTimerID='"+IrTimerID+"'");
			db.close();
//		}
//		catch(Exception e)
//		{
//			Log.d(TAG, "异常："+e.toString());
//			if (db!=null) {
//				db.close();
//			}
//
//		}
	}
	/**
	 * 获取设备ID
	 * @return
	 */
	public String getDeviceID()
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try{
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from paradata", null);
			if (cursor.moveToFirst()) {
				cursor.moveToLast();
				String deviceID=cursor.getString(cursor.getColumnIndex("IrTimerID"));
				return deviceID;
			}
			return null;
		}
		catch(Exception e)
		{
			Log.d(TAG, "异常："+e.toString());
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
	 * 根据DeviceID得到版本号
	 * @return
	 */
	public String getDeviceVersion()
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try{
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from paradata where IrTimerID='"+GlobalVariable.getIrTimerID()+"'", null);
			if (cursor.moveToFirst()) {
				cursor.moveToLast();
				String IrTimerVersion=cursor.getString(cursor.getColumnIndex("IrTimerVersion"));
				return IrTimerVersion;
			}
			return null;
		}
		catch(Exception e)
		{
			Log.d(TAG, "异常："+e.toString());
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
	 * 查询
	 * @return
	 */
	public Cursor selecetPara()
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try{
			db=getWritableDatabase();
			cursor=db.query("paradata", null, null, null, null, null, null, null);
			return cursor;
		}
		catch(Exception e)
		{
			Log.d(TAG, "异常："+e.toString());
			if (cursor!=null) {
				cursor.close();
			}
			if (db!=null) {
				db.close();
			}
			return null;
		}
	}
}

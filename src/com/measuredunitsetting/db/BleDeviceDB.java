package com.measuredunitsetting.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
import com.measuredunitsetting.global.LogUtil;

public class BleDeviceDB extends DataBaseHelper{
    private final static String TAG = BleDeviceDB.class.getSimpleName();

	public BleDeviceDB(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 查询数据库中的已保存的蓝牙设备名称和MAC地址
	 * @return
	 */
	public List<String> getBleNameAndBleAddress()
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			List<String> result=new ArrayList<String>();
			db=getWritableDatabase();
			cursor=db.query("bledevice", null, null, null, null, null, null, null);
			if(cursor.moveToFirst())
			{
				do{
					result.add(cursor.getString(cursor.getColumnIndex("name")));//获取原始计时器名称
					result.add(cursor.getString(cursor.getColumnIndex("address")));//获取原始计时器物理地址
				}
				while(cursor.moveToNext());
			}
			cursor.close();
			db.close();
			return result;
		}
		catch(Exception e)
		{
			if (cursor!=null) {
				cursor.close();
			}
			if (db!=null) {
				db.close();
			}
			LogUtil.e(TAG, "异常："+e.toString());
			return null;
		}
	}
	
	
	/**
	 * 插入
	 * @param deviceName
	 * @param deviceAddress
	 */
	public void insert(String deviceName,String deviceAddress)
	{
		SQLiteDatabase database=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("name", deviceName);
        values.put("address", deviceAddress);
        database.insert("bledevice", null, values);
        database.close();
	}
	
}

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
	 * ��ѯ���ݿ��е��ѱ���������豸���ƺ�MAC��ַ
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
					result.add(cursor.getString(cursor.getColumnIndex("name")));//��ȡԭʼ��ʱ������
					result.add(cursor.getString(cursor.getColumnIndex("address")));//��ȡԭʼ��ʱ�������ַ
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
			LogUtil.e(TAG, "�쳣��"+e.toString());
			return null;
		}
	}
	
	
	/**
	 * ����
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

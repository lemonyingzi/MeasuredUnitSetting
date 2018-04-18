package com.example.bluetooth.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class UserDB extends DataBaseHelper {
	private final static String TAG = "UserDB";
	public UserDB(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	/**
	 * 插入用户表
	 * @param db
	 * @param deviceName
	 * @param deviceAddress
	 * @param phoneNumber
	 */
	public void insertUser(String deviceName,String deviceAddress,String phoneNumber)
	{
		SQLiteDatabase database=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("name", "cr");
        values.put("password", "cr");
        values.put("blename", deviceName);
        values.put("bleaddress", deviceAddress);
        values.put("phonenumber", phoneNumber);
        database.insert("user", null, values);
        database.close();
        Log.d(TAG, "insert success");
	}
	
	public void updateBle(String deviceName,String deviceAddress)
	{
		SQLiteDatabase database=getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("blename", deviceName);
		values.put("bleaddress", deviceAddress);
		database.update("user", values, "id=?", new String[]{"1"});
		Log.d(TAG, "update success");
		database.close();
	}
	/**
	 * 将头像插入到user表中的最后一行中
	 * @param db
	 * @param img
	 * @return
	 */
	public void updateImage(byte[] img)
	{
		SQLiteDatabase database=getWritableDatabase();
		/**
		 * 将图像插入image字段
		 */
		ContentValues values=new ContentValues();
		values.put("image", img);
		database.update("USER", values, "id= ?", new String[]{"1"});
		database.close();

	}
	
	/**
	 * 取出头像图片
	 * @param db
	 * @return
	 */
	public Bitmap getBmp()
	{
		SQLiteDatabase database=null;
		Cursor cursor=null;
		try{
			Bitmap bmpout=null;
			database=getWritableDatabase();
			cursor = database.query("USER", null, null, null, null, null, null, null);
			if (cursor.moveToFirst()) {
			    byte[] in = cursor.getBlob(cursor.getColumnIndex("image"));
			    if (in!=null ) {
			        bmpout = BitmapFactory.decodeByteArray(in, 0, in.length);
				}
			}
		    cursor.close();
		    database.close();
		    return bmpout;
		}
		catch (Exception e) {
			if (cursor!=null) {
				cursor.close();
			}
			if (database!=null) {
				database.close();
			}
			Log.d(TAG, "读取头像图片异常："+e.toString());
			return null;
		}

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
			cursor=db.query("USER", null, null, null, null, null, null, null);
			if(cursor.moveToFirst())
			{
				do{
					result.add(cursor.getString(cursor.getColumnIndex("blename")));//获取原始计时器名称
					result.add(cursor.getString(cursor.getColumnIndex("bleaddress")));//获取原始计时器物理地址
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
			Log.d(TAG, "异常："+e.toString());
			return null;
		}
	}

	/**
	 * 根据蓝牙名称和蓝牙地址是否存在
	 * @param bleName
	 * @param bleAddress
	 * @return
	 */
	public boolean selectNameAndAddress(String bleName,String bleAddress)
	{
		boolean result=false;
		SQLiteDatabase database=null;
		Cursor cursor=null;
		try{
			database=getWritableDatabase();
			cursor=database.rawQuery("select * from USER t where t.blename='"+bleName+"' and t.bleaddress='"+bleAddress+"'", null);
			if (cursor.moveToFirst()) {
				result=true;
			}
			cursor.close();
			database.close();
			return result;
		}catch(Exception ex)
		{
			if (cursor!=null) {
				cursor.close();
			}
			if (database!=null) {
				database.close();
			}
			Log.d(TAG, "异常1："+ex.toString());
			return false;
		}
	}
	/**
	 * 根据地址删除
	 * @param address
	 */
	public void deleteAccordingToAddress(String address)
	{
		SQLiteDatabase database=null;
		try{
			database=getWritableDatabase();
			int i=database.delete("USER",null,null);
//			database.rawQuery("delete from USER where id='1'", null);
			Log.d(TAG, "删除成功"+i);
			database.close();
		}catch(Exception ex)
		{
			if (database!=null) {
				database.close();
			}
			Log.d(TAG, "异常1："+ex.toString());
		}
	}
}

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
	 * �����û���
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
	 * ��ͷ����뵽user���е����һ����
	 * @param db
	 * @param img
	 * @return
	 */
	public void updateImage(byte[] img)
	{
		SQLiteDatabase database=getWritableDatabase();
		/**
		 * ��ͼ�����image�ֶ�
		 */
		ContentValues values=new ContentValues();
		values.put("image", img);
		database.update("USER", values, "id= ?", new String[]{"1"});
		database.close();

	}
	
	/**
	 * ȡ��ͷ��ͼƬ
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
			Log.d(TAG, "��ȡͷ��ͼƬ�쳣��"+e.toString());
			return null;
		}

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
			cursor=db.query("USER", null, null, null, null, null, null, null);
			if(cursor.moveToFirst())
			{
				do{
					result.add(cursor.getString(cursor.getColumnIndex("blename")));//��ȡԭʼ��ʱ������
					result.add(cursor.getString(cursor.getColumnIndex("bleaddress")));//��ȡԭʼ��ʱ�������ַ
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
			Log.d(TAG, "�쳣��"+e.toString());
			return null;
		}
	}

	/**
	 * �����������ƺ�������ַ�Ƿ����
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
			Log.d(TAG, "�쳣1��"+ex.toString());
			return false;
		}
	}
	/**
	 * ���ݵ�ַɾ��
	 * @param address
	 */
	public void deleteAccordingToAddress(String address)
	{
		SQLiteDatabase database=null;
		try{
			database=getWritableDatabase();
			int i=database.delete("USER",null,null);
//			database.rawQuery("delete from USER where id='1'", null);
			Log.d(TAG, "ɾ���ɹ�"+i);
			database.close();
		}catch(Exception ex)
		{
			if (database!=null) {
				database.close();
			}
			Log.d(TAG, "�쳣1��"+ex.toString());
		}
	}
}

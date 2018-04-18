package com.example.bluetooth.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class RcIridDB extends DataBaseHelper {
	private final static String TAG = "rcirid";
	public RcIridDB(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	/**
	 * ���������
	 * @param db
	 * @param IrTimerID
	 * @param timeLimit
	 * @param UVP
	 * @param LCDContrast
	 * @param LEDBrightness
	 */
	public void insertPara(String rc,String irid)
	{
		SQLiteDatabase db=getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("rc", rc);
		values.put("irid", irid);
		db.insert("rcirid", null, values);
		db.close();
	}
	/**
	 * ����irid��ѯrc
	 * @param irid
	 * @return
	 */
	public String selectRc(String irid)
	{
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try
		{
			db=getWritableDatabase();
			cursor=db.rawQuery("select rc FROM rcirid t where t.irid='"+irid+"'", null);
			if (cursor.moveToFirst()) {
				String rc=cursor.getString(cursor.getColumnIndex("rc"));
				return rc;
			}
			cursor.close();
			db.close();
			return null;
		}
		catch(Exception e)
		{
			Log.d(TAG, "�쳣��"+e.toString());
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
	 * �鿴irid�Ƿ����
	 * @param irid
	 * @return
	 */
	public boolean selectIrid(String irid)
	{
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try
		{
			db=getWritableDatabase();
			cursor=db.rawQuery("select * FROM rcirid t where t.irid='"+irid+"'", null);
			if (cursor.moveToFirst()) {
				return true;
			}
			cursor.close();
			db.close();
			return false;
		}
		catch(Exception e)
		{
			Log.d(TAG, "�쳣��"+e.toString());
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
	 *����irid��ѯrc
	 * @param irid
	 * @return
	 */
	public String selectRC(String  irid)
	{
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try
		{
			db=getWritableDatabase();
			cursor=db.rawQuery("select rc FROM rcirid t where t.irid='"+irid+"'", null);
			if (cursor.moveToFirst()) {
				return cursor.getString(cursor.getColumnIndex("rc"));
			}
			cursor.close();
			db.close();
			return null;
		}
		catch(Exception e)
		{
			Log.d(TAG, "�쳣��"+e.toString());
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
	 * ��ѯicirid��
	 * @return
	 */
	public Cursor select()
	{
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try
		{
			db=getWritableDatabase();
			cursor=db.rawQuery("select * FROM rcirid", null);
			return cursor;
		}
		catch(Exception e)
		{
			Log.d(TAG, "�쳣��"+e.toString());
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
	 * ��ȡ���е�RCֵ��rcΪ��ʱ��Ӧirid��ֵ
	 * @return
	 */
	public List<String> getRCs()
	{
		List<String> rcList=new ArrayList<String>();
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try
		{
			db=getWritableDatabase();
			cursor=db.rawQuery("select * FROM rcirid", null);
			if (cursor.moveToFirst()) {
				do {
					String rc=cursor.getString(cursor.getColumnIndex("rc"));
					if (rc!=null && !rc.equals("")) {
						rcList.add(rc);
					}
					else
					{
						String irid=cursor.getString(cursor.getColumnIndex("irid"));
						rcList.add(irid);
					}
				} while (cursor.moveToNext());
			}
			db.close();
			return rcList;
		}
		catch(Exception e)
		{
			Log.d(TAG, "�쳣��"+e.toString());
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
	 * ����irid����rc
	 * @param rc
	 * @param irid
	 */
	public void updateRC(String rc,String irid)
	{
		Log.d(TAG, "rc:"+rc+",irid:"+irid);
		SQLiteDatabase database=getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("rc", rc);
		database.update("rcirid", values, "irid= ?", new String[]{irid});
		database.close();
	}
}

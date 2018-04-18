package com.measuredunitsetting.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.measuredunitsetting.data.PublicMethod;
import com.measuredunitsetting.entity.LoginResult;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
import com.measuredunitsetting.global.LogUtil;

public class UserDB extends DataBaseHelper{
	private final static String TAG=UserDB.class.getSimpleName();
	
	PublicMethod publicMethod=new PublicMethod();
	public UserDB(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	/**
	 * ����
	 * @param
	 * @param
	 */
	@SuppressLint({ "NewApi", "SimpleDateFormat" })
	public void insert(String name,String password,LoginResult loginResult)
	{	
		//��ȡʱ��
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�������ڸ�ʽ		
		String time=df.format(new Date());
		String newTime=publicMethod.formatTimeEight(time);
		
		SQLiteDatabase database=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("name", name);
        values.put("password", password);
        values.put("examOrganCode", loginResult.getExamOrganCode());
        values.put("token", loginResult.getToken());
        values.put("lastLoginTime", newTime);
        values.put("loginFlag",true);
        database.insert("user", null, values);
        database.close();
	}
	
	/**
	 * ��ѯ���ݿ��е��ѱ�����û�����Token
	 * @return
	 */
	public List<String> getNameAndToken()
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			List<String> nameAndToken = new ArrayList<String>();
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from user where loginFlag='true'", null);
			if(cursor.moveToLast())
			{
				String name =cursor.getString(cursor.getColumnIndex("name"));
				String token=cursor.getString(cursor.getColumnIndex("token"));
				nameAndToken.add(name);
				nameAndToken.add(token);
				return nameAndToken;
			}
		
			cursor.close();
			db.close();
			return null;
		}
		catch (Exception e) {
			LogUtil.e(TAG, "�쳣��"+e.toString());
			return null;
		}
	}

	public List<String> getLastLoginUser()
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			List<String> nameAndToken = new ArrayList<String>();
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from user order by lastLoginTime asc", null);
			if(cursor.moveToLast())
			{
				String name =cursor.getString(cursor.getColumnIndex("name"));
				String token=cursor.getString(cursor.getColumnIndex("token"));
				nameAndToken.add(name);
				nameAndToken.add(token);
				return nameAndToken;
			}

			cursor.close();
			db.close();
			return null;
		}
		catch (Exception e) {
			LogUtil.e(TAG, "�쳣��"+e.toString());
			return null;
		}
	}


	/**
	 * �����û�����ѯID
	 * @param name
	 * @return
	 */
	public int getId(String name)
	{
		SQLiteDatabase db=null;
		Cursor cursor=null;
		try
		{
			db=getWritableDatabase();
			cursor=db.rawQuery("select * from user where name='"+name+"'", null);
			if(cursor.moveToLast())
			{
				int id =cursor.getInt(cursor.getColumnIndex("id"));
				return id;
			}

			cursor.close();
			db.close();
			return 0;
		}
		catch (Exception e) {
			LogUtil.e(TAG, "�쳣��"+e.toString());
			return 0;
		}
	}
	
	/**
	 * ����
	 * @param id
	 * @param
	 */

	@SuppressLint({ "NewApi", "SimpleDateFormat" })
	public void update(int id,String token)
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�������ڸ�ʽ		
		String time=df.format(new Date());
		String newTime=publicMethod.formatTimeEight(time);
		SQLiteDatabase db=null;
		try{
			db=getWritableDatabase();			
			db.execSQL("update user set lastLoginTime='"+newTime+"',token='"+token+"',loginFlag='true' where id='"+id+"'");
			db.close();
		}
		catch(Exception e)
		{
			LogUtil.e(TAG, "�쳣��"+e.toString());
			if (db!=null) {
				db.close();
			}
		}
	}
	public void update(String name,String token)
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�������ڸ�ʽ
		String time=df.format(new Date());
		String newTime=publicMethod.formatTimeEight(time);
		SQLiteDatabase db=null;
		try{
			db=getWritableDatabase();
			db.execSQL("update user set lastLoginTime='"+newTime+"',token='"+token+"',loginFlag='true' where name='"+name+"'");
			db.close();
		}
		catch(Exception e)
		{
			LogUtil.e(TAG, "�쳣��"+e.toString());
			if (db!=null) {
				db.close();
			}
		}
	}
	public void updateLoginFlag()
	{
		SQLiteDatabase db=null;
		try
		{
			db=getWritableDatabase();
			db.execSQL("update user set loginFlag='false' where loginFlag='true'");
			db.close();
		}
		catch(Exception e)
		{
			LogUtil.e(TAG,"�쳣��"+e.toString());
		}
		finally
		{
			if (db!=null)
			{
				db.close();
			}
		}

	}

}

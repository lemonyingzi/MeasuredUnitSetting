package com.measuredunitsetting.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {
	private final static String TAG = "DataBaseHelper";
	/**
	 * ����USER��
	 */
	private static final String CREATE_USER="create table user ("
			+"id integer primary key autoincrement,"
			+"name text,"
			+"password text,"
			+"examOrganCode text,"
			+"lastLoginTime text,"
			+"token text,"
			+"loginFlag text)";
	
	/**
	 *  ���������豸��
	 */
	private static final String BLE_DEVICE="create table bledevice ("
			+"id integer primary key autoincrement,"
			+"name text,"
			+"address text)";
	/**
	 * ����Һѹˮ׼�����
	 */
	private static final String CREATE_HYDRAULICMONITORINGNETWORK="create table hydraulicMonitorNetwork ("
			+"id integer primary key autoincrement,"
			+"userId int,"
			+"projectName text,"
			+"monitorNetworkNumber text,"
			+"totalUnitNumber text)";
			
	/**
	 * ����Һѹˮ׼������Ԫ��
	 */
	private static final String CREATE_HYDRAULICMEASUREDUNIT="create table hydraulicMeasuredUnit ("
			+"id integer primary key autoincrement,"
			+"monitorNetworkId int,"//��������ID
			+"measuredUnitId text,"//������ԪID
			+"serialNumber int,"//���
			+"measurePointNum text,"//�����
			+"measureType text,"
			+"time text)";//�������
	/**
	 * �������ˮƽλ�Ƽ���
	 */
	private static final String CREATE_DISPLACEMENTMONITORPOINT="create table displacementMonitorPoint ("
			+"id integer primary key autoincrement,"
			+"userId int,"
			+"projectName text,"
			+"monitorPointNumber text,"
			+"monitorDepth text,"
			+"unitSpacing text)";
	/**
	 * �������ˮƽλ�Ʋ�����Ԫ��
	 */
	private static final String CREATE_DISPLACEMENTMEASUREUNIT="create table displacementMeasuredUnit ("
			+"id integer primary key autoincrement,"
			+"monitorPointId int,"//����ID
			+"measuredUnitId text,"//������ԪID
			+"serialNumber int,"//���
			+"depth text,"//���
			+"time text)";//�������
    
	private Context mContext;
	
	public DataBaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		mContext=context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(BLE_DEVICE);
		db.execSQL(CREATE_USER);
		db.execSQL(CREATE_HYDRAULICMONITORINGNETWORK);
		db.execSQL(CREATE_HYDRAULICMEASUREDUNIT);
		db.execSQL(CREATE_DISPLACEMENTMEASUREUNIT);
		db.execSQL(CREATE_DISPLACEMENTMONITORPOINT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists bledevice");
		db.execSQL("drop table if exists user");
		db.execSQL("drop table if exists hydraulicMonitorNetwork");
		db.execSQL("drop table if exists hydraulicMeasuredUnit");
		db.execSQL("drop table if exists displacementMonitorPoint");
		db.execSQL("drop table if exists displacementMeasuredUnit");		
		onCreate(db);
	}








}

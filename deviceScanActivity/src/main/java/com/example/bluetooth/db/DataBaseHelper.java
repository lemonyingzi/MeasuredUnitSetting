package com.example.bluetooth.db;

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
			+"blename text,"
			+"bleaddress text,"
			+"phonenumber text,"
			+"image BLOB)";
	/**
	 * �������ݱ�
	 */
	private static final String CREATE_DATA="create table syncdata ("
			+"id integer primary key autoincrement,"
			+"logCounter int,"
			+"roundCounter int,"
			+"mode text,"
			+"roundIRID text,"
			+"LAP int,"
			+"raceTime int,"
			+"receiveOKCounter int,"
			+"deviceID text,"//�豸ID
			+"calculated text)";//�Ƿ�������trueΪ�������falseΪδ�����
	/**
	 * ����������
	 */
	private static final String CREATE_PARA="create table paradata ("
			+"id integer primary key autoincrement,"
			+"IrTimerID text,"//IrTimerID
			+"timeLimit int,"//��ʾʱ��
			+"UVP int,"//�͵�ѹ����
			+"LCDContrast int,"//��Ļ�Աȶ�
			+"LEDBrightness int,"//LED������
			+"IrTimerVersion text)";//�汾��
	/**
	 * ����ʱ���
	 */
	private static final String CREATE_SYNCTIME="create table synctime ("
			+"id integer primary key autoincrement,"
			+"syncTime text,"
			+"track text,"//��ʱ��ҳ�������������
			+"timeset text)";
	/**
	 * ����roundCounter��
	 */
	private static final String CREATE_ROUNDCOUNTER="create table roundcounter ("
			+"id integer primary key autoincrement,"
			+"logCounter int,"
			+"roundCounter int,"
			+"IRID text,"
			+"LAP int,"
			+"time int,"
			+"LAPTime int,"//��LAP(Ȧ)���õ�ʱ��
			+"receiveOKCounter int,"//���յ�����ȷ����
			+"startDate text,"//��ʼ����
			+"startTime text,"//��ʼʱ��
			+"temperature text,"//�¶�
			+"humidity text,"//ʪ��
			+"pressure text,"//ѹ��
			+"mode text,"//ģʽ
			+"deviceID text,"//�豸ID
			+"calculated text)";//�Ƿ�����
	/**
	 * ����roundcounter��ϸ��Ϣ��
	 */
	private static final String CREATE_ROUNDCOUNTERDETAILINFO="create table roundcounterdetailinfo ("
			+"id integer primary key autoincrement,"
			+"startDate text,"//��ʼ����
			+"startTime text,"//��ʼʱ��
			+"roundcounter int,"
			+"irid text,"
			+"RCCar text,"
			+"track text,"
			+"Temperature text,"
			+"Humidity text,"
			+"Pressure text,"
			+"LAPS int,"
			+"AverageTime text,"
			+"TolTime text,"
			+"BestTime text,"
			+"BestLap int,"
			+"mode text,"
			+"deviceID text,"//�豸ID
			+"remark text)";
	
	/**
	 * ����������track
	 */
	private static final String CREATE_TRACK="create table track ("
			+"id integer primary key autoincrement,"
			+"track text,"
			+"longitude text,"//����
			+"latitude text,"//γ��
			+"selected text)";
	
	/**
	 * ����rc��irid��
	 */
    private static final String CREATE_RCIRID="create table rcirid ("
    		+"id integer primary key autoincrement,"
    		+"rc text,"
    		+"irid text)";
    
	private Context mContext;
	
	public DataBaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		mContext=context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_USER);
		db.execSQL(CREATE_DATA);
		db.execSQL(CREATE_PARA);
		db.execSQL(CREATE_SYNCTIME);
		db.execSQL(CREATE_ROUNDCOUNTER);
		db.execSQL(CREATE_ROUNDCOUNTERDETAILINFO);
		db.execSQL(CREATE_RCIRID);
		db.execSQL(CREATE_TRACK);
		Log.d(TAG, "create succeed");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists user");
		db.execSQL("drop table if exists syncdata");
		db.execSQL("drop table if exists paradata");
		db.execSQL("drop table if exists synctime");
		db.execSQL("drop table if exists roundcounter");
		db.execSQL("drop table if exists roundcounterdetailinfo");
		db.execSQL("drop table if exists rcirid");
		db.execSQL("drop table if exists track");
		onCreate(db);
	}








}

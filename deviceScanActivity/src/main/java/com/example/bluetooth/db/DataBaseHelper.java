package com.example.bluetooth.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {
	private final static String TAG = "DataBaseHelper";
	/**
	 * 创建USER表
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
	 * 创建数据表
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
			+"deviceID text,"//设备ID
			+"calculated text)";//是否计算过，true为计算过，false为未计算过
	/**
	 * 创建参数表
	 */
	private static final String CREATE_PARA="create table paradata ("
			+"id integer primary key autoincrement,"
			+"IrTimerID text,"//IrTimerID
			+"timeLimit int,"//显示时间
			+"UVP int,"//低电压保护
			+"LCDContrast int,"//屏幕对比度
			+"LEDBrightness int,"//LED的亮度
			+"IrTimerVersion text)";//版本号
	/**
	 * 创建时间表
	 */
	private static final String CREATE_SYNCTIME="create table synctime ("
			+"id integer primary key autoincrement,"
			+"syncTime text,"
			+"track text,"//计时器页面上输入的赛道
			+"timeset text)";
	/**
	 * 创建roundCounter表
	 */
	private static final String CREATE_ROUNDCOUNTER="create table roundcounter ("
			+"id integer primary key autoincrement,"
			+"logCounter int,"
			+"roundCounter int,"
			+"IRID text,"
			+"LAP int,"
			+"time int,"
			+"LAPTime int,"//该LAP(圈)所用的时间
			+"receiveOKCounter int,"//接收到的正确个数
			+"startDate text,"//开始日期
			+"startTime text,"//开始时间
			+"temperature text,"//温度
			+"humidity text,"//湿度
			+"pressure text,"//压力
			+"mode text,"//模式
			+"deviceID text,"//设备ID
			+"calculated text)";//是否计算过
	/**
	 * 创建roundcounter详细信息表
	 */
	private static final String CREATE_ROUNDCOUNTERDETAILINFO="create table roundcounterdetailinfo ("
			+"id integer primary key autoincrement,"
			+"startDate text,"//开始日期
			+"startTime text,"//开始时间
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
			+"deviceID text,"//设备ID
			+"remark text)";
	
	/**
	 * 创建赛道表track
	 */
	private static final String CREATE_TRACK="create table track ("
			+"id integer primary key autoincrement,"
			+"track text,"
			+"longitude text,"//经度
			+"latitude text,"//纬度
			+"selected text)";
	
	/**
	 * 创建rc和irid表
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

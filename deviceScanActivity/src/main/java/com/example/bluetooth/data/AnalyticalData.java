package com.example.bluetooth.data;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import com.example.bluetooth.db.*;
import com.example.bluetooth.le.R;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class AnalyticalData {
	private final static String TAG = "Analytical data";

	SyncDataDB syncDataDB;
	RcIridDB rcIridDB;
	TrackDB trackDB;
	Context context;
	/*
	 * 开始时间，LAP==0时，第一行数据
	 */
	String startTime=null;
	String startDate=null;
	/*
	 * 温度，LAP==0时，第二行数据
	 */
	int temperature=0;
	/*
	 * 湿度,LAP==0时，第三行数据
	 */
	int Humidity=0;
	/*
	 * 压强，LAP==0时，第四行数据
	 */
	int Pressure=0;
	int LAPLast=0;
	int raceTimeLast=0;
	int LAPTime=0;
	int LAP=0;
	long raceTime=0;
	/**构造函数
	 * 
	 * @param context
	 */
	public AnalyticalData(Context context)
	{
		this.context=context;
		syncDataDB=new SyncDataDB(context, "TimerStore",null, 7);
		rcIridDB=new RcIridDB(context, "TimerStore", null, 7);
		trackDB=new TrackDB(context, "TimerStore", null, 7);
	}

	boolean newRoundCounter=false;
	public void GetRoundCounterFromSyncData() {
//    	try
//    	{
			Cursor cursor = syncDataDB.queryCalculatedFalse();
    		int roundCounter=0;
    		String temperatureStr=null;
    		String pressureStr=null;
    		if(cursor.moveToFirst())
    		{
    			do{
    				
    				String deviceId=cursor.getString(cursor.getColumnIndex("deviceID"));
    				if (deviceId==null) {
						break;
					}
    				RoundCounterDB roundCounterDB= new RoundCounterDB(context, "TimerStore", null, 7);
    				Log.d(TAG, "获取 maxRoundCounter最大值");
    				Cursor roundCounterCursor =roundCounterDB.query(deviceId);
    				int maxRoundCounter=0;
    				try
    				{
    					maxRoundCounter=roundCounterCursor.getInt(roundCounterCursor.getColumnIndex("roundCounter"));
        				Log.d(TAG, "maxRoundCounter:"+String.valueOf(maxRoundCounter));
    				}
    				catch(Exception e)
    				{
    					maxRoundCounter=0;
    					Log.d(TAG, "异常："+e.toString());
    				}

    				int id=cursor.getInt(cursor.getColumnIndex("id"));
    
    				int logCounter=cursor.getInt(cursor.getColumnIndex("logCounter"));

    				roundCounter=cursor.getInt(cursor.getColumnIndex("roundCounter"));

    				
    				String mode=cursor.getString(cursor.getColumnIndex("mode"));
    				if (mode!=null && mode.equals("00")) { 
						mode="Time";
					}
    				else if (mode!=null && mode.equals("01")) {
						mode="Free";
					}
    				
    				String roundIRID=cursor.getString(cursor.getColumnIndex("roundIRID"));
    				
    				LAP=cursor.getInt(cursor.getColumnIndex("LAP"));
    				raceTime=cursor.getInt(cursor.getColumnIndex("raceTime"));
    				
    				int receiveOKCounter=cursor.getInt(cursor.getColumnIndex("receiveOKCounter"));
    				
    				Log.d(TAG, "id:"+String.valueOf(id)+" logCounter:"+String.valueOf(logCounter)+
    						" roundCounter:"+String.valueOf(roundCounter)+" mode:"+String.valueOf(mode)+
    						" roundIRID:"+String.valueOf(roundIRID)+" LAP:"+String.valueOf(LAP)+" raceTime:"
    						+String.valueOf(raceTime)+" receiveOKCounter:"+String.valueOf(receiveOKCounter));
					/**
    				 * 本次的roundCounter和roundCounter表中的最大值相等,说明该roundcounter已经计算过；
    				 */
    				if (roundCounter==maxRoundCounter) {
						//int maxLap=getLAPFromRoundCounter();
    					Log.d(TAG, "roundCounter==maxRoundCounter");
    					try{
    						startDate=roundCounterCursor.getString(roundCounterCursor.getColumnIndex("startDate"));
    						startTime=roundCounterCursor.getString(roundCounterCursor.getColumnIndex("startTime"));
        					temperature=roundCounterCursor.getInt(roundCounterCursor.getColumnIndex("temperature"));
        					Humidity=roundCounterCursor.getInt(roundCounterCursor.getColumnIndex("humidity"));
        					Pressure=roundCounterCursor.getInt(roundCounterCursor.getColumnIndex("pressure"));
    						raceTimeLast =roundCounterDB.getTime();//获取上一次的所用的时间
    					}
    					catch(Exception e)
    					{
    						Log.d(TAG, "异常："+e.toString());
    					}
						newRoundCounter=false;
						LAPTime=(int)raceTime-raceTimeLast;
					}
    				else//开始新的一轮
    				{
    					raceTimeLast=0;
    					
    					if((roundCounter==maxRoundCounter+1) && !newRoundCounter)
    					{
    						startDate=null;
    						startTime=null;
    						temperature=0;
    						Humidity=0;
    						Pressure=0;
    						newRoundCounter=true;
    					}

    					if(LAP==0 && startTime==null && startDate==null)
        				{
        					/**
        					 * 秒数转换为年月日
        					 */
    						Log.d(TAG, "raceTime："+String.valueOf(raceTime));
    						long sd=raceTime*1000-8*3600*1000;
    						Log.d(TAG, "毫秒数："+String.valueOf(sd));
    						Date dat=new Date(sd);
    						GregorianCalendar gc = new GregorianCalendar(); 
    						gc.setTime(dat);
    						java.util.Locale locale=new java.util.Locale("GB2312");
    						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",locale);
    						String dateStr=format.format(gc.getTime());
    						String[] str=dateStr.split(" ");
    						startDate=str[0];
    						startTime=str[1];
    						Log.d(TAG, "startDate:"+startDate+",startTime:"+startTime);
        				}
        				else if(LAP==0 && startTime!=null && temperature==0)
        				{
        					temperature=(int) raceTime;
        				}
        				else if (LAP==0 && startTime!=null && temperature!=0 && Humidity==0) {
    						Humidity=(int) raceTime;
    					}
        				else if (LAP==0 && startTime!=null && temperature!=0 && Humidity!=0 && Pressure==0) {
    						Pressure=(int) raceTime;
    					}
        				else if(LAP!=0){
    						LAPTime=(int)raceTime-raceTimeLast;
    					}
    					
    					float temperatureFloat=(float) ((float)temperature/10.0);
    					float pressureFloat=(float)((float)Pressure/100.0);
    					DecimalFormat df = new DecimalFormat("0.00");//格式化小数    
    					temperatureStr= df.format(temperatureFloat);//返回的是String类型  
    					pressureStr=df.format(pressureFloat);
    				}
    				Log.d(TAG, "temperature:"+temperatureStr+"startDate:"+startDate+",startTime:"+startTime+"humidity:"+Humidity+" Pressure:"+pressureStr+" LAPTime:"+LAPTime);
    				if (LAP!=0) {
        				/**
        				 * 插入到roundcounter表
        				 */
        				roundCounterDB.insert(logCounter, roundCounter, roundIRID, LAP, (int)raceTime,LAPTime, 
        						receiveOKCounter,startDate,startTime,temperatureStr,String.valueOf(Humidity),pressureStr,"false",mode,deviceId);
        				
        				Log.d(TAG, "insert roundcounter success");
					}
    				/**
    				 * 更新caculated
    				 */
    				syncDataDB.updateCaculated(String.valueOf(id), "true");
    				Log.d(TAG, "update syncdata success");
    				roundCounterCursor.close();

    			}while(cursor.moveToNext());
    		}
			cursor.close();
//    	}
//    	catch (Exception e) {
//			Log.d(TAG,"异常："+ e.toString());
//		}
	}
	
	/**
	 * 从RoundCounter表中获取详细信息，并存入RoundCounter表中
	 */
	public void GetRoundCounterDetailInfoFromRoundCounter()
	{
		RoundCounterDB roundCounterDB=new RoundCounterDB(context, "TimerStore", null, 7);
		DataConvert dataConvert=new DataConvert();
		Cursor uncalculatedDevice=roundCounterDB.queryCalculatedFalseDeviceId();

		try
		{
			if (uncalculatedDevice.moveToFirst()) {
				do {
					String deviceId=uncalculatedDevice.getString(uncalculatedDevice.getColumnIndex("deviceID"));
					if (deviceId==null) {
						break;
					}
					Cursor calculatedCursor=roundCounterDB.queryCalculatedFalse(deviceId);

					if (calculatedCursor.moveToFirst()) {//说明calculatedCursor不为空
						do {
							Log.d(TAG, "deviceId:"+deviceId);
							String track=trackDB.getSelectedTrack();
							if (track==null || track.equals("")) {
//								track="未指定";
								track=context.getResources().getString(R.string.unknow);
							}
							//如果为“未指定”，则全部改成英文
//							if (track.equals(context.getResources().getString(R.string.unknow))) {
//								track="Unknow";
//							}
							Log.d(TAG, "设置track:"+track);

							int roundCounter=calculatedCursor.getInt(calculatedCursor.getColumnIndex("roundCounter"));
					
							Cursor roundCounterCursor=roundCounterDB.queryAccordingToRoundCounter(roundCounter,deviceId);					
							roundCounterCursor.moveToLast();
							
							String startDate=roundCounterCursor.getString(roundCounterCursor.getColumnIndex("startDate"));
							String startTime=roundCounterCursor.getString(roundCounterCursor.getColumnIndex("startTime"));
							String temperatureStr=roundCounterCursor.getString(roundCounterCursor.getColumnIndex("temperature"));
							String HumidityStr=roundCounterCursor.getString(roundCounterCursor.getColumnIndex("humidity"));
							String PressureStr=roundCounterCursor.getString(roundCounterCursor.getColumnIndex("pressure"));
							String irid=roundCounterCursor.getString(roundCounterCursor.getColumnIndex("IRID"));
							String mode=roundCounterCursor.getString(roundCounterCursor.getColumnIndex("mode"));
							Log.d(TAG, "startDate:"+startDate+",startTime:"+startTime+",temperature:"+temperatureStr+",humidity:"+HumidityStr+",roundCounter:"+roundCounter);
							int laps=roundCounterCursor.getInt(roundCounterCursor.getColumnIndex("LAP"));//最后一圈的LAP

							
							String iridInt=dataConvert.getIRIDInt(irid);
							String RCCar=rcIridDB.selectRc(iridInt);
							
							if (RCCar==null || RCCar.equals("")) {
								RCCar=iridInt;
							}
					
							Log.d(TAG, "RCCar:"+RCCar);
							
							int tolTime=roundCounterCursor.getInt(roundCounterCursor.getColumnIndex("time"));//最后一圈的racetime
							String tolTimeStr=dataConvert.milsecondsToHHMMSS(tolTime);
			
							
							float averageTime=0;
							if(laps!=0)
							{
								averageTime=tolTime/laps;
							}
							String averageTimeStr=dataConvert.milsecondsToHHMMSS((int)averageTime);
							int bestTime=0;
							int bestLap=0;
							//获取最小的time和lap
							Cursor minCursor=roundCounterDB.queryMinTimeAndLap(String.valueOf(roundCounter));
							
							minCursor.moveToFirst();
							bestTime=minCursor.getInt(minCursor.getColumnIndex("LAPTime"));
				
							String bestTimeStr=dataConvert.milsecondsToHHMMSS(bestTime);
							bestLap=minCursor.getInt(minCursor.getColumnIndex("LAP"));

							RoundCounterDetailInfoDB roundCounterDetailInfoDB=new RoundCounterDetailInfoDB(context, "TimerStore", null, 7);
							Boolean isRoundCounterExist=roundCounterDetailInfoDB.IsRoundCounterExists(roundCounter,deviceId);
							
							Log.d(TAG, "startData:"+startDate+",startTime:"+startTime+",roundCounter:"+roundCounter+",irid:"+irid+",rccar:"+RCCar+",track:"+track);
							
							//判断RoundCounterDetailInfo表中是否有该roundcounter
							if (isRoundCounterExist) {//存在的话，更新数据
								
								roundCounterDetailInfoDB.update(roundCounter, laps, tolTimeStr, averageTimeStr, bestTimeStr, bestLap,deviceId);
							}
							else//不存在的话插入数据
							{
								Log.d(TAG, "插入");
								roundCounterDetailInfoDB.insert(startDate,startTime, roundCounter,irid, RCCar, track, temperatureStr, HumidityStr, PressureStr, 
										laps, tolTimeStr, averageTimeStr, bestTimeStr, bestLap,mode,deviceId);
							}
							roundCounterDB.upateCalculated(String.valueOf(roundCounter), "true",deviceId);
							roundCounterCursor.close();
							minCursor.close();
						}while(calculatedCursor.moveToNext());
					}
					else {
						Log.d(TAG, "calculated cursor 为空");
					}
					calculatedCursor.close();
				} while (uncalculatedDevice.moveToNext());
			
			}
		
		}
		catch(Exception e)
		{
			Log.e(TAG, "异常："+e.toString());
			return;
		}
	}
}

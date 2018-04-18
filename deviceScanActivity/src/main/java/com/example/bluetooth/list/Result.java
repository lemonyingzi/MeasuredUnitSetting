package com.example.bluetooth.list;

public class Result{
	private String roundCounter;
	private String date;
	private String time;
	private String laps;
	private String tolTime;
	private String bestTime;
	private String bestLap;
	private String track;
	private String RCCar;
	private String temperature;
	private String humidity;
	private String deviceID;
	
	public Result(String roundCounter,String date,String time,String laps,String tolTime,String bestTime,String bestLap,String track,
			String RCCar,String temperature,String humidity,String deviceID)
	{
		this.roundCounter=roundCounter;
		this.date=date;
		this.time=time;
		this.laps=laps;
		this.tolTime=tolTime;
		this.bestTime=bestTime;
		this.bestLap=bestLap;
		this.track=track;
		this.RCCar=RCCar;
		this.temperature=temperature;
		this.humidity=humidity;
		this.deviceID=deviceID;
	}
	public String getRoundCounter()
	{
		return roundCounter;
	}
	public String getDate()
	{
		return date;
	}
	public String getTime()
	{
		return time;
	}
	public String getLaps()
	{
		return laps;
	}
	public String getTolTime()
	{
		return tolTime;
	}
	public String getBestTime()
	{
		return bestTime;
	}
	public String getBestLap()
	{
		return bestLap;
	}
	public String getTrack()
	{
		return track;
	}
	public String getRCCar()
	{
		return RCCar;
	}
	public String getTemperature()
	{
		return temperature;
	}
	public String getHumidity()
	{
		return humidity;
	}
	
	public String getDeviceID()
	{
		return deviceID;
	}


}

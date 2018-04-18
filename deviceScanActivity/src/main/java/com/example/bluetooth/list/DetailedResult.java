package com.example.bluetooth.list;

public class DetailedResult {
	private String Lap;
	private String Time;
	private String LapTime;
	private String Receive;
	
	public DetailedResult(String Lap,String Time,String LapTime,String Receive)
	{
		this.Lap=Lap;
		this.Time=Time;
		this.LapTime=LapTime;
		this.Receive=Receive;
	}
	
	public String getLap()
	{
		return Lap;
	}
	
	public String getTime()
	{
		return Time;
	}
	
	public String getLapTime()
	{
		return LapTime;
	}
	
	public String getReceive() 
	{
		return Receive;
	}
}

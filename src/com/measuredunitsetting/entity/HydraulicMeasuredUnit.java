package com.measuredunitsetting.entity;

public class HydraulicMeasuredUnit {
	private int monitorNetwrokId;//液压水准监测网ID
	private int serialNumber;//序号
	private String measurePointNumber;//测点编号
	private String measureType;//测点类型
	private String measureUnitId;//测量单元ID
	private String time;
	
	
	public HydraulicMeasuredUnit(int serialNumber, String measurePointNumber, String measureType, String measuredUnitId)
	{
		this.serialNumber=serialNumber;
		this.measurePointNumber=measurePointNumber;
		this.measureType=measureType;
		this.measureUnitId=measuredUnitId;
	}
	public HydraulicMeasuredUnit(int monitorNetwrokId,int serialNumber,String measurePointNumber,String measureType,String measureUnitId,String time)
	{
		this.monitorNetwrokId=monitorNetwrokId;
		this.serialNumber=serialNumber;
		this.measurePointNumber=measurePointNumber;
		this.measureType=measureType;
		this.measureUnitId=measureUnitId;
		this.time=time;
	}
	public int getMonitorNetwrokId()
	{
		return monitorNetwrokId;
	}
	public int getSerialNumber()
	{
		return serialNumber;
	}
	
	public String getMeasurePointNumber()
	{
		return measurePointNumber;
	}
	
	public String getMeasureType()
	{
		return measureType;
	}
	
	public String getMeasureUnitId()
	{
		return measureUnitId;
	}
	public String getTime()
	{
		return time;
	}
}

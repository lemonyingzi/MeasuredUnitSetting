package com.measuredunitsetting.entity;

public class DisplacementMeasuredUnit {
	private int monitorPointId;//深层水平位移监测网ID
	private int serialNumber;//序号
	private String measureUnitId;//测量单元ID
	private float depth;//深度
	private String time;
	
	public DisplacementMeasuredUnit(int serialNumber,float depth,String measureUnitId )
	{
		this.serialNumber=serialNumber;
		this.measureUnitId=measureUnitId;
		this.depth=depth;
	}
	
	public DisplacementMeasuredUnit(int monitorNetwrokId,int serialNumber,String measureUnitId,float depth,String time)
	{
		this.monitorPointId=monitorNetwrokId;
		this.serialNumber=serialNumber;
		this.measureUnitId=measureUnitId;
		this.depth=depth;
		this.time=time;
	}
	public int getMonitorPointId()
	{
		return monitorPointId;
	}
	
	public int getSerialNumber()
	{
		return serialNumber;
	}
	
	public float getDepth()
	{
		return depth;
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

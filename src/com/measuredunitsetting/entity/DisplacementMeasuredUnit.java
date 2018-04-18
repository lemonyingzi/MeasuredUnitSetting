package com.measuredunitsetting.entity;

public class DisplacementMeasuredUnit {
	private int monitorPointId;//���ˮƽλ�Ƽ����ID
	private int serialNumber;//���
	private String measureUnitId;//������ԪID
	private float depth;//���
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

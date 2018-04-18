package com.measuredunitsetting.entity;

public class HydraulicMeasuredUnit {
	private int monitorNetwrokId;//Һѹˮ׼�����ID
	private int serialNumber;//���
	private String measurePointNumber;//�����
	private String measureType;//�������
	private String measureUnitId;//������ԪID
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

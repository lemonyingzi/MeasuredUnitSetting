package com.measuredunitsetting.entity;

public class HydraulicMeasuredUnitResult {
	private int serialNumber;//ĞòºÅ
	private String measurePointNumber;//²âµã±àºÅ
	private float pressure;//Ñ¹Á¦
	private float temperature;//ÎÂ¶È
	public HydraulicMeasuredUnitResult(int serialNumber,String measurePointNumber,float pressure,float temperature)
	{
		this.serialNumber=serialNumber;
		this.measurePointNumber=measurePointNumber;
		this.pressure=pressure;
		this.temperature=temperature;
	}

	public int getSerialNumber()
	{
		return serialNumber;
	}
	
	public String getMeasurePointNumber()
	{
		return measurePointNumber;
	}
	
	public float getPressure()
	{
		return pressure;
	}
	
	public float getTemperature()
	{
		return temperature;
	}

}

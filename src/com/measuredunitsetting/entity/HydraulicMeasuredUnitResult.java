package com.measuredunitsetting.entity;

public class HydraulicMeasuredUnitResult {
	private int serialNumber;//���
	private String measurePointNumber;//�����
	private float pressure;//ѹ��
	private float temperature;//�¶�
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

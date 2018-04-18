package com.measuredunitsetting.entity;

public class DisplacementMeasuredResult {
	private int serialNumber;//序列号
	private float depth;//深度
	private float x;//X
	private float y;//Y
	private float z;//Z
	private float temperature;
	public DisplacementMeasuredResult(int serialNumber, float depth, float x, float y, float z, float temperature)
	{
		this.serialNumber=serialNumber;
		this.depth=depth;
		this.x=x;
		this.y=y;
		this.z=z;
		this.temperature=temperature;
	}

	public int getSerialNumber()
	{
		return serialNumber;
	}
	public float getDepth() {
		return depth;
	}
	public float getX()
	{
		return x;
	}
	
	public float getY()
	{
		return y;
	}
	public float getZ()
	{
		return z;
	}
	public float getTemperature()
	{
		return temperature;
	}
}

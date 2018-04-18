package com.measuredunitsetting.entity;

public class HydraulicCaliSheet {

	public HydraulicCaliSheet() {
	}
	
	private String conclusion;//测点编号
	private String marginError;//测点类型
	private String outputValue;//测量单元ID
	private String measurePoint;
	private String temperature;
	private String error;
	private String measuredValue;
	private String coeff;
	
	public HydraulicCaliSheet(String conclusion,String marginError,String outputValue,String measurePoint,String temperature,
			String error,String measuredValue,String coeff)
	{
		this.conclusion=conclusion;
		this.marginError=marginError;
		this.outputValue=outputValue;
		this.measurePoint=measurePoint;
		this.temperature=temperature;
		this.measuredValue=measuredValue;
		this.error=error;
		this.coeff=coeff;
	}
	
	public String getConclusion()
	{
		return conclusion;
	}
	public String getMarginError()
	{
		return marginError;
	}
	
	public String getOutputValue()
	{
		return outputValue;
	}
	
	public String getMeasurePoint()
	{
		return measurePoint;
	}
	
	public String getTemperature()
	{
		return temperature;
	}
	public String getError()
	{
		return error;
	}
	public String getMeasuredValue()
	{
		return measuredValue;
	}
	public String getCoeff()
	{
		return coeff;
	}
}

package com.measuredunitsetting.entity;

public class DisplacementMonitorPoint {
	private int id;
	private int userId;
	private String projectName;
	private String monitorPointNumber;
	private float monitorDepth;
	private float unitSpacing;

	
	public DisplacementMonitorPoint(int userId,String projectName,String monitorPointNumber,float monitorDepth,float unitSpacing)
	{
		this.userId=userId;
		this.projectName=projectName;
		this.monitorPointNumber=monitorPointNumber;
		this.monitorDepth=monitorDepth;
		this.unitSpacing=unitSpacing;
	}
	
	public DisplacementMonitorPoint(int id,int userId,String projectName,String monitorPointNumber,float monitorDepth,float unitSpacing)
	{
		this.id=id;
		this.userId=userId;
		this.projectName=projectName;
		this.monitorPointNumber=monitorPointNumber;
		this.monitorDepth=monitorDepth;
		this.unitSpacing=unitSpacing;
	}
	
	
	public int getId()
	{
		return id;
	}
	public int getUserId()
	{
		return userId;
	}
	public String getProjectName()
	{
		return projectName;
	}
	
	public String getMonitorPointNumber()
	{
		return monitorPointNumber;
	}
	
	public float getMonitorDepth()
	{
		return monitorDepth;
	}
	
	public float getUnitSpacing()
	{
		return unitSpacing;
	}
	
	
}

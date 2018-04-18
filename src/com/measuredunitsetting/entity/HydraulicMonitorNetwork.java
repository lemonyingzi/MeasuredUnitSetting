package com.measuredunitsetting.entity;

public class HydraulicMonitorNetwork {
	private int id;
	private int userId;
	private String projectName;
	private String monitorNetworkNumber;
	private int totalUnitNumber;
	public HydraulicMonitorNetwork(int id,int userId,String projectName,String monitorNetworkNumber,int totalUnitNumber)
	{
		this.id=id;
		this.userId=userId;
		this.projectName=projectName;
		this.monitorNetworkNumber=monitorNetworkNumber;
		this.totalUnitNumber=totalUnitNumber;
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
	
	public String getMonitorNetworkNumber()
	{
		return monitorNetworkNumber;
	}
	
	public int getTotalUnitNumber()
	{
		return totalUnitNumber;
	}
	

	
}

package com.measuredunitsetting.entity;

public class DisplacementCaliSheet {

	public DisplacementCaliSheet() {
		// TODO Auto-generated constructor stub
	}

	private String repetitionDiff;
	private String conclusion;//
	private String compareAngle;
	private String temperature;
	private String zaxis;
	private String relativeDiff;
	private String delayDiff;
	public DisplacementCaliSheet(String compareAngle,String zaxis,String repetitionDiff,String relativeDiff,String delayDiff,String conclusion)
	{
		this.repetitionDiff=repetitionDiff;
		this.conclusion=conclusion;
		this.compareAngle=compareAngle;
		this.delayDiff=delayDiff;
		this.zaxis=zaxis;
		this.relativeDiff=relativeDiff;
	}
	
	public String getRepetitonDiff()
	{
		return repetitionDiff;
	}
	public String getConclusion()
	{
		return conclusion;
	}

	
	public String getCompareAngle()
	{
		return compareAngle;
	}
	public String getTemperature()
	{
		return temperature;
	}
	public String getZaxis()
	{
		return zaxis;
	}
	public String getRelativeDiff()
	{
		return relativeDiff;
	}
	public String getDelayDiff()
	{
		return delayDiff;
	}
}

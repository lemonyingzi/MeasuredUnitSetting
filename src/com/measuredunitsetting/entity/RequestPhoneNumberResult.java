package com.measuredunitsetting.entity;

public class RequestPhoneNumberResult {

	private String type;
	private String phoneNumber;
	
	public RequestPhoneNumberResult(String type,String phoneNumber)
	{
		this.type=type;
		this.phoneNumber=phoneNumber;
	}
	
	public String getType()
	{
		return type;
	}
	public String getPhoneNumber()
	{
		return phoneNumber;
	}

}

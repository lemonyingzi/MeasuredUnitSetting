package com.measuredunitsetting.entity;

public class LoginResult {
	private String type;
	private String examOrganCode;
	private String token;
	public LoginResult(String type,String examOrganCode,String token)
	{
		this.type=type;
		this.examOrganCode=examOrganCode;
		this.token=token;
	}
	
	public String getType()
	{
		return type;
	}
	public String getExamOrganCode()
	{
		return examOrganCode;
	}
	public String getToken()
	{
		return token;
	}
}

package com.example.bluetooth.list;

public class RC {
	private String IRIDInt;
	private String rc;
	private String IRID;
	
	public RC(String IRIDInt,String rc,String IRID)
	{
		this.IRIDInt=IRIDInt;
		this.rc=rc;
		this.IRID=IRID;
	}
	public String getIRIDInt()
	{
		return IRIDInt;
	}
	public String getRc()
	{
		return rc;
	}
	
	public String getIRID()
	{
		return IRID;
	}
}

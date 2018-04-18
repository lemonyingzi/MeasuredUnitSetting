package com.example.bluetooth.list;

public class Track {
	private String track;
	private String selected;
	private String longitude;//����
	private String latitude;//γ��
	
	
	public Track(String track,String selected,String longitude,String latitude)
	{
		this.track=track;
		this.selected=selected;
		this.longitude=longitude;
		this.latitude=latitude;
	}
	
	public String getTrack()
	{
		return track;
	}
	
	public String getSelected()
	{
		return selected;
	}
	
	public String getLongitude() {
		return longitude;
	}
	
	public String getLatitude()
	{
		return latitude;
	}
}

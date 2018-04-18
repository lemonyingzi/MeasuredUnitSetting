package com.measuredunitsetting.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class RequestResult implements Parcelable{

	
	private String type;
	private String deviceId;
	private String deviceType;
	private String author;
	private String date;
	private String version;
	private String inspection;
	private String marginError;
	private String coeff;
	private String caliSheet;
	
	public RequestResult()
	{
		
	}
	public RequestResult(String type,String deviceId,String deviceType,String author,String date,
			String version,String inspection,String marginError,String coeff,String caliSheet)
	{
		this.type=type;
		this.deviceId=deviceId;
		this.deviceType=deviceType;
		this.author=author;
		this.date=date;
		this.version=version;
		this.inspection=inspection;
		this.marginError=marginError;
		this.coeff=coeff;
		this.caliSheet=caliSheet;
	}
	
	public String getType()
	{
		return type;
	}
	public String getDeviceId()
	{
		return deviceId;
	}
	public String getDeviceType()
	{
		return deviceType;
	}
	public String getAuthor()
	{
		return author;
	}
	public String getDate()
	{
		return date;
	}
	public String getVersion()
	{
		return version;
	}
	public String getInspection()
	{
		return inspection;
	}
	public String getMarginError()
	{
		return marginError;
	}
	public String getCoeff()
	{
		return coeff;
	}
	public String getCaliSheet()
	{
		return caliSheet;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(type);
		dest.writeString(deviceId);
		dest.writeString(deviceType);
		dest.writeString(author);
		dest.writeString(date);
		dest.writeString(version);
		dest.writeString(inspection);
		dest.writeString(marginError);
		dest.writeString(coeff);
		dest.writeString(caliSheet);
	}
	
	public static final Parcelable.Creator<RequestResult> CREATOR=new Parcelable.Creator<RequestResult>()
			{

				@Override
				public RequestResult createFromParcel(Parcel arg0) {
					RequestResult requestResult=new RequestResult();				
					requestResult.type=arg0.readString();
					requestResult.deviceId=arg0.readString();
					requestResult.deviceType=arg0.readString();
					requestResult.author=arg0.readString();
					requestResult.date=arg0.readString();
					requestResult.version=arg0.readString();
					requestResult.inspection=arg0.readString();
					requestResult.marginError=arg0.readString();
					requestResult.coeff=arg0.readString();
					requestResult.caliSheet=arg0.readString();
					return requestResult;
				}

				@Override
				public RequestResult[] newArray(int arg0) {
					return new RequestResult[arg0];
				}
		
			};
}

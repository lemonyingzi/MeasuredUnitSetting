package com.measuredunitsetting.global;

public class GlobalVariable {
	private final static String DatabaseName="MeasureUnitStore";//���ݿ�����
	private static String examOrganCode="00E030";//����������
	private static int userId=1;//�û�ID
	private final static String login="login";
	private final static String request="request";
	private final static String loginSuccess="loginSuccess";
	private final static String loginFail="loginFail";
	private final static String requestSuccess="requestSuccess";
	private final static String invalidToken="invalidToken";
	private final static String notFound="notFound";
	private final static String noCaliSheet="noCaliSheet";
	private final static String requestPhoneNumber="requestPhoneNumber";
	private final static String resetPassword="resetPassword";
	private final static String resetSuccess="resetSuccess";
	private final static String resetFail="resetFail";
	private static Boolean bluetoothIsConnected=false;
	
    //////////////////���ݿ������/////////////////
	public static String getDataBaseName()
	{
		return DatabaseName;
	}
	////////////////����������//////////////////
	public final static String getExamOrganCode()
	{
		return examOrganCode;
	}
	
	public static void setExamOrganCode(String examOrganCode)
	{
		GlobalVariable.examOrganCode=examOrganCode;
	}
	//////////////////�û�ID////////////////////
	public static int getUserId()
	{
		return userId;
	}
	public static void setUserId(int userId)
	{
		GlobalVariable.userId=userId;
	}
	//////////////////login////////////////
	public final static String getLogin() {
		return login;
	}
	///////////////////����////////////////////
	public final static String getRequest()
	{
		return request;
	}
	/////////////////////��¼�ɹ�/////////////////
	public final static String getLoginSuccess()
	{
		return  loginSuccess;
	}
	/////////////////��¼ʧ��/////////////////////
	public final static String getLoginFail()
	{
		return loginFail;
	}
	//////////////////����ɹ�//////////////////
	public final static String getRequestSuccess()
	{
		return requestSuccess;
	}
	/////////////////ʧЧ������///////////////
	public final static String getInvalidToken()
	{
		return invalidToken;
	}
	
	//////////////////δ�ҵ����豸/////////////////
	public final static String getNotFound()
	{
		return notFound;
	}
	//////////////////��У׼��///////////////////
	public final static String getNoCaliSheet()
	{
		return noCaliSheet;
	}
	
	public final static String getRequestPhoneNumber()
	{
		return requestPhoneNumber;
	}
	
	public final static String getResetPassword()
	{
		return resetPassword;
	}
	
	public final static String getResetSuccess()
	{
		return resetSuccess;
	}
	
	public final static String getResetFail()
	{
		return resetFail;
	}

	public static boolean getBluetoothIsConnected()
	{
		return bluetoothIsConnected;
	}

	public static void setBluetoothIsConnected(boolean bluetoothIsConnected)
	{
		GlobalVariable.bluetoothIsConnected=bluetoothIsConnected;
	}
}

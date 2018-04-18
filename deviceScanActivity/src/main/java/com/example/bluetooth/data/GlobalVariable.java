package com.example.bluetooth.data;

import android.net.Uri;

public class GlobalVariable {
	private static boolean BluetoothExisted=false;//�����Ƿ���ڱ�־λ
	private static boolean mConnected = false;//���ӱ�־λ
    private static int mConnectCount=0;//���Ӽ�����
    private static boolean mHanded=false;  //���ֱ�־λ
    private static int mHandCount=0;//���ּ�����
    private static boolean mUploadPara=false; //�����ϴ�
    private static int mUploadParaCount=0;//�����ϴ�������
    private static boolean mSyncProgress=false;//ͬ�����ȱ�־λ
    private static int mProgressCount=0;//ͬ�����ȼ�����
    private static boolean mSync=false;//ͬ����־λ
    private static int mSyncCount=0;//��ʱ�ü�����
    private static int mLogTotal=0;//��¼������
    private static int syncedCount=0;//��ͬ��������    
    private static String IrTimerID=null;
    private static boolean syncReceived=false;
    private static int syncedFailCount=0;
    private static Uri newApkUri=null;
    private static boolean resyncFlag=false;//����ͬ����־λ
    //////////////////�����Ƿ���ڱ�־λ/////////////////
	public static boolean getBluetoothExisted()
	{
		return BluetoothExisted;
	}
	public static void setBluetoothExisted(boolean existedBluetooth)
	{
		GlobalVariable.BluetoothExisted=existedBluetooth;
	}
	//////////////////����������־λ/////////////////////////
	public static boolean getConnected()
	{
		return mConnected;
	}
	
	public static void setConnected(boolean mConnected)
	{
		GlobalVariable.mConnected=mConnected;
	}
	/////////////////���Ӽ�����//////////////////////////
	public static int getConnectCount()
	{
		return mConnectCount;
	}
	public static void setConnectCount(int connectCount)
	{
		GlobalVariable.mConnectCount=connectCount;
	}
	//////////////////���ֱ�־λ////////////////////////////
	public static boolean getHanded()
	{
		return mHanded;
	}
	public static void setHanded(boolean mHanded)
	{
		 GlobalVariable.mHanded=mHanded;
	}
	///////////////���ּ�����////////////////////////
	public static int getHandCount()
	{
		return mHandCount;
	}
	public static void setHandCount(int handCount)
	{
		GlobalVariable.mHandCount=handCount;
	}
	/////////////�����ϴ�//////////////////////////
	public static boolean getUploadPara()
	{
		return mUploadPara;
	}
	public static void setUploadPara(boolean uploadPara)
	{
		GlobalVariable.mUploadPara=uploadPara;
	}
	
	///////////�����ϴ�������////////////////////////
	public static int getUploadParaCount()
	{
		return mUploadParaCount;
	}
	public static void setUploadParaCount(int uploadParaCount)
	{
		GlobalVariable.mUploadParaCount=uploadParaCount;
	}
	////////////ͬ�����ȱ�־λ//////////////////////
	public static boolean getSyncProgress()
	{
		return mSyncProgress;
	}
	public static void setSyncProgress(boolean syncProgress)
	{
		GlobalVariable.mSyncProgress=syncProgress;
	}
	//////////////ͬ�����ȼ�����///////////////
	public static int getProgressCount()
	{
		return mProgressCount;
	}
	public static void setProgressCount(int progressCount)
	{
		GlobalVariable.mProgressCount=progressCount;
	}
	//////////////ͬ����־λ///////////////////
	public static boolean getSync() {
		return mSync;
	}
	public static void setSync(boolean sync)
	{
		GlobalVariable.mSync=sync;
	}
	////////////��ʱ�ü�����///////////////////
	public static int getSyncCount()
	{
		return  mSyncCount;
	}
	public static void setSyncCount(int syncCount)
	{
		GlobalVariable.mSyncCount=syncCount;
	}
	///////////////��¼������/////////////////////
	public static int getLogTotal()
	{
		return mLogTotal;
	}
	public static void setLogTotal(int logTotal)
	{
		GlobalVariable.mLogTotal=logTotal;
	}
	//////////////��ͬ��������/////////////////////
	public static int getSyncedCount()
	{
		return syncedCount;
	}
	public static void setSyncedCount(int syncedCount)
	{
		GlobalVariable.syncedCount=syncedCount;
	}
	//////////////IrTimerID////////////////////
	public static String getIrTimerID()
	{
		return IrTimerID;
	}
	public static void setIrTimerID(String irTimerID)
	{
		GlobalVariable.IrTimerID=irTimerID;
	}
	/////////////////////syncReceived////////////
	public static boolean getSyncReceived()
	{
		return syncReceived;
	}
	public static void setSyncReceived(boolean syncReceived)
	{
		GlobalVariable.syncReceived=syncReceived;
	}
	///////////syncedFailCount////////////////
	public static int getSyncedFailCount()
	{
		return syncedFailCount;
	}
	public static void setSyncedFailCount(int syncedFailCount)
	{
		GlobalVariable.syncedFailCount=syncedFailCount;
	}
	/////////////newApkPath///////////////////
	public static Uri getNewApkUri()
	{
		return newApkUri;
	}
	public static void setNewApkUri(Uri newApkPath) {
		GlobalVariable.newApkUri=newApkPath;
	}
	//////////////����ͬ����־λ/////////////////////////
	public static boolean getResyncFlag()
	{
		return resyncFlag;
	}
	public static void setResyncFlag(boolean resyncFlag)
	{
		GlobalVariable.resyncFlag=resyncFlag;
	}
}

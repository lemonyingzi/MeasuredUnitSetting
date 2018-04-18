package com.example.bluetooth.data;

import android.net.Uri;

public class GlobalVariable {
	private static boolean BluetoothExisted=false;//蓝牙是否存在标志位
	private static boolean mConnected = false;//连接标志位
    private static int mConnectCount=0;//连接计数器
    private static boolean mHanded=false;  //握手标志位
    private static int mHandCount=0;//握手计数器
    private static boolean mUploadPara=false; //参数上传
    private static int mUploadParaCount=0;//参数上传计数器
    private static boolean mSyncProgress=false;//同步进度标志位
    private static int mProgressCount=0;//同步进度计数器
    private static boolean mSync=false;//同步标志位
    private static int mSyncCount=0;//计时用计数器
    private static int mLogTotal=0;//记录总条数
    private static int syncedCount=0;//已同步的数据    
    private static String IrTimerID=null;
    private static boolean syncReceived=false;
    private static int syncedFailCount=0;
    private static Uri newApkUri=null;
    private static boolean resyncFlag=false;//重新同步标志位
    //////////////////蓝牙是否存在标志位/////////////////
	public static boolean getBluetoothExisted()
	{
		return BluetoothExisted;
	}
	public static void setBluetoothExisted(boolean existedBluetooth)
	{
		GlobalVariable.BluetoothExisted=existedBluetooth;
	}
	//////////////////连接蓝牙标志位/////////////////////////
	public static boolean getConnected()
	{
		return mConnected;
	}
	
	public static void setConnected(boolean mConnected)
	{
		GlobalVariable.mConnected=mConnected;
	}
	/////////////////连接计数器//////////////////////////
	public static int getConnectCount()
	{
		return mConnectCount;
	}
	public static void setConnectCount(int connectCount)
	{
		GlobalVariable.mConnectCount=connectCount;
	}
	//////////////////握手标志位////////////////////////////
	public static boolean getHanded()
	{
		return mHanded;
	}
	public static void setHanded(boolean mHanded)
	{
		 GlobalVariable.mHanded=mHanded;
	}
	///////////////握手计数器////////////////////////
	public static int getHandCount()
	{
		return mHandCount;
	}
	public static void setHandCount(int handCount)
	{
		GlobalVariable.mHandCount=handCount;
	}
	/////////////参数上传//////////////////////////
	public static boolean getUploadPara()
	{
		return mUploadPara;
	}
	public static void setUploadPara(boolean uploadPara)
	{
		GlobalVariable.mUploadPara=uploadPara;
	}
	
	///////////参数上传计数器////////////////////////
	public static int getUploadParaCount()
	{
		return mUploadParaCount;
	}
	public static void setUploadParaCount(int uploadParaCount)
	{
		GlobalVariable.mUploadParaCount=uploadParaCount;
	}
	////////////同步进度标志位//////////////////////
	public static boolean getSyncProgress()
	{
		return mSyncProgress;
	}
	public static void setSyncProgress(boolean syncProgress)
	{
		GlobalVariable.mSyncProgress=syncProgress;
	}
	//////////////同步进度计数器///////////////
	public static int getProgressCount()
	{
		return mProgressCount;
	}
	public static void setProgressCount(int progressCount)
	{
		GlobalVariable.mProgressCount=progressCount;
	}
	//////////////同步标志位///////////////////
	public static boolean getSync() {
		return mSync;
	}
	public static void setSync(boolean sync)
	{
		GlobalVariable.mSync=sync;
	}
	////////////计时用计数器///////////////////
	public static int getSyncCount()
	{
		return  mSyncCount;
	}
	public static void setSyncCount(int syncCount)
	{
		GlobalVariable.mSyncCount=syncCount;
	}
	///////////////记录总条数/////////////////////
	public static int getLogTotal()
	{
		return mLogTotal;
	}
	public static void setLogTotal(int logTotal)
	{
		GlobalVariable.mLogTotal=logTotal;
	}
	//////////////已同步的数据/////////////////////
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
	//////////////重新同步标志位/////////////////////////
	public static boolean getResyncFlag()
	{
		return resyncFlag;
	}
	public static void setResyncFlag(boolean resyncFlag)
	{
		GlobalVariable.resyncFlag=resyncFlag;
	}
}

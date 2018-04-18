/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.bluetooth.le;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.example.bluetooth.data.AnalyticalData;
import com.example.bluetooth.data.DataConvert;
import com.example.bluetooth.data.GlobalVariable;
import com.example.bluetooth.db.*;
import com.example.bluetooth.result.ResultActivity;
import com.example.bluetooth.set.MainSettingActivity;
import com.example.bluetooth.track.TrackActivity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Vibrator;  

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public  class MainActivity extends AppCompatActivity implements OnClickListener{
	private final static String TAG = MainActivity.class.getSimpleName();

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private BluetoothLeService mBluetoothLeService;
    
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    
	private static String originalBlename=null;//原始计时器名称
	private static String originalBleaddress=null;//原始计时器物理地址
//	private static String IrTimerID=null;//IrTimer的id
	private static String IrTimerVersion=null;//IrTimer的版本
	Timer timer = new Timer();  
    boolean connect_status_bit=false;
    private long exitTime=0;
 
    private EditText modeTimeTv;//模式时间
    private EditText protectTimeTv;//保护时间
    TextView trackTv;

	String lastedSyncTime=null;//最新同步时间
	LinearLayout trackLl;
	LinearLayout startTimeMode_linearLayout;//启动计时模式
	LinearLayout startFreeMode_linearLayout;//启动自由模式
	LinearLayout setting_linearLayout;

	LinearLayout iTv;//我
	LinearLayout resultTv;//成绩
	LinearLayout rankingTv;//排名
	LinearLayout timerTv;

	
    DataConvert dataConvert;

	UserDB userDB;
	SyncTimeDB syncTimeDB;
    RcIridDB rcIridDB;
    TrackDB trackDB;
    RoundCounterDB roundCounterDB;
    RoundCounterDetailInfoDB roundCounterDetailInfoDB;
    ParaDB paraDB;

	//将同步数据插入数据库
    SyncDataDB syncDataDB;
	TextView unconnectedTv;
	TextView syncStateTv;
	SwipeRefreshLayout swipeRefreshLayout;
	 Vibrator vibrator;  //震动
	 //////////////////////手势////////////////
	 private GestureDetector mGestureDetector;
	 private int verticalMinDistance = 180;
	 private int minVelocity         = 100;
	 LinearLayout mainLL;
	 //音乐播放器
	 MediaPlayer mMediaPlayer;

    @SuppressWarnings("static-access")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "on create");

        setContentView(R.layout.activity_main);
        
        Log.d(TAG, "on create");

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);   
        dataConvert=new DataConvert();
        
//        mainLL=(LinearLayout)findViewById(R.id.mainll);
//        mainLL.setOnTouchListener(this);
//        mainLL.setLongClickable(true);
//        mGestureDetector = new GestureDetector(MainActivity.this);

        userDB=new UserDB(getApplicationContext(),"TimerStore",null,7);
        syncTimeDB=new SyncTimeDB(getApplicationContext(),"TimerStore",null,7);
        rcIridDB=new RcIridDB(getApplicationContext(), "TimerStore", null, 7);
        syncDataDB=new SyncDataDB(getApplicationContext(), "TimerStore", null, 7);
        trackDB=new TrackDB(getApplicationContext(), "TimerStore", null, 7);
        roundCounterDB=new RoundCounterDB(getApplicationContext(), "TimerStore", null, 7);
        roundCounterDetailInfoDB=new RoundCounterDetailInfoDB(getApplicationContext(), "TimerStore", null, 7);
        paraDB=new ParaDB(getApplicationContext(), "TimerStore", null, 7);
        
        mHandler = new Handler();
        modeTimeTv=(EditText) findViewById(R.id.modeTime);
        
        setRegion(modeTimeTv, 1, 255);//设置最大值和最小值
        
        protectTimeTv=(EditText) findViewById(R.id.protectTime);
        
        setRegion(protectTimeTv, 1, 255);//设置最大值和最小值
        trackTv=(TextView)findViewById(R.id.trackTextView);
        String track=trackDB.getSelectedTrack();
        Log.d(TAG, "track:"+track);
        
        if (track!=null) {
        	trackTv.setText(track);
		}

        unconnectedTv=(TextView) findViewById(R.id.label_unconnected);
        syncStateTv=(TextView) findViewById(R.id.syncState);
        ///////////////////启动限时模式//////////////////////////////////////
        startTimeMode_linearLayout=(LinearLayout)findViewById(R.id.start_time_mode);//启动限时模式
        startTimeMode_linearLayout.setOnClickListener(new View.OnClickListener() {
    	public void onClick(View v) {
    		if(connect_status_bit)
        	{
    			int timeSet=1;
    			int timeLimit=1;
    			try {
    				timeSet = Integer.parseInt(modeTimeTv.getText().toString());
    				timeLimit=Integer.parseInt(protectTimeTv.getText().toString());
    				} catch (NumberFormatException e) {
    				
    				} 
        		mBluetoothLeService.t_data_start_time_clock(timeSet,timeLimit);
        		String timeset=modeTimeTv.getText().toString();
	        	syncTimeDB.updateSetTime(timeset);      
        	}
        	else{
      			  Toast toast = Toast.makeText(MainActivity.this, R.string.deviceNotConnect, Toast.LENGTH_SHORT); 
      			  toast.show(); 
      		}
    		}
        });
        ///////////////////启动自由模式///////////////////////////////////////////////
        startFreeMode_linearLayout=(LinearLayout)findViewById(R.id.start_free_mode);//启动自由模式
        startFreeMode_linearLayout.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			if(connect_status_bit)
			{
				int timeLimit=1;
				try
				{
					timeLimit=Integer.parseInt(protectTimeTv.getText().toString());
				}
				catch (NumberFormatException e) {
				}
				mBluetoothLeService.t_data_start_free_clock(timeLimit);
			}
			else{
				  Toast toast = Toast.makeText(MainActivity.this, R.string.deviceNotConnect, Toast.LENGTH_SHORT); 
		  			  toast.show(); 
		  		}
			}
		 });

       trackLl=(LinearLayout)findViewById(R.id.tracklinearlayout);
       trackLl.setAddStatesFromChildren(true);

       trackLl.setOnClickListener(new OnClickListener(){
    	   public void onClick(View v)
    	   {
    			Intent intent=new Intent(MainActivity.this,TrackActivity.class);
				startActivity(intent);
    	   }
       });
       //参数设定
       setting_linearLayout=(LinearLayout)findViewById(R.id.parasetting_layout);
       setting_linearLayout.setOnClickListener(new OnClickListener()
    		   {

				@Override
				public void onClick(View v) {
					Intent intent=new Intent(MainActivity.this,MainSettingActivity.class);
					startActivity(intent);
				}
    	   
    		   });
   
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            AlertDialog.Builder builder=new Builder(MainActivity.this);  
            builder.setMessage(R.string.ble_not_supported);  
            builder.setTitle(R.string.note);  
            //添加AlertDialog.Builder对象的setPositiveButton()方法   
            builder.setPositiveButton(R.string.confirmStr, new android.content.DialogInterface.OnClickListener() {  
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();					
				}  
            });  
              
            //添加AlertDialog.Builder对象的setNegativeButton()方法   
            builder.setNegativeButton(R.string.cancelStr, new android.content.DialogInterface.OnClickListener() {  
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();					
				}  
            });  
            builder.create().show();  
        }

        //绑定蓝牙
	    Intent gattServiceIntent = new Intent(MainActivity.this, BluetoothLeService.class);
	    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        try
        {
        	final BluetoothManager bluetoothManager =(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter =(BluetoothAdapter)bluetoothManager.getAdapter();
        }
        catch(Exception ex)
        {
          mBluetoothAdapter = ((BluetoothAdapter) getSystemService(Context.BLUETOOTH_SERVICE)).getDefaultAdapter();

        }

        
        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
//            finish();
//            return;
        }
        
              
        // 如果本地蓝牙没有开启，则开启  
        if (!mBluetoothAdapter.isEnabled()) 
        {
            // 我们通过startActivityForResult()方法发起的Intent将会在onActivityResult()回调方法中获取用户的选择，比如用户单击了Yes开启，  
            // 那么将会收到RESULT_OK的结果，  
            // 如果RESULT_CANCELED则代表用户不愿意开启蓝牙  
            Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);  
            startActivityForResult(mIntent, 1);  
            // 用enable()方法来开启，无需询问用户(实惠无声息的开启蓝牙设备),这时就需要用到android.permission.BLUETOOTH_ADMIN权限。  
            // mBluetoothAdapter.enable();  
            // mBluetoothAdapter.disable();//关闭蓝牙  
        }

        getOriginalBleNameAddress();

		//查询数据库中的最新同步时间
        lastedSyncTime=syncTimeDB.getSyncTime();
		if(lastedSyncTime!=null)
		{
			syncStateTv.setText(getResources().getString(R.string.syncCompleted)+lastedSyncTime);
		}
		else
		{	
			syncStateTv.setText(R.string.notSync);
		}
		 ////////////////////////设置限时模式时间//////////////////////////////////////
        String timeset=syncTimeDB.getTimeset();
        Log.d(TAG, "111111111111111111timeset:"+timeset);
		  
        if (timeset!=null) {
            modeTimeTv.setText(timeset);//设置限时模式时间
		}
		        
		
		if(originalBlename==null || originalBleaddress==null)
		{
			unconnectedTv.setText(R.string.notBoundIrTimer);
		}
		else
		{
			 timer.schedule(task, 1000, 200); // 1s后执行task,经过200ms再次执行  
			 
			 unconnectedTv.setText(R.string.connecting);
		}
		//设置IRTIMERID
		if (GlobalVariable.getIrTimerID()==null) {
			if (paraDB.getDeviceID()!=null) {
				GlobalVariable.setIrTimerID(paraDB.getDeviceID());
			}
		}
	    
       
        Log.d(TAG, "GlobalVariable IrTimerID:"+GlobalVariable.getIrTimerID());
        
        iTv=(LinearLayout) findViewById(R.id.bottomI);
        iTv.setOnClickListener(bottomListener);
        resultTv=(LinearLayout) findViewById(R.id.bottomResults);
        resultTv.setOnClickListener(bottomListener);
        rankingTv=(LinearLayout) findViewById(R.id.bottomRanking);
        rankingTv.setOnClickListener(bottomListener);
        timerTv=(LinearLayout) findViewById(R.id.bottomTimer);
        timerTv.setOnClickListener(bottomListener);
		 ///////////////////////////下拉刷新//////////////////////////////////////////
		 swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
		 swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
		 swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener(){
			 @Override
			 public void onRefresh() {
				 if (GlobalVariable.getConnected()) {
					GlobalVariable.setSyncProgress(true);	
				}
				 else
				 {
					//3秒之后关闭
					new Handler().postDelayed(new Runnable() {
						public void run() {
							swipeRefreshLayout.setRefreshing(false);
						}
					}, 3000);
				 }
			}
		 
		});
	
    }
    
 

     
    private void getOriginalBleNameAddress()
    {
		//查询数据库中的已保存的蓝牙设备名称和MAC地址
        List<String> blenameaddress=new ArrayList<String>();
        blenameaddress=userDB.getBleNameAndBleAddress();
        if (blenameaddress!=null && blenameaddress.size()!=0) {
			originalBlename=blenameaddress.get(0);
			originalBleaddress=blenameaddress.get(1);
			Log.d(TAG, "originalBleAddress:"+originalBleaddress);
		}
    }
    
 
    //底部定时器 成绩 排名 我 跳转
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//创建监听对象    
        public void onClick(View v)
        {    
        	int id = v.getId();
			if (id == R.id.bottomI) 
			{
				Intent intent=new Intent(MainActivity.this,IActivity.class);
				startActivity(intent);
			}
			else if (id==R.id.bottomRanking) {
				Intent intent=new Intent(MainActivity.this,RankingActivity.class);
				startActivity(intent);
			}
			else if (id==R.id.bottomResults) {
				Intent intent=new Intent(MainActivity.this,ResultActivity.class);
				startActivity(intent);
			}
		}
    };  
    /**
     * 匿名类，重写了onServiceConnected和onServiceDisconnected方法
     */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

    	//在活动与服务成功绑定的时候调用
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.i(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(originalBleaddress);
            Log.i(TAG, "initaialize success");
        }
        //在活动与服务连接断开的时候调用
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
        
    };
    
    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
            	Log.e(TAG, "broadcastreceiver connected");
                connect_status_bit=true;
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                GlobalVariable.setConnected(false);//连接标志位
                GlobalVariable.setHanded(false);
                updateConnectionState(R.string.disconnected);//断开连接
                connect_status_bit=false;
                invalidateOptionsMenu();
                Log.d(TAG, "ation gatt disconnected");

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
            	Log.i(TAG, "service discovered");
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    
    
    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
 
        if( gattServices.size()>0&&mBluetoothLeService.get_connected_status( gattServices )>=4 )
        {
	        if( connect_status_bit )
			  {
	        	GlobalVariable.setConnected(true);
	        	GlobalVariable.setHanded(true);
	        	show_view( true );
				mBluetoothLeService.enable_JDY_ble(true);
				 try {  
			            Thread.currentThread();  
			            Thread.sleep(100);  
			        } catch (InterruptedException e) {  
			            e.printStackTrace();  
			        }  
				 mBluetoothLeService.enable_JDY_ble(true);
				 updateConnectionState(R.string.connecting);//正在连接，此时已经连接上
		
			  }else{
				  Toast toast = Toast.makeText(MainActivity.this,R.string.deviceNotConnect, Toast.LENGTH_SHORT); 
				  toast.show(); 
			  }
        }
    }
    
	void show_view( boolean p )
    {
    	if(p){
    		unconnectedTv.setText(R.string.connected);
    		
    	}else{
    		unconnectedTv.setText(R.string.connecting);
    	}
    }
	//发送蓝牙指令
    Handler handler = new Handler() {  
        public void handleMessage(Message msg) {  
        	if (msg.what == 1) {  
            	if (mBluetoothLeService != null) {
            		GlobalVariable.setHandCount(GlobalVariable.getHandCount()+1);//同步和握手时钟
            		GlobalVariable.setUploadParaCount(GlobalVariable.getUploadParaCount()+1);//参数上传
            		GlobalVariable.setConnectCount(GlobalVariable.getConnectCount()+1);
            		GlobalVariable.setSyncCount(GlobalVariable.getSyncCount()+1);
            		GlobalVariable.setProgressCount(GlobalVariable.getProgressCount()+1);//同步进度上传
                	if( GlobalVariable.getConnected()==false && GlobalVariable.getBluetoothExisted()==true)//一秒钟连接一次
                	{
                		if (GlobalVariable.getConnectCount() >=10) {
                			
                      		updateConnectionState(R.string.connecting);//更新UI
                      		Boolean connectResult=mBluetoothLeService.connect(originalBleaddress);
                      		Log.d(TAG, "connect result:"+connectResult);
                    		GlobalVariable.setConnectCount(0);
                    		GlobalVariable.setHandCount(0);
						}
                	}
                	else if(GlobalVariable.getConnected()==true && GlobalVariable.getHanded()==true)//握手和同步时钟
                	{
            			if(GlobalVariable.getHandCount()>=20)
            			{
            				try
            				{
            					mBluetoothLeService.t_data_hand_sync_clock();
                    			Log.d(TAG, "handed and sync clock");
                    			GlobalVariable.setHandCount(0);
            				}
            				catch(Exception e)
            				{
            					Log.d(TAG, e.toString());
            				}
            		
            			}
                	}
                	else if(GlobalVariable.getConnected()==true && GlobalVariable.getUploadPara()==true)//参数上传
                	{
                		if(GlobalVariable.getUploadParaCount()>=5)
                		{
                			try
                			{
                				mBluetoothLeService.t_data_parm_upload();
                    			GlobalVariable.setUploadParaCount(0);
                    			Log.d(TAG, "upload para");
                			}
                			catch(Exception e)
                			{
                				Log.d(TAG, e.toString());
                			}
                		}
                	}
                	else if(GlobalVariable.getConnected()==true && GlobalVariable.getSyncProgress()==true)//同步进度上传，失败的话5秒重试一遍，连续三次失败，说明已经断开，重新连接
                	{	
                		if(GlobalVariable.getProgressCount()>=5)
                		{
                	  		mBluetoothLeService.t_data_sync_progress();
                    		Log.d(TAG, "sync progress");
                    		GlobalVariable.setProgressCount(0);
                		}
                		else
                		{
                			GlobalVariable.setSyncProgress(false);
                		}
                	}
                	else if(GlobalVariable.getConnected()==true && GlobalVariable.getSync()==true)//同步
                	{
                		if(GlobalVariable.getSyncReceived())//如果为true，说明接受到，则直接发送
                		{
                			int syncedCount=GlobalVariable.getSyncedCount();
                			mBluetoothLeService.t_data_sync(syncedCount+1);
                			Log.d(TAG, "syncing "+String.valueOf(syncedCount+1));
                			GlobalVariable.setSyncCount(0);
                			syncStateTv.setText(getResources().getString(R.string.syncState)+String.valueOf(syncedCount+1)+"/"+GlobalVariable.getLogTotal());
                			GlobalVariable.setSyncReceived(false);
                		}
                		else if (GlobalVariable.getSyncReceived()==false && GlobalVariable.getSyncCount()>=10) {
                			Log.d(TAG, "同步失败次数："+GlobalVariable.getSyncedFailCount());
                			if (GlobalVariable.getSyncedFailCount()>=3) {
								syncStateTv.setText(R.string.syncFail);
								Log.d(TAG, "同步失败");
								GlobalVariable.setSync(false);
							}
                			else
                			{
                				GlobalVariable.setSyncedFailCount(GlobalVariable.getSyncedFailCount()+1);
                    			int syncedCount=GlobalVariable.getSyncedCount();
                    			mBluetoothLeService.t_data_sync(syncedCount+1);
                    			Log.d(TAG, "syncing等待两秒"+String.valueOf(syncedCount+1));
                    			GlobalVariable.setSyncCount(0);
                    			syncStateTv.setText(getResources().getString(R.string.syncState)+String.valueOf(syncedCount+1)+"/"+GlobalVariable.getLogTotal());
                			}
                	
						}
                	}
                }
        	}
            super.handleMessage(msg);  
        };  
    };  
    
    
    private int getHeadPosition(String data)
    {
  		if (data.length()>14) {//头尾加起来的最小长度
	    	for(int i=0;i<data.length();i++)
	    	{
    			if (data.substring(i,i+8).equalsIgnoreCase("4A534B46")) {
        			return i;
    			}
	    	} 
  		}
    	return -1;
    }
    
    private int getTailPosition(int headPosition, String data)
    {
 		if (data.length()>headPosition+6+8) {//头尾加起来的最小长度
	    	for(int i=headPosition;i<data.length();i++)
	    	{
	    		if (data.length()<i+6) {
					return -1;
				}
				if (data.substring(i,i+6).equalsIgnoreCase("534A4B")) {
	    			return i;
				}
	    	} 
 		}
    	return -1;    
    }
    
    String da="";
    int len_g = 0;
    String data2="";
    byte[] syncData=new byte[18];
    private void displayData( String data1 ) {
	  	if( data1!=null&&data1.length()>=12)
    	{
	  		try
	  		{
	  		
	  			data2=data2+data1;
	  			Log.d(TAG, "data2:"+data2+",length:"+data2.length());
		  		int headPosition=getHeadPosition(data2);
		  		int tailPosition=getTailPosition(headPosition, data2);
		  		if (headPosition==-1 || tailPosition==-1) {
					return;
				}
		  		if(headPosition!=-1 && tailPosition!=-1)
		  		{
			  		String data3=data2.substring(headPosition,tailPosition+6);
			  		String length=data3.substring(8,10);//长度
			  		String order=data3.substring(10,12);//命令
		  			if(length.equalsIgnoreCase("14") && order.equalsIgnoreCase("01"))//握手同步时钟
		  			{
		  				GlobalVariable.setIrTimerID(data3.substring(20, 28));
		  				Log.d(TAG, "IrTimerID:"+GlobalVariable.getIrTimerID());
		  				String version1Str=data3.substring(28,30);
		  				String version2Str=data3.substring(30,32);
		  				String version3Str=data3.substring(32,34);
//		  				 final StringBuilder stringBuilder = new StringBuilder(versionBuffer.length);
//		                 for(byte byteChar : versionBuffer)
//		                    stringBuilder.append(String.format("%02X", byteChar));
//		                 Toast.makeText(getApplicationContext(), stringBuilder, Toast.LENGTH_SHORT).show();
		  				int version1=Integer.parseInt(version1Str)-30;
		  				int version2=Integer.parseInt(version2Str)-30;
		  				int version3=Integer.parseInt(version3Str)-30;
		  				IrTimerVersion=String.valueOf(version1)+"."+String.valueOf(version2)+"."+String.valueOf(version3);
		  				data2="";
		  				updateConnectionState(R.string.connected);//更新状态为已连接
		  	 			GlobalVariable.setHanded(false);
		  	 			GlobalVariable.setUploadPara(true);
		  	 			Log.d(TAG, "hand and sync success");
		  			}
		 			else if(length.equalsIgnoreCase("09") && order.equalsIgnoreCase("02"))//心跳
		  			{
		  				Log.d(TAG,"心跳");
		  				data2="";
		  			}
		  			else if(length.equalsIgnoreCase("0D") && order.equals("03"))//参数上传
		  			{
		  		        String track=syncTimeDB.getTrack();
		  		        Log.d(TAG, "track:"+track);
		  		 
		  				String timeLimit=data3.substring(12,14);//保护限制时间
		  				byte[] timeLimitByte=dataConvert.HexStringToByteArray(timeLimit);
		  				int timeLimitShort=timeLimitByte[0];
		  				//低电压保护
		  				String UVP=data3.substring(14,16);
		  				byte[] UVPByte=dataConvert.HexStringToByteArray(UVP);
		  				int UVPShort=UVPByte[0];
		  				//LCD对比度
		  				String LCDContrast=data3.substring(16,18);
		  				byte[] LCDContrastByte=dataConvert.HexStringToByteArray(LCDContrast);
		  				int LCDContrastInt=LCDContrastByte[0];
	  				    //LED状态灯亮度
		  				String LEDBrightness=data3.substring(18,20);
		  				byte[] LEDBrightnessByte=dataConvert.HexStringToByteArray(LEDBrightness);
		  				int LEDBrightnessInt=LEDBrightnessByte[0];		
		  				if (!paraDB.seletDeviceId(GlobalVariable.getIrTimerID())) {
		  					Log.d(TAG, "device id不存在");
			  				paraDB.insertPara(GlobalVariable.getIrTimerID(),timeLimitShort, UVPShort, LCDContrastInt, LEDBrightnessInt,IrTimerVersion);
						}
		  				else
		  				{
		  					Log.d(TAG, "device id存在");
		  					paraDB.update(GlobalVariable.getIrTimerID(), timeLimitShort, UVPShort, LCDContrastInt, LEDBrightnessInt,IrTimerVersion);
		  				}
	
		  				protectTimeTv.setText(String.valueOf(timeLimitShort));
		  				GlobalVariable.setUploadPara(false);
		  				GlobalVariable.setSyncProgress(true);
		  				data2="";
		  				Log.d(TAG, "para upload success");
		  				
		  			}
		  			else if(length.equalsIgnoreCase("0D") && order.equalsIgnoreCase("05"))//同步进度上传
		  			{
		  				
		  				String logCounter=data3.substring(12,16);//记录序号
		  				byte[] logCounterByte=dataConvert.HexStringToByteArray(logCounter);
		  				GlobalVariable.setLogTotal(dataConvert.byte2Short(logCounterByte));//总记录条数
		  				
		  				String syncCounter=data3.substring(16,20);//的数量
		  				byte[] syncCounterByte=dataConvert.HexStringToByteArray(syncCounter);
		  				if (GlobalVariable.getResyncFlag()) {//判断重新同步
							GlobalVariable.setSyncedCount(0);
						}
		  				else
		  				{
			  				GlobalVariable.setSyncedCount(dataConvert.byte2Short(syncCounterByte));
		  				}
		  				Log.d(TAG,"记录总条数："+String.valueOf(GlobalVariable.getLogTotal())+"\r\n"+"已同步条数："+String.valueOf(GlobalVariable.getSyncedCount()));
		  				data2="";
		  				GlobalVariable.setSyncProgress(false);//同步进度标志位置为false
		  				if(GlobalVariable.getSyncedCount()<GlobalVariable.getLogTotal())//已同步数据小于记录数则开始同步
		  				{
		  					GlobalVariable.setSync(true);//同步标志位为true
		  				}
		  				else
		  				{
		  					swipeRefreshLayout.setRefreshing(false);
		  				}
		  			}
		  			else if(length.equalsIgnoreCase("1D") && order.equalsIgnoreCase("06"))//数据同步
		  			{
		  				byte[] data=dataConvert.HexStringToByteArray(data3);
		  				System.arraycopy(data, 6, syncData, 0, 18);
		  				String logCounter=data3.substring(12,16);//记录序号
		  				byte[] logCounterByte=dataConvert.HexStringToByteArray(logCounter);
		  				int logCounterShort=dataConvert.byte2Short(logCounterByte);
		  				
		  				String roundCounter=data3.substring(16,24);//轮序号四字节
		  				byte[] roundCounterByte=dataConvert.HexStringToByteArray(roundCounter);
		  				int roundCounterShort=dataConvert.byte2int(roundCounterByte);
		  				
		  				String mode=data3.substring(24,26);//模式

		  				String roundIRID=data3.substring(26,38);//发射器号码
		  				
		  				String LAP=data3.substring(38,42);//圈数序号
		  				byte[] LAPbyte=dataConvert.HexStringToByteArray(LAP);
		  				int LAPint=dataConvert.byte2Short(LAPbyte);
		  				
		  				String time=data3.substring(42,50);//时间
		  				
		  				byte[] timeByte=dataConvert.HexStringToByteArray(time);
		  				int timeInt=dataConvert.byte2int(timeByte);
		  				
		  				String receiveOKCounter=data3.substring(50,52);//接收正确个数
		  				byte[] receiveOKCounterByte=dataConvert.HexStringToByteArray(receiveOKCounter);
		  				int receiveOKCounterInt=receiveOKCounterByte[0];
		  				if (receiveOKCounterInt<0) {
		  					receiveOKCounterInt=receiveOKCounterInt+256;
						}
		  				Log.d(TAG, "LAP和time：LAP:"+LAP+"time:"+time);
		  				String convertIrid=dataConvert.getIRIDInt(roundIRID);
		  				if (!rcIridDB.selectIrid(convertIrid)) {
							rcIridDB.insertPara(null, convertIrid);
						}
		  				
		  				if((GlobalVariable.getSyncedCount()+1)==logCounterShort)
		  				{
		  					Log.d(TAG, "已同步syncedCount和记录号logCounterShort相同");
		  					if (!syncDataDB.isLogcounterRoundcounterDeviceIdExist(logCounterShort, roundCounterShort)) {
		  						Log.d(TAG, "不存在则插入");
						        syncDataDB.insert(logCounterShort, roundCounterShort, mode, roundIRID, LAPint, timeInt,receiveOKCounterInt,"false");
							}
		  					else
		  					{
		  						Log.d(TAG, "存在，则跳过插入");
		  					}
					        GlobalVariable.setSyncedCount(logCounterShort);
		  				}
		  				else
		  				{
		  					Log.d(TAG, "已同步syncedCount和记录号logCounterShort不同,\nsyncedCount="+GlobalVariable.getSyncedCount()+"logCounterShort:"+logCounterShort);
		  				}
		  				if(GlobalVariable.getSyncedCount()==GlobalVariable.getLogTotal())//同步完成
		  				{
		  					//获取当前时间
		  					Date now = new Date(); 
		  					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式 
		  					//将当前时间存入数据库
		  					syncTimeDB.insertSyncTime(dateFormat.format(now));
		  					syncStateTv.setText(getResources().getString(R.string.syncCompleted)+dateFormat.format(now));
		  					swipeRefreshLayout.setRefreshing(false);
		  					GlobalVariable.setSync(false);
		  			        //分析数据
		  			        AnalyticalData analyticalData=new AnalyticalData(getApplicationContext());
		  			        analyticalData.GetRoundCounterFromSyncData();
		  			        analyticalData.GetRoundCounterDetailInfoFromRoundCounter();
		  			        
		  			        if (GlobalVariable.getResyncFlag()) {
								GlobalVariable.setResyncFlag(false);
							}
		  				}
		  				else if(GlobalVariable.getSyncedCount()>GlobalVariable.getLogTotal())
		  				{
		  					GlobalVariable.setSync(false);
		  				}
		  				GlobalVariable.setSyncReceived(true);
		  				data2="";
		  			}
		  			else if(length.equalsIgnoreCase("09") && order.equalsIgnoreCase("12"))//数据正确
		  			{
		  			    Log.d(TAG,"数据正确");
		  			    data2="";
		  			}
		  			else if(length.equalsIgnoreCase("09") && order.equalsIgnoreCase("13"))//数据错误
		  			{
		  				Log.d(TAG,"数据错误");
		  				data2="";
		  			}
		  			else if(length.equalsIgnoreCase("09") && order.equalsIgnoreCase("11"))
		  			{
		  				Log.d(TAG,"完成");
		  				Toast.makeText(getApplicationContext(), R.string.completeStr, Toast.LENGTH_LONG).show();
		  				 //震动30毫秒  
		  		        vibrator.vibrate(500);  
		  				data2="";
		  			}
		  			else if(length.equalsIgnoreCase("09") && order.equalsIgnoreCase("14"))//错误
		  			{
		  				Log.d(TAG,"错误");
		  				data2="";
		  			}
		  			else if(length.equalsIgnoreCase("09") && order.equalsIgnoreCase("10") )//正忙请稍后
		  			{
		  				Log.d(TAG,"正忙");
		  				Toast.makeText(getApplicationContext(), R.string.busyStr, Toast.LENGTH_SHORT).show();
		  				if (GlobalVariable.getSyncProgress()) {
			  				GlobalVariable.setSyncProgress(false);
						}
		  				if (GlobalVariable.getUploadPara()) {
							GlobalVariable.setUploadPara(false);
						}
						swipeRefreshLayout.setRefreshing(false);

		  				data2="";
		  			}
		 
		  			else if(length.equalsIgnoreCase("09") && order.equalsIgnoreCase("15"))
		  			{
		  				Log.d(TAG,"连接完成");
		  				data2="";
		  			}
		  			else if (length.equalsIgnoreCase("09") && order.equalsIgnoreCase("16")) 
		  			{
						//震动
		  				vibrator.vibrate(300);
		  				data2="";
		  				startAlarm();
					}
		  			else if (length.equalsIgnoreCase("09") && order.equalsIgnoreCase("17")) {
						vibrator.vibrate(300);
						startAlarm();
						data2="";
					}
		  		}
	    		len_g += data1.length()/2;
	    		Log.d(TAG, ""+len_g);//累计接收到的字节数
	  		}
	  		catch(IndexOutOfBoundsException ex)
	  		{
	  			Log.d(TAG,ex.toString());
	  			
	  		}
	  		catch(Exception ex)
	  		{
	  			Log.d(TAG, ex.toString());
	  		}
    	}
    }
    //播放铃声
    private void startAlarm() throws InterruptedException {  
        mMediaPlayer = MediaPlayer.create(this, getSystemDefultRingtoneUri());  
        mMediaPlayer.setLooping(false);  
        Log.d(TAG, "alarm start");
        try {  
            mMediaPlayer.prepare();  
        } catch (IllegalStateException e) {  
        	Log.d(TAG, e.toString());
//            e.printStackTrace();  
        } catch (IOException e) {  
//            e.printStackTrace();  
        	Log.d(TAG, e.toString());

        }  
        mMediaPlayer.start();  
        Thread.sleep(1000);
        mMediaPlayer.pause();
        mMediaPlayer.release();
        Log.d(TAG, "alarm start");

    }  
    
    //获取系统默认铃声的Uri  
    private Uri getSystemDefultRingtoneUri() {  
        return RingtoneManager.getActualDefaultRingtoneUri(this,  
                RingtoneManager.TYPE_NOTIFICATION);  
    } 
   
    
    //每个1秒钟发送一个消息
    TimerTask task = new TimerTask() {  
        @Override  
        public void run() {  
            // 需要做的事:发送消息  
            Message message = new Message();  
            message.what = 1;  
            handler.sendMessage(message);  
            System.gc();
        }  
    }; 
    

	public void onClick(View v) {
		switch (v.getId()) {
		case 0:
			break;
		}
	}
	
    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	unconnectedTv.setText(resourceId);
            }
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);//10s后调用此方法 停止扫描

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);

        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Log.d(TAG, "startLeScan call back");
					Log.d(TAG, "device:"+device.getAddress());
//					userDB.insertUser(device.getName(), device.getAddress(), "12");
					//插入数据库
			        if(originalBlename!=null && originalBleaddress!=null && originalBleaddress.equals(device.getAddress()))
					{
						Log.d(TAG,  "扫描到的设备和保存的设备相同");
//						if (mBluetoothLeService==null) {
						 Intent gattServiceIntent = new Intent(MainActivity.this, BluetoothLeService.class);
						 bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
//						}
					   
					    updateConnectionState(R.string.connecting);
				        if (mScanning) {
				            mBluetoothAdapter.stopLeScan(mLeScanCallback);
				            mScanning = false;
				        }
				        GlobalVariable.setBluetoothExisted(true);
					}
				}
			});
		}
	};

    /**
     * 匿名类，重写了onServiceConnected和onServiceDisconnected方法
     */
    private final ServiceConnection mGetServiceConnection = new ServiceConnection() {

    	//在活动与服务成功绑定的时候调用
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (mBluetoothLeService==null) {
				Log.d(TAG, "mBluetoothService 为空");
			}
            else
            {
            	Log.d(TAG, "mBluetoothService 不为空");
            }
        }
        //在活动与服务连接断开的时候调用
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
	@Override
	protected void onResume() {//打开APP时扫描设备
		super.onResume();
		Log.d(TAG, "on resume");
		scanLeDevice(true);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		if (mBluetoothLeService==null) {
			
		}

        getOriginalBleNameAddress();
//	 
   
        
//		if (GlobalVariable.getUploadPara() && GlobalVariable.getConnected()) {
//			unconnectedTv.setText("已连接");
//		}
        if (task==null) {
          	timer=new Timer();
        	task=new TimerTask(){  
                @Override  
                public void run() {  
                    // 需要做的事:发送消息  
                    Message message = new Message();  
                    message.what = 1;  
                    handler.sendMessage(message); 
                    System.gc();
                }  
            }; 
            Log.d(TAG,"启动定时器");
            try
            {
            	timer.schedule(task, 1000, 200);  
            }
            catch (Exception e) {
    			Log.d(TAG, e.toString());
    		}
            
		}
	}


	@Override
	protected void onPause() {//停止扫描
		super.onPause();
		scanLeDevice(false);
	    Log.d(TAG, "on pasue"); 
		GlobalVariable.setHanded(false);
	    task.cancel();
	    timer.cancel();
	    task=null;
	    timer=null;
	  
	   
	}

	@Override 
	protected void onDestroy()
	{
		super.onDestroy();
		GlobalVariable.setSyncedFailCount(0);
		Log.d(TAG, "on destory");
		  try{
		    	 if (mServiceConnection!=null) {
		 			unbindService(mServiceConnection);
		 		}
		 		unregisterReceiver(mGattUpdateReceiver);

		    }
		  catch(IllegalArgumentException e) {  
			    if (e.getMessage().contains("Receiver not registered")) {  
			        // Ignore this exception. This is exactly what is desired  
			    }
			    else if (e.getMessage().contains("Service not registered")) {
					
				}
			    else {  
			        // unexpected, re-throw  
			        throw e;  
			    }  
		  }
		  catch (Exception e) {
			  	
				Log.e(TAG, e.toString());
				
			}
	}
	
	protected void onStart() {
		super.onStart();


		Log.d(TAG, "on start");
	}
	
	protected void onRestart() {
		super.onRestart();
		Log.d(TAG, "on restart");
//		try {
		Intent gattServiceIntent = new Intent(MainActivity.this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
		if (mBluetoothLeService!=null) {
			if (GlobalVariable.getConnected()) {
				unconnectedTv.setText(R.string.connected);
			}
			Log.d(TAG, "on restart mBluetoothService不为空");
		}
		Log.d(TAG, "handed:"+GlobalVariable.getHanded()+",uploaded:"+GlobalVariable.getUploadPara()+",connected:"+GlobalVariable.getConnected());
	
//		} catch (Exception e) {
//			Log.d(TAG, e.toString());
//		}
//		
		trackTv.setText(trackDB.getSelectedTrack());
	}
	
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "on stop");
	
	}
	
	class ViewHolder {
		TextView tv_devName, tv_devAddress;
	}
    
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
   //////////////////按两次返回键退出程序///////////////////////////////////
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), R.string.exitApplication,
                    Toast.LENGTH_SHORT).show();
            exitTime=System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    
    
    /** edittext只能输入数值的时候做最大最小的限制 */
	public static void setRegion(final EditText edit, final int MIN_MARK, final int MAX_MARK) {
		edit.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (start > 1) {
					if (MIN_MARK != -1 && MAX_MARK != -1) {
						int num = Integer.parseInt(s.toString());
						if (num > MAX_MARK) {
							s = String.valueOf(MAX_MARK);
							edit.setText(s);
						} else if (num < MIN_MARK) {
							s = String.valueOf(MIN_MARK);
							edit.setText(s);
						}
						edit.setSelection(s.length());
					}
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
				if (s != null && !s.equals("") && !s.equals(" ")) {
					if (MIN_MARK != -1 && MAX_MARK != -1) {
						try
						{
						    int markVal = Integer.parseInt(s.toString());
							if (markVal > MAX_MARK) {
								edit.setText(String.valueOf(MAX_MARK));
								edit.setSelection(String.valueOf(MAX_MARK).length());
							}
							else if (markVal==0) {
								edit.setText(String.valueOf(MIN_MARK));
								edit.setSelection(String.valueOf(MIN_MARK).length());
							}
						}
						catch(NumberFormatException e)
						{
							
						}
						return;
					}
				}
				
			}
		});
	}
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getX() - e2.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {
            // 切换Activity,向左手势
        	   Intent intent = new Intent(MainActivity.this, ResultActivity.class);
               startActivity(intent);
        } else if (e2.getX() - e1.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {
            // 切换Activity，向右手势
//             finish();
        }
    
        return false;
    }
    
    
//	@Override
//	public boolean onDown(MotionEvent e) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public void onShowPress(MotionEvent e) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public boolean onSingleTapUp(MotionEvent e) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public void onLongPress(MotionEvent e) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public boolean onTouch(View v, MotionEvent event) {
//		return mGestureDetector.onTouchEvent(event);
//	}
//
//	@Override  
//	  public boolean dispatchTouchEvent(MotionEvent ev) {  
//	      if (mGestureDetector != null) {  
//	          if (mGestureDetector.onTouchEvent(ev))  
//	              //If the gestureDetector handles the event, a swipe has been executed and no more needs to be done.  
//	              return true;  
//	      }  
//	      return super.dispatchTouchEvent(ev);  
//	  }  
//	  
//	    
//	  @Override  
//	  public boolean onTouchEvent(MotionEvent event) {  
//	          return mGestureDetector.onTouchEvent(event);  
//	  }  

}
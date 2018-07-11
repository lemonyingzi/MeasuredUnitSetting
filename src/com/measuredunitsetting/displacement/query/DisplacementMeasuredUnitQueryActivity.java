package com.measuredunitsetting.displacement.query;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.measuredunitsetting.BluetoothLeService;
import com.measuredunitsetting.R;
import com.measuredunitsetting.data.PublicMethod;
import com.measuredunitsetting.db.UserDB;
import com.measuredunitsetting.global.GlobalVariable;
import com.measuredunitsetting.global.LogUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import zxing.activity.CaptureActivity;

public class DisplacementMeasuredUnitQueryActivity extends Activity {
	private final static String TAG=DisplacementMeasuredUnitQueryActivity.class.getSimpleName();
	private BluetoothLeService mBluetoothLeService;//蓝牙连接服务
	PublicMethod publicMethod=new PublicMethod();
	
	TextView backTv;
	//手动查
	LinearLayout manualCheckLl;
	//扫码查
	LinearLayout sweepCodeCheckLl;
	
	//定时器
	Timer paraUploadtimer = new Timer(); //参数上传定时器
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_displacementmeasuredunitquery);
	
		//返回
		backTv=(TextView)findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
	
		//手动查
		manualCheckLl=(LinearLayout)findViewById(R.id.manualCheckLl);
		manualCheckLl.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View arg0) {

						Intent intent=new Intent(DisplacementMeasuredUnitQueryActivity.this,DisplacementMeasuredUnitQueryInputActivity.class);
						startActivity(intent);
					}
			
				});
		
		//扫码查
		sweepCodeCheckLl=(LinearLayout)findViewById(R.id.sweepCodeCheckLl);
		sweepCodeCheckLl.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View arg0) {
						Intent intent=new Intent(DisplacementMeasuredUnitQueryActivity.this,CaptureActivity.class);
						startActivity(intent);
					}
				});
		//绑定蓝牙服务
	    Intent gattServiceIntent = new Intent(DisplacementMeasuredUnitQueryActivity.this, BluetoothLeService.class);
	    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	    
	    paraUploadtimer.schedule(new paraUploadTimerTask(), 500, 1000); // 1s后执行task,经过500ms再次执行  
	}
	
	/**
	 * 参数上传
	 * @author Administrator
	 *
	 */
	  class paraUploadTimerTask extends TimerTask
	    {
	    	  @Override  
	          public void run() {  
	              // 需要做的事:发送消息  
	              Message message = new Message();  
	              message.what = 1;  
	              handler.sendMessage(message);  
	          }  
	    	
	    };
    /**
     * 顶部返回键
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//创建监听对象    
    	public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv) {
        	
            ((Activity)DisplacementMeasuredUnitQueryActivity.this).finish();
			}
		}
    };  
    
	//发送蓝牙指令
    Handler handler = new Handler() {  
        public void handleMessage(Message msg) {  
        	if (msg.what == 1) {
        		try
				{
					if (mBluetoothLeService != null) {
						mBluetoothLeService.t_data_query_displacement();
					}
				}
				catch(Exception ex)
				{
					LogUtil.e(TAG,ex.toString());
				}
        	}
			super.handleMessage(msg);
        };  
    };  
    /**
     * 匿名类，重写了onServiceConnected和onServiceDisconnected方法
     */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

    	//在活动与服务成功绑定的时候调用
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (mBluetoothLeService==null) {
				LogUtil.i(TAG, "mBluetoothService 为空");
			}
            else
            {
            	LogUtil.i(TAG, "mBluetoothService 不为空");
            }
        }
        //在活动与服务连接断开的时候调用
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    
	protected void onRestart()
	{
		super.onRestart();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		if (paraUploadtimer==null) {
				paraUploadtimer=new Timer();
  		    paraUploadtimer.schedule(new paraUploadTimerTask(), 500, 1000); // 1s后执行task,经过1000ms再次执行  
		}
	
		//绑定蓝牙服务
	    Intent gattServiceIntent = new Intent(DisplacementMeasuredUnitQueryActivity.this, BluetoothLeService.class);
	    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	}
	
	protected void onDestroy()
	{
		super.onDestroy();
		  try{
		 		unbindService(mServiceConnection);
		 		unregisterReceiver(mGattUpdateReceiver);
		 		
				if (paraUploadtimer!=null ) {
					paraUploadtimer.cancel();
					paraUploadtimer=null;
				}
			
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
			  	
				LogUtil.e(TAG, e.toString());
				
			}
	}
	
	
	protected void onResume() {//打开APP时扫描设备
		super.onResume();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mGattUpdateReceiver);
		if (paraUploadtimer!=null ) {
			paraUploadtimer.cancel();
			paraUploadtimer=null;
		}
	
	    try{
	    	unbindService(mServiceConnection);
	    }catch (Exception e) {
			LogUtil.e(TAG, e.toString());
		}
	}
	
	
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
            	LogUtil.i(TAG, "broadcastreceiver connected");
//                mConnected=true;
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                LogUtil.i(TAG, "broadcastreceiver disconnected");
                
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
            	LogUtil.i(TAG, "services discovered");
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            	LogUtil.i(TAG, "display data");
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };
    
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    
    String data2="";
    @SuppressWarnings("unused")
	@SuppressLint("NewApi")
	private void displayData( String data1 ) {
    	
	  	if( data1!=null)
    	{
	  		try
	  		{
	  			data2=data2+data1;
	  			LogUtil.i(TAG, "data2:"+data2+",length:"+data2.length());
		  
		  		if (data2!=null) {
		  			int headPosition=publicMethod.getHeadPosition(data2);
			  		int tailPosition=publicMethod.getTailPosition(headPosition, data2);
			  		if (headPosition==-1) {
						data2="";
						return;
					}
			  		if (tailPosition==-1) {
						return;
					}
		  			//参数上传
					int diffPosition=tailPosition-headPosition;
					if (diffPosition==54||diffPosition==62) {
			  			LogUtil.i(TAG, "头尾正确");
			  			//序号
		  				String serialNumberStr=data2.substring(headPosition+6,headPosition+10);
		  				byte[] serialNumberByte=publicMethod.HexStringToByteArray(serialNumberStr);
		  				int serialNumber=serialNumberByte[0]*255+serialNumberByte[1];
			  			
			  			//测量单元ID
						String measuredUnitIdStr="";
						if (diffPosition==54 ) {
							measuredUnitIdStr=data2.substring(34+headPosition,54+headPosition);
						}
						else
							measuredUnitIdStr=data2.substring(42+headPosition,62+headPosition);
			  			byte[] measuredUnitIdByte=publicMethod.HexStringToByteArray(measuredUnitIdStr);
			  			String measuredUnitId=new String(measuredUnitIdByte);
						UserDB userDB=new UserDB(getApplicationContext(), GlobalVariable.getDataBaseName(), null, 7);
						List<String> nameAndToken=userDB.getNameAndToken();
						if (nameAndToken!=null && nameAndToken.size()==2) {
							Intent intent = new Intent(DisplacementMeasuredUnitQueryActivity.this, DisplacementMeasuredUnitQueryWaitActivity.class);
							intent.putExtra("id", measuredUnitId);
							startActivity(intent);
						}
						else {
							Toast.makeText(getApplicationContext(), getResources().getString(R.string.unlogin), Toast.LENGTH_SHORT).show();
						}
						data2="";
			  		
			  		}
			  		//错误代码
			  		else if (tailPosition-headPosition==18) {
						String stateStr=data2.substring(headPosition+16,headPosition+18);
						if (stateStr!=null && stateStr.equals("01")) {//设备类型错误
							
						}
						else if (stateStr!=null && stateStr.equals("02")) {//检测机构代码错误
							
						}
						else if (stateStr!=null && stateStr.equals("03")) {//锁定

						}
						else if (stateStr!=null && stateStr.equals("04")) {//故障

						}
						data2="";
					}
				}
		  	
		  	
	  		}
	  		catch(IndexOutOfBoundsException ex)
	  		{
	  			LogUtil.i(TAG,ex.toString());
	  			
	  		}
	  		catch(Exception ex)
	  		{
	  			LogUtil.i(TAG, ex.toString());
	  		}
    	}
    }
    
    
    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null){
        	LogUtil.i(TAG, "gattServices is null");
        	return;
        } 
        else 
        {
        	 LogUtil.i(TAG, "gattServices size:"+gattServices.size());
        }
     
        if( gattServices.size()>0&&mBluetoothLeService.get_connected_status( gattServices )>=4 )
        {
//	        if( true )
//			  {
				mBluetoothLeService.enable_JDY_ble(true);
				 try {  
			            Thread.currentThread();  
			            Thread.sleep(100);  
			        } catch (InterruptedException e) {  
			            e.printStackTrace();  
			        }  
				 mBluetoothLeService.enable_JDY_ble(true);
//			  }else{
//				  Toast toast = Toast.makeText(DisplacementMeasuredUnitSettingThreeActivity.this, R.string.deviceNotConnect, Toast.LENGTH_SHORT); 
//				  toast.show(); 
//			  }
        }
     
    }
    
}

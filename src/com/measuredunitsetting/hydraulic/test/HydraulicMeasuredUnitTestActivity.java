package com.measuredunitsetting.hydraulic.test;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.measuredunitsetting.BluetoothLeService;
import com.measuredunitsetting.R;
import com.measuredunitsetting.data.PublicMethod;

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
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.measuredunitsetting.global.LogUtil;

public class HydraulicMeasuredUnitTestActivity extends Activity {
	private static final String TAG=HydraulicMeasuredUnitTestActivity.class.getSimpleName();

	private BluetoothLeService mBluetoothLeService;//蓝牙连接服务
	PublicMethod publicMethod=new PublicMethod();

	//测量单元ID
	String measuredUnitId;
	float calibrationCoeff;
	//定时器
	Timer paraUploadtimer = new Timer(); //参数上传定时器
	Timer collecttimer;//采集定时器
	//连接测量单元OK
	TextView connectMeasureUnitOKTv;
	//正在采集数据
	TextView collectingTv;
	//等待返回数据
	TextView waitDataBackOKTv;
	
	//ID
	TextView IDTv;
	//序号
	TextView serialNumberTv;
	//测电编号
	TextView measurePointNumTv;
	//测点类型
	TextView measureTypeTv;
	//压力
	TextView pressureTv;
	//温度
	TextView temperatureTv;
	//标定系数 
	TextView calibrationCoeffTv;
	//测试结果
	TextView resultTv;
	TextView exceptionResultTv;
	String originalMeasureUnitId=null;
	boolean isOverTime=false;

	protected void onCreate(Bundle savedInstanceState) {
		TextView backTv;
		LinearLayout backLl;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hydraulicmeasuredunittest);
	
		//返回
		backTv=(TextView)findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
		//底部返回
		backLl=(LinearLayout)findViewById(R.id.backLl);
		backLl.setOnClickListener(bottomListener);
		//连接测量单元OK
		connectMeasureUnitOKTv=(TextView)findViewById(R.id.connectMeasureUnitOKTv);
		//正在采集数据
		collectingTv=(TextView)findViewById(R.id.collectingTv);
		//等待返回数据
		waitDataBackOKTv=(TextView)findViewById(R.id.waitDataBackOKTv);
		//ID
		IDTv=(TextView)findViewById(R.id.IDTv);
		//序号
		serialNumberTv=(TextView)findViewById(R.id.serialNumberTv);
		//测点编号
		measurePointNumTv=(TextView)findViewById(R.id.measurePointNumTv);
		//测点类型
		measureTypeTv=(TextView)findViewById(R.id.measureTypeTv);
		//压力
		pressureTv=(TextView)findViewById(R.id.pressureTv);
		//温度
		temperatureTv=(TextView)findViewById(R.id.temperatureTv);
		//标定系数
		calibrationCoeffTv=(TextView)findViewById(R.id.calibrationCoeffTv);
		//测试结果
		resultTv=(TextView)findViewById(R.id.resultTv);
		exceptionResultTv=(TextView)findViewById(R.id.exceptionResultTv);
		//绑定蓝牙服务
	    Intent gattServiceIntent = new Intent(HydraulicMeasuredUnitTestActivity.this, BluetoothLeService.class);
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
	   * 监视采集是否超时
	   * @author Administrator
	   *
	   */
	  class collectTimerTask extends TimerTask
	  {
		  @Override  
          public void run() {  
			if (!isOverTime) {//超时
				Message message=new Message();
				message.what=3;
				handler.sendMessage(message);
			}
          }  
	  };
	    
		//发送蓝牙指令
	    Handler handler = new Handler() {  
	        public void handleMessage(Message msg) {  
	        	if (msg.what == 1) {  
	            	if (mBluetoothLeService != null) {
        					mBluetoothLeService.t_data_query_hydraulic();
                    		LogUtil.i(TAG, "query");
	                	}
	        	}
	        	else if (msg.what==2) {
	        		if (mBluetoothLeService!=null) {
						mBluetoothLeService.t_data_collect_hydraulic();
						collectingTv.setText(R.string.collectingOK);
						LogUtil.i(TAG, "collect");
						}
				}
	        	else if (msg.what==3) {//超时
	        		waitDataBackOKTv.setText(R.string.waitDataBackOverTime);
	  				IDTv.setText("");
	  				serialNumberTv.setText("");
	  				temperatureTv.setText("");
	  				measureTypeTv.setText("");
	  				calibrationCoeffTv.setText("");
	  				measurePointNumTv.setText("");
	  				pressureTv.setText("");
	  				resultTv.setText(R.string.exception);
	  				exceptionResultTv.setText(R.string.retest);
	  				if (collecttimer!=null) {
	  					collecttimer.cancel();
	  					collecttimer=null;
					}
	  				if (paraUploadtimer==null) {
						paraUploadtimer=new Timer();
						paraUploadtimer.schedule(new paraUploadTimerTask(), 500,1000);
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
	    Intent gattServiceIntent = new Intent(HydraulicMeasuredUnitTestActivity.this, BluetoothLeService.class);
	    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	}
	
    /**
     * 顶部和底部返回键
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//创建监听对象    
    	public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv || id==R.id.backLl) {
        		((Activity)HydraulicMeasuredUnitTestActivity.this).finish();
			}
		}
    };  
    
	protected void onDestroy()
	{
		super.onDestroy();
		  try{
		 		unbindService(mServiceConnection);
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
//		if (collecttimer!=null) {
//			collecttimer.cancel();
//			collecttimer=null;
//		}
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
			  		if (tailPosition-headPosition==62) {
				  		if(headPosition!=-1 && tailPosition!=-1)
				  		{
				  			LogUtil.i(TAG, "head and tail correct");
							connectMeasureUnitOKTv.setText(R.string.connectMeasureUnitOK);

//							//序号
			  				String serialNumberStr=data2.substring(headPosition+6,headPosition+10);
			  				byte[] serialNumberByte=publicMethod.HexStringToByteArray(serialNumberStr);
			  				int serialNumber=serialNumberByte[0]*255+serialNumberByte[1];
				  			
				  			//测量单元ID
				  			String measuredUnitIdStr=data2.substring(30+headPosition,50+headPosition);	  	
				  			byte[] measuredUnitIdByte=publicMethod.HexStringToByteArray(measuredUnitIdStr);
				  			measuredUnitId=new String(measuredUnitIdByte);
				  		
				  			//标定系数
				  			String calibrationCoefStr=data2.substring(50+headPosition,66+headPosition);
				  			byte[] calibrationCoefByte=publicMethod.HexStringToByteArray(calibrationCoefStr);
				  			calibrationCoeff=publicMethod.byte2float(calibrationCoefByte, 0);
				  			data2="";
				  			if (originalMeasureUnitId!=null && measuredUnitId.equals(originalMeasureUnitId)) 
				  			{
								return;
							}
				  			if (paraUploadtimer!=null) {
								paraUploadtimer.cancel();
								paraUploadtimer=null;
							}

				  			LogUtil.i(TAG,"collect");

							new Handler().postDelayed(new Runnable(){
								public void run() {
									mBluetoothLeService.t_data_collect_hydraulic();
									collectingTv.setText(R.string.collectingOK);
									waitDataBackOKTv.setText(R.string.waitDataBack);
									IDTv.setText("");
									serialNumberTv.setText("");
									temperatureTv.setText("");
									measureTypeTv.setText("");
									calibrationCoeffTv.setText("");
									measurePointNumTv.setText("");
									pressureTv.setText("");
									resultTv.setText("");
									exceptionResultTv.setText("");

								}
							}, 500);

							LogUtil.i(TAG, "发送采集指令");
							isOverTime=false;
							//启动采集参数
							if (collecttimer==null) {
								collecttimer=new Timer();
								collecttimer.schedule(new collectTimerTask(),(long) (10000+serialNumber*100),(long) (10000+serialNumber*100));
							}
				  			
				  		}
					}
			  		//采集
			  		else if (tailPosition-headPosition==46) {
		  				LogUtil.i(TAG, "头尾正确");
		  				//id
		  				IDTv.setText(measuredUnitId);
		  				//序号
		  				String serialNumberStr=data2.substring(headPosition+6,headPosition+10);
		  				byte[] serialNumberByte=publicMethod.HexStringToByteArray(serialNumberStr);
		  				int serialNumber=serialNumberByte[0]*255+serialNumberByte[1];
		  				serialNumberTv.setText(String.valueOf(serialNumber));
		  				//测点编号
		  				String measurePointNumStr=data2.substring(headPosition+10,headPosition+26);
		  				byte[] measurePointNumByte=publicMethod.HexStringToByteArray(measurePointNumStr);
		  				String measurePointNum=new String(measurePointNumByte);
		  				measurePointNumTv.setText(measurePointNum);
		  				//测点类型
		  				String measureTypeStr=data2.substring(headPosition+26,headPosition+28);
		  				if (measureTypeStr!=null && measureTypeStr.equals("A0")) {
							measureTypeTv.setText(getResources().getString(R.string.benchmark));
						}
		  				else if (measureTypeStr!=null && measureTypeStr.equals("B0")) {
							measureTypeTv.setText(getResources().getString(R.string.commonMeasurePoint));
						}
		  				//压力
		  				String pressureStr=data2.substring(headPosition+30,headPosition+38);
		  				byte[] pressureByte=publicMethod.HexStringToByteArray(pressureStr);
		  				float pressureFloat=publicMethod.byte2float(pressureByte, 0);
						DecimalFormat decimalFormat=new DecimalFormat(".0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
						String pf=decimalFormat.format(pressureFloat);//format 返回的是字符串
		  				pressureTv.setText(pf+getResources().getString(R.string.mmwaterColumn));
		  				//温度
		  				String temperatureStr=data2.substring(headPosition+38,headPosition+46);
		  				byte[] temperatureByte=publicMethod.HexStringToByteArray(temperatureStr);
		  				float temperatureFloat=publicMethod.byte2float(temperatureByte, 0);
		  				String tf=decimalFormat.format(pressureFloat);
		  				temperatureTv.setText(tf+getResources().getString(R.string.degree));
		  				//标定系数
						String cc=decimalFormat.format(calibrationCoeff);
		  				calibrationCoeffTv.setText(cc);
		  				//测试结果
		  				resultTv.setText(getResources().getString(R.string.normal));
		  				exceptionResultTv.setText("");
		  				waitDataBackOKTv.setText(R.string.waitDataBackOK);
		  				data2="";
		  				
		  				if (collecttimer!=null) {
							collecttimer.cancel();
							collecttimer=null;
						}
		  				
		  				if (paraUploadtimer==null) {
							paraUploadtimer=new Timer();
							paraUploadtimer.schedule(new paraUploadTimerTask(), 500,1000);
						}
		  				isOverTime=true;
		  				
			  			originalMeasureUnitId=measuredUnitId;

					}
			  		//错误代码
			  		else if (tailPosition-headPosition==28) {
						String stateStr=data2.substring(headPosition+26,headPosition+28);
						if (stateStr!=null && stateStr.equals("01")) {//设备类型错误
							waitDataBackOKTv.setText(R.string.waitDataBackTypeError);
							IDTv.setText("");
			  				serialNumberTv.setText("");
			  				temperatureTv.setText("");
			  				measureTypeTv.setText("");
			  				calibrationCoeffTv.setText("");
			  				measurePointNumTv.setText("");
			  				pressureTv.setText("");
							resultTv.setText(R.string.noHydraulic);
							exceptionResultTv.setText(R.string.useCorrectProduct);
						}
						else if (stateStr!=null && stateStr.equals("02")) {//用户ID错误
							waitDataBackOKTv.setText(R.string.waitDataBackUnauthorizedProduct);
							IDTv.setText("");
			  				serialNumberTv.setText("");
			  				temperatureTv.setText("");
			  				measureTypeTv.setText("");
			  				calibrationCoeffTv.setText("");
			  				measurePointNumTv.setText("");
			  				pressureTv.setText("");
							resultTv.setText(R.string.noDeviceUsageRights);
							exceptionResultTv.setText(R.string.useLicensedProduct);
						}
						else if (stateStr!=null && stateStr.equals("03")) {//锁定
							waitDataBackOKTv.setText(R.string.waitDataBackLock);
							IDTv.setText("");
			  				serialNumberTv.setText("");
			  				temperatureTv.setText("");
			  				measureTypeTv.setText("");
			  				calibrationCoeffTv.setText("");
			  				measurePointNumTv.setText("");
			  				pressureTv.setText("");
							resultTv.setText(R.string.measurementUnitLocked);
							exceptionResultTv.setText(R.string.contactFactory);
						}
						data2="";
					}
			  		else {
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

package com.measuredunitsetting.displacement.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.measuredunitsetting.BluetoothLeService;
import com.measuredunitsetting.R;
import com.measuredunitsetting.data.PublicMethod;
import com.measuredunitsetting.db.DisplacementMeasuredUnitDB;
import com.measuredunitsetting.db.DisplacementMonitorPointDB;
import com.measuredunitsetting.entity.DisplacementMeasuredResult;
import com.measuredunitsetting.entity.DisplacementMeasuredUnit;
import com.measuredunitsetting.entity.DisplacementMonitorPoint;
import com.measuredunitsetting.global.GlobalVariable;
import com.measuredunitsetting.list.DisplacementMeasuredResultAdapter;
import com.measuredunitsetting.list.DisplacementMeasuredUnitAdapter;

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
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.measuredunitsetting.global.LogUtil;

public class DisplacementMonitorNetworkTestTwoActivity extends Activity{
	private static final String TAG=DisplacementMonitorNetworkTestTwoActivity.class.getSimpleName();
	PublicMethod publicMethod=new PublicMethod();
	private BluetoothLeService mBluetoothLeService;//蓝牙连接服务
	TextView backTv;
	LinearLayout backLl;
	//监测网ID
	int monitorNetworkId;
	//测量单元总个数
	int totalUnitNumber;
	DisplacementMeasuredUnitDB displacementMeasuredUnitDB;
	DisplacementMonitorPointDB displacementMonitorPointDB;
	//工程编号
	TextView projectNameTv;
	//监测点编号
	TextView monitorPointNumberTv;
	//监测深度
	TextView monitorDepthTv;
	//测量单元间距
	TextView unitSpacingTv;
	//测量单元总数
	TextView measureTotalNumberTv;
	//已设置测量单元个数
	TextView measurementUnitHasBeenSettedTv;
	//尝试连接测量单元
	TextView tryConnectMeasurementUnitTV;
	//尝试采集数据
	TextView tryCollectDataTV;
	//测试结果显示
	TextView testResultTv;
	//定时器
	Timer paraUploadtimer = new Timer(); //参数上传定时器
	Timer monitorParaUploadTimer=new Timer();//监视参数上传定时器
	DisplacementMonitorPoint monitorPoint;
	//测量单元信息
	DisplacementMeasuredUnitAdapter measuredUnitAdapter;
    private List<DisplacementMeasuredUnit> measureUnitList=new ArrayList<DisplacementMeasuredUnit>(); 
    ListView measureUnitLv;
  
    
    //测量单元结果
    DisplacementMeasuredResultAdapter measureUnitResultAdapter;
    private List<DisplacementMeasuredResult> displacementMeasuredUnitResultList=new ArrayList<DisplacementMeasuredResult>();
    ListView measureUnitResultLv;
    //测量单元失败的单元
    ListView measureUnitFailLv;
    DisplacementMeasuredUnitAdapter measuredUnitAdapters;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_displacementmonitorpointtesttwo);
		//返回
		backTv=(TextView)findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
		//返回
		backLl=(LinearLayout)findViewById(R.id.backLl);
		backLl.setOnClickListener(bottomListener);
		
		//初始化
		monitorNetworkId=getIntent().getIntExtra("monitorNetworkId", 0);
		//初始化数据库
		displacementMeasuredUnitDB=new DisplacementMeasuredUnitDB(getApplicationContext(),GlobalVariable.getDataBaseName(), null, 7);
		displacementMonitorPointDB=new DisplacementMonitorPointDB(getApplicationContext(),GlobalVariable.getDataBaseName() , null, 7);
		//工程编号
		projectNameTv=(TextView)findViewById(R.id.projectNameTv);
		//监测点编号
		monitorPointNumberTv=(TextView)findViewById(R.id.monitorPointNumberTv);
		//监测深度
		monitorDepthTv=(TextView)findViewById(R.id.monitorDepthTv);
		//测量单元间距
		unitSpacingTv=(TextView)findViewById(R.id.unitSpacingTv);
		//测量单元总数
		measureTotalNumberTv=(TextView)findViewById(R.id.measureTotalNumberTv);
		//已设置测量单元个数
		measurementUnitHasBeenSettedTv=(TextView)findViewById(R.id.measurementUnitHasBeenSettedTv);
		//连接测量单元
		tryConnectMeasurementUnitTV=(TextView)findViewById(R.id.tryConnectMeasurementUnitTV);
		//尝试采集数据
		tryCollectDataTV=(TextView)findViewById(R.id.tryCollectDataTV);
		//测试结果显示
		testResultTv=(TextView)findViewById(R.id.testResultTv);
		testResultTv.setMovementMethod(ScrollingMovementMethod.getInstance()); 
		//查询监测网信息
		monitorPoint=displacementMonitorPointDB.selectAccordingToMonitorPointId(monitorNetworkId,GlobalVariable.getUserId());
		//测量单元测试失败
		measureUnitFailLv=(ListView)findViewById(R.id.measureUnitFailLv);
		
		//查询测量单元个数
		long measuredUnitCount=displacementMeasuredUnitDB.selectCountAccordingToMonitorNetworkNumberId(monitorPoint.getId());
		if (monitorPoint!=null) {
			projectNameTv.setText(monitorPoint.getProjectName());
			monitorPointNumberTv.setText(monitorPoint.getMonitorPointNumber());
			monitorDepthTv.setText(String.valueOf(monitorPoint.getMonitorDepth()));
			unitSpacingTv.setText(String.valueOf(monitorPoint.getUnitSpacing()));
			measureTotalNumberTv.setText(String.valueOf((int)(monitorPoint.getMonitorDepth()/monitorPoint.getUnitSpacing())));
			measurementUnitHasBeenSettedTv.setText(String.valueOf(measuredUnitCount));
		}
		
		
		//绑定蓝牙服务
	    Intent gattServiceIntent = new Intent(DisplacementMonitorNetworkTestTwoActivity.this, BluetoothLeService.class);
	    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	    //启动定时器
	    paraUploadtimer.schedule(new paraUploadTimerTask(), 200, 10000); // 1s后执行task,经过500ms再次执行  
	    monitorParaUploadTimer.schedule(new monitorParaUploadTimerTask(),(long) (monitorPoint.getMonitorDepth()/monitorPoint.getUnitSpacing())*100+500,13000+(long)(monitorPoint.getMonitorDepth()/monitorPoint.getUnitSpacing())*100);
	    
		measuredUnitAdapter=new DisplacementMeasuredUnitAdapter(DisplacementMonitorNetworkTestTwoActivity.this, R.layout.serialnumberdepthid_item, measureUnitList);
	    View vhead=View.inflate(this, R.layout.serialnumberdepthid_item_head, null);
		measureUnitLv=(ListView) findViewById(R.id.measureUnitLv);
		measureUnitLv.addHeaderView(vhead);
	    measureUnitLv.setAdapter(measuredUnitAdapter);

	    
	    //测量单元结果
	    measureUnitResultAdapter=new DisplacementMeasuredResultAdapter(DisplacementMonitorNetworkTestTwoActivity.this, R.layout.displacementmeasuredresult_item, displacementMeasuredUnitResultList);
	    View vh=View.inflate(this, R.layout.displacementmeasuredresult_item_head, null);
	    measureUnitResultLv=(ListView) findViewById(R.id.measureUnitResultLv);
	    measureUnitResultLv.addHeaderView(vh);
	    measureUnitResultLv.setAdapter(measureUnitResultAdapter);

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
	    int count=0;
	    class monitorParaUploadTimerTask extends TimerTask
	    {
	    	  @Override  
	          public void run() {  
	              // 需要做的事:发送消息  
	    		  count++;
	    		  if (count==1) {
	    			   Message message = new Message();  
	 	               message.what =2;  
	 	               handler.sendMessage(message); 
	    		  }	
	    		  else
	    		  {
	    			  count=0;
	    			  Message message = new Message();  
	 	              message.what =3;  
	 	              handler.sendMessage(message); 	    			  
	    		  }
	            
	          }  
	    	
	    };
	    

		//发送蓝牙指令
	    Handler handler = new Handler()
		{
	        public void handleMessage(Message msg) {  
	        	if (msg.what == 1) {  
	            	if (mBluetoothLeService != null) {
	            			try {
								mBluetoothLeService.t_data_query_queue_displacement();
							}
							catch(Exception ex)
							{
								LogUtil.e(TAG,ex.toString());
							}
	                	}
	        	}
	        	else if (msg.what==2) {
					if (mBluetoothLeService!=null) {
						try {
							mBluetoothLeService.t_data_collect_displacement();
						}
						catch(Exception ex)
						{
							LogUtil.e(TAG,ex.toString());
						}

					}
				}
	        	else if (msg.what==3) {//采集时间到，判断采集结
	        		if (monitorParaUploadTimer!=null) {
						monitorParaUploadTimer.cancel();
						monitorParaUploadTimer=null;
					}
	        		//正常
	        		if (displacementMeasuredUnitResultList.size()==monitorPoint.getMonitorDepth()/monitorPoint.getUnitSpacing() && measureUnitList.size()==monitorPoint.getMonitorDepth()/monitorPoint.getUnitSpacing()) {
						testResultTv.setText(R.string.allMeasurementUnitsNormal);
					}
	        		//缺少数据
	        		else if (displacementMeasuredUnitResultList.size()<(int)(monitorPoint.getMonitorDepth()/monitorPoint.getUnitSpacing()) ) {
						List<Integer> serialNumberLackList=new ArrayList<Integer>();

						//测量单元采样结果中缺少数据
	        			if (displacementMeasuredUnitResultList.size()<(int)(monitorPoint.getMonitorDepth()/monitorPoint.getUnitSpacing())) {
							List<Integer> serialNumberList=new ArrayList<Integer>();
							for(int i=0;i<displacementMeasuredUnitResultList.size();i++)
							{
								DisplacementMeasuredResult hmu=displacementMeasuredUnitResultList.get(i);
								serialNumberList.add(hmu.getSerialNumber());								

							}
							for(int i=1;i<=(int)(monitorPoint.getMonitorDepth()/monitorPoint.getUnitSpacing());i++)
							{
								if (!serialNumberList.contains(i)) {
									serialNumberLackList.add(i);
								}
							}
						}
	        			//测量单元查询时缺少数据
						else if (measureUnitList.size()<monitorPoint.getMonitorDepth()/monitorPoint.getUnitSpacing()) {
							List<Integer> serialNumberList=new ArrayList<Integer>();
							for(int i=0;i<measureUnitList.size();i++)
							{
								DisplacementMeasuredUnit hmu=measureUnitList.get(i);
								serialNumberList.add(hmu.getSerialNumber());
							}
							
							if (serialNumberList!=null && serialNumberList.size()!=0) 
							{
								for(int i=1;i<=(int)(monitorPoint.getMonitorDepth()/monitorPoint.getUnitSpacing());i++)
								{
									if (!serialNumberList.contains(i) && !serialNumberLackList.contains(i)) 
									{
										serialNumberLackList.add(i);
									}
								}
							}
						}
	        			//
	        			if (serialNumberLackList!=null && serialNumberLackList.size()!=0) {
	        				List<DisplacementMeasuredUnit> serialNumberLackSettedList=new ArrayList<DisplacementMeasuredUnit>();
	        				List<Integer> serialNumberLackUnsettedList=new ArrayList<Integer>();
	        				
							//设置好的测量单元没有接收到数据
	        				for(int i=0;i<serialNumberLackList.size();i++)
							{
								DisplacementMeasuredUnit displacementMeasuredUnit=displacementMeasuredUnitDB.selectMeasureUnitsAccordingToSerialNumber(monitorPoint.getId(), serialNumberLackList.get(i));
								if (displacementMeasuredUnit!=null) {
									serialNumberLackSettedList.add(displacementMeasuredUnit);
								}
								else {
									serialNumberLackUnsettedList.add(serialNumberLackList.get(i));
								}
							}

							String lackInformation="";

							if (serialNumberLackSettedList!=null && serialNumberLackSettedList.size()!=0) {
	        					measuredUnitAdapters=new DisplacementMeasuredUnitAdapter(DisplacementMonitorNetworkTestTwoActivity.this, R.layout.serialnumberdepthid_item, serialNumberLackSettedList);
	        				    View vHead=View.inflate(DisplacementMonitorNetworkTestTwoActivity.this, R.layout.serialnumberdepthid_item_head, null);
	        				    measureUnitFailLv=(ListView) findViewById(R.id.measureUnitFailLv);
	        				    measureUnitFailLv.addHeaderView(vHead);
	        				    measureUnitFailLv.setAdapter(measuredUnitAdapters);
	        				    lackInformation=getResources().getString(R.string.aboveMeasurementUnitAbnormal)+"\r\n";
							}
							//有没有设置的测量单元
							if (serialNumberLackUnsettedList!=null && serialNumberLackUnsettedList.size()!=0) {
								String serialNumberStr="";
								for(int i=0;i<serialNumberLackUnsettedList.size();i++)
								{
									serialNumberStr=serialNumberStr+" "+serialNumberLackUnsettedList.get(i);
								}
								lackInformation=lackInformation+getResources().getString(R.string.serialNumberStr)+serialNumberStr+getResources().getString(R.string.measurementUnitNotSet)+"\r\n"+getResources().getString(R.string.remainingMeasurementUnitsAreNormal);
							}
							//没有没设置的测量单元
							else
							{
								lackInformation=lackInformation+getResources().getString(R.string.remainingMeasurementUnitsAreNormal);
							}
							
		        			if (monitorPoint.getMonitorDepth()/monitorPoint.getUnitSpacing()>34) {
								lackInformation=lackInformation+"\r\n"+getResources().getString(R.string.measurementUnitsTooMany);
							}
							testResultTv.setText(lackInformation);

						}
	        			
					}
				}
	            super.handleMessage(msg);  
	        };  
	    };  
	    
	    
    /**
     * 顶部和底部返回键
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//创建监听对象    
    	public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv || id==R.id.backLl) {
        	
            ((Activity)DisplacementMonitorNetworkTestTwoActivity.this).finish();
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
			  	
				LogUtil.e(TAG, e.toString());
				
			}
	}
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

	
	protected void onResume() {//打开APP时扫描设备
		super.onResume();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}
	
	protected void onRestart()
	{
		super.onRestart();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());	
		//绑定蓝牙服务
	    Intent gattServiceIntent = new Intent(DisplacementMonitorNetworkTestTwoActivity.this, BluetoothLeService.class);
	    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
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
    
    String data2="";
    @SuppressWarnings("unused")
	@SuppressLint("NewApi")
	private void displayData( String data1 ) {
    	
	  	if( data1!=null)
    	{
	  		try
	  		{
	  			data2=data2+data1;
	  			LogUtil.d(TAG, "data2:"+data2+",length:"+data2.length());
		  
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
			  			LogUtil.i(TAG, "canshushangchuan");
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
			  			//深度
		  				String depthStr=data2.substring(10+headPosition,16+headPosition);
						byte[] depthByte=publicMethod.HexStringToByteArray(depthStr);
						float depth=Float.parseFloat(String.valueOf(depthByte[0])+String.valueOf(depthByte[1])+"."+String.valueOf(depthByte[2]));

			  			DisplacementMeasuredUnit displacementMeasuredUnit=new DisplacementMeasuredUnit(serialNumber, depth, measuredUnitId);
			  			measureUnitList.add(displacementMeasuredUnit);
			  			
			  		    //测量单元数据
			  			measuredUnitAdapter=new DisplacementMeasuredUnitAdapter(DisplacementMonitorNetworkTestTwoActivity.this, R.layout.serialnumberdepthid_item, measureUnitList);
			  		
			  		    measureUnitLv.setAdapter(measuredUnitAdapter);
			  		    //停止发送参数上传指令
			  		    if (paraUploadtimer!=null) {
							paraUploadtimer.cancel();
							paraUploadtimer=null;
						}
			  			data2="";
			  			tryConnectMeasurementUnitTV.setText(R.string.tryConnectMeasurementUnitOK);
				  		
					}
			  		//采集
			  		else if (tailPosition-headPosition==50) {
		  				LogUtil.i(TAG, "caiji");

		  				//id
		  				//序号
		  				String serialNumberStr=data2.substring(headPosition+6,headPosition+10);
		  				byte[] serialNumberByte=publicMethod.HexStringToByteArray(serialNumberStr);
		  				int serialNumber=serialNumberByte[0]*255+serialNumberByte[1];
		  				//深度
		  				String depthStr=data2.substring(10+headPosition,16+headPosition);
						byte[] depthByte=publicMethod.HexStringToByteArray(depthStr);

						float depth=Float.parseFloat(String.valueOf(depthByte[0])+String.valueOf(depthByte[1])+"."+String.valueOf(depthByte[2]));
						LogUtil.i(TAG, "shendu:"+depth);
						//X
		  				String xStr=data2.substring(headPosition+18,headPosition+26);
		  				byte[] xByte=publicMethod.HexStringToByteArray(xStr);
		  				byte[] xByteTemp=new byte[4];
		  				xByteTemp[0]=xByte[3];
		  				xByteTemp[1]=xByte[2];
		  				xByteTemp[2]=xByte[1];
		  				xByteTemp[3]=xByte[0];
		  				float xFloat=publicMethod.byte2float(xByteTemp, 0);
		  				//Y
		  				String yStr=data2.substring(headPosition+26,headPosition+34);
		  				byte[] yByte=publicMethod.HexStringToByteArray(yStr);
		  				byte[] yByteTemp=new byte[4];
		  				yByteTemp[0]=yByte[3];
		  				yByteTemp[1]=yByte[2];
		  				yByteTemp[2]=yByte[1];
		  				yByteTemp[3]=yByte[0];
		  				float yFloat=publicMethod.byte2float(yByteTemp,0);
		  				//Z
		  				String zStr=data2.substring(headPosition+34,headPosition+42);
		  				byte[] zByte=publicMethod.HexStringToByteArray(zStr);
		  				byte[] zByteTemp=new byte[4];
		  				zByteTemp[0]=zByte[3];
		  				zByteTemp[1]=zByte[2];
		  				zByteTemp[2]=zByte[1];
		  				zByteTemp[3]=zByte[0];
		  				float zFloat=publicMethod.byte2float(zByteTemp,0);
		  				//温度
		  				String temperatureStr=data2.substring(headPosition+42,headPosition+50);
		  				byte[] temperatureByte=publicMethod.HexStringToByteArray(temperatureStr);
		  				byte[] temperatureByteTemp=new byte[4];
		  				temperatureByteTemp[0]=temperatureByte[3];
		  				temperatureByteTemp[1]=temperatureByte[2];
		  				temperatureByteTemp[2]=temperatureByte[1];
		  				temperatureByteTemp[3]=temperatureByte[0];
		  				float temperatureFloat=publicMethod.byte2float(temperatureByteTemp, 0);
		  				
		  				DisplacementMeasuredResult displacementMeasuredUnitResult=new DisplacementMeasuredResult(serialNumber,depth,xFloat,yFloat,zFloat,temperatureFloat);
		  				displacementMeasuredUnitResultList.add(displacementMeasuredUnitResult);
		  				
		  				data2="";
		  				
		  			    measureUnitResultAdapter=new DisplacementMeasuredResultAdapter(DisplacementMonitorNetworkTestTwoActivity.this, R.layout.displacementmeasuredresult_item, displacementMeasuredUnitResultList);
		  			    measureUnitResultLv.setAdapter(measureUnitResultAdapter);
		  		
		  			   tryCollectDataTV.setText(R.string.tryCollectDataOK);
					}
			  		//错误代码
			  		else if (tailPosition-headPosition==18) {
						String stateStr=data2.substring(headPosition+16,headPosition+18);
						if (stateStr!=null && stateStr.equals("01")) {//设备类型错误
							LogUtil.i(TAG, "设备类型代码错误");
						}
						else if (stateStr!=null && stateStr.equals("02")) {//检测机构代码错误
							LogUtil.i(TAG, "检测机构代码错误");
						}
						else if (stateStr!=null && stateStr.equals("03")) {//锁定
							LogUtil.i(TAG, "锁定");
						}
						else if (stateStr!=null && stateStr.equals("04")) {
							LogUtil.i(TAG, "传感器故障");
						}
						else if (stateStr!=null && stateStr.equals("05")) {
							LogUtil.i(TAG, "传感器本来就是解锁状态");
						}
						data2="";
					}
				}
		  	
		  	
	  		}
	  		catch(IndexOutOfBoundsException ex)
	  		{
	  			LogUtil.e(TAG,ex.toString());
	  			
	  		}
	  		catch(Exception ex)
	  		{
	  			LogUtil.e(TAG, ex.toString());
	  		}
    	}
    }
    
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
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

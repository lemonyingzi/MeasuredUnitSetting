package com.measuredunitsetting.hydraulic.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.measuredunitsetting.BluetoothLeService;
import com.measuredunitsetting.R;
import com.measuredunitsetting.R.layout;
import com.measuredunitsetting.data.PublicMethod;
import com.measuredunitsetting.db.HydraulicMeasuredUnitDB;
import com.measuredunitsetting.db.HydraulicMonitorNetworkDB;
import com.measuredunitsetting.entity.HydraulicMeasuredUnit;
import com.measuredunitsetting.entity.HydraulicMeasuredUnitResult;
import com.measuredunitsetting.entity.HydraulicMonitorNetwork;
import com.measuredunitsetting.global.GlobalVariable;
import com.measuredunitsetting.hydraulic.monitornetwork.HydraulicMonitorNetworkDetailActivity;
import com.measuredunitsetting.hydraulic.test.HydraulicMeasuredUnitTestActivity.collectTimerTask;
import com.measuredunitsetting.hydraulic.test.HydraulicMeasuredUnitTestActivity.paraUploadTimerTask;
import com.measuredunitsetting.list.MeasuredUnitAdapter;
import com.measuredunitsetting.list.MeasuredUnitResultAdapter;

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

public class HydraulicMonitorNetworkTestTwoActivity extends Activity{
	private static final String TAG=HydraulicMonitorNetworkTestTwoActivity.class.getSimpleName();
	PublicMethod publicMethod=new PublicMethod();
	private BluetoothLeService mBluetoothLeService;//蓝牙连接服务
	TextView backTv;
	LinearLayout backLl;
	//监测网ID
	int monitorNetworkId;
	//测量单元总个数
	int totalUnitNumber;
	HydraulicMeasuredUnitDB hydraulicMeasuredUnitDB;
	HydraulicMonitorNetworkDB hydraulicMonitorNetworkDB;
	//工程编号
	TextView projectNameTv;
	//监测网编号
	TextView monitorNetworkNumberTv;
	//测点总数
	TextView measurePointTotalNumberTv;
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
	HydraulicMonitorNetwork monitorNetwork;
	//测量单元信息
	MeasuredUnitAdapter measuredUnitAdapter;
    private List<HydraulicMeasuredUnit> measureUnitList=new ArrayList<HydraulicMeasuredUnit>(); 
    ListView measureUnitLv;
  
    
    //测量单元结果
    MeasuredUnitResultAdapter measureUnitResultAdapter;
    private List<HydraulicMeasuredUnitResult> hydraulicMeasuredUnitResultList=new ArrayList<HydraulicMeasuredUnitResult>();
    ListView measureUnitResultLv;
    //测量单元失败的单元
    ListView measureUnitFailLv;
    MeasuredUnitAdapter measuredUnitAdapters;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hydraulicmonitornetwroktesttwo);
		//返回
		backTv=(TextView)findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
		//返回
		backLl=(LinearLayout)findViewById(R.id.backLl);
		backLl.setOnClickListener(bottomListener);
		
		//初始化
		monitorNetworkId=getIntent().getIntExtra("monitorNetworkId", 0);
		totalUnitNumber=getIntent().getIntExtra("totalUnitNumber", 0);
		//初始化数据库
		hydraulicMeasuredUnitDB=new HydraulicMeasuredUnitDB(getApplicationContext(),GlobalVariable.getDataBaseName(), null, 7);
		hydraulicMonitorNetworkDB=new HydraulicMonitorNetworkDB(getApplicationContext(),GlobalVariable.getDataBaseName() , null, 7);
		//工程编号
		projectNameTv=(TextView)findViewById(R.id.projectNameTv);
		//监测网编号
		monitorNetworkNumberTv=(TextView)findViewById(R.id.monitorNetworkNumberTv);
		//测点总数
		measurePointTotalNumberTv=(TextView)findViewById(R.id.measurePointTotalNumberTv);
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
		monitorNetwork=hydraulicMonitorNetworkDB.selectAccordingToId(monitorNetworkId,GlobalVariable.getUserId());
		//测量单元测试失败
		measureUnitFailLv=(ListView)findViewById(R.id.measureUnitFailLv);
		
		//查询测量单元个数
		long measuredUnitCount=0;
		measuredUnitCount=hydraulicMeasuredUnitDB.selectCountAccordingToMonitorNetworkNumberId(monitorNetwork.getId());
		
		if (monitorNetwork!=null) {
			projectNameTv.setText(monitorNetwork.getProjectName());
			monitorNetworkNumberTv.setText(monitorNetwork.getMonitorNetworkNumber());
			measurePointTotalNumberTv.setText(String.valueOf(monitorNetwork.getTotalUnitNumber()));
			measurementUnitHasBeenSettedTv.setText(String.valueOf(measuredUnitCount));
		}
		
		
		//绑定蓝牙服务
	    Intent gattServiceIntent = new Intent(HydraulicMonitorNetworkTestTwoActivity.this, BluetoothLeService.class);
	    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	    //启动定时器
	    paraUploadtimer.schedule(new paraUploadTimerTask(), 200, 10000); // 1s后执行task,经过500ms再次执行  
	    monitorParaUploadTimer.schedule(new monitorParaUploadTimerTask(), monitorNetwork.getTotalUnitNumber()*100+500,10000+monitorNetwork.getTotalUnitNumber()*100);
	    
		measuredUnitAdapter=new MeasuredUnitAdapter(HydraulicMonitorNetworkTestTwoActivity.this, R.layout.measureunit_item, measureUnitList);
	    View vhead=View.inflate(this, R.layout.measureunit_item_head, null);
		measureUnitLv=(ListView) findViewById(R.id.measureUnitLv);
		measureUnitLv.addHeaderView(vhead);
	    measureUnitLv.setAdapter(measuredUnitAdapter);

	    
	    //测量单元
	    measureUnitResultAdapter=new MeasuredUnitResultAdapter(HydraulicMonitorNetworkTestTwoActivity.this, R.layout.measureunitresult_item, hydraulicMeasuredUnitResultList);
	    View vh=View.inflate(this, R.layout.measureunitresult_item_head, null);
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
	    }
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
	    	
	    }

		//发送蓝牙指令
	    Handler handler = new Handler() {  
	        public void handleMessage(Message msg) {  
	        	if (msg.what == 1) {  
	            	if (mBluetoothLeService != null) {
        					mBluetoothLeService.t_data_query_queue_hydraulic();
                    		LogUtil.i(TAG, "发送查询");
	                	}
	        	}
	        	else if (msg.what==2) {
					if (mBluetoothLeService!=null) {
						mBluetoothLeService.t_data_collect_hydraulic();
						LogUtil.i(TAG, "发送采集");
					}
				}
	        	else if (msg.what==3) {//采集时间到，判断采集结

	        		if (monitorParaUploadTimer!=null) {
						monitorParaUploadTimer.cancel();
						monitorParaUploadTimer=null;
					}
	        		//正常
	        		if (hydraulicMeasuredUnitResultList.size()==monitorNetwork.getTotalUnitNumber() && measureUnitList.size()==monitorNetwork.getTotalUnitNumber()) {
						testResultTv.setText(R.string.allMeasurementUnitsNormal);
					}
	        		//缺少数据
	        		else if (hydraulicMeasuredUnitResultList.size()<monitorNetwork.getTotalUnitNumber() ) {
						List<Integer> serialNumberLackList=new ArrayList<Integer>();
		        		//测量单元采样结果中缺少数据
	        			if (hydraulicMeasuredUnitResultList.size()<monitorNetwork.getTotalUnitNumber()) {
							List<Integer> serialNumberList=new ArrayList<Integer>();
							for(int i=0;i<hydraulicMeasuredUnitResultList.size();i++)
							{
								HydraulicMeasuredUnitResult hmu=hydraulicMeasuredUnitResultList.get(i);
								serialNumberList.add(hmu.getSerialNumber());
							}
							for(int i=1;i<=monitorNetwork.getTotalUnitNumber();i++)
							{
								if (!serialNumberList.contains(i)) {
									serialNumberLackList.add(i);
								}
							}
						}
	        			//测量单元查询时缺少数据
						else if (measureUnitList.size()<monitorNetwork.getTotalUnitNumber()) {
							List<Integer> serialNumberList=new ArrayList<Integer>();
							for(int i=0;i<measureUnitList.size();i++)
							{
								HydraulicMeasuredUnit hmu=measureUnitList.get(i);
								serialNumberList.add(hmu.getSerialNumber());
							}
							if (serialNumberList!=null && serialNumberList.size()!=0) 
							{
								for(int i=1;i<=monitorNetwork.getTotalUnitNumber();i++)
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
							List<HydraulicMeasuredUnit> serialNumberLackSettedList = new ArrayList<HydraulicMeasuredUnit>();//设置好的测量单元没有接收到数据
							List<Integer> serialNumberLackUnsettedList = new ArrayList<Integer>();

							//设置好的测量单元没有接收到数据
							for (int i = 0; i < serialNumberLackList.size(); i++) {
								HydraulicMeasuredUnit hydraulicMeasuredUnit = hydraulicMeasuredUnitDB.selectMeasureUnitsAccordingToSerialNumber(monitorNetwork.getId(), serialNumberLackList.get(i));
								if (hydraulicMeasuredUnit != null) {
									serialNumberLackSettedList.add(hydraulicMeasuredUnit);//设置好的测量单元没有接受到数据
								} else {
									serialNumberLackUnsettedList.add(serialNumberLackList.get(i));//没有设置该测量单元
								}
							}
							String lackInformation = "";

							if (serialNumberLackSettedList != null && serialNumberLackSettedList.size() != 0) {
								measuredUnitAdapters = new MeasuredUnitAdapter(HydraulicMonitorNetworkTestTwoActivity.this, R.layout.measureunit_item, serialNumberLackSettedList);
								View vHead = View.inflate(HydraulicMonitorNetworkTestTwoActivity.this, R.layout.measureunit_item_head, null);
								measureUnitFailLv = (ListView) findViewById(R.id.measureUnitFailLv);
								measureUnitFailLv.addHeaderView(vHead);
								measureUnitFailLv.setAdapter(measuredUnitAdapters);
								lackInformation = getResources().getString(R.string.aboveMeasurementUnitAbnormal) + "\r\n";
							}
							//有没有设置的测量单元
							if (serialNumberLackUnsettedList != null && serialNumberLackUnsettedList.size() != 0) {
								String serialNumberStr = "";
								for (int i = 0; i < serialNumberLackUnsettedList.size(); i++) {
									serialNumberStr = serialNumberStr + " " + serialNumberLackUnsettedList.get(i);
								}
								lackInformation = lackInformation + getResources().getString(R.string.serialNumberStr) + serialNumberStr + getResources().getString(R.string.measurementUnitNotSet) + "\r\n" + getResources().getString(R.string.remainingMeasurementUnitsAreNormal);
							}
							//没有没设置的测量单元
							else {
								lackInformation = lackInformation + getResources().getString(R.string.remainingMeasurementUnitsAreNormal);
							}
							if (monitorNetwork.getTotalUnitNumber() > 37) {
								lackInformation = lackInformation + "\r\n" + getResources().getString(R.string.measurementUnitsTooMany);
							}
							testResultTv.setText(lackInformation);
						}
					}
				}
	            super.handleMessage(msg);  
	        }
	    };  
	    
	    
    /**
     * 顶部和底部返回键
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//创建监听对象    
    	public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv || id==R.id.backLl) {
        	
            ((Activity)HydraulicMonitorNetworkTestTwoActivity.this).finish();
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
	    Intent gattServiceIntent = new Intent(HydraulicMonitorNetworkTestTwoActivity.this, BluetoothLeService.class);
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
			  		if (tailPosition-headPosition==74) {
				  		if(headPosition!=-1 && tailPosition!=-1)
				  		{
				  			LogUtil.i(TAG, "头尾正确");
				  			//序号
			  				String serialNumberStr=data2.substring(headPosition+6,headPosition+10);
			  				byte[] serialNumberByte=publicMethod.HexStringToByteArray(serialNumberStr);
			  				int serialNumber=serialNumberByte[0]*255+serialNumberByte[1];
			  				//测点编号
			  				String measurePointNumStr=data2.substring(headPosition+10,headPosition+26);
			  				byte[] measurePointNumByte=publicMethod.HexStringToByteArray(measurePointNumStr);
			  				String measurePointNum=new String(measurePointNumByte);
			  				//测点类型
			  				String measureTypeStr=data2.substring(headPosition+26,headPosition+28);
			  				String measureType;
			  				if (measureTypeStr!=null && measureTypeStr.equals("A0")) {
				  				measureType=getResources().getString(R.string.benchmark);
							}
			  				else if (measureTypeStr!=null && measureTypeStr.equals("B0")) {
								measureType=getResources().getString(R.string.commonMeasurePoint);
							}
				  			//测量单元ID
				  			String measuredUnitIdStr=data2.substring(30+headPosition,50+headPosition);	  	
				  			byte[] measuredUnitIdByte=publicMethod.HexStringToByteArray(measuredUnitIdStr);
				  			String measuredUnitId=new String(measuredUnitIdByte);
				  		
				  			HydraulicMeasuredUnit hydraulicMeasuredUnit=new HydraulicMeasuredUnit(serialNumber, measurePointNum, measureTypeStr, measuredUnitId);
				  			measureUnitList.add(hydraulicMeasuredUnit);
				  			
				  		    //测量单元数据
				  			measuredUnitAdapter=new MeasuredUnitAdapter(HydraulicMonitorNetworkTestTwoActivity.this, R.layout.measureunit_item, measureUnitList);
				  		
				  		    measureUnitLv.setAdapter(measuredUnitAdapter);
				  		    //停止发送参数上传指令
				  		    if (paraUploadtimer!=null) {
								paraUploadtimer.cancel();
								paraUploadtimer=null;
							}
				  			data2="";
				  			tryConnectMeasurementUnitTV.setText(R.string.tryConnectMeasurementUnitOK);
				  		}
					}
			  		//采集
			  		else if (tailPosition-headPosition==46) {
		  				LogUtil.i(TAG, "头尾正确");
		  				//id
		  				//序号
		  				String serialNumberStr=data2.substring(headPosition+6,headPosition+10);
		  				byte[] serialNumberByte=publicMethod.HexStringToByteArray(serialNumberStr);
		  				int serialNumber=serialNumberByte[0]*255+serialNumberByte[1];
		  				//测点编号
		  				String measurePointNumStr=data2.substring(headPosition+10,headPosition+26);
		  				byte[] measurePointNumByte=publicMethod.HexStringToByteArray(measurePointNumStr);
		  				String measurePointNum=new String(measurePointNumByte);
		  				//测点类型
		  				String measureTypeStr=data2.substring(headPosition+26,headPosition+28);
		  				String measureType;
		  				if (measureTypeStr!=null && measureTypeStr.equals("A0")) {
			  				measureType=getResources().getString(R.string.benchmark);

						}
		  				else if (measureTypeStr!=null && measureTypeStr.equals("B0")) {
							measureType=getResources().getString(R.string.commonMeasurePoint);
						}
		  				//压力
		  				String pressureStr=data2.substring(headPosition+30,headPosition+38);
		  				byte[] pressureByte=publicMethod.HexStringToByteArray(pressureStr);
		  				float pressureFloat=publicMethod.byte2float(pressureByte, 0);
		  				//温度
		  				String temperatureStr=data2.substring(headPosition+38,headPosition+46);
		  				byte[] temperatureByte=publicMethod.HexStringToByteArray(temperatureStr);
		  				float temperatureFloat=publicMethod.byte2float(temperatureByte, 0);
		  				
		  				HydraulicMeasuredUnitResult hydraulicMeasuredUnitResult=new HydraulicMeasuredUnitResult(serialNumber, measurePointNum, pressureFloat, temperatureFloat);
		  				hydraulicMeasuredUnitResultList.add(hydraulicMeasuredUnitResult);
		  				
		  				data2="";
		  				
		  			    measureUnitResultAdapter=new MeasuredUnitResultAdapter(HydraulicMonitorNetworkTestTwoActivity.this, R.layout.measureunitresult_item, hydraulicMeasuredUnitResultList);
		  			    measureUnitResultLv.setAdapter(measureUnitResultAdapter);
		  		
		  			   tryCollectDataTV.setText(R.string.tryCollectDataOK);
					}
			  		//错误代码
			  		else if (tailPosition-headPosition==30) {
						String stateStr=data2.substring(headPosition+28,headPosition+30);
						if (stateStr!=null && stateStr.equals("01")) {//设备类型错误
							
						}
						else if (stateStr!=null && stateStr.equals("02")) {//检测机构代码错误
							
						}
						else if (stateStr!=null && stateStr.equals("03")) {//锁定
					
						}
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

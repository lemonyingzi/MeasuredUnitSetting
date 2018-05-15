package com.measuredunitsetting.hydraulic.setting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.measuredunitsetting.BluetoothLeService;
import com.measuredunitsetting.R;
import com.measuredunitsetting.data.PublicMethod;
import com.measuredunitsetting.db.HydraulicMeasuredUnitDB;
import com.measuredunitsetting.db.HydraulicMonitorNetworkDB;
import com.measuredunitsetting.entity.HydraulicMeasuredUnit;
import com.measuredunitsetting.entity.HydraulicMonitorNetwork;
import com.measuredunitsetting.global.GlobalVariable;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import java.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.measuredunitsetting.global.LogUtil;

public class HydraulicMeasuredUnitSettingThreeActivity extends Activity {
	private static final String TAG=HydraulicMeasuredUnitSettingThreeActivity.class.getSimpleName();
	boolean unitConnect=false;
	TextView backTv;
	//连接状态
	TextView connectedWaitingSettingTv;
	//工程名称
	TextView projectNameTv;
	//监测网编号
	TextView monitoringNetworkNumberTv;
	//测量单元ID
	TextView unitIdTv;
	//序号
	TextView serialNumberTv;
	//测点编号
	EditText measurePointNumberEt;
	//测点类型
	Spinner measureTypeSpinner;
	//上一个
	LinearLayout lastLl;
	//下一个
	LinearLayout nextLl;
	
	private List<String> measureTypeList;
	private ArrayAdapter<String> measureTypeAdapter;
	//确定
	LinearLayout confirmLl;
	private BluetoothLeService mBluetoothLeService;//蓝牙连接服务
	//定时器
	Timer paraUploadtimer = new Timer(); //参数上传定时器
	Timer stateMonitorTimer=new Timer();//监听连接状态定时器
	
	PublicMethod publicMethod=new PublicMethod();
	//数据库
	HydraulicMonitorNetworkDB hydraulicMonitorNetworkDB;
	HydraulicMeasuredUnitDB hydraulicMeasuredUnitDB;
	//测量网络
	HydraulicMonitorNetwork monitorNetwork;
	//测量单元ID
	String measuredUnitId;
	//测量单元设置完成标志位
	boolean measureUnitIsSetted;
	String measurePointNumSetted;
	//测量单元总个数
	int totalUnitNumber;
	@SuppressLint("ResourceAsColor")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hydraulicmeasuredunitthreesetting);
		
		//初始化数据库
		hydraulicMonitorNetworkDB=new HydraulicMonitorNetworkDB(getApplicationContext(), GlobalVariable.getDataBaseName(), null, 7);
		hydraulicMeasuredUnitDB=new HydraulicMeasuredUnitDB(getApplicationContext(), GlobalVariable.getDataBaseName(), null, 7);
		//初始化
		serialNumberTv=(TextView)findViewById(R.id.serialNumberTv);
		projectNameTv=(TextView)findViewById(R.id.projectNameTv);
		monitoringNetworkNumberTv=(TextView)findViewById(R.id.monitoringNetworkNumberTv);
	    unitIdTv=(TextView)findViewById(R.id.unitIdTv);
	    measurePointNumberEt=(EditText)findViewById(R.id.measurePointNumberEt);
	    measureUnitIsSetted=false;
	    lastLl=(LinearLayout)findViewById(R.id.lastLl);
	    nextLl=(LinearLayout)findViewById(R.id.nextLl);

	
        //返回
 		backTv=(TextView)findViewById(R.id.backTv);
 		backTv.setOnClickListener(bottomListener);
 		confirmLl=(LinearLayout)findViewById(R.id.confirmLl);
 	
		
		//绑定蓝牙服务
	    Intent gattServiceIntent = new Intent(HydraulicMeasuredUnitSettingThreeActivity.this, BluetoothLeService.class);
	    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	    
	    
	    //连接状态
	    connectedWaitingSettingTv=(TextView)findViewById(R.id.connectedWaitingSettingTv);

	    paraUploadtimer.schedule(new paraUploadTimerTask(), 500, 1000); // 1s后执行task,经过500ms再次执行  
		stateMonitorTimer.schedule(new monitorStateTimerTask(), 500,1200);//0.5s后执行task,经过1000s再次执行
		
		String projectName=getIntent().getStringExtra("projectName");
		String monitorNetworkNumber=getIntent().getStringExtra("monitorNetworkNumber");
		totalUnitNumber=getIntent().getIntExtra("totalUnitNumber", 255);

		projectNameTv.setText(projectName);
		monitoringNetworkNumberTv.setText(monitorNetworkNumber);
		
		monitorNetwork=hydraulicMonitorNetworkDB.selectAccordingToProjectNameAndMonitorNum(GlobalVariable.getUserId(),projectName, monitorNetworkNumber);
		
	    //初始化测点类型
	    measureTypeSpinner=(Spinner)findViewById(R.id.measureTypeSpinner);
	    measureTypeList=new ArrayList<String>();
	    measureTypeList.add(getResources().getString(R.string.benchmark));
	    measureTypeList.add(getResources().getString(R.string.commonMeasurePoint));
	    
        if (measureTypeList!=null && measureTypeList.size()>0) {
	       	 //适配器
    	    measureTypeAdapter=new ArrayAdapter<String>(this, R.layout.spinner_measuretype,R.id.text,measureTypeList);
	       	 //设置样式
    	    measureTypeAdapter.setDropDownViewResource(R.layout.spinner_measuretype);
			//加载适配器
	       	measureTypeSpinner.setAdapter(measureTypeAdapter);
	       	
	       long measureTypeCount=hydraulicMeasuredUnitDB.selectMeasureUnitAccordingToMeasureType(monitorNetwork.getId(), "A0");
	       if (measureTypeCount<1) {
		       	measureTypeSpinner.setSelection(0);
	       }
	       else
	       {
	    	   measureTypeSpinner.setSelection(1);
	       }
      	 }
        
		
		List<HydraulicMeasuredUnit> hydraulicMeasuredUnitList=new ArrayList<HydraulicMeasuredUnit>();
		if (monitorNetwork!=null) {
			hydraulicMeasuredUnitList=hydraulicMeasuredUnitDB.getMeasureUnits(monitorNetwork.getId());
			if (hydraulicMeasuredUnitList==null || hydraulicMeasuredUnitList.size()==0) {
				serialNumberTv.setText("1");
				measurePointNumberEt.setText("00000000");
				lastLl.setBackgroundResource(R.xml.paddingbackgroundgrayleft2dp);
				nextLl.setBackgroundResource(R.xml.paddingbackgroundwhiteright2dp);
			}
			else
			{
				int  serialNumber=hydraulicMeasuredUnitList.get(hydraulicMeasuredUnitList.size()-1).getSerialNumber();
				if(serialNumber<totalUnitNumber)
				{
					serialNumberTv.setText(String.valueOf(serialNumber+1));
					if ((serialNumber+1)==totalUnitNumber) {
						nextLl.setBackgroundResource(R.xml.paddingbackgroundgrayright2dp);
					}
					String mpn=hydraulicMeasuredUnitList.get(hydraulicMeasuredUnitList.size()-1).getMeasurePointNumber();
					measurePointNumberEt.setText(publicMethod.GetNextMeasurePointNum(mpn));
				}
				else
				{
					serialNumberTv.setText(String.valueOf(serialNumber));
					String mpn=hydraulicMeasuredUnitList.get(hydraulicMeasuredUnitList.size()-1).getMeasurePointNumber();
					measurePointNumberEt.setText(mpn);
					nextLl.setBackgroundResource(R.xml.paddingbackgroundgrayright2dp);
				}
			}
		}
		
		
		
		//设置按钮
		confirmLl.setOnClickListener(new View.OnClickListener()
			{
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				if (TextUtils.isEmpty(serialNumberTv.getText())){
					Toast.makeText(getApplicationContext(),getResources().getString(R.string.settedCompleted), Toast.LENGTH_SHORT).show();
					return;
				}
				if (TextUtils.isEmpty(measurePointNumberEt.getText())) {
					Toast.makeText(getApplicationContext(),getResources().getString(R.string.settedCompleted), Toast.LENGTH_SHORT).show();
					return;
				}
				int serialNumber =Integer.parseInt(serialNumberTv.getText().toString());
				String measurePointNumber=measurePointNumberEt.getText().toString();
				String measureType=measureTypeSpinner.getSelectedItem().toString();
				final String measureUnitId=unitIdTv.getText().toString();
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式		
				
				if (measurePointNumber==null || measurePointNumber.equals("")) {
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.noInputMeasurePointNumber), Toast.LENGTH_SHORT).show();
					return;
				}
				if (connectedWaitingSettingTv.getText()!=null && connectedWaitingSettingTv.getText().equals(getResources().getString(R.string.setupCompleted))) {
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.measurementUnitNotReplaced),Toast.LENGTH_SHORT).show();
				}
				//已连接
				if (connectedWaitingSettingTv.getText().equals(getResources().getString(R.string.connectedWaitingSettings))) {
			
					final HydraulicMeasuredUnit hmu=new HydraulicMeasuredUnit(monitorNetwork.getId(), serialNumber, measurePointNumber, measureType, measureUnitId, df.format(new Date()));
					//序号已存在
					if (hydraulicMeasuredUnitDB.selectMeasureUnitAccordingToSerialNumber(monitorNetwork.getId(), serialNumber)) {
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.serialNumberAlreadyExist), Toast.LENGTH_SHORT).show();
						return;
					}
					//测点编号已存在
					if (hydraulicMeasuredUnitDB.selectMeasureUnitAccordingToMeasurePointNum(monitorNetwork.getId(),measurePointNumber)) {
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.measurePointNumAlreadyExist), Toast.LENGTH_SHORT).show();
						return;
					}
					//测点类型已存在
					if (measureType!=null && measureType.equals(getResources().getString(R.string.benchmark))) {
						long count=hydraulicMeasuredUnitDB.selectMeasureUnitAccordingToMeasureType(monitorNetwork.getId(), "A0");
						if (count>0) {
							Toast.makeText(getApplicationContext(), getResources().getString(R.string.benchmarkAlreadyExist), Toast.LENGTH_SHORT).show();
							return;
						}
					}
				
					//测量单元ID已存在
					if (hydraulicMeasuredUnitDB.selectMeasureUnitAccordingToMeasureUnitId(monitorNetwork.getId(), measureUnitId)) {
						//定义AlertDialog.Builder对象，当长按列表项的时候弹出确认删除对话框   
		                AlertDialog.Builder builder=new Builder(HydraulicMeasuredUnitSettingThreeActivity.this);  
		                builder.setMessage(R.string.measurePointNumAlreadyExisConnect);  
		                builder.setTitle(R.string.note);  
		                  
		                //添加AlertDialog.Builder对象的setPositiveButton()方法   
		                builder.setPositiveButton(R.string.confirm, new OnClickListener() {  
		                    @Override  
		                    public void onClick(DialogInterface dialog, int which) {  
		                    	try {
		                    		hydraulicMeasuredUnitDB.deleteAccordingToMonitorNetworkNumberAndMeausreUnitId(monitorNetwork.getId(), measureUnitId);
									usersetting(hmu);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
		                    }  
		                });  
		                  
		                //添加AlertDialog.Builder对象的setNegativeButton()方法   
		                builder.setNegativeButton(R.string.cancel, new OnClickListener() {  
		                      
		                    @Override  
		                    public void onClick(DialogInterface dialog, int which) {  
		                        return ;
		                    }  
		                });  
		                  
		                builder.create().show();  
					}
					else
					{
						try {
							usersetting(hmu);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				//未连接
				else if (connectedWaitingSettingTv.getText().equals(getResources().getString(R.string.conntentMeasueUnit))) {
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.measurementUnitNotConnected),	android.widget.Toast.LENGTH_SHORT).show();
				}
			}});
		//上一个
		lastLl.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View arg0) {
						if (TextUtils.isEmpty(serialNumberTv.getText())) 
						{
							return;
						}
						int serialNumber=Integer.parseInt(serialNumberTv.getText().toString());
						String measurePointNum=measurePointNumberEt.getText().toString();
						if (serialNumber>1 && !connectedWaitingSettingTv.getText().equals(getResources().getString(R.string.setupCompleted))) {
							serialNumberTv.setText(String.valueOf(serialNumber-1));
							measurePointNumberEt.setText(publicMethod.GetLastMeasurePointNum(measurePointNum));
							if ((serialNumber-1)==1) {
								lastLl.setBackgroundResource(R.xml.paddingbackgroundgrayleft2dp);
							}
							else
							{
								lastLl.setBackgroundResource(R.xml.paddingbackgroundwhiteleft2dp);
							}
							if ((serialNumber-1)<totalUnitNumber) {
								nextLl.setBackgroundResource(R.xml.paddingbackgroundwhiteright2dp);
							}
						}
				
						
					}
						
				});
		
		//下一个
		nextLl.setOnClickListener(new View.OnClickListener()
				{

					@Override
					public void onClick(View arg0) {
						if (TextUtils.isEmpty(serialNumberTv.getText())) 
						{
							return;
						}
						int serialNumber=Integer.parseInt(serialNumberTv.getText().toString());
						
						String measurePointNum=measurePointNumberEt.getText().toString();
						if (serialNumber<totalUnitNumber && !connectedWaitingSettingTv.getText().equals(getResources().getString(R.string.setupCompleted))) {
							serialNumberTv.setText(String.valueOf(serialNumber+1));
							measurePointNumberEt.setText(publicMethod.GetNextMeasurePointNum(measurePointNum));
							if (serialNumber+1==totalUnitNumber) {
								nextLl.setBackgroundResource(R.xml.paddingbackgroundgrayright2dp);
							}
							else
							{
								nextLl.setBackgroundResource(R.xml.paddingbackgroundwhiteright2dp);
							}
							if ((serialNumber+1)>1) {
								lastLl.setBackgroundResource(R.xml.paddingbackgroundwhiteleft2dp);
							}
						}
					}
			
			
				});
	}
	
	
	
	private void usersetting(HydraulicMeasuredUnit hydraulicMeasuredUnit) throws InterruptedException
	{
		if (stateMonitorTimer!=null) {
			stateMonitorTimer.cancel();
			stateMonitorTimer=null;
		}
		if (paraUploadtimer!=null) {
			paraUploadtimer.cancel();
			paraUploadtimer=null;
		}
		if (mBluetoothLeService != null) {
			mBluetoothLeService.t_data_usersetting_hydraulic(hydraulicMeasuredUnit);
		}
		
	}
	//发送蓝牙指令
    Handler handler = new Handler() {  
        public void handleMessage(Message msg) {  
        	if (msg.what == 1) {  
            	if (mBluetoothLeService != null) {
            				if (!unitConnect) {
            					mBluetoothLeService.t_data_query_hydraulic();
                        		LogUtil.i(TAG, "发送查询");
							}	
                		}
        	}
        	else if (msg.what==2) {
	  			connectedWaitingSettingTv.setText(R.string.conntentMeasueUnit);
			}
        	else if (msg.what==3) {
			}
            super.handleMessage(msg);  
        };  
    };  
    
 
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
    
    public class monitorStateTimerTask extends TimerTask
    {
        int i=0;

		@Override
		public void run() {
			if (!unitConnect) {//未连接
				i=i+1;
				if (i>3) {
					i=0;
					Message message=new Message();
					message.what=2;
					handler.sendMessage(message);
				}
			}
			else//已连接
			{
				Message message=new Message();
				message.what=3;
				handler.sendMessage(message);
				unitConnect=false;
			}
		}
    }

    String data2="";
    @SuppressLint({ "NewApi", "ResourceAsColor" })
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
				  			//id
				  			String measuredUnitIdStr=data2.substring(30+headPosition,50+headPosition);	  	
				  			byte[] measuredUnitIdByte=publicMethod.HexStringToByteArray(measuredUnitIdStr);
				  	
				  			measuredUnitId=new String(measuredUnitIdByte);
				  			if (measuredUnitId!=null && !measuredUnitId.equals(measurePointNumSetted)) {
								measureUnitIsSetted=false;
							}
				  			
				  			LogUtil.i(TAG, "头尾正确");
				  			if (!measureUnitIsSetted) {
					  			connectedWaitingSettingTv.setText(R.string.connectedWaitingSettings);
							}
				  			
				  			unitConnect=true;
				  			unitIdTv.setText(measuredUnitId);
				  			data2="";
				  		}
					}
			  		//用户设定
			  		else if (tailPosition-headPosition==30) {
		  				LogUtil.i(TAG, "头尾正确");
		  				//序号
		  				String serialNumberStr=data2.substring(6+headPosition,10+headPosition);
		  				byte[] serialNumberByte=publicMethod.HexStringToByteArray(serialNumberStr);
		  				int serialNumber=serialNumberByte[0]*256+serialNumberByte[1];
		  				//测点编号
		  				String measurePointNumberStr=data2.substring(10+headPosition,26+headPosition);
		  				byte[] measurePointNumberByte=publicMethod.HexStringToByteArray(measurePointNumberStr);
		  				String measurePointNumber=new String(measurePointNumberByte);
		  				//测点类型
			  			String typeStr=data2.substring(26+headPosition,28+headPosition);	  	
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式	
						//状态位
						String stateStr=data2.substring(28+headPosition,30+headPosition);
						if (stateStr!=null && stateStr.equals("00")) {
							String time=df.format(new Date());
							measurePointNumSetted=measuredUnitId;
			  				HydraulicMeasuredUnit hydraulicMeasuredUnit=new HydraulicMeasuredUnit(monitorNetwork.getId(), serialNumber, measurePointNumber, typeStr, measuredUnitId, time);
			  				hydraulicMeasuredUnitDB.insert(hydraulicMeasuredUnit);
				  			connectedWaitingSettingTv.setText(R.string.setupCompleted);
				  			
				  			int serialNumberInt=Integer.parseInt(serialNumberTv.getText().toString());
							String measurePointNum=measurePointNumberEt.getText().toString();
							if (serialNumberInt<totalUnitNumber) {
								serialNumberTv.setText(String.valueOf(serialNumberInt+1));
								if (serialNumberInt+1==totalUnitNumber) {
									nextLl.setBackgroundResource(R.xml.paddingbackgroundgrayright2dp);
								}
								if (serialNumberInt+1>1) {
									lastLl.setBackgroundResource(R.xml.paddingbackgroundwhiteleft2dp);
								}
								
								measurePointNumberEt.setText(publicMethod.GetNextMeasurePointNum(measurePointNum));
							}
					    
				  			if (paraUploadtimer==null) {
				  				paraUploadtimer=new Timer();
					  		    paraUploadtimer.schedule(new paraUploadTimerTask(), 500, 1000); // 1s后执行task,经过1000ms再次执行  
							}
				  			if (stateMonitorTimer==null) {
				  			    stateMonitorTimer=new Timer();
					  			stateMonitorTimer.schedule(new monitorStateTimerTask(), 500,1200);//0.5s后执行task,经过1000s再次执行
							}
							measureUnitIsSetted=true;
						}
						else
						{
							Toast.makeText(getApplicationContext(), getResources().getString(R.string.setupFail),Toast.LENGTH_SHORT).show();
							measureUnitIsSetted=false;
						}
//						else if (stateStr!=null && stateStr.equals("01")) {
//
//						}
						data2="";
					}
					else if (tailPosition-headPosition!=62 || tailPosition-headPosition!=30) {
						connectedWaitingSettingTv.setText(R.string.errorMeasuringUnitConnection);
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
    
	  /**
     * 顶部返回键
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//创建监听对象    
    	public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv) {
        		back();
			}
		}
    }; 
    
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
               if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            	    back();
            	   	return true;
                 }
                 return super.onKeyDown(keyCode, event);
    }
    
    
    /**
     * 系统返回键
     */
 
//    public void onBackPressed()
//    {
//    	super.onBackPressed();
//
//    	back();
//    }
    private void back()
    {
		//判断序号最大值是否为总测流量单元个数
		long count=hydraulicMeasuredUnitDB.selectCountAccordingToMonitorNetworkNumberId(monitorNetwork.getId());
		long benchmarkCount=hydraulicMeasuredUnitDB.selectMeasureUnitAccordingToMeasureType(monitorNetwork.getId(), "A0");
		//没有设置完成
		if (count<totalUnitNumber || benchmarkCount!=1) {
            AlertDialog.Builder builder=new Builder(HydraulicMeasuredUnitSettingThreeActivity.this);  
            if (count<totalUnitNumber) {
                builder.setMessage(getResources().getString(R.string.alreadySetted)+count+getResources().getString(R.string.lack)
                		+String.valueOf(totalUnitNumber-count)+getResources().getString(R.string.numberMeasureUnit)+"\r\n"
                		+getResources().getString(R.string.currentMonitoringNetworkSetupHasNotBeenCompleted));  
			}
            else if (benchmarkCount!=1) {
	                builder.setMessage(getResources().getString(R.string.hasSetted)+benchmarkCount+getResources().getString(R.string.incorrectBaselineSettingLeadFailure));  
			}
      
            builder.setTitle(R.string.warn);  
              
            //添加AlertDialog.Builder对象的setPositiveButton()方法   
            builder.setPositiveButton(R.string.continueSet, new OnClickListener() {  
                @Override  
                public void onClick(DialogInterface dialog, int which) {  
                	return;
                }  
            });  
              
            //添加AlertDialog.Builder对象的setNegativeButton()方法   
            builder.setNegativeButton(R.string.exit, new OnClickListener() {  
                  
                @Override  
                public void onClick(DialogInterface dialog, int which) {  
                    ((Activity)HydraulicMeasuredUnitSettingThreeActivity.this).finish();
                }  
            });  
              
            builder.create().show();  
		}
		else
		{
            ((Activity)HydraulicMeasuredUnitSettingThreeActivity.this).finish();
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
    
    
	protected void onResume() {//打开APP时扫描设备
		super.onResume();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}
	
	protected void onRestart()
	{
		super.onRestart();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		if (paraUploadtimer==null) {
				paraUploadtimer=new Timer();
  		    paraUploadtimer.schedule(new paraUploadTimerTask(), 500, 1000); // 1s后执行task,经过1000ms再次执行  
		}
			if (stateMonitorTimer==null) {
			    stateMonitorTimer=new Timer();
  			stateMonitorTimer.schedule(new monitorStateTimerTask(), 500,1200);//0.5s后执行task,经过1000s再次执行
		}
		
		//绑定蓝牙服务
	    Intent gattServiceIntent = new Intent(HydraulicMeasuredUnitSettingThreeActivity.this, BluetoothLeService.class);
	    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	}
	
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
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mGattUpdateReceiver);
		if (paraUploadtimer!=null ) {
			paraUploadtimer.cancel();
			paraUploadtimer=null;
		}
		if (stateMonitorTimer!=null) {
			stateMonitorTimer.cancel();
			stateMonitorTimer=null;
		}
	    try{
	    	unbindService(mServiceConnection);
	    }catch (Exception e) {
			LogUtil.e(TAG, e.toString());
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

}

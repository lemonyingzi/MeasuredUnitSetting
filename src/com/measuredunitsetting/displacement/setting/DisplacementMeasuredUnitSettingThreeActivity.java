package com.measuredunitsetting.displacement.setting;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.measuredunitsetting.BluetoothLeService;
import com.measuredunitsetting.R;
import com.measuredunitsetting.data.PublicMethod;
import com.measuredunitsetting.db.DisplacementMeasuredUnitDB;
import com.measuredunitsetting.db.DisplacementMonitorPointDB;
import com.measuredunitsetting.entity.DisplacementMeasuredUnit;
import com.measuredunitsetting.entity.DisplacementMonitorPoint;
import com.measuredunitsetting.global.GlobalVariable;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.measuredunitsetting.global.LogUtil;

public class DisplacementMeasuredUnitSettingThreeActivity extends Activity {
	private static final String TAG=DisplacementMeasuredUnitSettingThreeActivity.class.getSimpleName();
	boolean unitConnect=false;
	TextView backTv;
	//连接状态
	TextView connectedWaitingSettingTv;
	//工程名称
	TextView projectNameTv;
	//监测点编号
	TextView monitoringPointNumberTv;
	//测量深度
	TextView monitorDepthTv;
	//测量单元间距
	TextView unitSpacingTv;
	//测量单元ID
	TextView unitIdTv;
	//序号
	TextView serialNumberTv;
	//上一个
	LinearLayout lastLl;
	//下一个
	LinearLayout nextLl;
	//确定
	LinearLayout confirmLl;
	
	
	private BluetoothLeService mBluetoothLeService;//蓝牙连接服务
	//定时器
	Timer paraUploadtimer = new Timer(); //参数上传定时器
	Timer stateMonitorTimer=new Timer();//监听连接状态定时器
	
	PublicMethod publicMethod=new PublicMethod();
	//数据库
	DisplacementMonitorPointDB displacementMonitorPointDB;
	DisplacementMeasuredUnitDB displacementMeasuredUnitDB;
	//测量网络
	DisplacementMonitorPoint monitorPoint;
	//测量单元ID
	String measuredUnitId;
	//监测深度
	TextView monitorDepthTextView;
	
	//测量单元设置完成标志位
	boolean measureUnitIsSetted;
	String measurePointNumSetted;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_displacementmeasuredunitthreesetting);
		
		//初始化数据库
		displacementMonitorPointDB=new DisplacementMonitorPointDB(getApplicationContext(), GlobalVariable.getDataBaseName(), null, 7);
		displacementMeasuredUnitDB=new DisplacementMeasuredUnitDB(getApplicationContext(), GlobalVariable.getDataBaseName(), null, 7);
		//初始化
		serialNumberTv=(TextView)findViewById(R.id.serialNumberTv);
		projectNameTv=(TextView)findViewById(R.id.projectNameTv);
		monitoringPointNumberTv=(TextView)findViewById(R.id.monitoringPointNumberTv);
		monitorDepthTv=(TextView)findViewById(R.id.monitorDepthTv);
		unitSpacingTv=(TextView)findViewById(R.id.unitSpacingTv);
		
	    unitIdTv=(TextView)findViewById(R.id.unitIdTv);
	    
	    lastLl=(LinearLayout)findViewById(R.id.lastLl);
	    nextLl=(LinearLayout)findViewById(R.id.nextLl);
	    monitorDepthTextView=(TextView)findViewById(R.id.monitorDepthTextView);
	    
        //返回
 		backTv=(TextView)findViewById(R.id.backTv);
 		backTv.setOnClickListener(bottomListener);
 		confirmLl=(LinearLayout)findViewById(R.id.confirmLl);
 		
	    measureUnitIsSetted=false;

		//绑定蓝牙服务
	    Intent gattServiceIntent = new Intent(DisplacementMeasuredUnitSettingThreeActivity.this, BluetoothLeService.class);
	    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	    
	    
	    //连接状态
	    connectedWaitingSettingTv=(TextView)findViewById(R.id.connectedWaitingSettingTv);

	    paraUploadtimer.schedule(new paraUploadTimerTask(), 500, 1000); // 1s后执行task,经过500ms再次执行  
		stateMonitorTimer.schedule(new monitorStateTimerTask(), 500,1200);//0.5s后执行task,经过1000s再次执行
		
		String projectName=getIntent().getStringExtra("projectName");
		String monitoringPointNumber=getIntent().getStringExtra("monitoringPointNumber");
		projectNameTv.setText(projectName);
		monitoringPointNumberTv.setText(monitoringPointNumber);
		monitorPoint=displacementMonitorPointDB.selectAccordingToProjectNameAndMonitorNum(GlobalVariable.getUserId(),projectName, monitoringPointNumber);
		
		monitorDepthTv.setText(String.valueOf(monitorPoint.getMonitorDepth()));
		unitSpacingTv.setText(String.valueOf(monitorPoint.getUnitSpacing()));
		
		DisplacementMeasuredUnit displacementMeasuredUnit=null;
		if (monitorPoint!=null) {
			displacementMeasuredUnit=displacementMeasuredUnitDB.getMeasureUnit(monitorPoint.getId());
			if (displacementMeasuredUnit==null) {
				int serialNumber=(int)(monitorPoint.getMonitorDepth()/monitorPoint.getUnitSpacing());
				serialNumberTv.setText(String.valueOf(serialNumber));

				String unitId=displacementMeasuredUnitDB.getMeasureUnitId(serialNumber);
				unitIdTv.setText(unitId);

				monitorDepthTextView.setText(String.valueOf(monitorPoint.getMonitorDepth()));
				nextLl.setBackgroundResource(R.xml.paddingbackgroundgrayright2dp);
				if ((int)(monitorPoint.getMonitorDepth()/monitorPoint.getUnitSpacing())==1)
				{
					lastLl.setBackgroundResource(R.xml.paddingbackgroundgrayright2dp);
				}
			}
			else
			{
				int  serialNumber=displacementMeasuredUnit.getSerialNumber()-1;
				if (serialNumber<1)//说明已经设置完成，此时显示序号为1的值
				{
					nextLl.setBackgroundResource(R.xml.paddingbackgroundgrayright2dp);
					monitorDepthTextView.setText(String.valueOf(monitorPoint.getMonitorDepth()));
					int serialNum=(int)(monitorPoint.getMonitorDepth()/monitorPoint.getUnitSpacing());
					serialNumberTv.setText(String.valueOf(serialNum));
					String unitId=displacementMeasuredUnitDB.getMeasureUnitId(serialNum);
					unitIdTv.setText(unitId);

				}
				else
				{
					serialNumberTv.setText(String.valueOf(serialNumber));
					monitorDepthTextView.setText(String.valueOf(displacementMeasuredUnit.getDepth()-monitorPoint.getUnitSpacing()));
					lastLl.setBackgroundResource(R.xml.paddingbackgroundwhiteleft2dp);
					String unitId=displacementMeasuredUnitDB.getMeasureUnitId(serialNumber);
					unitIdTv.setText(unitId);

				}

				if ((int)(monitorPoint.getMonitorDepth()/monitorPoint.getUnitSpacing())==1)
				{
					lastLl.setBackgroundResource(R.xml.paddingbackgroundgrayleft2dp);
				}
				else {
					lastLl.setBackgroundResource(R.xml.paddingbackgroundwhiteleft2dp);
				}
			}
		}
		
		//设置按钮
		confirmLl.setOnClickListener(new View.OnClickListener()
			{
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				
				final int serialNumber =Integer.parseInt(serialNumberTv.getText().toString());
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
				float monitorDepth=Float.parseFloat(monitorDepthTextView.getText().toString());
				
				if (connectedWaitingSettingTv.getText()!=null && connectedWaitingSettingTv.getText().equals(getResources().getString(R.string.setupCompleted))) {
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.measurementUnitNotReplaced),Toast.LENGTH_SHORT).show();
					return;
				}
				//已连接
				if (connectedWaitingSettingTv.getText().equals(getResources().getString(R.string.connectedWaitingSettings))) {
			
					final DisplacementMeasuredUnit hmu=new DisplacementMeasuredUnit(monitorPoint.getId(), serialNumber, measuredUnitId, monitorDepth,df.format(new Date()));

					boolean isSerialNumberExisted=displacementMeasuredUnitDB.selectMeasureUnitAccordingToSerialNumber(monitorPoint.getId(), serialNumber);//true 为存在，false为不存在,判断序号是否存在
					boolean isMeasurePointNumberExisted=displacementMeasuredUnitDB.selectMeasureUnitAccordingToMeasureUnitId(monitorPoint.getId(), measuredUnitId);//true为存在，false为不存在，判断测量单元ID是否存在
					//四种情况之一：序号已存在、测量单元ID已存在
					if (isSerialNumberExisted && isMeasurePointNumberExisted) {
						//定义AlertDialog.Builder对象，当长按列表项的时候弹出确认删除对话框
						AlertDialog.Builder builder=new Builder(DisplacementMeasuredUnitSettingThreeActivity.this);
						builder.setMessage(R.string.serialNumberAndMeasurePointNumAlreadyExist);
						builder.setTitle(R.string.note);
						//添加AlertDialog.Builder对象的setPositiveButton()方法
						builder.setPositiveButton(R.string.confirm, new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								try {
									displacementMeasuredUnitDB.deleteAccordingToMonitorNetworkNumberAndSerialNumber(monitorPoint.getId(), serialNumber);
									displacementMeasuredUnitDB.deleteAccordingToMonitorNetworkNumberAndMeausreUnitId(monitorPoint.getId(), measuredUnitId);
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
					//测点深度
//					if (displacementMeasuredUnitDB.selectMeasureUnitAccordingToDepth(monitorPoint.getId(), monitorDepth)) {
//						Toast.makeText(getApplicationContext(), getResources().getString(R.string.monitorDepthExist), Toast.LENGTH_SHORT).show();
//						return;
//					}
							
					//四种情况之二：测量单元ID已存在，序号不存在
					else if (!isSerialNumberExisted && isMeasurePointNumberExisted) {
						
						//定义AlertDialog.Builder对象，当长按列表项的时候弹出确认删除对话框   
		                AlertDialog.Builder builder=new Builder(DisplacementMeasuredUnitSettingThreeActivity.this);  
		                builder.setMessage(R.string.measurePointNumAlreadyExisConnect);  
		                builder.setTitle(R.string.note);  
		                  
		                //添加AlertDialog.Builder对象的setPositiveButton()方法   
		                builder.setPositiveButton(R.string.confirm, new OnClickListener() {  
		                    @Override  
		                    public void onClick(DialogInterface dialog, int which) {  
		                    	try {
									displacementMeasuredUnitDB.deleteAccordingToMonitorNetworkNumberAndMeausreUnitId(monitorPoint.getId(), measuredUnitId);
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
					//四种情况之三：序号已存在，测量单元ID不存在
					else if (isSerialNumberExisted  && !isMeasurePointNumberExisted)
					{
						AlertDialog.Builder builder=new Builder(DisplacementMeasuredUnitSettingThreeActivity.this);
						builder.setMessage(R.string.serialNumberAlreadyExisConnect);
						builder.setTitle(R.string.note);
						//添加AlertDialog.Builder对象的setPositiveButton()方法
						builder.setPositiveButton(R.string.confirm, new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								try {
									displacementMeasuredUnitDB.deleteAccordingToMonitorNetworkNumberAndSerialNumber(monitorPoint.getId(), serialNumber);
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
					//四种情况之四：序号不存在，测量单元ID不存在
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
						int serialNumber=Integer.parseInt(serialNumberTv.getText().toString());
						float monitorD=Float.parseFloat(monitorDepthTextView.getText().toString());
						serialNumber--;
						if (serialNumber>=1 && !connectedWaitingSettingTv.getText().equals(getResources().getString(R.string.setupCompleted))) {
							serialNumberTv.setText(String.valueOf(serialNumber));
							monitorDepthTextView.setText(String.valueOf((monitorD-monitorPoint.getUnitSpacing())));
							String unidId=displacementMeasuredUnitDB.getMeasureUnitId(serialNumber);
							unitIdTv.setText(unidId);
							if (serialNumber==1) {
								lastLl.setBackgroundResource(R.xml.paddingbackgroundgrayleft2dp);
							}
							else
							{
								lastLl.setBackgroundResource(R.xml.paddingbackgroundwhiteleft2dp);
							}
							nextLl.setBackgroundResource(R.xml.paddingbackgroundwhiteright2dp);
						}
					
					}
						
				});
		
		//下一个
		nextLl.setOnClickListener(new View.OnClickListener()
				{

					@Override
					public void onClick(View arg0) {
						int serialNumber=Integer.parseInt(serialNumberTv.getText().toString());
						float monitorD=Float.parseFloat(monitorDepthTextView.getText().toString());
						serialNumber++;
						if (serialNumber<=monitorPoint.getMonitorDepth()/monitorPoint.getUnitSpacing() && !connectedWaitingSettingTv.getText().equals(getResources().getString(R.string.setupCompleted))) {
							serialNumberTv.setText(String.valueOf(serialNumber));
							monitorDepthTextView.setText(String.valueOf(monitorD+monitorPoint.getUnitSpacing()));
							String unidId=displacementMeasuredUnitDB.getMeasureUnitId(serialNumber);
							unitIdTv.setText(unidId);
							if (serialNumber==monitorPoint.getMonitorDepth()/monitorPoint.getUnitSpacing()) {
								nextLl.setBackgroundResource(R.xml.paddingbackgroundgrayright2dp);
							}
							else
							{
								nextLl.setBackgroundResource(R.xml.paddingbackgroundwhiteright2dp);
							}
							lastLl.setBackgroundResource(R.xml.paddingbackgroundwhiteleft2dp);
						}
					}
					
			
			
				});
	}
	
	private void usersetting(DisplacementMeasuredUnit displacementMeasuredUnit) throws InterruptedException
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
			mBluetoothLeService.t_data_usersetting_displacement(displacementMeasuredUnit);
		}
		
	}
	//发送蓝牙指令
    Handler handler = new Handler() {  
        public void handleMessage(Message msg) {  
        	if (msg.what == 1) {
        		try
				{
					if (mBluetoothLeService != null) {
						if (!unitConnect) {
							mBluetoothLeService.t_data_query_displacement();
						}
					}
				}catch (Exception ex)
				{
					LogUtil.e(TAG,ex.toString());
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
					int diffPosition=tailPosition-headPosition;
		  			//参数上传 20180417修改、上行数据去除温度修正值，长度由70改成62
			  		if (diffPosition==54||diffPosition==62) {
				  		if(headPosition!=-1 && tailPosition!=-1)
				  		{
				  			//id
							String measuredUnitIdStr="";
							if (diffPosition==54 ) {
								measuredUnitIdStr=data2.substring(34+headPosition,54+headPosition);
							}
							else
								measuredUnitIdStr=data2.substring(42+headPosition,62+headPosition);
				  			byte[] measuredUnitIdByte=publicMethod.HexStringToByteArray(measuredUnitIdStr);
				  			LogUtil.i(TAG, "measuredUnitIdStr:"+measuredUnitIdStr);
				  			measuredUnitId=new String(measuredUnitIdByte);
				  			if (measuredUnitId!=null && !measuredUnitId.equals(measurePointNumSetted)) {
								measureUnitIsSetted=false;
							}
				  			
				  			LogUtil.i(TAG, "头尾正确");
				  			if (!measureUnitIsSetted) {
					  			connectedWaitingSettingTv.setText(R.string.connectedWaitingSettings);
							}
				  			LogUtil.i(TAG, "measuredUnitId:"+measuredUnitId);
				  			unitConnect=true;
				  			unitIdTv.setText(measuredUnitId);
				  			data2="";
				  		}
					}
			  		//用户设定
			  		else if (diffPosition==18) {
		  				LogUtil.i(TAG, "头尾正确");
		  				//20180515传感器未解锁
		  				String stateStr=data2.substring(16+headPosition,18+headPosition);
		  				if(stateStr!=null && stateStr.equals("00"))
						{
							//序号
							String serialNumberStr=data2.substring(6+headPosition,10+headPosition);
							byte[] serialNumberByte=publicMethod.HexStringToByteArray(serialNumberStr);
							int serialNumber=serialNumberByte[0]*256+serialNumberByte[1];

							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
							String time=df.format(new Date());
							measurePointNumSetted=measuredUnitId;

							String depthStr=data2.substring(10+headPosition,16+headPosition);
							byte[] depthByte=publicMethod.HexStringToByteArray(depthStr);
							float depth=Float.parseFloat(String.valueOf(depthByte[0])+String.valueOf(depthByte[1])+"."+String.valueOf(depthByte[2]));

							DisplacementMeasuredUnit hydraulicMeasuredUnit=new DisplacementMeasuredUnit(monitorPoint.getId(), serialNumber, measuredUnitId, depth, time);
							displacementMeasuredUnitDB.insert(hydraulicMeasuredUnit);

							connectedWaitingSettingTv.setText(R.string.setupCompleted);

							int serialNumberInt=Integer.parseInt(serialNumberTv.getText().toString());
							float nextDepth=depth-monitorPoint.getUnitSpacing();
							serialNumberInt--;
							if (serialNumberInt>=1) {
								serialNumberTv.setText(String.valueOf(serialNumberInt));
								monitorDepthTextView.setText(String.valueOf(nextDepth));
								if (serialNumber==1) {
									lastLl.setBackgroundResource(R.xml.paddingbackgroundgrayleft2dp);
								}
								else
								{
									lastLl.setBackgroundResource(R.xml.paddingbackgroundwhiteleft2dp);
								}
							}
							if (paraUploadtimer==null) {
								paraUploadtimer=new Timer();
								paraUploadtimer.schedule(new paraUploadTimerTask(), 500, 1000); // 1s后执行task,经过1000ms再次执行
							}
							if (stateMonitorTimer==null) {
								stateMonitorTimer=new Timer();
								stateMonitorTimer.schedule(new monitorStateTimerTask(), 500,1200);//0.5s后执行task,经过1000s再次执行
							}
							data2="";
							measureUnitIsSetted=true;
						}
						else
						{
							Toast.makeText(getApplicationContext(), getResources().getString(R.string.setupFail),Toast.LENGTH_SHORT).show();
							measureUnitIsSetted=false;
							data2="";
						}
//		  				else if(state=="01")//设备类型代码错误
//						{
//							Toast.makeText(getApplicationContext(), getResources().getString(R.string.deviceCodeError),Toast.LENGTH_SHORT).show();
//							measureUnitIsSetted=false;
//							data2="";
//						}
//						else if(state=="02")//检测机构代码错误
//						{
//							Toast.makeText(getApplicationContext(), getResources().getString(R.string.examOrganCodeError),Toast.LENGTH_SHORT).show();
//							measureUnitIsSetted=false;
//							data2="";
//						}
//						else if(state=="03")//传感器锁定
//						{
//							Toast.makeText(getApplicationContext(), getResources().getString(R.string.sensorLockError),Toast.LENGTH_SHORT).show();
//							measureUnitIsSetted=false;
//							data2="";
//						}
//						else if(state=="04")//传感器故障
//						{
//							Toast.makeText(getApplicationContext(), getResources().getString(R.string.sensorError),Toast.LENGTH_SHORT).show();
//							measureUnitIsSetted=false;
//							data2="";
//						}
					}
					else
					{
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
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			back();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private  void back()
	{
		//判断序号最大值是否为总测量单元个数
		long count=displacementMeasuredUnitDB.selectCountAccordingToMonitorNetworkNumberId(monitorPoint.getId());
		int totalCount=(int) (monitorPoint.getMonitorDepth()/monitorPoint.getUnitSpacing());
		//没有设置完成
		if (count<totalCount) {
			AlertDialog.Builder builder=new Builder(DisplacementMeasuredUnitSettingThreeActivity.this);
			builder.setMessage(getResources().getString(R.string.alreadySetted)+count+getResources().getString(R.string.lack)
					+String.valueOf(totalCount-count)+getResources().getString(R.string.numberMeasureUnit)+"\r\n"
					+getResources().getString(R.string.currentMonitoringNetworkSetupHasNotBeenCompleted));

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
					((Activity)DisplacementMeasuredUnitSettingThreeActivity.this).finish();
				}
			});

			builder.create().show();
		}
		else
		{
			((Activity)DisplacementMeasuredUnitSettingThreeActivity.this).finish();
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
	    Intent gattServiceIntent = new Intent(DisplacementMeasuredUnitSettingThreeActivity.this, BluetoothLeService.class);
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

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
	//����״̬
	TextView connectedWaitingSettingTv;
	//��������
	TextView projectNameTv;
	//������
	TextView monitoringPointNumberTv;
	//�������
	TextView monitorDepthTv;
	//������Ԫ���
	TextView unitSpacingTv;
	//������ԪID
	TextView unitIdTv;
	//���
	TextView serialNumberTv;
	//��һ��
	LinearLayout lastLl;
	//��һ��
	LinearLayout nextLl;
	//ȷ��
	LinearLayout confirmLl;
	
	
	private BluetoothLeService mBluetoothLeService;//�������ӷ���
	//��ʱ��
	Timer paraUploadtimer = new Timer(); //�����ϴ���ʱ��
	Timer stateMonitorTimer=new Timer();//��������״̬��ʱ��
	
	PublicMethod publicMethod=new PublicMethod();
	//���ݿ�
	DisplacementMonitorPointDB displacementMonitorPointDB;
	DisplacementMeasuredUnitDB displacementMeasuredUnitDB;
	//��������
	DisplacementMonitorPoint monitorPoint;
	//������ԪID
	String measuredUnitId;
	//������
	TextView monitorDepthTextView;
	
	//������Ԫ������ɱ�־λ
	boolean measureUnitIsSetted;
	String measurePointNumSetted;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_displacementmeasuredunitthreesetting);
		
		//��ʼ�����ݿ�
		displacementMonitorPointDB=new DisplacementMonitorPointDB(getApplicationContext(), GlobalVariable.getDataBaseName(), null, 7);
		displacementMeasuredUnitDB=new DisplacementMeasuredUnitDB(getApplicationContext(), GlobalVariable.getDataBaseName(), null, 7);
		//��ʼ��
		serialNumberTv=(TextView)findViewById(R.id.serialNumberTv);
		projectNameTv=(TextView)findViewById(R.id.projectNameTv);
		monitoringPointNumberTv=(TextView)findViewById(R.id.monitoringPointNumberTv);
		monitorDepthTv=(TextView)findViewById(R.id.monitorDepthTv);
		unitSpacingTv=(TextView)findViewById(R.id.unitSpacingTv);
		
	    unitIdTv=(TextView)findViewById(R.id.unitIdTv);
	    
	    lastLl=(LinearLayout)findViewById(R.id.lastLl);
	    nextLl=(LinearLayout)findViewById(R.id.nextLl);
	    monitorDepthTextView=(TextView)findViewById(R.id.monitorDepthTextView);
	    
        //����
 		backTv=(TextView)findViewById(R.id.backTv);
 		backTv.setOnClickListener(bottomListener);
 		confirmLl=(LinearLayout)findViewById(R.id.confirmLl);
 		
	    measureUnitIsSetted=false;

		//����������
	    Intent gattServiceIntent = new Intent(DisplacementMeasuredUnitSettingThreeActivity.this, BluetoothLeService.class);
	    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	    
	    
	    //����״̬
	    connectedWaitingSettingTv=(TextView)findViewById(R.id.connectedWaitingSettingTv);

	    paraUploadtimer.schedule(new paraUploadTimerTask(), 500, 1000); // 1s��ִ��task,����500ms�ٴ�ִ��  
		stateMonitorTimer.schedule(new monitorStateTimerTask(), 500,1200);//0.5s��ִ��task,����1000s�ٴ�ִ��
		
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
				if (serialNumber<1)//˵���Ѿ�������ɣ���ʱ��ʾ���Ϊ1��ֵ
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
		
		//���ð�ť
		confirmLl.setOnClickListener(new View.OnClickListener()
			{
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				
				final int serialNumber =Integer.parseInt(serialNumberTv.getText().toString());
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�������ڸ�ʽ
				float monitorDepth=Float.parseFloat(monitorDepthTextView.getText().toString());
				
				if (connectedWaitingSettingTv.getText()!=null && connectedWaitingSettingTv.getText().equals(getResources().getString(R.string.setupCompleted))) {
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.measurementUnitNotReplaced),Toast.LENGTH_SHORT).show();
					return;
				}
				//������
				if (connectedWaitingSettingTv.getText().equals(getResources().getString(R.string.connectedWaitingSettings))) {
			
					final DisplacementMeasuredUnit hmu=new DisplacementMeasuredUnit(monitorPoint.getId(), serialNumber, measuredUnitId, monitorDepth,df.format(new Date()));

					boolean isSerialNumberExisted=displacementMeasuredUnitDB.selectMeasureUnitAccordingToSerialNumber(monitorPoint.getId(), serialNumber);//true Ϊ���ڣ�falseΪ������,�ж�����Ƿ����
					boolean isMeasurePointNumberExisted=displacementMeasuredUnitDB.selectMeasureUnitAccordingToMeasureUnitId(monitorPoint.getId(), measuredUnitId);//trueΪ���ڣ�falseΪ�����ڣ��жϲ�����ԪID�Ƿ����
					//�������֮һ������Ѵ��ڡ�������ԪID�Ѵ���
					if (isSerialNumberExisted && isMeasurePointNumberExisted) {
						//����AlertDialog.Builder���󣬵������б����ʱ�򵯳�ȷ��ɾ���Ի���
						AlertDialog.Builder builder=new Builder(DisplacementMeasuredUnitSettingThreeActivity.this);
						builder.setMessage(R.string.serialNumberAndMeasurePointNumAlreadyExist);
						builder.setTitle(R.string.note);
						//���AlertDialog.Builder�����setPositiveButton()����
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
						//���AlertDialog.Builder�����setNegativeButton()����
						builder.setNegativeButton(R.string.cancel, new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								return ;
							}
						});
						builder.create().show();
					}
					//������
//					if (displacementMeasuredUnitDB.selectMeasureUnitAccordingToDepth(monitorPoint.getId(), monitorDepth)) {
//						Toast.makeText(getApplicationContext(), getResources().getString(R.string.monitorDepthExist), Toast.LENGTH_SHORT).show();
//						return;
//					}
							
					//�������֮����������ԪID�Ѵ��ڣ���Ų�����
					else if (!isSerialNumberExisted && isMeasurePointNumberExisted) {
						
						//����AlertDialog.Builder���󣬵������б����ʱ�򵯳�ȷ��ɾ���Ի���   
		                AlertDialog.Builder builder=new Builder(DisplacementMeasuredUnitSettingThreeActivity.this);  
		                builder.setMessage(R.string.measurePointNumAlreadyExisConnect);  
		                builder.setTitle(R.string.note);  
		                  
		                //���AlertDialog.Builder�����setPositiveButton()����   
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
		                  
		                //���AlertDialog.Builder�����setNegativeButton()����   
		                builder.setNegativeButton(R.string.cancel, new OnClickListener() {  
		                      
		                    @Override  
		                    public void onClick(DialogInterface dialog, int which) {  
		                        return ;
		                    }  
		                });  
		                  
		                builder.create().show();  
					}
					//�������֮��������Ѵ��ڣ�������ԪID������
					else if (isSerialNumberExisted  && !isMeasurePointNumberExisted)
					{
						AlertDialog.Builder builder=new Builder(DisplacementMeasuredUnitSettingThreeActivity.this);
						builder.setMessage(R.string.serialNumberAlreadyExisConnect);
						builder.setTitle(R.string.note);
						//���AlertDialog.Builder�����setPositiveButton()����
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
						//���AlertDialog.Builder�����setNegativeButton()����
						builder.setNegativeButton(R.string.cancel, new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								return ;
							}
						});
						builder.create().show();
					}
					//�������֮�ģ���Ų����ڣ�������ԪID������
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
				//δ����
				else if (connectedWaitingSettingTv.getText().equals(getResources().getString(R.string.conntentMeasueUnit))) {
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.measurementUnitNotConnected),	android.widget.Toast.LENGTH_SHORT).show();
				}
			}});
		//��һ��
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
		
		//��һ��
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
	//��������ָ��
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
              // ��Ҫ������:������Ϣ  
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
			if (!unitConnect) {//δ����
				i=i+1;
				if (i>3) {
					i=0;
					Message message=new Message();
					message.what=2;
					handler.sendMessage(message);
				}
			}
			else//������
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
		  			//�����ϴ� 20180417�޸ġ���������ȥ���¶�����ֵ��������70�ĳ�62
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
				  			
				  			LogUtil.i(TAG, "ͷβ��ȷ");
				  			if (!measureUnitIsSetted) {
					  			connectedWaitingSettingTv.setText(R.string.connectedWaitingSettings);
							}
				  			LogUtil.i(TAG, "measuredUnitId:"+measuredUnitId);
				  			unitConnect=true;
				  			unitIdTv.setText(measuredUnitId);
				  			data2="";
				  		}
					}
			  		//�û��趨
			  		else if (diffPosition==18) {
		  				LogUtil.i(TAG, "ͷβ��ȷ");
		  				//20180515������δ����
		  				String stateStr=data2.substring(16+headPosition,18+headPosition);
		  				if(stateStr!=null && stateStr.equals("00"))
						{
							//���
							String serialNumberStr=data2.substring(6+headPosition,10+headPosition);
							byte[] serialNumberByte=publicMethod.HexStringToByteArray(serialNumberStr);
							int serialNumber=serialNumberByte[0]*256+serialNumberByte[1];

							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�������ڸ�ʽ
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
								paraUploadtimer.schedule(new paraUploadTimerTask(), 500, 1000); // 1s��ִ��task,����1000ms�ٴ�ִ��
							}
							if (stateMonitorTimer==null) {
								stateMonitorTimer=new Timer();
								stateMonitorTimer.schedule(new monitorStateTimerTask(), 500,1200);//0.5s��ִ��task,����1000s�ٴ�ִ��
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
//		  				else if(state=="01")//�豸���ʹ������
//						{
//							Toast.makeText(getApplicationContext(), getResources().getString(R.string.deviceCodeError),Toast.LENGTH_SHORT).show();
//							measureUnitIsSetted=false;
//							data2="";
//						}
//						else if(state=="02")//�������������
//						{
//							Toast.makeText(getApplicationContext(), getResources().getString(R.string.examOrganCodeError),Toast.LENGTH_SHORT).show();
//							measureUnitIsSetted=false;
//							data2="";
//						}
//						else if(state=="03")//����������
//						{
//							Toast.makeText(getApplicationContext(), getResources().getString(R.string.sensorLockError),Toast.LENGTH_SHORT).show();
//							measureUnitIsSetted=false;
//							data2="";
//						}
//						else if(state=="04")//����������
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
		//�ж�������ֵ�Ƿ�Ϊ�ܲ�����Ԫ����
		long count=displacementMeasuredUnitDB.selectCountAccordingToMonitorNetworkNumberId(monitorPoint.getId());
		int totalCount=(int) (monitorPoint.getMonitorDepth()/monitorPoint.getUnitSpacing());
		//û���������
		if (count<totalCount) {
			AlertDialog.Builder builder=new Builder(DisplacementMeasuredUnitSettingThreeActivity.this);
			builder.setMessage(getResources().getString(R.string.alreadySetted)+count+getResources().getString(R.string.lack)
					+String.valueOf(totalCount-count)+getResources().getString(R.string.numberMeasureUnit)+"\r\n"
					+getResources().getString(R.string.currentMonitoringNetworkSetupHasNotBeenCompleted));

			builder.setTitle(R.string.warn);
			//���AlertDialog.Builder�����setPositiveButton()����
			builder.setPositiveButton(R.string.continueSet, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			});
			//���AlertDialog.Builder�����setNegativeButton()����
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
     * �������ؼ�
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//������������    
    	public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv) {
        		back();
			}
		}
    }; 
    
    /**
     * �����࣬��д��onServiceConnected��onServiceDisconnected����
     */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

    	//�ڻ�����ɹ��󶨵�ʱ�����
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (mBluetoothLeService==null) {
				LogUtil.i(TAG, "mBluetoothService Ϊ��");
			}
            else
            {
            	LogUtil.i(TAG, "mBluetoothService ��Ϊ��");
            }
        }
        //�ڻ��������ӶϿ���ʱ�����
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
    
    
	protected void onResume() {//��APPʱɨ���豸
		super.onResume();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}
	
	protected void onRestart()
	{
		super.onRestart();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		if (paraUploadtimer==null) {
				paraUploadtimer=new Timer();
  		    paraUploadtimer.schedule(new paraUploadTimerTask(), 500, 1000); // 1s��ִ��task,����1000ms�ٴ�ִ��  
		}
			if (stateMonitorTimer==null) {
			    stateMonitorTimer=new Timer();
  			stateMonitorTimer.schedule(new monitorStateTimerTask(), 500,1200);//0.5s��ִ��task,����1000s�ٴ�ִ��
		}
		
		//����������
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

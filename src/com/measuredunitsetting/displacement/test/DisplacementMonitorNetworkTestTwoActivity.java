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
	private BluetoothLeService mBluetoothLeService;//�������ӷ���
	TextView backTv;
	LinearLayout backLl;
	//�����ID
	int monitorNetworkId;
	//������Ԫ�ܸ���
	int totalUnitNumber;
	DisplacementMeasuredUnitDB displacementMeasuredUnitDB;
	DisplacementMonitorPointDB displacementMonitorPointDB;
	//���̱��
	TextView projectNameTv;
	//������
	TextView monitorPointNumberTv;
	//������
	TextView monitorDepthTv;
	//������Ԫ���
	TextView unitSpacingTv;
	//������Ԫ����
	TextView measureTotalNumberTv;
	//�����ò�����Ԫ����
	TextView measurementUnitHasBeenSettedTv;
	//�������Ӳ�����Ԫ
	TextView tryConnectMeasurementUnitTV;
	//���Բɼ�����
	TextView tryCollectDataTV;
	//���Խ����ʾ
	TextView testResultTv;
	//��ʱ��
	Timer paraUploadtimer = new Timer(); //�����ϴ���ʱ��
	Timer monitorParaUploadTimer=new Timer();//���Ӳ����ϴ���ʱ��
	DisplacementMonitorPoint monitorPoint;
	//������Ԫ��Ϣ
	DisplacementMeasuredUnitAdapter measuredUnitAdapter;
    private List<DisplacementMeasuredUnit> measureUnitList=new ArrayList<DisplacementMeasuredUnit>(); 
    ListView measureUnitLv;
  
    
    //������Ԫ���
    DisplacementMeasuredResultAdapter measureUnitResultAdapter;
    private List<DisplacementMeasuredResult> displacementMeasuredUnitResultList=new ArrayList<DisplacementMeasuredResult>();
    ListView measureUnitResultLv;
    //������Ԫʧ�ܵĵ�Ԫ
    ListView measureUnitFailLv;
    DisplacementMeasuredUnitAdapter measuredUnitAdapters;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_displacementmonitorpointtesttwo);
		//����
		backTv=(TextView)findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
		//����
		backLl=(LinearLayout)findViewById(R.id.backLl);
		backLl.setOnClickListener(bottomListener);
		
		//��ʼ��
		monitorNetworkId=getIntent().getIntExtra("monitorNetworkId", 0);
		//��ʼ�����ݿ�
		displacementMeasuredUnitDB=new DisplacementMeasuredUnitDB(getApplicationContext(),GlobalVariable.getDataBaseName(), null, 7);
		displacementMonitorPointDB=new DisplacementMonitorPointDB(getApplicationContext(),GlobalVariable.getDataBaseName() , null, 7);
		//���̱��
		projectNameTv=(TextView)findViewById(R.id.projectNameTv);
		//������
		monitorPointNumberTv=(TextView)findViewById(R.id.monitorPointNumberTv);
		//������
		monitorDepthTv=(TextView)findViewById(R.id.monitorDepthTv);
		//������Ԫ���
		unitSpacingTv=(TextView)findViewById(R.id.unitSpacingTv);
		//������Ԫ����
		measureTotalNumberTv=(TextView)findViewById(R.id.measureTotalNumberTv);
		//�����ò�����Ԫ����
		measurementUnitHasBeenSettedTv=(TextView)findViewById(R.id.measurementUnitHasBeenSettedTv);
		//���Ӳ�����Ԫ
		tryConnectMeasurementUnitTV=(TextView)findViewById(R.id.tryConnectMeasurementUnitTV);
		//���Բɼ�����
		tryCollectDataTV=(TextView)findViewById(R.id.tryCollectDataTV);
		//���Խ����ʾ
		testResultTv=(TextView)findViewById(R.id.testResultTv);
		testResultTv.setMovementMethod(ScrollingMovementMethod.getInstance()); 
		//��ѯ�������Ϣ
		monitorPoint=displacementMonitorPointDB.selectAccordingToMonitorPointId(monitorNetworkId,GlobalVariable.getUserId());
		//������Ԫ����ʧ��
		measureUnitFailLv=(ListView)findViewById(R.id.measureUnitFailLv);
		
		//��ѯ������Ԫ����
		long measuredUnitCount=displacementMeasuredUnitDB.selectCountAccordingToMonitorNetworkNumberId(monitorPoint.getId());
		if (monitorPoint!=null) {
			projectNameTv.setText(monitorPoint.getProjectName());
			monitorPointNumberTv.setText(monitorPoint.getMonitorPointNumber());
			monitorDepthTv.setText(String.valueOf(monitorPoint.getMonitorDepth()));
			unitSpacingTv.setText(String.valueOf(monitorPoint.getUnitSpacing()));
			measureTotalNumberTv.setText(String.valueOf((int)(monitorPoint.getMonitorDepth()/monitorPoint.getUnitSpacing())));
			measurementUnitHasBeenSettedTv.setText(String.valueOf(measuredUnitCount));
		}
		
		
		//����������
	    Intent gattServiceIntent = new Intent(DisplacementMonitorNetworkTestTwoActivity.this, BluetoothLeService.class);
	    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	    //������ʱ��
	    paraUploadtimer.schedule(new paraUploadTimerTask(), 200, 10000); // 1s��ִ��task,����500ms�ٴ�ִ��  
	    monitorParaUploadTimer.schedule(new monitorParaUploadTimerTask(),(long) (monitorPoint.getMonitorDepth()/monitorPoint.getUnitSpacing())*100+500,13000+(long)(monitorPoint.getMonitorDepth()/monitorPoint.getUnitSpacing())*100);
	    
		measuredUnitAdapter=new DisplacementMeasuredUnitAdapter(DisplacementMonitorNetworkTestTwoActivity.this, R.layout.serialnumberdepthid_item, measureUnitList);
	    View vhead=View.inflate(this, R.layout.serialnumberdepthid_item_head, null);
		measureUnitLv=(ListView) findViewById(R.id.measureUnitLv);
		measureUnitLv.addHeaderView(vhead);
	    measureUnitLv.setAdapter(measuredUnitAdapter);

	    
	    //������Ԫ���
	    measureUnitResultAdapter=new DisplacementMeasuredResultAdapter(DisplacementMonitorNetworkTestTwoActivity.this, R.layout.displacementmeasuredresult_item, displacementMeasuredUnitResultList);
	    View vh=View.inflate(this, R.layout.displacementmeasuredresult_item_head, null);
	    measureUnitResultLv=(ListView) findViewById(R.id.measureUnitResultLv);
	    measureUnitResultLv.addHeaderView(vh);
	    measureUnitResultLv.setAdapter(measureUnitResultAdapter);

	}
	
	/**
	 * �����ϴ�
	 * @author Administrator
	 *
	 */
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
	    int count=0;
	    class monitorParaUploadTimerTask extends TimerTask
	    {
	    	  @Override  
	          public void run() {  
	              // ��Ҫ������:������Ϣ  
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
	    

		//��������ָ��
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
	        	else if (msg.what==3) {//�ɼ�ʱ�䵽���жϲɼ���
	        		if (monitorParaUploadTimer!=null) {
						monitorParaUploadTimer.cancel();
						monitorParaUploadTimer=null;
					}
	        		//����
	        		if (displacementMeasuredUnitResultList.size()==monitorPoint.getMonitorDepth()/monitorPoint.getUnitSpacing() && measureUnitList.size()==monitorPoint.getMonitorDepth()/monitorPoint.getUnitSpacing()) {
						testResultTv.setText(R.string.allMeasurementUnitsNormal);
					}
	        		//ȱ������
	        		else if (displacementMeasuredUnitResultList.size()<(int)(monitorPoint.getMonitorDepth()/monitorPoint.getUnitSpacing()) ) {
						List<Integer> serialNumberLackList=new ArrayList<Integer>();

						//������Ԫ���������ȱ������
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
	        			//������Ԫ��ѯʱȱ������
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
	        				
							//���úõĲ�����Ԫû�н��յ�����
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
							//��û�����õĲ�����Ԫ
							if (serialNumberLackUnsettedList!=null && serialNumberLackUnsettedList.size()!=0) {
								String serialNumberStr="";
								for(int i=0;i<serialNumberLackUnsettedList.size();i++)
								{
									serialNumberStr=serialNumberStr+" "+serialNumberLackUnsettedList.get(i);
								}
								lackInformation=lackInformation+getResources().getString(R.string.serialNumberStr)+serialNumberStr+getResources().getString(R.string.measurementUnitNotSet)+"\r\n"+getResources().getString(R.string.remainingMeasurementUnitsAreNormal);
							}
							//û��û���õĲ�����Ԫ
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
     * �����͵ײ����ؼ�
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//������������    
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

	
	protected void onResume() {//��APPʱɨ���豸
		super.onResume();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}
	
	protected void onRestart()
	{
		super.onRestart();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());	
		//����������
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
		  			//�����ϴ�
					int diffPosition=tailPosition-headPosition;
					if (diffPosition==54||diffPosition==62) {
			  			LogUtil.i(TAG, "canshushangchuan");
			  			//���
		  				String serialNumberStr=data2.substring(headPosition+6,headPosition+10);
		  				byte[] serialNumberByte=publicMethod.HexStringToByteArray(serialNumberStr);
		  				int serialNumber=serialNumberByte[0]*255+serialNumberByte[1];
		  	  			//������ԪID
						String measuredUnitIdStr="";
						if (diffPosition==54 ) {
							measuredUnitIdStr=data2.substring(34+headPosition,54+headPosition);
						}
						else
							measuredUnitIdStr=data2.substring(42+headPosition,62+headPosition);
			  			byte[] measuredUnitIdByte=publicMethod.HexStringToByteArray(measuredUnitIdStr);
			  			String measuredUnitId=new String(measuredUnitIdByte);
			  			//���
		  				String depthStr=data2.substring(10+headPosition,16+headPosition);
						byte[] depthByte=publicMethod.HexStringToByteArray(depthStr);
						float depth=Float.parseFloat(String.valueOf(depthByte[0])+String.valueOf(depthByte[1])+"."+String.valueOf(depthByte[2]));

			  			DisplacementMeasuredUnit displacementMeasuredUnit=new DisplacementMeasuredUnit(serialNumber, depth, measuredUnitId);
			  			measureUnitList.add(displacementMeasuredUnit);
			  			
			  		    //������Ԫ����
			  			measuredUnitAdapter=new DisplacementMeasuredUnitAdapter(DisplacementMonitorNetworkTestTwoActivity.this, R.layout.serialnumberdepthid_item, measureUnitList);
			  		
			  		    measureUnitLv.setAdapter(measuredUnitAdapter);
			  		    //ֹͣ���Ͳ����ϴ�ָ��
			  		    if (paraUploadtimer!=null) {
							paraUploadtimer.cancel();
							paraUploadtimer=null;
						}
			  			data2="";
			  			tryConnectMeasurementUnitTV.setText(R.string.tryConnectMeasurementUnitOK);
				  		
					}
			  		//�ɼ�
			  		else if (tailPosition-headPosition==50) {
		  				LogUtil.i(TAG, "caiji");

		  				//id
		  				//���
		  				String serialNumberStr=data2.substring(headPosition+6,headPosition+10);
		  				byte[] serialNumberByte=publicMethod.HexStringToByteArray(serialNumberStr);
		  				int serialNumber=serialNumberByte[0]*255+serialNumberByte[1];
		  				//���
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
		  				//�¶�
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
			  		//�������
			  		else if (tailPosition-headPosition==18) {
						String stateStr=data2.substring(headPosition+16,headPosition+18);
						if (stateStr!=null && stateStr.equals("01")) {//�豸���ʹ���
							LogUtil.i(TAG, "�豸���ʹ������");
						}
						else if (stateStr!=null && stateStr.equals("02")) {//�������������
							LogUtil.i(TAG, "�������������");
						}
						else if (stateStr!=null && stateStr.equals("03")) {//����
							LogUtil.i(TAG, "����");
						}
						else if (stateStr!=null && stateStr.equals("04")) {
							LogUtil.i(TAG, "����������");
						}
						else if (stateStr!=null && stateStr.equals("05")) {
							LogUtil.i(TAG, "�������������ǽ���״̬");
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

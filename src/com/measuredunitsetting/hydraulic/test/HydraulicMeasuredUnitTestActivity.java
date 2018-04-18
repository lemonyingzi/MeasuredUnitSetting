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

	private BluetoothLeService mBluetoothLeService;//�������ӷ���
	PublicMethod publicMethod=new PublicMethod();

	//������ԪID
	String measuredUnitId;
	float calibrationCoeff;
	//��ʱ��
	Timer paraUploadtimer = new Timer(); //�����ϴ���ʱ��
	Timer collecttimer;//�ɼ���ʱ��
	//���Ӳ�����ԪOK
	TextView connectMeasureUnitOKTv;
	//���ڲɼ�����
	TextView collectingTv;
	//�ȴ���������
	TextView waitDataBackOKTv;
	
	//ID
	TextView IDTv;
	//���
	TextView serialNumberTv;
	//�����
	TextView measurePointNumTv;
	//�������
	TextView measureTypeTv;
	//ѹ��
	TextView pressureTv;
	//�¶�
	TextView temperatureTv;
	//�궨ϵ�� 
	TextView calibrationCoeffTv;
	//���Խ��
	TextView resultTv;
	TextView exceptionResultTv;
	String originalMeasureUnitId=null;
	boolean isOverTime=false;

	protected void onCreate(Bundle savedInstanceState) {
		TextView backTv;
		LinearLayout backLl;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hydraulicmeasuredunittest);
	
		//����
		backTv=(TextView)findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
		//�ײ�����
		backLl=(LinearLayout)findViewById(R.id.backLl);
		backLl.setOnClickListener(bottomListener);
		//���Ӳ�����ԪOK
		connectMeasureUnitOKTv=(TextView)findViewById(R.id.connectMeasureUnitOKTv);
		//���ڲɼ�����
		collectingTv=(TextView)findViewById(R.id.collectingTv);
		//�ȴ���������
		waitDataBackOKTv=(TextView)findViewById(R.id.waitDataBackOKTv);
		//ID
		IDTv=(TextView)findViewById(R.id.IDTv);
		//���
		serialNumberTv=(TextView)findViewById(R.id.serialNumberTv);
		//�����
		measurePointNumTv=(TextView)findViewById(R.id.measurePointNumTv);
		//�������
		measureTypeTv=(TextView)findViewById(R.id.measureTypeTv);
		//ѹ��
		pressureTv=(TextView)findViewById(R.id.pressureTv);
		//�¶�
		temperatureTv=(TextView)findViewById(R.id.temperatureTv);
		//�궨ϵ��
		calibrationCoeffTv=(TextView)findViewById(R.id.calibrationCoeffTv);
		//���Խ��
		resultTv=(TextView)findViewById(R.id.resultTv);
		exceptionResultTv=(TextView)findViewById(R.id.exceptionResultTv);
		//����������
	    Intent gattServiceIntent = new Intent(HydraulicMeasuredUnitTestActivity.this, BluetoothLeService.class);
	    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	    
	    paraUploadtimer.schedule(new paraUploadTimerTask(), 500, 1000); // 1s��ִ��task,����500ms�ٴ�ִ��  
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
	  /**
	   * ���Ӳɼ��Ƿ�ʱ
	   * @author Administrator
	   *
	   */
	  class collectTimerTask extends TimerTask
	  {
		  @Override  
          public void run() {  
			if (!isOverTime) {//��ʱ
				Message message=new Message();
				message.what=3;
				handler.sendMessage(message);
			}
          }  
	  };
	    
		//��������ָ��
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
	        	else if (msg.what==3) {//��ʱ
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

    
	protected void onRestart()
	{
		super.onRestart();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		if (paraUploadtimer==null) {
				paraUploadtimer=new Timer();
  		    paraUploadtimer.schedule(new paraUploadTimerTask(), 500, 1000); // 1s��ִ��task,����1000ms�ٴ�ִ��  
		}
	
		//����������
	    Intent gattServiceIntent = new Intent(HydraulicMeasuredUnitTestActivity.this, BluetoothLeService.class);
	    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	}
	
    /**
     * �����͵ײ����ؼ�
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//������������    
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
	
	
	protected void onResume() {//��APPʱɨ���豸
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
		  			//�����ϴ�
			  		if (tailPosition-headPosition==62) {
				  		if(headPosition!=-1 && tailPosition!=-1)
				  		{
				  			LogUtil.i(TAG, "head and tail correct");
							connectMeasureUnitOKTv.setText(R.string.connectMeasureUnitOK);

//							//���
			  				String serialNumberStr=data2.substring(headPosition+6,headPosition+10);
			  				byte[] serialNumberByte=publicMethod.HexStringToByteArray(serialNumberStr);
			  				int serialNumber=serialNumberByte[0]*255+serialNumberByte[1];
				  			
				  			//������ԪID
				  			String measuredUnitIdStr=data2.substring(30+headPosition,50+headPosition);	  	
				  			byte[] measuredUnitIdByte=publicMethod.HexStringToByteArray(measuredUnitIdStr);
				  			measuredUnitId=new String(measuredUnitIdByte);
				  		
				  			//�궨ϵ��
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

							LogUtil.i(TAG, "���Ͳɼ�ָ��");
							isOverTime=false;
							//�����ɼ�����
							if (collecttimer==null) {
								collecttimer=new Timer();
								collecttimer.schedule(new collectTimerTask(),(long) (10000+serialNumber*100),(long) (10000+serialNumber*100));
							}
				  			
				  		}
					}
			  		//�ɼ�
			  		else if (tailPosition-headPosition==46) {
		  				LogUtil.i(TAG, "ͷβ��ȷ");
		  				//id
		  				IDTv.setText(measuredUnitId);
		  				//���
		  				String serialNumberStr=data2.substring(headPosition+6,headPosition+10);
		  				byte[] serialNumberByte=publicMethod.HexStringToByteArray(serialNumberStr);
		  				int serialNumber=serialNumberByte[0]*255+serialNumberByte[1];
		  				serialNumberTv.setText(String.valueOf(serialNumber));
		  				//�����
		  				String measurePointNumStr=data2.substring(headPosition+10,headPosition+26);
		  				byte[] measurePointNumByte=publicMethod.HexStringToByteArray(measurePointNumStr);
		  				String measurePointNum=new String(measurePointNumByte);
		  				measurePointNumTv.setText(measurePointNum);
		  				//�������
		  				String measureTypeStr=data2.substring(headPosition+26,headPosition+28);
		  				if (measureTypeStr!=null && measureTypeStr.equals("A0")) {
							measureTypeTv.setText(getResources().getString(R.string.benchmark));
						}
		  				else if (measureTypeStr!=null && measureTypeStr.equals("B0")) {
							measureTypeTv.setText(getResources().getString(R.string.commonMeasurePoint));
						}
		  				//ѹ��
		  				String pressureStr=data2.substring(headPosition+30,headPosition+38);
		  				byte[] pressureByte=publicMethod.HexStringToByteArray(pressureStr);
		  				float pressureFloat=publicMethod.byte2float(pressureByte, 0);
						DecimalFormat decimalFormat=new DecimalFormat(".0");//���췽�����ַ���ʽ�������С������2λ,����0����.
						String pf=decimalFormat.format(pressureFloat);//format ���ص����ַ���
		  				pressureTv.setText(pf+getResources().getString(R.string.mmwaterColumn));
		  				//�¶�
		  				String temperatureStr=data2.substring(headPosition+38,headPosition+46);
		  				byte[] temperatureByte=publicMethod.HexStringToByteArray(temperatureStr);
		  				float temperatureFloat=publicMethod.byte2float(temperatureByte, 0);
		  				String tf=decimalFormat.format(pressureFloat);
		  				temperatureTv.setText(tf+getResources().getString(R.string.degree));
		  				//�궨ϵ��
						String cc=decimalFormat.format(calibrationCoeff);
		  				calibrationCoeffTv.setText(cc);
		  				//���Խ��
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
			  		//�������
			  		else if (tailPosition-headPosition==28) {
						String stateStr=data2.substring(headPosition+26,headPosition+28);
						if (stateStr!=null && stateStr.equals("01")) {//�豸���ʹ���
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
						else if (stateStr!=null && stateStr.equals("02")) {//�û�ID����
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
						else if (stateStr!=null && stateStr.equals("03")) {//����
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

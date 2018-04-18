package com.measuredunitsetting;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;
import com.measuredunitsetting.db.BleDeviceDB;
import com.measuredunitsetting.db.UserDB;
import com.measuredunitsetting.displacement.DisplacementActivity;
import com.measuredunitsetting.entity.Bluetooth;
import com.measuredunitsetting.entity.LoginResult;
import com.measuredunitsetting.global.GlobalVariable;
import com.measuredunitsetting.global.LogUtil;
import com.measuredunitsetting.hydraulic.HydraulicActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
public class MainActivity extends android.app.Activity {
	private final static String TAG = MainActivity.class.getSimpleName();
	//���ݿ�
//	BleDeviceDB bleDeviceDB;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeService mBluetoothLeService;
    private boolean mScanning;
    boolean connect_status_bit=false;
    private Handler mHandler;
    SwipeRefreshLayout swipeRefreshLayout;
	//��¼
	LinearLayout loginStateLl;
	TextView loginStateTv;
	//Һѹˮ׼
	LinearLayout hydraulicLevelLL;
	//���ˮƽλ��
	LinearLayout displacementLL;

	//����״̬
	TextView connectStateTv;
	private static final long SCAN_PERIOD = 10000;
	private static String newBindAddress=null;//ԭʼ��ʱ�������ַ
	//δ����
	LinearLayout conntectStateLL;
	
	Timer scanTimer=null;
	UserDB userDB=null;
	List<String> nameAndToken=new ArrayList<String>();
	ArrayList<BluetoothDevice> bleList=new ArrayList<>();
	@SuppressWarnings("static-access")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//���ݿ�
//		bleDeviceDB=new BleDeviceDB(getApplicationContext(),GlobalVariable.getDataBaseName(),null,7);
		userDB=new UserDB(getApplicationContext(), GlobalVariable.getDataBaseName(), null, 7);
		
		//��¼״̬
		loginStateTv=(TextView)findViewById(R.id.loginStateTv);
		
		//�����ݿ��ѯ���е�¼
		nameAndToken=userDB.getLastLoginUser();
		//������ݿ������û��������룬�����û�����������е�¼
		if (nameAndToken!=null && nameAndToken.size()==2) {
			new Thread(runnable).start();
		}
		
        mHandler = new Handler();
        //����״̬
        connectStateTv=(TextView)findViewById(R.id.connectStateTv);
		//��¼
		loginStateLl=(LinearLayout)findViewById(R.id.loginStateLL);//������ʱģʽ
		loginStateLl.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		Intent intent=new Intent(MainActivity.this,LoginActivity.class);
	    		startActivityForResult(intent, 2);
				startActivity(intent);
	    		}
	        });
		
		//Һѹˮ׼
		hydraulicLevelLL=(LinearLayout)findViewById(R.id.hydraulicLevelLL);
		hydraulicLevelLL.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//��״̬���ݹ�ȥ
				Intent intent=new Intent(MainActivity.this,HydraulicActivity.class);
				intent.putExtra("connectState", connectStateTv.getText());
				startActivity(intent);
			}
			
		});
		
		//���ˮƽλ��
		displacementLL=(LinearLayout)findViewById(R.id.displacementLL);
		displacementLL.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View arg0) {
				//��״̬���ݹ�ȥ
				Intent intent=new Intent(MainActivity.this,DisplacementActivity.class);
				intent.putExtra("connectState", connectStateTv.getText());
				startActivity(intent);
				
			}
	
		});
       //����״̬
		conntectStateLL=(LinearLayout) findViewById(R.id.conntectStateLL);
		conntectStateLL.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0) {
//				if(connectStateTv.getText().equals(getResources().getString(R.string.unconnected)))
//				{
					Intent intent=new Intent(MainActivity.this,SettersActivity.class);
					Bundle bundle = new Bundle();
					bundle.putParcelableArrayList("bleList", bleList);
					intent.putExtras(bundle);
					startActivityForResult(intent, 1);//�ȴ��ظ�
//				}
			}
		});
		
		
		   if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
	        		!=PackageManager.PERMISSION_GRANTED) {
					ActivityCompat.requestPermissions(MainActivity.this,
							new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, 1);
			}
	        // Use this check to determine whether BLE is supported on the device.  Then you can
	        // selectively disable BLE-related features.
	        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
	            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
	            finish();
	        }

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
	        }
	        
	              
	        // �����������û�п���������  
	        if (!mBluetoothAdapter.isEnabled()) 
	        {
	            // ����ͨ��startActivityForResult()���������Intent������onActivityResult()�ص������л�ȡ�û���ѡ�񣬱����û�������Yes������  
	            // ��ô�����յ�RESULT_OK�Ľ����  
	            // ���RESULT_CANCELED������û���Ը�⿪������  
	            Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);  
	            startActivityForResult(mIntent, 1);  
	            // ��enable()����������������ѯ���û�(ʵ������Ϣ�Ŀ��������豸),��ʱ����Ҫ�õ�android.permission.BLUETOOTH_ADMINȨ�ޡ�  
	            // mBluetoothAdapter.enable();  
	            // mBluetoothAdapter.disable();//�ر�����  
	        }
	        
	   	 ///////////////////////////����ˢ��//////////////////////////////////////////
		 swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
		 swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
		 swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener(){
			 @Override
			 public void onRefresh() {
				 if(mScanning)
				 {
					 scanLeDevice(true);
				
				 }
					new Handler().postDelayed(new Runnable() {
						public void run() {
							swipeRefreshLayout.setRefreshing(false);
						}
					}, 3000);
			}
		 
		});
		 
		 
		 scanLeDevice(true);

		 scanTimer=new Timer();
		 scanTimer.schedule(new scanTimerTask(), 200,200);
   
	}
	
	//�����̵߳�¼
	Runnable runnable = new Runnable(){
	    @Override
	    public void run() {
	    	LogUtil.i(TAG,"name:"+nameAndToken.get(0)+",token:"+nameAndToken.get(1));
	        String state=NetUilts.loginofPost(GlobalVariable.getLogin(),nameAndToken.get(0), "",nameAndToken.get(1));
	        showResponse(state);
	    }
	};
	
	
	//��ʾ���
	private void showResponse(final String  response)
	{
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				try
				{
					Gson gson=new Gson();
				    LoginResult loginResult=gson.fromJson(response, LoginResult.class);
					if (loginResult!=null && loginResult.getType().equals(GlobalVariable.getLoginSuccess())) {
						loginStateTv.setText(nameAndToken.get(0)+getResources().getString(R.string.logined));
						userDB.update(nameAndToken.get(0),nameAndToken.get(1));
					}
					else
					{
						Toast.makeText(getApplicationContext(),getResources().getString(R.string.loginFail), Toast.LENGTH_SHORT).show();
						userDB.updateLoginFlag();
					}			
				}
				catch(Exception ex)
				{
					LogUtil.e(TAG, ex.toString());
				}
		    	
			}
		});
	}
	
	
	//��������ָ��
    Handler handler = new Handler() {  
        public void handleMessage(Message msg) {  
        	if (msg.what == 1) {  
            	if (mBluetoothLeService != null) {
            				if (!connect_status_bit) {
            					mBluetoothLeService.connect(newBindAddress);
            					show_view(false);
							}	
                		}
            	else
            	{
            		if (!connect_status_bit && !mScanning) {
               		 	scanLeDevice(true);
					}
            	}
        	}
            super.handleMessage(msg);  
        };  
    };  
    
	
   class scanTimerTask extends TimerTask
    {
    	  @Override  
          public void run() {
			  // ��Ҫ������:������Ϣ
			  Message message = new Message();
			  message.what = 1;
			  handler.sendMessage(message);
		  }
    };
	    
	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data)
	{			
		switch(requestCode)
		{
			//�����Ӵ��ڷ���
			case 1:
					if (data!=null)
					{
						newBindAddress=data.getStringExtra("address");
						Intent gattServiceIntent = new Intent(MainActivity.this, BluetoothLeService.class);
						bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
					}
					break;
			//�ӵ�¼���ڷ���ֵ
			case 2:
					//�ӵ�¼���ڷ���ʱ����ѯ
					try
					{
						nameAndToken=userDB.getLastLoginUser();
						if (nameAndToken!=null && nameAndToken.size()==2) {
							new Thread(runnable).start();
						}
					}
					catch (Exception e) {
						LogUtil.d(TAG,"Exception:"+ e.toString());
					}
					break;
			default:
		
		}
	}

	
	
	
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                LogUtil.i(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            boolean connectResult=mBluetoothLeService.connect(newBindAddress);
            if (connectResult) {
            	LogUtil.i(TAG, "initaialize success");
			}
            else
            {
            	LogUtil.i(TAG, "initaialize fail");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        	LogUtil.i(TAG, "���ӶϿ�");
            mBluetoothLeService = null;
        }
        
    };
    
    /**
     * ����ɨ��ص�����
     */
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(device.getName()!=null){
						 if (bleList!=null && bleList.contains(device))//bleList��Ϊ�գ���bleList�Ѿ�������ѯ�����豸
						 {
						 }
						 else
						 {
						 	bleList.add(device);
						 }
				        if (mScanning) {
				            mBluetoothAdapter.stopLeScan(mLeScanCallback);
				            mScanning = false;
				        }
					}
				}
			});
		}
	};
	
	
	
	@Override
	protected void onResume() {//��APPʱɨ���豸
		super.onResume();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		//����������
		if (!GlobalVariable.getBluetoothIsConnected())
		{
			show_view(false);
			GlobalVariable.setBluetoothIsConnected(false);
		}
		if (scanTimer==null) {
			scanTimer=new Timer();
			scanTimer.schedule(new scanTimerTask(), 200,200);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mGattUpdateReceiver);
//		bleList.clear();
	}
	
	
	@Override 
	protected void onDestroy()
	{
		super.onDestroy();
		  try{
			  if (scanTimer!=null) {
					scanTimer.cancel();
					scanTimer=null;
				}
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
                connect_status_bit=true;
//                mConnected=true;
				GlobalVariable.setBluetoothIsConnected(true);
				show_view(true);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                connect_status_bit=false;
				GlobalVariable.setBluetoothIsConnected(false);
				show_view(false);
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
 
    
    
    private void displayData( String data1 ) {
	  	if( data1!=null&&data1.length()>=17)
    	{
	  		try
	  		{
	  			LogUtil.i(TAG, data1);
	  		}
	  		catch(IndexOutOfBoundsException ex)
	  		{
	  			LogUtil.i(TAG,ex.toString());
	  			
	  		}
	  		catch(Exception ex)
	  		{
	  			LogUtil.d(TAG, ex.toString());
	  		}
    	}
	  	else
	  	{
	  		LogUtil.i(TAG, data1);
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
	        if( connect_status_bit )
			  {
			  	GlobalVariable.setBluetoothIsConnected(true);
	        	show_view( true );
				mBluetoothLeService.enable_JDY_ble(true);
				 try {  
			            Thread.currentThread();  
			            Thread.sleep(100);  
			        } catch (InterruptedException e) {  
			            e.printStackTrace();  
			        }  
				 mBluetoothLeService.enable_JDY_ble(true);
			  }else{
				  Toast toast = Toast.makeText(MainActivity.this, R.string.deviceNotConnect, Toast.LENGTH_SHORT); 
				  toast.show(); 
			  }
        }
     
    }
    
	void show_view( boolean p )
    {
    	if(p){
    		connectStateTv.setText(R.string.connected);
    		
    	}else{
    		connectStateTv.setText(R.string.unconnected);
    	}
    }
	
	
    @SuppressWarnings("deprecation")
	private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                    LogUtil.i(TAG, "ÿ��10sִ��һ��");
                }
            }, SCAN_PERIOD);//10s����ô˷��� ֹͣɨ��
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        	
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }
}

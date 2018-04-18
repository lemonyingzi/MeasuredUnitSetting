package com.example.bluetooth.le;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.example.bluetooth.data.GlobalVariable;
import com.example.bluetooth.db.ParaDB;
import com.example.bluetooth.db.SyncTimeDB;
import com.example.bluetooth.db.UserDB;
import com.example.bluetooth.list.BindingBledeviceAdapter;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BindTimerActivity extends Activity{
	private final static String TAG = BindTimerActivity.class.getSimpleName();
	BindingBledeviceAdapter bledeviceAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private BluetoothLeService mBluetoothLeService;
    
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
	private static String newBindName=null;//原始计时器名称
	private static String newBindAddress=null;//原始计时器物理地址
	
	//数据库中存在的原绑定定时器名称和地址
	private static String oldBindName=null;
	private static String oldBindAddress=null;
	
	private static String IrTimerID=null;//IrTimer的id
	private static String IrTimerVersion=null;//IrTimer的版本
    
	Timer timer = new Timer();  
    boolean connect_status_bit=false;
	UserDB userDB;
	SyncTimeDB syncTimeDB;
	ParaDB paraDB;
	ListView listView;
	TextView  backTv;
	private boolean isBinding=false;
    private long exitTime=0;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bindingtimer);

        userDB=new UserDB(getApplicationContext(),"TimerStore",null,7);
        syncTimeDB=new SyncTimeDB(getApplicationContext(),"TimerStore",null,7);
        paraDB=new ParaDB(getApplicationContext(), "TimerStore", null, 7);
        
        backTv=(TextView)findViewById(R.id.backTv);
        backTv.setOnClickListener(bottomListener);
        mHandler = new Handler();
        
        if (ContextCompat.checkSelfPermission(BindTimerActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
        		!=PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(BindTimerActivity.this,
						new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, 1);
		}
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }    
        // 如果本地蓝牙没有开启，则开启  
        if (!mBluetoothAdapter.isEnabled()) 
        {
            // 我们通过startActivityForResult()方法发起的Intent将会在onActivityResult()回调方法中获取用户的选择，比如用户单击了Yes开启，  
            // 那么将会收到RESULT_OK的结果，  
            // 如果RESULT_CANCELED则代表用户不愿意开启蓝牙  
            Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);  
            startActivityForResult(mIntent, 1);  
            // 用enable()方法来开启，无需询问用户(实惠无声息的开启蓝牙设备),这时就需要用到android.permission.BLUETOOTH_ADMIN权限。  
            // mBluetoothAdapter.enable();  
            // mBluetoothAdapter.disable();//关闭蓝牙  
        }
        
        
	    timer.schedule(task, 1000, 200); // 1s后执行task,经过1s再次执行  
	    
		getOriginalBleNameAddress();
		bledeviceAdapter=new BindingBledeviceAdapter(BindTimerActivity.this, R.layout.bindingbledevice_item);
	  	listView=(ListView) findViewById(R.id.ble_list);
	  	listView.setAdapter(bledeviceAdapter);
	    View vhead=View.inflate(this, R.layout.bindingbledevice_item_head, null);
	    listView.addHeaderView(vhead);
	    listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
	    	@Override
	    	public void onItemClick(AdapterView<?> parent,View view,final int position,long id){
	    		if (position>0) {
	    	           AlertDialog.Builder builder=new Builder(BindTimerActivity.this);  
	    	            builder.setMessage(R.string.deletePairedIrTimer);  
	    	            builder.setTitle(R.string.note);  
	    	            //添加AlertDialog.Builder对象的setPositiveButton()方法   
	    	            builder.setPositiveButton(R.string.confirmStr, new OnClickListener() {  
	    	                @Override  
	    	                public void onClick(DialogInterface dialog, int which) {  
	    	                  	BluetoothDevice bluetoothDevice=bledeviceAdapter.getDevice(position-1);
	    					   	//判断是否已存在
	    					   	boolean isExist=userDB.selectNameAndAddress(bluetoothDevice.getName(), bluetoothDevice.getAddress());
	    					    //插入数据库
	    					    if (!isExist) {
	    					    	newBindName=bluetoothDevice.getName();
	    					        newBindAddress=bluetoothDevice.getAddress();
	    					        Log.d(TAG, "select ble address:"+newBindAddress);
	    						    //绑定新的服务
	    					        try
	    					        {
	    					        	Intent gattServiceIntent = new Intent(BindTimerActivity.this, BluetoothLeService.class);
		    						    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
		            		            isBinding=true;
		            		            exitTime=System.currentTimeMillis();
	    					        }
	    					        catch (Exception e) {
										Log.d(TAG, e.toString());
									}
    					            
	    						    Log.d(TAG, "bind service");
	    					        if (mScanning) {
	    					            mBluetoothAdapter.stopLeScan(mLeScanCallback);
	    					            mScanning = false;
	    					        }
	    						}
	    					    else
	    					    {
	    					    	 Toast.makeText(getApplicationContext(), "已绑定", Toast.LENGTH_SHORT).show();
	    					    }
	    	                }  
	    	            });  
	    	              
	    	            //添加AlertDialog.Builder对象的setNegativeButton()方法   
	    	            builder.setNegativeButton(R.string.cancelStr, new OnClickListener() {  
	    	                  
	    	                @Override  
	    	                public void onClick(DialogInterface dialog, int which) {  
	    	                      
	    	                }  
	    	            });  
	    	            builder.create().show();  
				}
	
		    }});

    }
    
    public void onRequestPermissionResult(int requestCode,String[] permissions,int[] grantResults)
    {
    	switch(requestCode)
    	{
    	case 1:
    		if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
				return;
			}
    		else {
				Toast.makeText(this, "you denied the permission", Toast.LENGTH_SHORT).show();
			}
    		break;
    		default:
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
        		if (isBinding) 
            	{
                    Toast.makeText(getApplicationContext(), R.string.bindingNotQuit,Toast.LENGTH_SHORT).show();
    			}
            	else if (!isBinding) {
            		((Activity)BindTimerActivity.this).finish();
    			}

			}
		}
    };  
    

    private void getOriginalBleNameAddress()
    {
		//查询数据库中的已保存的蓝牙设备名称和MAC地址
        List<String> blenameaddress=new ArrayList<String>();
        blenameaddress=userDB.getBleNameAndBleAddress();
        if (blenameaddress!=null && blenameaddress.size()!=0) {
			oldBindName=blenameaddress.get(0);
			oldBindAddress=blenameaddress.get(1);
		}
    }
    /**
     * 匿名类，重写了onServiceConnected和onServiceDisconnected方法
     */
//    private final ServiceConnection mGetServiceConnection = new ServiceConnection() {
//    	//在活动与服务成功绑定的时候调用
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder service) {
//            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
//            if (mBluetoothLeService!=null) {
//            	 Log.d(TAG, "mBluetoothService不为空");
//			}
//           
//        }
//        //在活动与服务连接断开的时候调用
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//            mBluetoothLeService = null;
//        }
//    };
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.d(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(newBindAddress);
            Log.d(TAG, "initaialize success");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        	Log.d(TAG, "连接断开");
            mBluetoothLeService = null;
        }
        
    };
    
    //////////////////按两次返回键退出程序///////////////////////////////////
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	if (isBinding) 
        	{
                Toast.makeText(getApplicationContext(), R.string.bindingNotQuit,Toast.LENGTH_SHORT).show();
			}
        	else if (!isBinding) {
        		exitTime=0;
				finish();
				System.exit(0);
			}
        	if ((System.currentTimeMillis()-exitTime>10000)&& exitTime!=0) {
        		exitTime=0;
        		isBinding=false;
				finish();
				System.exit(0);
			}
        	return false;
        }
        return super.onKeyDown(keyCode, event);
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
            	Log.d(TAG, "broadcastreceiver connected");
                connect_status_bit=true;
//                mConnected=true;
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
            	GlobalVariable.setConnected(false);
                GlobalVariable.setHanded(false);
                connect_status_bit=false;
                invalidateOptionsMenu();
                Log.d(TAG, "broadcastreceiver disconnected");
//                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
            	Log.d(TAG, "services discovered");
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            	Log.d(TAG, "display data");
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null){
        	Log.d(TAG, "gattServices is null");
        	return;
        } 
        else 
        {
        	 Log.d(TAG, "gattServices size:"+gattServices.size());
        }
     
        if( gattServices.size()>0&&mBluetoothLeService.get_connected_status( gattServices )>=4 )
        {
	        if( connect_status_bit )
			  {
	        	GlobalVariable.setConnected(true);
	        	GlobalVariable.setHanded(true);
				mBluetoothLeService.enable_JDY_ble(true);
				 try {  
			            Thread.currentThread();  
			            Thread.sleep(100);  
			        } catch (InterruptedException e) {  
			            e.printStackTrace();  
			        }  
				 mBluetoothLeService.enable_JDY_ble(true);
			  }else{
				  Toast toast = Toast.makeText(BindTimerActivity.this, R.string.deviceNotConnect, Toast.LENGTH_SHORT); 
				  toast.show(); 
			  }
	        
        }
     
    }

	//发送蓝牙指令
    Handler handler = new Handler() {  
        public void handleMessage(Message msg) {  
        	if (msg.what == 1) {  
            	if (mBluetoothLeService != null) {
            		GlobalVariable.setHandCount(GlobalVariable.getHandCount()+1);
            		GlobalVariable.setConnectCount(GlobalVariable.getConnectCount()+1);
                	if( GlobalVariable.getConnected()==false && newBindAddress!=null)//一秒钟连接一次
                	{
                		if (GlobalVariable.getConnectCount() >=10) {
                			Log.d(TAG, "connect device:"+newBindAddress);
                      		mBluetoothLeService.connect(newBindAddress);
                      		GlobalVariable.setConnectCount(0);
						}
          
                	}
                	else if(GlobalVariable.getConnected()==true && GlobalVariable.getHanded()==true && newBindAddress!=null)//握手和同步时钟
                	{
            			if(GlobalVariable.getHandCount()>=30)
            			{
            				try
            				{
            					mBluetoothLeService.t_data_hand_sync_clock();
                    			Log.d(TAG, "handed and sync clock");
                    			GlobalVariable.setHandCount(0);
            				}
            				catch (Exception e) {
								Log.d(TAG, e.toString());
								GlobalVariable.setConnected(false);
							}
            			}
                	}
                }
        	}
            super.handleMessage(msg);  
        };  
    };  
    
    String da="";
    int len_g = 0;
    String data2="";
    byte[] syncData=new byte[18];
    private void displayData( String data1 ) {
	  	if( data1!=null&&data1.length()>=12)
    	{
	  		try
	  		{
	  		
	  			data2=data2+data1;
	  			Log.d(TAG, data2);
	  		  	String head=data2.substring(0,8);//头
		  		String tail=data2.substring(data2.length()-6,data2.length());//尾
		  		String length=data2.substring(8,10);//长度
		  		String order=data2.substring(10,12);//命令
		  		if(head.equalsIgnoreCase("4A534B46") && tail.equalsIgnoreCase("534A4B"))
		  		{
		  			if(length.equalsIgnoreCase("14") && order.equalsIgnoreCase("01"))//握手同步时钟
		  			{
		  				GlobalVariable.setIrTimerID(data2.substring(20, 28));
		  				String version1Str=data2.substring(28,30);
		  				String version2Str=data2.substring(30,32);
		  				String version3Str=data2.substring(32,34);
		  				int version1=Integer.parseInt(version1Str)-30;
		  				int version2=Integer.parseInt(version2Str)-30;
		  				int version3=Integer.parseInt(version3Str)-30;
		  				IrTimerVersion=String.valueOf(version1)+"."+String.valueOf(version2)+"."+String.valueOf(version3);
		  				data2="";
		  				GlobalVariable.setHanded(false);
		  	 			Log.d(TAG, "hand and sync success");
		  	 			//写入数据库
		  	 			List<String> blenameaddress=userDB.getBleNameAndBleAddress();
		  	 			if (blenameaddress!=null && blenameaddress.size()!=0) {
		  	 				Log.d(TAG, "update");
							userDB.updateBle(newBindName, newBindAddress);
						}
		  	 			else {
		  	 				Log.d(TAG, "insert");
		  	 			  userDB.insertUser(newBindName, newBindAddress, null);
						}
		  	 			paraDB.insertPara(GlobalVariable.getIrTimerID(), 0, 0, 0, 0, IrTimerVersion);
		  	 			GlobalVariable.setUploadPara(true);
		        	    Toast.makeText(getApplicationContext(),R.string.bindSuccess, Toast.LENGTH_SHORT).show();
		        	    isBinding=false;
		        	    exitTime=0;
		  			}
		  			else if(length.equalsIgnoreCase("09") && order.equalsIgnoreCase("12"))//数据正确
		  			{
		  			    Log.d(TAG,"数据正确");
		  			    data2="";
		  			}
		  			else if(length.equalsIgnoreCase("09") && order.equalsIgnoreCase("13"))//数据错误
		  			{
		  				Log.d(TAG,"数据错误");
		  				data2="";
		  			}
		  			else if(length.equalsIgnoreCase("09") && order.equalsIgnoreCase("11"))
		  			{
		  				Log.d(TAG,"完成");
		  				data2="";
		  			}
		  			else if(length.equalsIgnoreCase("09") && order.equalsIgnoreCase("14"))//错误
		  			{
		  				Log.d(TAG,"错误");
		  				data2="";
		  			}
		  			else if(length.equalsIgnoreCase("09") && order.equalsIgnoreCase("10") )//正忙请稍后
		  			{
		  				Log.d(TAG,"正忙，请稍后");
		  				data2="";
		  			}
		 
		  			else if(length.equalsIgnoreCase("09") && order.equalsIgnoreCase("15"))
		  			{
		  				Log.d(TAG,"连接完成");
		  				data2="";
		  			}
		  		}
	    		len_g += data1.length()/2;
	    		Log.d(TAG, ""+len_g);//累计接收到的字节数
	  		}
	  		catch(IndexOutOfBoundsException ex)
	  		{
	  			Log.d(TAG,ex.toString());
	  			
	  		}
	  		catch(Exception ex)
	  		{
	  			Log.d(TAG, ex.toString());
	  		}
    	}
    }
 

    //每个1秒钟发送一个消息
    TimerTask task = new TimerTask() {  
        @Override  
        public void run() {  
            // 需要做的事:发送消息  
            Message message = new Message();  
            message.what = 1;  
            handler.sendMessage(message);  
        }  
    }; 
    

	public void onClick(View v) {
		switch (v.getId()) {
		case 0:
			break;
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
                }
            }, SCAN_PERIOD);//10s后调用此方法 停止扫描
            Log.d(TAG, "start scan");
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);

        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Log.d(TAG, "scan call back,"+"name:"+device.getName()+"address:"+device.getAddress());
					if (oldBindAddress==null || !device.getAddress().equals(oldBindAddress)) {
						if (device.getName()!=null && device.getName().contains("Timer")) {
				        	bledeviceAdapter.addDevice(device);
				        	bledeviceAdapter.notifyDataSetChanged();
						}
					}
				}
			});
		}
	};

	
	@Override
	protected void onResume() {//打开APP时扫描设备
		super.onResume();
		scanLeDevice(true);
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}

	@Override
	protected void onPause() {//停止扫描
		super.onPause();
		scanLeDevice(false);
		try
		{
			unregisterReceiver(mGattUpdateReceiver);
		}
		catch(Exception e)
		{
			
			Log.d(TAG, e.toString());
		}
	
	    bledeviceAdapter.clear();
	    newBindAddress=null;
	}

	@Override 
	protected void onDestroy()
	{
		super.onDestroy();
	    timer.cancel();
	    timer=null;
	    newBindAddress=null;
	    try{
	    	unbindService(mServiceConnection);
	    }catch (Exception e) {
			Log.d(TAG, e.toString());
		}
		GlobalVariable.setUploadPara(false);	  
	}
	
	class ViewHolder {
		TextView tv_devName, tv_devAddress;
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

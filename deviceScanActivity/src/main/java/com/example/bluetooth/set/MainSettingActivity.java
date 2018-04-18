package com.example.bluetooth.set;

import java.util.Timer;
import java.util.TimerTask;

import com.example.bluetooth.data.GlobalVariable;
import com.example.bluetooth.db.UserDB;
import com.example.bluetooth.le.BluetoothLeService;
import com.example.bluetooth.le.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainSettingActivity extends Activity{
	private final String TAG=MainSettingActivity.class.getSimpleName();
	LinearLayout timerSettingLayout;//计时器参数设定
	TextView backTv;
	LinearLayout resync_button;//重新同步
	LinearLayout deleteTimerData_button;//删除定时器数据
	private BluetoothLeService mBluetoothLeService;//蓝牙连接服务
	UserDB userDB;
	Timer timer = new Timer(); 
	boolean isTimerTaskExist=false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainsetting);
        
        userDB=new UserDB(getApplicationContext(),"TimerStore",null,7);
        //返回
        backTv=(TextView)findViewById(R.id.backTv);
        backTv.setOnClickListener(bottomListener);
        //can
        timerSettingLayout=(LinearLayout)findViewById(R.id.timerParaSetting_layout);
        timerSettingLayout.setOnClickListener(new OnClickListener()
        		{
					@Override
					public void onClick(View v) {
						Intent intent=new Intent(MainSettingActivity.this,TimerSettingActivity.class);
						startActivity(intent);
						
					}
        	
        		});
       
	    Intent gattServiceIntent = new Intent(MainSettingActivity.this, BluetoothLeService.class);
	    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	    
        ////////////////////////重新同步////////////////////////////////////
      resync_button=(LinearLayout)findViewById(R.id.resync_layout);
      resync_button.setOnClickListener(new OnClickListener(){
      	public void onClick(View v)
      	{
      		if (GlobalVariable.getConnected()) {
      	            AlertDialog.Builder builder=new Builder(MainSettingActivity.this);  
    	            builder.setMessage(R.string.takeLongTimeResynchronize);  
    	            builder.setTitle(R.string.note);  
    	              
    	            //添加AlertDialog.Builder对象的setPositiveButton()方法   
    	            builder.setPositiveButton(R.string.confirmStr, new android.content.DialogInterface.OnClickListener() {  
						@Override
						public void onClick(DialogInterface dialog, int which) {
							GlobalVariable.setSyncedCount(0);
							GlobalVariable.setSyncProgress(true);
							GlobalVariable.setResyncFlag(true);
							GlobalVariable.setProgressCount(0);
							if (!isTimerTaskExist) {
								timer.schedule(task, 500, 200); // 1s后执行task,经过200ms再次执行  
								isTimerTaskExist=true;
							}
						
							
						}  
    	            });  
    	            
    	            //添加AlertDialog.Builder对象的setNegativeButton()方法   
    	            builder.setNegativeButton(R.string.cancelStr, new android.content.DialogInterface.OnClickListener() {  
    	                  
    	                @Override  
    	                public void onClick(DialogInterface dialog, int which) {  
    	                      
    	                }
    	            }); 
    	            builder.create().show();  
				}
      		else
      		{
	      			  Toast.makeText(MainSettingActivity.this, R.string.deviceNotConnect, Toast.LENGTH_SHORT).show(); 
      		}
      	}
      });
      
      /////////////////////////////删除计时器数据/////////////////////////////
    deleteTimerData_button=(LinearLayout) findViewById(R.id.deleteTimerDataLayout);
    deleteTimerData_button.setOnClickListener(new OnClickListener(){
    	public void onClick(View v)
    	{
    		if (GlobalVariable.getConnected()) {
	            AlertDialog.Builder builder=new Builder(MainSettingActivity.this);  
	            builder.setMessage(R.string.clearTimerOrNot);  
	            builder.setTitle(R.string.note);  
	              
	            //添加AlertDialog.Builder对象的setPositiveButton()方法   
	            builder.setPositiveButton(R.string.confirmStr, new android.content.DialogInterface.OnClickListener() {  
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mBluetoothLeService.t_data_clear();
					}  
	            });  
	            
	            //添加AlertDialog.Builder对象的setNegativeButton()方法   
	            builder.setNegativeButton(R.string.cancelStr, new android.content.DialogInterface.OnClickListener() {  
	                  
	                @Override  
	                public void onClick(DialogInterface dialog, int which) {  
	                      
	                }
	            }); 
	            builder.create().show();  
    		}
    		else
    		{
      			  Toast.makeText(MainSettingActivity.this, R.string.deviceNotConnect, Toast.LENGTH_SHORT).show(); 
    		}
    	}
    });
    
    }
	//发送蓝牙指令
    Handler handler = new Handler() {  
        public void handleMessage(Message msg) {  
        	if (msg.what == 1) {  
            	if (mBluetoothLeService != null) {
            		GlobalVariable.setProgressCount(GlobalVariable.getProgressCount()+1);
            		//先发送参同步
            	 	if(GlobalVariable.getConnected()==true && GlobalVariable.getSyncProgress()==true)//同步进度上传，失败的话5秒重试一遍，连续三次失败，说明已经断开，重新连接
                	{	
                		if(GlobalVariable.getProgressCount()>=5)
                		{
                	  		mBluetoothLeService.t_data_sync_progress();
                    		Log.d(TAG, "sync progress");
                    		GlobalVariable.setProgressCount(0);
                		}
                	}
            		else if(GlobalVariable.getConnected()==true && GlobalVariable.getSync()==true)//同步
                	{
                		GlobalVariable.setSyncCount(GlobalVariable.getSyncCount()+1);//同步计数器
                		if(GlobalVariable.getSyncReceived())//如果为true，说明接受到，则直接发送
                		{
                			mBluetoothLeService.t_data_sync(GlobalVariable.getSyncedCount()+1);
                			Log.d(TAG, "syncing "+String.valueOf(GlobalVariable.getSyncedCount()+1));
                			GlobalVariable.setSyncCount(0);
                			GlobalVariable.setSyncReceived(false);
                		}
                		else if (GlobalVariable.getSyncReceived()==false && GlobalVariable.getSyncCount()>=10) {
                			Log.d(TAG, "同步失败次数："+GlobalVariable.getSyncedFailCount());
                			if (GlobalVariable.getSyncedFailCount()>=3) {
								GlobalVariable.setSync(false);
							}
                			else
                			{
                				GlobalVariable.setSyncedFailCount(GlobalVariable.getSyncedFailCount()+1);
	                			try
	                			{
	                				mBluetoothLeService.t_data_sync(GlobalVariable.getSyncedCount()+1);
	                    			Log.d(TAG, "syncing等待两秒"+String.valueOf(GlobalVariable.getSyncedCount()+1));
	                    			GlobalVariable.setSyncCount(0);
	                			}
	                			catch (Exception e) {
									Log.d(TAG, e.toString());
								}
                			}
						}
                	}
            		
            
                }
        	}
            super.handleMessage(msg);  
        };  
    };  
    
 
    
    /**
     * 顶部返回键
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//创建监听对象    
        public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv) {
        		((Activity)MainSettingActivity.this).finish();
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
				Log.d(TAG, "mBluetoothService 为空");
			}
            else
            {
            	Log.d(TAG, "mBluetoothService 不为空");
            }
        }
        //在活动与服务连接断开的时候调用
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    
    
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
    
	protected void onDestroy()
	{
		super.onDestroy();
		Log.d(TAG, "on destory");
	    try{
	    	 if (mServiceConnection!=null) {
	 			unbindService(mServiceConnection);
	 		    task.cancel();
	 		    timer.cancel();
	 		    task=null;
	 		    timer=null;
	 		}
	    	 task.cancel();
	    	 isTimerTaskExist=false;
	    	 GlobalVariable.setSyncedFailCount(0);
	    }catch (Exception e) {
			Log.d(TAG, e.toString());
		}
	}

    
}

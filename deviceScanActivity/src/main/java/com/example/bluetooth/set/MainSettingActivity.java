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
	LinearLayout timerSettingLayout;//��ʱ�������趨
	TextView backTv;
	LinearLayout resync_button;//����ͬ��
	LinearLayout deleteTimerData_button;//ɾ����ʱ������
	private BluetoothLeService mBluetoothLeService;//�������ӷ���
	UserDB userDB;
	Timer timer = new Timer(); 
	boolean isTimerTaskExist=false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainsetting);
        
        userDB=new UserDB(getApplicationContext(),"TimerStore",null,7);
        //����
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
	    
        ////////////////////////����ͬ��////////////////////////////////////
      resync_button=(LinearLayout)findViewById(R.id.resync_layout);
      resync_button.setOnClickListener(new OnClickListener(){
      	public void onClick(View v)
      	{
      		if (GlobalVariable.getConnected()) {
      	            AlertDialog.Builder builder=new Builder(MainSettingActivity.this);  
    	            builder.setMessage(R.string.takeLongTimeResynchronize);  
    	            builder.setTitle(R.string.note);  
    	              
    	            //���AlertDialog.Builder�����setPositiveButton()����   
    	            builder.setPositiveButton(R.string.confirmStr, new android.content.DialogInterface.OnClickListener() {  
						@Override
						public void onClick(DialogInterface dialog, int which) {
							GlobalVariable.setSyncedCount(0);
							GlobalVariable.setSyncProgress(true);
							GlobalVariable.setResyncFlag(true);
							GlobalVariable.setProgressCount(0);
							if (!isTimerTaskExist) {
								timer.schedule(task, 500, 200); // 1s��ִ��task,����200ms�ٴ�ִ��  
								isTimerTaskExist=true;
							}
						
							
						}  
    	            });  
    	            
    	            //���AlertDialog.Builder�����setNegativeButton()����   
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
      
      /////////////////////////////ɾ����ʱ������/////////////////////////////
    deleteTimerData_button=(LinearLayout) findViewById(R.id.deleteTimerDataLayout);
    deleteTimerData_button.setOnClickListener(new OnClickListener(){
    	public void onClick(View v)
    	{
    		if (GlobalVariable.getConnected()) {
	            AlertDialog.Builder builder=new Builder(MainSettingActivity.this);  
	            builder.setMessage(R.string.clearTimerOrNot);  
	            builder.setTitle(R.string.note);  
	              
	            //���AlertDialog.Builder�����setPositiveButton()����   
	            builder.setPositiveButton(R.string.confirmStr, new android.content.DialogInterface.OnClickListener() {  
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mBluetoothLeService.t_data_clear();
					}  
	            });  
	            
	            //���AlertDialog.Builder�����setNegativeButton()����   
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
	//��������ָ��
    Handler handler = new Handler() {  
        public void handleMessage(Message msg) {  
        	if (msg.what == 1) {  
            	if (mBluetoothLeService != null) {
            		GlobalVariable.setProgressCount(GlobalVariable.getProgressCount()+1);
            		//�ȷ��Ͳ�ͬ��
            	 	if(GlobalVariable.getConnected()==true && GlobalVariable.getSyncProgress()==true)//ͬ�������ϴ���ʧ�ܵĻ�5������һ�飬��������ʧ�ܣ�˵���Ѿ��Ͽ�����������
                	{	
                		if(GlobalVariable.getProgressCount()>=5)
                		{
                	  		mBluetoothLeService.t_data_sync_progress();
                    		Log.d(TAG, "sync progress");
                    		GlobalVariable.setProgressCount(0);
                		}
                	}
            		else if(GlobalVariable.getConnected()==true && GlobalVariable.getSync()==true)//ͬ��
                	{
                		GlobalVariable.setSyncCount(GlobalVariable.getSyncCount()+1);//ͬ��������
                		if(GlobalVariable.getSyncReceived())//���Ϊtrue��˵�����ܵ�����ֱ�ӷ���
                		{
                			mBluetoothLeService.t_data_sync(GlobalVariable.getSyncedCount()+1);
                			Log.d(TAG, "syncing "+String.valueOf(GlobalVariable.getSyncedCount()+1));
                			GlobalVariable.setSyncCount(0);
                			GlobalVariable.setSyncReceived(false);
                		}
                		else if (GlobalVariable.getSyncReceived()==false && GlobalVariable.getSyncCount()>=10) {
                			Log.d(TAG, "ͬ��ʧ�ܴ�����"+GlobalVariable.getSyncedFailCount());
                			if (GlobalVariable.getSyncedFailCount()>=3) {
								GlobalVariable.setSync(false);
							}
                			else
                			{
                				GlobalVariable.setSyncedFailCount(GlobalVariable.getSyncedFailCount()+1);
	                			try
	                			{
	                				mBluetoothLeService.t_data_sync(GlobalVariable.getSyncedCount()+1);
	                    			Log.d(TAG, "syncing�ȴ�����"+String.valueOf(GlobalVariable.getSyncedCount()+1));
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
     * �������ؼ�
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//������������    
        public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv) {
        		((Activity)MainSettingActivity.this).finish();
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
				Log.d(TAG, "mBluetoothService Ϊ��");
			}
            else
            {
            	Log.d(TAG, "mBluetoothService ��Ϊ��");
            }
        }
        //�ڻ��������ӶϿ���ʱ�����
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    
    
    //ÿ��1���ӷ���һ����Ϣ
    TimerTask task = new TimerTask() {  
        @Override  
        public void run() {  
            // ��Ҫ������:������Ϣ  
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

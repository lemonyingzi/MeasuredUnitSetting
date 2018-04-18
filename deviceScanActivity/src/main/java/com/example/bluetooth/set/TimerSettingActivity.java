package com.example.bluetooth.set;

import java.util.ArrayList;
import java.util.List;

import com.example.bluetooth.data.GlobalVariable;
import com.example.bluetooth.db.ParaDB;
import com.example.bluetooth.le.BluetoothLeService;
import com.example.bluetooth.le.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class TimerSettingActivity extends Activity{
	final private String TAG=TimerSettingActivity.class.getSimpleName();
	TextView backTv;
	///////////////低电压保护下拉框/////////////////////
	Spinner uvpSpinner;//低电压保护
	private List<String> uvpList;
	private ArrayAdapter<String> uvpSpinnerAdapter;
	
	/////////////////////LED//////////////////////
	Spinner ledSpinner;//LED状态度亮度
	private List<String> ledList;
	private ArrayAdapter<String> ledSpinnerAdapter;
	
	ParaDB paraDB;
	private BluetoothLeService mBluetoothLeService;//蓝牙连接服务
	LinearLayout confirmLy;
	
	EditText protectionTimeEt;//保护限制时间
	EditText lcdContrastEt;//LCD对比度
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timerparasetting);
        paraDB=new ParaDB(getApplicationContext(), "TimerStore", null, 7);
        
	    Intent gattServiceIntent = new Intent(TimerSettingActivity.this, BluetoothLeService.class);
	    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	    
        ////////////////////低电压保护////////////////////////////////////////
	    
	    uvpSpinner=(Spinner)findViewById(R.id.uvpSpinner);

        uvpList = new ArrayList<String>();
        uvpList.add("OFF");
        uvpList.add("2S");
        uvpList.add("3S");
        
        if (uvpList!=null && uvpList.size()>0) {
        	  //适配器
        	uvpSpinnerAdapter= new ArrayAdapter<String>(this, R.layout.spinner_track, R.id.text,uvpList);
            //设置样式
        	uvpSpinnerAdapter.setDropDownViewResource(R.layout.spinner_track);
            //加载适配器
            uvpSpinner.setAdapter(uvpSpinnerAdapter);
		}
        
        ///////////////////LED//////////////////////////////////////
        ledSpinner=(Spinner) findViewById(R.id.ledSpinner);
        ledList=new ArrayList<String>();
        ledList.add("OFF");
        ledList.add("50%");
        ledList.add("100%");
        
        if (ledList!=null && ledList.size()>0) {
			ledSpinnerAdapter=new ArrayAdapter<String>(this, R.layout.spinner_track,R.id.text,ledList);
			ledSpinnerAdapter.setDropDownViewResource(R.layout.spinner_track);
			ledSpinner.setAdapter(ledSpinnerAdapter);
		}
        
        
	    //确定按钮
	    confirmLy=(LinearLayout)findViewById(R.id.confirmLy);
	    confirmLy.setOnClickListener(new OnClickListener()
	    		{
					@Override
					public void onClick(View v) {
				  		if (GlobalVariable.getConnected()) {
				  			//解析保护限制时间
				  			String protectionTimeStr=protectionTimeEt.getText().toString();
				  			int protectionTimeInt=0;
				  			if (protectionTimeStr!=null && !protectionTimeStr.equals("") ) {
				  				if (protectionTimeStr.contains(getResources().getString(R.string.sencondStr))) {
									protectionTimeStr=protectionTimeStr.replace(getResources().getString(R.string.sencondStr), "");
								}
				  				protectionTimeInt=Integer.parseInt(protectionTimeStr);
				  				if (protectionTimeInt>255 || protectionTimeInt<1) {
									Toast.makeText(getApplicationContext(), getResources().getString(R.string.lowVoltageProtectStr)+":1~255", Toast.LENGTH_SHORT).show();
									return;
								}
							}
				  			else
				  			{
				  				Toast.makeText(getApplicationContext(), getResources().getString(R.string.timingprotecttime)+":1~255", Toast.LENGTH_SHORT).show();
								return;
				  			}
				  			//解析低电压保护
				  			String uvpStr=uvpSpinner.getSelectedItem().toString();
				  			byte uvpInt=0;
				  			if (uvpStr!=null && uvpStr.equalsIgnoreCase("off")) {
								uvpInt=0;
							}
				  			else if (uvpStr.equalsIgnoreCase("2s")) {
								uvpInt=0x42;
							}
							else if (uvpStr.equalsIgnoreCase("3s")) {
								uvpInt=0x63;
							}
				  			else
				  			{
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.lowVoltageProtectStr)+":OFF、2S、3S", Toast.LENGTH_SHORT).show();
								return;
				  			}
				  			//解析显示屏对比度
				  			String LCDStr=lcdContrastEt.getText().toString();
				  			int lcdInt=0;
				  			if (LCDStr!=null && !LCDStr.equals("")) {
				  				if (LCDStr.contains("%")) {
									LCDStr=LCDStr.replaceAll("%", "");
								}
				  				if (LCDStr==null || LCDStr.equals("")) {
									Toast.makeText(getApplicationContext(), getResources().getString(R.string.LCDContrastStr)+":0~100", Toast.LENGTH_SHORT).show();
									return;
								}
				  				lcdInt=Integer.parseInt(LCDStr);
								if (lcdInt<0 || lcdInt>100) {
									Toast.makeText(getApplicationContext(), getResources().getString(R.string.LCDContrastStr)+":0~100", Toast.LENGTH_SHORT).show();
									return;
								}
							}
				  			else
				  			{
				  				Toast.makeText(getApplicationContext(), getResources().getString(R.string.LCDContrastStr)+":0~100", Toast.LENGTH_SHORT).show();
								return;
				  			}
				  			//解析LED亮度
				  			String LEDStr=ledSpinner.getSelectedItem().toString();
				  			byte ledInt=0;
				  			if (LEDStr!=null && LEDStr.equals("OFF")) {
								ledInt=0;
							}
				  			else if (LEDStr!=null && LEDStr.equals("50%")) {
								ledInt=0x32;
							}
				  			else if (LEDStr.equals("100%")) {
								ledInt=0x64;
							}
				  			else
				  			{
								Toast.makeText(getApplicationContext(),getResources().getString(R.string.LEDStateLampBrightnessStr)+ ":OFF、50、100", Toast.LENGTH_SHORT).show();
								return;
				  			}
				  			Log.d(TAG, "protectTime:"+protectionTimeInt+",uvp:"+uvpInt+",lcd:"+lcdInt+",led:"+ledInt);
				  			mBluetoothLeService.t_data_parm_setting((byte)protectionTimeInt,uvpInt,(byte)lcdInt,ledInt);
						}
			      		else
			      		{
			      			Toast.makeText(TimerSettingActivity.this, R.string.deviceNotConnect, Toast.LENGTH_SHORT).show(); 
			      		}
					}
	    	
	    		});
        //返回
        backTv=(TextView)findViewById(R.id.backTv);
        backTv.setOnClickListener(bottomListener);
        	    
	    lcdContrastEt=(EditText)findViewById(R.id.LCDContrastEditText);
	    protectionTimeEt=(EditText)findViewById(R.id.protectionTimeEditText);
	    
	    
	    Cursor paraCursor=paraDB.selecetPara();
	    String timeLimit=null;
	    String uvpStr=null;
	    String LEDBrightnessStr=null;
	    String LCDContrastStr=null;
	    if (paraCursor.moveToLast()) {
			uvpStr=paraCursor.getString(paraCursor.getColumnIndex("UVP"));
			LEDBrightnessStr=paraCursor.getString(paraCursor.getColumnIndex("LEDBrightness"));
			timeLimit=paraCursor.getString(paraCursor.getColumnIndex("timeLimit"));
			LCDContrastStr=paraCursor.getString(paraCursor.getColumnIndex("LCDContrast"));
		}
	    paraCursor.close();
	    //显示低电压保护
	    if(uvpStr!=null && uvpStr.equals("0")) {
			uvpSpinner.setSelection(0);
		}
	    else if (uvpStr!=null && uvpStr.equals("66")) 
	    {
	    	uvpSpinner.setSelection(1);
	    }
	    else if (uvpStr!=null && uvpStr.equals("99")) {
			uvpSpinner.setSelection(2);
		}
	    
	    
		if (LEDBrightnessStr!=null && LEDBrightnessStr.equals("0")) {
			ledSpinner.setSelection(0);
		}
		else if (LEDBrightnessStr!=null && LEDBrightnessStr.equals("50")) {
			ledSpinner.setSelection(1);
		}
		else if (LEDBrightnessStr!=null && LEDBrightnessStr.equals("100")) {
			ledSpinner.setSelection(2);
		}
		
		
		if (LCDContrastStr!=null) {
			lcdContrastEt.setText(LCDContrastStr+"%");
		}
		if (timeLimit!=null) {
			protectionTimeEt.setText(timeLimit+getResources().getString(R.string.sencondStr));
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
        		((Activity)TimerSettingActivity.this).finish();
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
       
        }
        //在活动与服务连接断开的时候调用
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    
	protected void onDestroy()
	{
		super.onDestroy();
		Log.d(TAG, "on destory");
	    try{
	    	 if (mServiceConnection!=null) {
	 			unbindService(mServiceConnection);
	 		}
	    }catch (Exception e) {
			Log.d(TAG, e.toString());
		}
	}

}

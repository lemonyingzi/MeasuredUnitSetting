package com.example.bluetooth.le;

import java.util.List;

import com.example.bluetooth.db.SyncTimeDB;
import com.example.bluetooth.db.UserDB;
import com.example.bluetooth.list.BledeviceAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BindedAndUnbindedActivity extends Activity{
	private final String TAG=BindedAndUnbindedActivity.class.getSimpleName();
	UserDB userDB;
	BledeviceAdapter bledeviceAdapter;
	ListView listView;
	LinearLayout bindNewDeviceLayout;
	protected Object mBluetoothLeService;
	TextView  backTv;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bindandunbindtimer);
        
        userDB=new UserDB(getApplicationContext(),"TimerStore",null,7);
		bledeviceAdapter=new BledeviceAdapter(BindedAndUnbindedActivity.this, R.layout.bledevice_item);
		bindNewDeviceLayout=(LinearLayout) findViewById(R.id.bindNewDeviceLayout);
	    backTv=(TextView)findViewById(R.id.backTv);
        backTv.setOnClickListener(bottomListener);
		initBindedDevice();
	

	  	listView=(ListView) findViewById(R.id.ble_list);
	  	listView.setAdapter(bledeviceAdapter);
	    View vhead=View.inflate(this, R.layout.bledevice_item_head, null);
	    listView.addHeaderView(vhead);

//	    //listView长按删除事件
//	    listView.setOnItemLongClickListener(new OnItemLongClickListener() {  
//	    	@Override  
//           public boolean onItemLongClick(AdapterView<?> parent, View view,  
//                    final int position, long id) {  
//	    		if (position>0) {
//	    		       //定义AlertDialog.Builder对象，当长按列表项的时候弹出确认删除对话框   
//	                AlertDialog.Builder builder=new Builder(BindedAndUnbindedActivity.this);  
//	                builder.setMessage("确定删除?");  
//	                builder.setTitle("提示");  
//	                  
//	                //添加AlertDialog.Builder对象的setPositiveButton()方法   
//	                builder.setPositiveButton("确定", new OnClickListener() {  
//	                    @Override  
//	                    public void onClick(DialogInterface dialog, int which) {  
//	                    	String address=bledeviceAdapter.getDevice(position-1).getAddress();
//	                    	Log.d(TAG, "address:"+address);
//	                    	bledeviceAdapter.remove(position-1);
//	                    	userDB.deleteAccordingToAddress(address);
//	                        bledeviceAdapter.notifyDataSetChanged();  
//	                    }  
//	                });  
//	                  
//	                //添加AlertDialog.Builder对象的setNegativeButton()方法   
//	                builder.setNegativeButton("取消", new OnClickListener() {  
//	                      
//	                    @Override  
//	                    public void onClick(DialogInterface dialog, int which) {  
//	                          
//	                    }  
//	                });  
//	                  
//	                builder.create().show();  
//	                return false;  
//				}
//	    		return false;
//            }  
//        });  
	  	
	  	bindNewDeviceLayout.setOnClickListener(new View.OnClickListener() {
  		public void onClick(View v) {
            	Intent intent=new Intent(BindedAndUnbindedActivity.this,BindTimerActivity.class);
    			startActivity(intent); 
  			}
  		 });
    }
    /**
     * 顶部返回键
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//创建监听对象    
        public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv) {
        		((Activity)BindedAndUnbindedActivity.this).finish();
			}
		}
    };
    
    private void initBindedDevice() {
		List<String> bleList=userDB.getBleNameAndBleAddress();
		if (bleList!=null && bleList.size()>0) {
			android.util.Log.d(TAG, "ble list larger than 0"+",address:"+bleList.get(1));
			BluetoothDevice bluetoothDevice;
			BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
			bluetoothDevice=bluetoothAdapter.getRemoteDevice(bleList.get(1));
			bledeviceAdapter.addDevice(bluetoothDevice);
		}
	
	}
    @Override
    public void onDestroy()
    {
    	super.onDestroy();
    }
    @Override
    public void onPause() {
    	super.onPause();
    	bledeviceAdapter.clear();
    }
    @Override
    public void onResume()
    {
    	super.onResume();
    	initBindedDevice();
		bledeviceAdapter.notifyDataSetChanged();
    }
}

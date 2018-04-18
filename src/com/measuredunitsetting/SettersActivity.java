package com.measuredunitsetting;

import java.util.ArrayList;
import java.util.List;

import com.measuredunitsetting.db.BleDeviceDB;
import com.measuredunitsetting.global.GlobalVariable;
import com.measuredunitsetting.hydraulic.monitornetwork.HydraulicMonitorNetworkActivity;
import com.measuredunitsetting.list.BledeviceAdapter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import com.measuredunitsetting.global.LogUtil;
import com.measuredunitsetting.list.MonitorNetworkAdatper;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class SettersActivity extends Activity {
	private final String TAG=SettersActivity.class.getSimpleName();

	BledeviceAdapter bledeviceAdapter;
	ListView bleLv;
	BleDeviceDB bleDeviceDB;
	ArrayList<BluetoothDevice> bleList=new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setters);

		bleList=getIntent().getParcelableArrayListExtra("bleList");
		bleDeviceDB=new BleDeviceDB(getApplicationContext(),GlobalVariable.getDataBaseName(),null,7);
		bledeviceAdapter=new BledeviceAdapter(SettersActivity.this, R.layout.bledevice_item);
		bleLv=(ListView) findViewById(R.id.bleList);
		bleLv.setAdapter(bledeviceAdapter);
	    View vhead=View.inflate(this, R.layout.bledevice_item_head, null);
		bleLv.addHeaderView(vhead);
	    
		initBindedDevice();
		bleLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
	    	@Override
	    	public void onItemClick(AdapterView<?> parent,View view,final int position,long id){
	    		if (position>0) {
                  	BluetoothDevice bluetoothDevice=bledeviceAdapter.getDevice(position-1);
	    	        Intent intent=new Intent();
	    	        intent.putExtra("address",bluetoothDevice.getAddress());
	    	        setResult(RESULT_OK,intent);
	    	        finish();
				}
	
		    }});	
	}
	
	
    private void initBindedDevice() {
//		List<String> bleList=bleDeviceDB.getBleNameAndBleAddress();
		if (bleList!=null && bleList.size()>0) {
			BluetoothDevice bluetoothDevice;
			BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
			for (int i=0;i<bleList.size();i++)
			{
				bluetoothDevice=bleList.get(i);
				if(bluetoothDevice==null)
				{
					LogUtil.i(TAG, "bluetooth device is null");
				}
				bledeviceAdapter.addDevice(bluetoothDevice);
			}
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

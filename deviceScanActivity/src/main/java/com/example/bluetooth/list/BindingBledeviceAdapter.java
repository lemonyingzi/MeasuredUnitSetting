package com.example.bluetooth.list;

import java.util.ArrayList;
import com.example.bluetooth.le.R;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BindingBledeviceAdapter  extends ArrayAdapter<BluetoothDevice> {
	private final String TAG=BledeviceAdapter.class.getSimpleName();
	private static ArrayList<BluetoothDevice> mLeDevices=new ArrayList<BluetoothDevice>();
	
	private int resourceId;
	public BindingBledeviceAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId, mLeDevices);
		resourceId=textViewResourceId;
	}
	
    public void addDevice(BluetoothDevice device) {
        if(!mLeDevices.contains(device)) {
        	Log.d("BleAdapter", "mleDevices size:"+mLeDevices.size());
            mLeDevices.add(device);
        }
    }

    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }
    
    
	@Override
	public View getView(int position,View convertView,ViewGroup parent)
	{
		View view=convertView;
		ViewHolder viewHolder;
		BluetoothDevice result=getDevice(position);
		if (convertView==null) {
			view=LayoutInflater.from(getContext()).inflate(resourceId, parent,false);
			viewHolder=new ViewHolder();
			viewHolder.name=(TextView) view.findViewById(R.id.device_name);
			viewHolder.address=(TextView) view.findViewById(R.id.device_address);
			view.setTag(viewHolder);
		}
		else
		{
			viewHolder=(ViewHolder) view.getTag();
		}
		viewHolder.name.setText(result.getName());
		viewHolder.address.setText(result.getAddress());

		return view;
	}
	
    class ViewHolder
    {
    	TextView name;
    	TextView address;
    }
}

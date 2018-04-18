package com.measuredunitsetting.list;

import java.util.List;

import com.measuredunitsetting.R;
import com.measuredunitsetting.entity.HydraulicMonitorNetwork;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MonitorNetworkAdatper  extends ArrayAdapter<HydraulicMonitorNetwork> {
	private int resourceId;
	public MonitorNetworkAdatper(Context context, int textViewResourceId, List<HydraulicMonitorNetwork> objects) {
		super(context, textViewResourceId, objects);
		resourceId=textViewResourceId;
	}

	@Override
	public View getView(int position,View convertView,ViewGroup parent)
	{
		HydraulicMonitorNetwork result=getItem(position);
		View view=convertView;
		ViewHolder viewHolder;
		if (convertView==null) {
			view=LayoutInflater.from(getContext()).inflate(resourceId, parent,false);
			viewHolder=new ViewHolder();
			viewHolder.projectName=(TextView) view.findViewById(R.id.projectNameTv);
			viewHolder.monitorNetworkNumber=(TextView) view.findViewById(R.id.monitorNumberTv);
			view.setTag(viewHolder);
		}
		else
		{
			viewHolder=(ViewHolder) view.getTag();
		}
		viewHolder.projectName.setText(result.getProjectName());
		viewHolder.monitorNetworkNumber.setText(result.getMonitorNetworkNumber());
		return view;
	}
	
    
    class ViewHolder
    {
    	TextView projectName;
    	TextView monitorNetworkNumber;
    }
}
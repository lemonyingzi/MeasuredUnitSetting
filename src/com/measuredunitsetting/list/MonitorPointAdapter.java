package com.measuredunitsetting.list;

import java.util.List;

import com.measuredunitsetting.R;
import com.measuredunitsetting.entity.DisplacementMonitorPoint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MonitorPointAdapter  extends ArrayAdapter<DisplacementMonitorPoint>{
	private int resourceId;
	public MonitorPointAdapter(Context context, int textViewResourceId, List<DisplacementMonitorPoint> objects) {
		super(context, textViewResourceId, objects);
		resourceId=textViewResourceId;
	}

	@Override
	public View getView(int position,View convertView,ViewGroup parent)
	{
		DisplacementMonitorPoint result=getItem(position);
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
		viewHolder.monitorNetworkNumber.setText(result.getMonitorPointNumber());
		return view;
	}
	
    
    class ViewHolder
    {
    	TextView projectName;
    	TextView monitorNetworkNumber;
    }
}

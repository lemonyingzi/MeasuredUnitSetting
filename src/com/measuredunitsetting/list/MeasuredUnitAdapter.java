package com.measuredunitsetting.list;

import java.util.List;


import com.measuredunitsetting.MyApplication;
import com.measuredunitsetting.R;
import com.measuredunitsetting.entity.HydraulicMeasuredUnit;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MeasuredUnitAdapter  extends ArrayAdapter<HydraulicMeasuredUnit> {
	private int resourceId;
	public MeasuredUnitAdapter(Context context, int textViewResourceId, List<HydraulicMeasuredUnit> objects) {
		super(context, textViewResourceId, objects);
		resourceId=textViewResourceId;
	}

	@Override
	public View getView(int position,View convertView,ViewGroup parent)
	{
		HydraulicMeasuredUnit result=getItem(position);
		View view=convertView;
		ViewHolder viewHolder;
		if (convertView==null) {
			view=LayoutInflater.from(getContext()).inflate(resourceId, parent,false);
			viewHolder=new ViewHolder();
			viewHolder.serialNumberTv=(TextView) view.findViewById(R.id.serialNumberTv);
			viewHolder.measurePointNumTv=(TextView) view.findViewById(R.id.measurePointNumTv);
			viewHolder.measureTypeTv=(TextView)view.findViewById(R.id.measureTypeTv);
			viewHolder.IDTv=(TextView)view.findViewById(R.id.IDTv);
			view.setTag(viewHolder);
		}
		else
		{
			viewHolder=(ViewHolder) view.getTag();
		}
		viewHolder.serialNumberTv.setText(""+result.getSerialNumber());
		viewHolder.measurePointNumTv.setText(result.getMeasurePointNumber());
		if (result.getMeasureType().equals("A0"))
		{
			viewHolder.measureTypeTv.setText(MyApplication.getContext().getString(R.string.benchmark));
		}
		else
		{
			viewHolder.measureTypeTv.setText(MyApplication.getContext().getString(R.string.commonMeasurePoint));
		}
		viewHolder.IDTv.setText(result.getMeasureUnitId());
		return view;
	}
	
    
    class ViewHolder
    {
    	TextView serialNumberTv;
    	TextView measurePointNumTv;
    	TextView measureTypeTv;
    	TextView IDTv;
    }
}
package com.measuredunitsetting.list;

import java.util.List;

import com.measuredunitsetting.R;
import com.measuredunitsetting.entity.DisplacementMeasuredUnit;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DisplacementMeasuredUnitAdapter  extends ArrayAdapter<DisplacementMeasuredUnit> {
	private int resourceId;
	public DisplacementMeasuredUnitAdapter(Context context, int textViewResourceId, List<DisplacementMeasuredUnit> objects) {
		super(context, textViewResourceId, objects);
		resourceId=textViewResourceId;
	}
	@Override
	public View getView(int position,View convertView,ViewGroup parent)
	{
		DisplacementMeasuredUnit result=getItem(position);
		View view=convertView;
		ViewHolder viewHolder;
		if (convertView==null) {
			view=LayoutInflater.from(getContext()).inflate(resourceId, parent,false);
			viewHolder=new ViewHolder();
			viewHolder.serialNumberTv=(TextView) view.findViewById(R.id.serialNumberTv);
			viewHolder.depthTv=(TextView) view.findViewById(R.id.depthTv);
			viewHolder.IDTv=(TextView)view.findViewById(R.id.IDTv);
			view.setTag(viewHolder);
		}
		else
		{
			viewHolder=(ViewHolder) view.getTag();
		}
		viewHolder.serialNumberTv.setText(""+result.getSerialNumber());
		viewHolder.depthTv.setText(String.valueOf(result.getDepth()));
		viewHolder.IDTv.setText(result.getMeasureUnitId());
		return view;
	}
	
    
    class ViewHolder
    {
    	TextView serialNumberTv;
    	TextView depthTv;
    	TextView IDTv;
    }
}
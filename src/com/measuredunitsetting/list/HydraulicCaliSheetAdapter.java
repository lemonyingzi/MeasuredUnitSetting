package com.measuredunitsetting.list;

import java.util.List;

import com.measuredunitsetting.R;
import com.measuredunitsetting.entity.HydraulicCaliSheet;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HydraulicCaliSheetAdapter  extends ArrayAdapter<HydraulicCaliSheet> {
	private int resourceId;
	public HydraulicCaliSheetAdapter(Context context, int textViewResourceId, List<HydraulicCaliSheet> objects) {
		super(context, textViewResourceId, objects);
		resourceId=textViewResourceId;
	}

	@Override
	public View getView(int position,View convertView,ViewGroup parent)
	{
		HydraulicCaliSheet result=getItem(position);
		View view=convertView;
		ViewHolder viewHolder;
		if (convertView==null) {
			view=LayoutInflater.from(getContext()).inflate(resourceId, parent,false);
			viewHolder=new ViewHolder();
			viewHolder.conclusionTv=(TextView) view.findViewById(R.id.conclusionTv);
			viewHolder.marginErrorTv=(TextView) view.findViewById(R.id.marginErrorTv);
			viewHolder.outputValueTv=(TextView) view.findViewById(R.id.outputValueTv);
			viewHolder.measurePointTv=(TextView)view.findViewById(R.id.measurePointTv);
			viewHolder.temperatureTv=(TextView) view.findViewById(R.id.temperatureTv);
			viewHolder.errorTv=(TextView) view.findViewById(R.id.errorTv);
			viewHolder.measuredValueTv=(TextView) view.findViewById(R.id.measuredValueTv);
			viewHolder.coeffTv=(TextView) view.findViewById(R.id.coeffTv);
			
			view.setTag(viewHolder);
		}
		else
		{
			viewHolder=(ViewHolder) view.getTag();
		}
		viewHolder.conclusionTv.setText(result.getConclusion());
		viewHolder.marginErrorTv.setText(result.getMarginError());
		viewHolder.outputValueTv.setText(result.getOutputValue());
		viewHolder.measurePointTv.setText(result.getMeasurePoint());
		viewHolder.temperatureTv.setText(result.getTemperature());
		viewHolder.errorTv.setText(result.getError());
		viewHolder.measuredValueTv.setText(result.getMeasuredValue());
		viewHolder.coeffTv.setText(result.getCoeff());
		return view;
	}
	
    
    class ViewHolder
    {
    	TextView conclusionTv;
    	TextView marginErrorTv;
    	TextView outputValueTv;
    	TextView measurePointTv;
    	TextView temperatureTv;
    	TextView errorTv;
    	TextView measuredValueTv;
    	TextView coeffTv;
    }
}
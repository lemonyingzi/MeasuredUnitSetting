package com.measuredunitsetting.list;

import java.text.DecimalFormat;
import java.util.List;

import com.measuredunitsetting.R;
import com.measuredunitsetting.entity.HydraulicMeasuredUnitResult;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MeasuredUnitResultAdapter extends ArrayAdapter<HydraulicMeasuredUnitResult>{
	private int resourceId;
	public MeasuredUnitResultAdapter(Context context, int textViewResourceId, List<HydraulicMeasuredUnitResult> objects) {
		super(context, textViewResourceId, objects);
		resourceId=textViewResourceId;
	}

	@Override
	public View getView(int position,View convertView,ViewGroup parent)
	{
		HydraulicMeasuredUnitResult result=getItem(position);
		View view=convertView;
		ViewHolder viewHolder;
		if (convertView==null) {
			view=LayoutInflater.from(getContext()).inflate(resourceId, parent,false);
			viewHolder=new ViewHolder();
			viewHolder.serialNumberTv=(TextView) view.findViewById(R.id.serialNumberTv);
			viewHolder.measurePointNumTv=(TextView) view.findViewById(R.id.measurePointNumTv);
			viewHolder.pressureTv=(TextView)view.findViewById(R.id.pressureTv);
			viewHolder.temperatureTv=(TextView)view.findViewById(R.id.temperatureTv);
			view.setTag(viewHolder);
		}
		else
		{
			viewHolder=(ViewHolder) view.getTag();
		}
		viewHolder.serialNumberTv.setText(""+result.getSerialNumber());

		viewHolder.measurePointNumTv.setText(result.getMeasurePointNumber());
		DecimalFormat decimalFormat=new DecimalFormat(".0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
		viewHolder.pressureTv.setText(decimalFormat.format(result.getPressure()));
		viewHolder.temperatureTv.setText(decimalFormat.format(result.getTemperature()));
		return view;
	}
	
    
    class ViewHolder
    {
    	TextView serialNumberTv;
    	TextView measurePointNumTv;
    	TextView pressureTv;
    	TextView temperatureTv;
    }
}

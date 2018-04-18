package com.measuredunitsetting.list;

import java.util.List;

import com.measuredunitsetting.R;
import com.measuredunitsetting.entity.DisplacementMeasuredResult;
import android.annotation.SuppressLint;
import android.content.Context;
import java.text.DecimalFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DisplacementMeasuredResultAdapter extends ArrayAdapter<DisplacementMeasuredResult> {
	private int resourceId;
	public DisplacementMeasuredResultAdapter(Context context, int textViewResourceId, List<DisplacementMeasuredResult> objects) {
		super(context, textViewResourceId, objects);
		resourceId=textViewResourceId;
	}
	@SuppressLint("NewApi")
	@Override
	public View getView(int position,View convertView,ViewGroup parent)
	{
		DisplacementMeasuredResult result=getItem(position);
		View view=convertView;
		ViewHolder viewHolder;
		if (convertView==null) {
			view=LayoutInflater.from(getContext()).inflate(resourceId, parent,false);
			viewHolder=new ViewHolder();
			viewHolder.serialNumberTv=(TextView) view.findViewById(R.id.serialNumberTv);
			viewHolder.depthTv=(TextView) view.findViewById(R.id.depthTv);
			viewHolder.xTv=(TextView)view.findViewById(R.id.xTv);
			viewHolder.yTv=(TextView)view.findViewById(R.id.yTv);
			viewHolder.zTv=(TextView)view.findViewById(R.id.zTv);
			viewHolder.temperatuerTv=(TextView)view.findViewById(R.id.temperatureTv);

			view.setTag(viewHolder);
		}
		else
		{
			viewHolder=(ViewHolder) view.getTag();
		}
		viewHolder.serialNumberTv.setText(""+result.getSerialNumber());
		DecimalFormat decimalFormat=new DecimalFormat("0.0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
		viewHolder.depthTv.setText(decimalFormat.format(result.getDepth()));
		viewHolder.xTv.setText(decimalFormat.format(result.getX()));
		viewHolder.yTv.setText(decimalFormat.format(result.getY()));
		viewHolder.zTv.setText(decimalFormat.format(result.getZ()));
		viewHolder.temperatuerTv.setText(decimalFormat.format(result.getTemperature()));
		return view;
	}
	
    
    class ViewHolder
    {
    	TextView serialNumberTv;
    	TextView depthTv;
    	TextView xTv;
    	TextView yTv;
    	TextView zTv;
    	TextView temperatuerTv;
    }
}

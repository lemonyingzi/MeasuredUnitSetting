package com.example.bluetooth.list;

import java.util.List;

import com.example.bluetooth.le.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DetailedResultAdapter extends ArrayAdapter<DetailedResult> {
	
	private int resourceId;
	public DetailedResultAdapter(Context context, int textViewResourceId, List<DetailedResult> objects) {
		super(context, textViewResourceId, objects);
		resourceId=textViewResourceId;
	}

	@Override
	public View getView(int position,View convertView,ViewGroup parent)
	{
		DetailedResult result=getItem(position);
		View view=convertView;
		ViewHolder viewHolder;
		if (convertView==null) {
			view=LayoutInflater.from(getContext()).inflate(resourceId, parent,false);
			viewHolder=new ViewHolder();
			viewHolder.Lap=(TextView) view.findViewById(R.id.Lap);
			viewHolder.time=(TextView) view.findViewById(R.id.Time);
			viewHolder.LapTime=(TextView) view.findViewById(R.id.LapTime);
			viewHolder.Receive=(TextView) view.findViewById(R.id.Receive);
			view.setTag(viewHolder);
		}
		else
		{
			viewHolder=(ViewHolder) view.getTag();
		}
		viewHolder.Lap.setText(result.getLap());
		viewHolder.time.setText(result.getTime());
		viewHolder.LapTime.setText(result.getLapTime());
		viewHolder.Receive.setText(result.getReceive());
		return view;
	}
	
    
    class ViewHolder
    {
    	TextView Lap;
    	TextView time;
    	TextView LapTime;
    	TextView Receive;
    }
}

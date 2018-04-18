package com.example.bluetooth.list;

import java.util.List;

import com.example.bluetooth.le.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ResultAdapter extends ArrayAdapter<Result> {

	private int resourceId;
	public ResultAdapter(Context context, int textViewResourceId, List<Result> objects) {
		super(context, textViewResourceId, objects);
		resourceId=textViewResourceId;
	}

	@Override
	public View getView(int position,View convertView,ViewGroup parent)
	{
		Result result=getItem(position);
		View view=null;
		if (convertView==null) {
			view=LayoutInflater.from(getContext()).inflate(resourceId, parent,false);
		}
		else
		{
			view=convertView;
		}	

		TextView date=(TextView) view.findViewById(R.id.date);
		date.setText(result.getDate());
		TextView time=(TextView) view.findViewById(R.id.time);
		time.setText(result.getTime());
		TextView laps=(TextView) view.findViewById(R.id.laps);
		laps.setText(result.getLaps());
		TextView tolTime=(TextView) view.findViewById(R.id.tolTime);
		tolTime.setText(result.getTolTime());
		TextView bestTime=(TextView) view.findViewById(R.id.bestTime);
		bestTime.setText(result.getBestTime());
		return view;
	}

}

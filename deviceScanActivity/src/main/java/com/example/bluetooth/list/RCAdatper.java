package com.example.bluetooth.list;

import java.util.List;

import com.example.bluetooth.le.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RCAdatper  extends ArrayAdapter<RC> {
	private int resourceId;
	public RCAdatper(Context context, int textViewResourceId, List<RC> objects) {
		super(context, textViewResourceId, objects);
		resourceId=textViewResourceId;
	}

	@Override
	public View getView(int position,View convertView,ViewGroup parent)
	{
		RC result=getItem(position);
		View view=convertView;
		ViewHolder viewHolder;
		if (convertView==null) {
			view=LayoutInflater.from(getContext()).inflate(resourceId, parent,false);
			viewHolder=new ViewHolder();
			viewHolder.IRID=(TextView) view.findViewById(R.id.irid_view);
			viewHolder.rc=(TextView) view.findViewById(R.id.rc_view);
			view.setTag(viewHolder);
		}
		else
		{
			viewHolder=(ViewHolder) view.getTag();
		}
		viewHolder.IRID.setText(result.getIRIDInt());
		viewHolder.rc.setText(result.getRc());

		return view;
	}
	
    
    class ViewHolder
    {
    	TextView IRID;
    	TextView rc;
    }
}
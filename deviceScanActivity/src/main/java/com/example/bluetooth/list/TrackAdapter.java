package com.example.bluetooth.list;

import java.util.List;

import com.example.bluetooth.le.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * track  ≈‰∆˜
 * @author Administrator
 *
 */
public class TrackAdapter extends ArrayAdapter<Track>{
	private int resourceId;
	public TrackAdapter(Context context, int textViewResourceId, List<Track> objects) {
		super(context, textViewResourceId, objects);
		resourceId=textViewResourceId;
	}

	@Override
	public View getView(int position,View convertView,ViewGroup parent)
	{
		Track result=getItem(position);
		View view=convertView;
		ViewHolder viewHolder;
		if (convertView==null) {
			view=LayoutInflater.from(getContext()).inflate(resourceId, parent,false);
			viewHolder=new ViewHolder();
			viewHolder.track=(TextView) view.findViewById(R.id.trackTv);
			view.setTag(viewHolder);
		}
		else
		{
			viewHolder=(ViewHolder) view.getTag();
		}
		viewHolder.track.setText(result.getTrack());
		return view;
	}
	
    
    class ViewHolder
    {
    	TextView track;
    }
}
